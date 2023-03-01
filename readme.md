## Tic-Tac-Toe game

<p>This is a Tic-Tac-Toe game written in Java. The game has a grid of 9 fields in which the user can choose to click on in order to place a nought or cross. The winner is the player who places all three marks in the horizontal, vertical, or diagonal row. The player can use any alias to repesent him/her in the game, which has to be provided at the start of the game. This program is able to handle multiple instances of games at once locally. The program uses the Swing GUI widget toolkit and SQLite database.</p>
<p>The first input is the preffered name from the player. When the program starts up, It will ask the user to input his/her preffered nickname. The alias has to be at least four characters long, otherwise the program will keep asking for the nickname. The second input is the user click on the field. When the player clicks on an empty field, it will insert the player’s mark (an “X” or an “O”) into the database. The next input is fetching the saved players moves from the database, that are then placed with the opponent’s mark in the approprieate fields, based on which the opponent has clicked on. The last input is clicking on the leaderboard button, located on the side menu, which displays a table with place, username and win rows in a new JPanel window.</p>


## How to play the game

1. Start the program;
2. Write your nickname in the given input;
3. Wait for the opponent to do the first and second step. While you wait for the opponent, you can check if there are any players on the leaderboard and their places, wins, by pressing the “leaderboard” button in the side menu;
4. When the opponent has connected you can start playing by clicking on any 9 fields you wish. Or, if you were not the first, that connected to the game, you have to wait for the opponent to make a move. The side menu will tell you, when the turn is yours. Then you have to click on the field that you choose;
5. The player who manages to place all three marks in the horizontal, vertical, or diagonal row, is the winner! If no one manages to do that, then it is a draw.
