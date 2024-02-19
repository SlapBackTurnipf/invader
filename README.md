This is the space invaders program I wrote for my SOFT2201 Assignment at the University of Sydney 

I employed multiple design techniques as mentioned below.

Features implemented:
- All difficulties implemented using the singleton pattern, can be changed mid-game using the pause menu
- Time implemented using no particular design pattern however Score used the facade design pattern
- Undo was implemented using the memento design pattern
- Cheat didn't use any particular design pattern.

Manual:
"q" removes fast projectiles
"w" removes slow projectiles
"e" removes fast aliens (aliens that shoot fast projectiles)
"r" removes slow aliens (aliens that shoot slow projectiles)
"esc" key pauses the game

Save can be me implemented via the pause menu. If there are no save states the console will print a message
stating that there are no save states.

If a save state is present for that particular difficulty instance, then the undo button will reload the
game back to that particular save state. (save states for each difficulty will still remain even after 
switching out of a difficulty. Meaning if you were to switch back to that difficulty, that same save state
will still be present.)

Game was engineered to reset your score if you switch difficulty (or continue where you left off). This was a personal decision as I believe
it wouldn't be a fun game if you racked up a score on easy mode and then switched to hard. This way it rewards
players who battled it through a harder difficulty.

Also the cheats are on the opposite side of the keyboard meaning player can't accidentally press them, chances are they 
would press the keys if they meant to. To also make game more challenging, player has to pause game to save and undo. This makes
the game more challenging.
