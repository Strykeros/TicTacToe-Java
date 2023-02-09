package layout;

import logic.CheckWinner;
import logic.Database;
import logic.Matchmaking;
import logic.PlayerTurn;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Objects;

public class Grid{

    private JPanel mainPanel;

    private boolean gameOver = false;
    private static boolean gridIsLocked = true;
    private static String playerName;
    private static int currentGameId;

    Database db = new Database();

    public Grid(JPanel panel, String player){
        this.mainPanel = panel;
        playerName = player;
    }

    // Create the game grid with buttons
    public void createGrid(int gridRows, int  gridCols) {
        PlayerTurn playerTurn = new PlayerTurn(playerName, currentGameId);
        GridBagConstraints constraints = new GridBagConstraints();
        int btnNum = 1;
        Matchmaking matchmaking = new Matchmaking(this.mainPanel, playerName);
        int gameId = matchmaking.getGameId();

        for (int i = 0; i < gridRows; i++){
            for (int j = 0; j < gridCols; j++){
                JButton btn = new JButton();
                Font font = new Font("Arial", Font.BOLD, 40);
                btn.setFont(font);
                addClickEvt(btn, playerTurn, gameId);

                btn.setName("Button" + btnNum);

                constraints.gridx = j;
                constraints.weightx = 0.5;
                constraints.weighty = 0.5;
                constraints.gridy = i;
                constraints.fill = GridBagConstraints.BOTH;

                mainPanel.add(btn, constraints);
                btnNum++;
            }
        }



    }

    // Add a click event for each game button
    private void addClickEvt(JButton btn, PlayerTurn turn, int matchId){
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SideMenu sideMenu = new SideMenu(mainPanel);
                CheckWinner winner = new CheckWinner(mainPanel, playerName);

                if(Objects.equals(btn.getText(), "") && !gameOver && !gridIsLocked){

                    String playerTurn = checkTurnStatus(turn);
                    String nextTurn = checkNextTurnStatus(turn);

                    btn.setText(playerTurn);
                    setGridLock(true);
                    mainPanel.repaint();
                    String win;
                    String draw;

                    win = getWinStatus(winner);
                    draw = getDrawStatus(winner);

                    insertTurn(matchId, btn, playerTurn, nextTurn);

                    checkForWinAndDraw(win, draw, playerTurn, sideMenu);


                }

            }
        });
    }

    // returns the current turn for the game
    private String checkTurnStatus(PlayerTurn turn){
        String playerTurn;
        try {
            playerTurn = turn.checkTurn();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        return playerTurn;
    }

    // returns the next turn for the game
    private String checkNextTurnStatus(PlayerTurn turn){
        String nextTurn;
        try {
            nextTurn = turn.getNextTurn();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        return nextTurn;
    }

    // returns draw if exists
    private String getDrawStatus(CheckWinner winner){
        String draw;

        try {
            draw = winner.checkForDraw();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        return draw;
    }

    // returns the winner if exists
    private String getWinStatus(CheckWinner winner){
        String win;
        try {
            win = winner.checkForWin();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        return win;
    }

    // Checks for the winner and draw
    private void checkForWinAndDraw(String win, String draw, String playerTurn, SideMenu sideMenu){
        if(win != null){
            gameOver = true;
            try {
                sideMenu.changeTxtValue(win, gameOver);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
        else if(draw != null && draw.equals("draw")){
            gameOver = true;
            try {
                sideMenu.changeTxtValue("draw", gameOver);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
        else {
            try {
                sideMenu.changeTxtValue(playerTurn, gameOver);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private void insertTurn(int matchId, JButton btn, String playerTurn, String nextTurn){
        try {
            String matchIdStr = String.valueOf(matchId);
            db.insertGameTurns(matchIdStr, btn.getName(), playerTurn, nextTurn);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    // change "gameOver" boolean
    public void setGameOver(boolean status){
        this.gameOver = status;
    }

    // return "gameOver" boolean value
    public boolean isGameOver(){
        return this.gameOver;
    }

    // change "gridIsLocked" boolean
    public void setGridLock(boolean status){gridIsLocked = status;}

    // return "gameOver" boolean value
    public boolean isGridLocked() {return gridIsLocked;}

    // set "currentGameId" value
    public void setGameId(int gameId){currentGameId = gameId;}

}
