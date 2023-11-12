package invaders.score;

public interface Score {
    //please assume this as score the verb and not the noun, as in "to 'score' a point"
    void updateScore(int score);
    int getScore();
}
