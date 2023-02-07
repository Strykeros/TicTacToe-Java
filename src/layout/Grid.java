package layout;

import com.game.App;
import logic.CheckWinner;
import logic.Database;
import logic.Matchmaking;
import logic.PlayerTurn;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public void createGrid(int gridRows, int  gridCols) {
        PlayerTurn playerTurn = new PlayerTurn(playerName, currentGameId);
        GridBagConstraints constraints = new GridBagConstraints();
        int btnNum = 1;
        Matchmaking matchmaking = new Matchmaking(this.mainPanel, playerName);
        int gameId = matchmaking.getGameId();

        for (int i = 0; i < gridRows; i++){
            for (int j = 0; j < gridCols; j++){
                JButton btn = new JButton();

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

    private void addClickEvt(JButton btn, PlayerTurn turn, int matchId){
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SideMenu sideMenu = new SideMenu(mainPanel);
                CheckWinner winner = new CheckWinner(mainPanel, playerName);

                if(Objects.equals(btn.getText(), "") && !gameOver && !gridIsLocked){

                    String playerTurn = null;
                    try {
                        playerTurn = turn.checkTurn();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                    String nextTurn = null;
                    try {
                        nextTurn = turn.getNextTurn();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                    btn.setText(playerTurn);
                    setGridLock(true);
                    mainPanel.repaint();
                    String win;
                    String draw;
                    try {
                        win = winner.checkForWin();
                        draw = winner.checkForDraw();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }

                    try {
                        String matchIdStr = String.valueOf(matchId);
                        db.insertGameTurns(matchIdStr, btn.getName(), playerTurn, nextTurn);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }

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

            }
        });
    }

    public void setGameOver(boolean status){
        this.gameOver = status;
    }

    public boolean isGameOver(){
        return this.gameOver;
    }

    public void setGridLock(boolean status){gridIsLocked = status;}

    public boolean isGridLocked() {return gridIsLocked;}

    public void setGameId(int gameId){currentGameId = gameId;}

}
