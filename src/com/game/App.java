package com.game;

import layout.Grid;
import layout.SideMenu;
import logic.CheckWinner;
import logic.Database;
import logic.Matchmaking;
import logic.PlayerTurn;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public class App {
    private JPanel mainPanel;
    private static String playerName;
    private static JFrame game;
    private static PlayerTurn turn;
    private static CheckWinner winner;
    private static SideMenu menu;
    private static Grid grid;
    private static Database db = new Database();

    public static void main(String[] args) throws SQLException {
        int rowCount = 3;
        int colCount = 3;

        GridBagLayout gridLayout = new GridBagLayout();

        db.connectToDB();
        JPanel newApp = new App().mainPanel;
        setPlayerName(newApp);

        Matchmaking matchmaking = new Matchmaking(newApp, playerName);
        int gameId = matchmaking.getGameId();

        initGame(gameId, rowCount, colCount, newApp, gridLayout);

        createMainThread(newApp, gameId);

    }

    // Synchronize the grid, turn, winner from the database
    public static void syncData(Database database, JPanel appPanel, Grid grid, int currentGameId) throws SQLException {

        Component[] childs = appPanel.getComponents();
        HashMap<String, List<String>> dbData = database.getGameTurns();

        boolean gridIsLocked = grid.isGridLocked();

        if(dbData == null && !gridIsLocked) return;

        for (int i = 0; i < childs.length; i++) {
            if (childs[i] instanceof JButton) {

                String btnName = childs[i].getName();
                String btnValue = ((JButton) childs[i]).getText();
                ArrayList<String> dbValues = new ArrayList<>();
                Object[] keys = dbData.keySet().toArray();

                for (int j = 0; j < keys.length; j++){
                    List<String> innerList = dbData.get(keys[j]);
                    int dbGameId = Integer.parseInt(innerList.get(0));

                    if(currentGameId == dbGameId){
                        dbValues.add(dbData.get(keys[j]).toString().replace("[", "").replace("]", "").replace(",", ""));
                    }
                }

                if(dbValues.isEmpty()) return;

                String arrayValue = dbValues.get(dbValues.size()-1);

                String[] array = arrayValue.split(" ");

                String savedBtnName = array[1];
                String savedBtnValue = array[2];
                JButton currentBtn = (JButton) childs[i];

                refreshGrid(btnValue, savedBtnValue, btnName, savedBtnName, currentBtn);

            }
        }


    }

    // Get and set the player alias from the user input
    private static void setPlayerName(JPanel appPanel) throws SQLException {

        final int nameLength = 4;
        while (playerName == null || playerName.length() < nameLength){
            playerName = JOptionPane.showInputDialog("Enter your nickname.\nNickname has to be at least 4 characters long.");
        }
        Matchmaking matchmaking = new Matchmaking(appPanel, playerName);

        matchmaking.updatePlayer(playerName);

        matchmaking.searchOpponent();

    }

    // Create JPanel for the game
    private static void initGame(int gameId, int gridRows, int gridCols, JPanel panel, GridBagLayout gridLayout){
        game = new JFrame("TicTacToe - " + playerName);
        game.setResizable(false);
        game.setBounds(100, 100, 450, 650);


        turn = new PlayerTurn(playerName, gameId);
        menu = new SideMenu(panel);
        winner = new CheckWinner(panel, playerName);

        game.setContentPane(panel);
        game.setLayout(gridLayout);

        game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initGrid(gameId, gridRows, gridCols, panel);

        menu.initSidePanel(playerName, gameId);

        game.pack();
        game.setSize(800, 600);
        game.setLocationRelativeTo(null);
        game.setVisible(true);
    }

    // Creates the game's grid
    private static void initGrid(int gameId, int gridRows, int gridCols, JPanel panel){
        grid = new Grid(panel, playerName);
        grid.setGameId(gameId);
        grid.createGrid(gridRows, gridCols);
    }

    // Creates a main thread for syncing data
    private static void createMainThread(JPanel panel, int gameId){
        Thread mainThread = new Thread(() -> {
            try {

                while (true){

                    syncData(db, panel, grid, gameId);
                    Thread.sleep(500);
                }

            } catch (SQLException | InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        });
        mainThread.start();
    }

    // synchronise grid from database
    private static void refreshGrid(String btnValue, String savedBtnValue, String btnName, String savedBtnName, JButton gridBtn) throws SQLException {
        if(!Objects.equals(btnValue, savedBtnValue) && btnValue.isEmpty()){

            if (Objects.equals(btnName, savedBtnName)){
                ((JButton) gridBtn).setText(savedBtnValue);
                turn.checkTurn();
                turn.getNextTurn();
                ((JButton) gridBtn).repaint();

                grid.setGridLock(false);
                syncWinAndDraw(savedBtnValue);

            }
        }

    }

    // synchronise win or draw status from database
    private static void syncWinAndDraw(String savedBtnValue){
        String win;
        String draw;

        try {
            win = winner.checkForWin();
            draw = winner.checkForDraw();

            if(win != null){
                grid.setGameOver(true);
                menu.changeTxtValue(savedBtnValue, grid.isGameOver());
            }
            else if(draw != null && draw.equals("draw")){
                grid.setGameOver(true);
                menu.changeTxtValue("draw", grid.isGameOver());
            }
            else {
                menu.changeTxtValue(savedBtnValue, grid.isGameOver());
            }

        } catch (SQLException ex) {
            throw new RuntimeException(ex);

        }
    }

}
