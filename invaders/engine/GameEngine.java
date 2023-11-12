package invaders.engine;

import java.util.ArrayList;
import java.util.List;

import invaders.App;
import invaders.ConfigReader;
import invaders.builder.BunkerBuilder;
import invaders.builder.Director;
import invaders.builder.EnemyBuilder;
import invaders.factory.EnemyProjectile;
import invaders.factory.Projectile;
import invaders.gameobject.Bunker;
import invaders.gameobject.Enemy;
import invaders.gameobject.GameObject;
import invaders.entities.Player;
import invaders.memento.GameMemento;
import invaders.rendering.Renderable;
import invaders.score.ScoreUpdateFacade;
import invaders.strategy.FastProjectileStrategy;
import invaders.strategy.SlowProjectileStrategy;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.json.simple.JSONObject;
import invaders.hud.HudManager;

/**
 * This class manages the main loop and logic of the game
 */
public class GameEngine {
	private List<GameObject> gameObjects = new ArrayList<>(); // A list of game objects that gets updated each frame
	private List<GameObject> pendingToAddGameObject = new ArrayList<>();
	private List<GameObject> pendingToRemoveGameObject = new ArrayList<>();

	private List<Renderable> pendingToAddRenderable = new ArrayList<>();
	private List<Renderable> pendingToRemoveRenderable = new ArrayList<>();

	private List<Renderable> renderables =  new ArrayList<>();
	private boolean isPaused = false;

	private Player player;

	private boolean left;
	private boolean right;
	private int gameWidth;
	private int gameHeight;
	private int timer = 45;
	private App app;
	private GameWindow window;
	private String config;
	private HudManager hudManager;
	private ScoreUpdateFacade scoreUpdateFacade;
	private int score = 0;
	private float time;
	private GameMemento memento;
	private ArrayList<Renderable> mementoRenderables;

	public GameEngine(String config, App app){
		this.app = app;
		this.config = config;
		this.hudManager = new HudManager();
		// Read the config here
		ConfigReader.parse(config);

		// Get game width and height
		gameWidth = ((Long)((JSONObject) ConfigReader.getGameInfo().get("size")).get("x")).intValue();
		gameHeight = ((Long)((JSONObject) ConfigReader.getGameInfo().get("size")).get("y")).intValue();

		//Get player info
		this.player = new Player(ConfigReader.getPlayerInfo());
		renderables.add(player);


		Director director = new Director();
		BunkerBuilder bunkerBuilder = new BunkerBuilder();
		//Get Bunkers info
		for(Object eachBunkerInfo:ConfigReader.getBunkersInfo()){
			Bunker bunker = director.constructBunker(bunkerBuilder, (JSONObject) eachBunkerInfo);
			gameObjects.add(bunker);
			renderables.add(bunker);
		}


		EnemyBuilder enemyBuilder = new EnemyBuilder();
		//Get Enemy info
		for(Object eachEnemyInfo:ConfigReader.getEnemiesInfo()){
			Enemy enemy = director.constructEnemy(this,enemyBuilder,(JSONObject)eachEnemyInfo);
			gameObjects.add(enemy);
			renderables.add(enemy);
		}

		scoreUpdateFacade = new ScoreUpdateFacade();


	}
	public void pause() {
		isPaused = true;

	}
	public void resume() {
		KeyboardInputHandler.getInstance(this, App.currentDifficulty).resetKeyState();
		isPaused = false; }
	public void togglePause() {
			pause();
			App.getWindow().pauseGame();
			KeyboardInputHandler.getInstance(this, App.currentDifficulty).resetKeyState();
			Button resumeButton = new Button("Resume");
			resumeButton.setOnAction(f -> {
				KeyboardInputHandler.getInstance(this, App.currentDifficulty).resetKeyState();
				app.startGame(config);
			});

			Button mainMenuButton = new Button("Change Difficulty");
			// Assuming setupMainMenu() is a method in App class
			mainMenuButton.setOnAction(f -> {
				KeyboardInputHandler.getInstance(this, App.currentDifficulty).resetKeyState();
				app.setupMainMenu();
			});
			Button undoButton = new Button("Undo");
			undoButton.setOnAction(f -> {
				KeyboardInputHandler.getInstance(this, App.currentDifficulty).resetKeyState();
				restoreStateFromMemento();

				app.startGame(config);

			});

			VBox buttonLayout = new VBox(10, resumeButton, mainMenuButton, undoButton);
			buttonLayout.setAlignment(Pos.CENTER);

			StackPane layout = new StackPane(buttonLayout);
			layout.setAlignment(Pos.CENTER);

			Scene pauseMenuScene = new Scene(layout, 300, 250);
			app.getPrimaryStage().setScene(pauseMenuScene);

		}


	public void setPaused(boolean b) {
		isPaused = b;
	}

	/**
	 * Updates the game/simulation
	 */
	public void update(){
		if (isPaused) {
			return;
		}
		timer+=1;
		movePlayer();

		for(GameObject go: gameObjects){
			go.update(this);
		}

		for (int i = 0; i < renderables.size(); i++) {
			Renderable renderableA = renderables.get(i);
			for (int j = i+1; j < renderables.size(); j++) {
				Renderable renderableB = renderables.get(j);

				//added this to make life easier
				if (renderableA.getRenderableObjectName().equals("PlayerProjectile")) {
					//swap the renderables
					Renderable temp = renderableA;
					renderableA = renderableB;
					renderableB = temp;
				}

				if(((renderableA.getRenderableObjectName().equals("slow_alien") || renderableA.getRenderableObjectName().equals("fast_alien")) && (renderableB.getRenderableObjectName().equals("slow_projectile") || renderableB.getRenderableObjectName().equals("fast_projectile")))
						||(renderableA.getRenderableObjectName().equals("slow_projectile") || renderableA.getRenderableObjectName().equals("fast_projectile")) && (renderableB.getRenderableObjectName().equals("slow_alien") || renderableB.getRenderableObjectName().equals("fast_alien"))||
						(renderableA.getRenderableObjectName().equals("slow_projectile") || renderableA.getRenderableObjectName().equals("fast_projectile")) && (renderableB.getRenderableObjectName().equals("slow_projectile") || renderableB.getRenderableObjectName().equals("fast_projectile"))){
				}else{
					if(renderableA.isColliding(renderableB) && (renderableA.getHealth()>0 && renderableB.getHealth()>0)) {
						renderableA.takeDamage(1);
						renderableB.takeDamage(1);
						scoreUpdateFacade.updateScore(renderableA, renderableB, score);
						score = scoreUpdateFacade.updateScore(renderableA, renderableB, score);
						scoreUpdateFacade.showScore(App.getWindow(), score);
					}
				}
			}
		}


		// ensure that renderable foreground objects don't go off-screen
		int offset = 1;
		for(Renderable ro: renderables){
			if(!ro.getLayer().equals(Renderable.Layer.FOREGROUND)){
				continue;
			}
			if(ro.getPosition().getX() + ro.getWidth() >= gameWidth) {
				ro.getPosition().setX((gameWidth - offset) -ro.getWidth());
			}

			if(ro.getPosition().getX() <= 0) {
				ro.getPosition().setX(offset);
			}

			if(ro.getPosition().getY() + ro.getHeight() >= gameHeight) {
				ro.getPosition().setY((gameHeight - offset) -ro.getHeight());
			}

			if(ro.getPosition().getY() <= 0) {
				ro.getPosition().setY(offset);
			}
		}

	}

	public List<Renderable> getRenderables(){
		return renderables;
	}

	public List<GameObject> getGameObjects() {
		return gameObjects;
	}
	public List<GameObject> getPendingToAddGameObject() {
		return pendingToAddGameObject;
	}

	public List<GameObject> getPendingToRemoveGameObject() {
		return pendingToRemoveGameObject;
	}

	public List<Renderable> getPendingToAddRenderable() {
		return pendingToAddRenderable;
	}

	public List<Renderable> getPendingToRemoveRenderable() {
		return pendingToRemoveRenderable;
	}


	public void leftReleased() {
		this.left = false;
	}

	public void rightReleased(){
		this.right = false;
	}

	public void leftPressed() {
		this.left = true;
	}
	public void rightPressed(){
		this.right = true;
	}

	public boolean shootPressed(){
		if(timer>45 && player.isAlive()){
			Projectile projectile = player.shoot();
			gameObjects.add(projectile);
			renderables.add(projectile);
			timer=0;
			return true;
		}
		return false;
	}

	private void movePlayer(){
		if(left){
			player.left();
		}

		if(right){
			player.right();
		}
	}

	public int getGameWidth() {
		return gameWidth;
	}

	public int getGameHeight() {
		return gameHeight;
	}

	public Player getPlayer() {
		return player;
	}
	public boolean getIsPaused() {
		return isPaused;
	}
	public int getScore() {
		return score;
	}

	public void setGameObjects(List<GameObject> gameObjects) {
	}

	public void setPendingToAddGameObject(List<GameObject> pendingToAddGameObject) {
	}

	public void setPendingToRemoveGameObject(List<GameObject> pendingToRemoveGameObject) {
	}

	public void setPendingToAddRenderable(List<Renderable> pendingToAddRenderable) {
	}

	public void setPendingToRemoveRenderable(List<Renderable> pendingToRemoveRenderable) {
	}

	public void setRenderables(List<Renderable> renderables) {
	}


	public void setPlayer(Player player) {
	}

	public void setTimer(int timer) {
	}

	public void setScore(int score) {
		this.score = score;
	}


	public float getTimer() {
		return App.getWindow().getTime();
	}
	public void saveStateToMemento() {
		List<Renderable> enemiesAndProjectiles = new ArrayList<>();
		for (Renderable r : renderables) {
			if (r instanceof Enemy || r instanceof Projectile || r instanceof Bunker) {
				enemiesAndProjectiles.add(r);
			}
		}
		memento = new GameMemento(new ArrayList<>(enemiesAndProjectiles), score, getTimer());


	}

	public void restoreStateFromMemento() {
		System.out.println(renderables.equals(memento.getRenderablesSnapshot()));
		if (memento != null && !memento.getRenderablesSnapshot().isEmpty()) {
			// Clear existing enemies, projectiles, and other entities from the game state
			clearEnemiesAndProjectiles();
			App.getWindow().removeAllEntitiesFromScreen();

			// Restore the state from the memento
			memento.restoreState(this);

			// Clear current renderables and game objects to prepare for restored state

			renderables.clear();
			gameObjects.clear();

			// Retrieve the snapshot of renderables from the memento
			List<Renderable> renderableSnapshot = new ArrayList<>(memento.getRenderablesSnapshot());

			// Add player to the list of renderables
			renderableSnapshot.add(player);
			System.out.println(renderables);
			// Update the game state with the restored renderables and game objects
			renderables.addAll(renderableSnapshot);
			for (Renderable r : renderableSnapshot) {
				if (r instanceof GameObject) {
					gameObjects.add((GameObject) r);
				}
			}
			System.out.println(renderables);
		}
	}
	public void addAllRenderablesAndGameObjects(List<Renderable> newRenderables) { //from git
		renderables.addAll(newRenderables);

		for (Renderable r : newRenderables) {
			if (r instanceof GameObject) {
				gameObjects.add((GameObject) r);
			}
		}
	}
	public void removeAllRenderablesAndGameObjects(List<Renderable> newRenderables) { //from git
		renderables.removeAll(newRenderables);

		for (Renderable renderable : newRenderables) { //added from git
			if (renderable instanceof GameObject) {
				gameObjects.remove((GameObject) renderable);
			}
		}
		//the previous images are still on the screen so we need to remove them, they are still here because we are not removing them from the pane
		//we are just removing them from the renderables list



	}
	public void clearEnemiesAndProjectiles() { //added
		// Remove only the enemies and enemy projectiles from gameObjects and renderables
		gameObjects.removeIf(obj -> obj instanceof Enemy || obj instanceof EnemyProjectile);
		renderables.removeIf(ren -> ren instanceof Enemy || ren instanceof EnemyProjectile);
	}

	private List<Renderable> getEnemiesAndProjectiles() { //added from git
		List<Renderable> enemiesAndProjectiles = new ArrayList<>();
		for (Renderable renderable : renderables) {
			if (renderable instanceof Enemy || renderable instanceof EnemyProjectile) {
				enemiesAndProjectiles.add(renderable);
			}
		}
		return enemiesAndProjectiles;
	}

	//EVERYTHING BELOW ADDED FROM GITHUB
	public void updateScore(int value) {
		this.score += value;
		//then this score is passed to the facade
		scoreUpdateFacade.updateScore(score);
		scoreUpdateFacade.showScore(App.getWindow(), score);

	}
	public void removeFastProjectiles() { //added from git
		List<Renderable> fastProjectiles = new ArrayList<>();
		for (Renderable renderable : renderables) {
			if (renderable instanceof EnemyProjectile && ((EnemyProjectile) renderable).getStrategy() instanceof FastProjectileStrategy) {
				fastProjectiles.add(renderable);
			}
		}
		for (Renderable projectile : fastProjectiles) {
			projectile.takeDamage(projectile.getHealth());
		}
		updateScore(fastProjectiles.size() * 2);
	}

	public void removeSlowProjectiles() {  //added from git
		List<Renderable> slowProjectiles = new ArrayList<>();
		for (Renderable renderable : renderables) {
			if (renderable instanceof EnemyProjectile && ((EnemyProjectile) renderable).getStrategy() instanceof SlowProjectileStrategy) {
				slowProjectiles.add(renderable);
			}
		}
		for (Renderable projectile : slowProjectiles) {
			projectile.takeDamage(projectile.getHealth());
		}
		updateScore(slowProjectiles.size());
	}





	public void removeFastAliens() { //added from git
		List<Renderable> fastAliens = new ArrayList<>();
		for (Renderable renderable : renderables) {
			if (renderable instanceof Enemy && ((Enemy) renderable).getProjectileStrategy() instanceof FastProjectileStrategy) {
				fastAliens.add(renderable);
			}
		}
		for (Renderable enemy : fastAliens) {
			enemy.takeDamage(enemy.getHealth());
		}
		updateScore(fastAliens.size() * 4);
	}




	public void removeSlowAliens() { //added from git
		List<Renderable> slowAliens = new ArrayList<>();
		for (Renderable renderable : renderables) {
			if (renderable instanceof Enemy && ((Enemy) renderable).getProjectileStrategy() instanceof SlowProjectileStrategy) {
				slowAliens.add(renderable);
			}
		}
		for (Renderable enemy : slowAliens) {
			enemy.takeDamage(enemy.getHealth());
		}
		updateScore(slowAliens.size() * 3);
	}


	public void setTime(float time) {
		this.time = time;
	}

	public float getTime() {
		return time;
	}

	public Renderable getSlowEnemy() {
		return renderables.stream()
				.filter(r -> r instanceof Enemy && ((Enemy) r).getProjectileStrategy() instanceof SlowProjectileStrategy)
				.findFirst()
				.orElse(null);

	}
}
