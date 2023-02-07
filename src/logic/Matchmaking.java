package logic;

import layout.Grid;
import layout.SideMenu;

import javax.swing.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Matchmaking {
    private static JPanel mainPanel;
    private String playerName;
    private Timer matchmakingTimer;
    private Database db = new Database();
    private static int gameId;

    public Matchmaking(JPanel panel, String player){
        mainPanel = panel;
        playerName = player;
    }

    public void searchOpponent(){
        SideMenu menu = new SideMenu(mainPanel);
        Grid grid = new Grid(mainPanel, playerName);

        matchmakingTimer = new Timer(100, e -> {

            try {
                String user = menu.getUsername("X");

               boolean isWaiting = getWaitingStatus();

                if(!isWaiting){
                    if(!Objects.equals(playerName, "none") && Objects.equals(playerName, user)){
                        grid.setGridLock(false);
                        menu.changeInitialText(playerName);
                        matchmakingTimer.stop();
                    }
                    else {
                        grid.setGridLock(true);
                        menu.changeInitialText(playerName);
                        matchmakingTimer.stop();
                    }
                }

            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        matchmakingTimer.start();
    }

    public void updatePlayer(String name) throws SQLException {
        Grid grid = new Grid(mainPanel, name);

        HashMap<String, List<String>> playerData = db.getPlayerData();

        ArrayList<String> playerValuesArr = new ArrayList<>();


        if(playerData == null){
            db.insertPlayerData(name, "none", "none", "true");
            grid.setGridLock(true);
            gameId = 1;
        }
        else {

            for (String key : playerData.keySet()){
                playerValuesArr.add(key + " " + playerData.get(key).toString().replace("[", "").replace("]", "").replace(",", ""));
            }

            for (int i = 0; i < playerValuesArr.size(); i++){
                String arrVal = playerValuesArr.get(i);
                String[] convArr = arrVal.split(" ");

                int id = Integer.parseInt(convArr[0]);
                String playerX = convArr[1];
                String playerO = convArr[2];
                String winner = convArr[3];
                boolean isWaiting = Boolean.parseBoolean(convArr[4]);

                if(isWaiting){
                    if(Objects.equals(playerX, "none") && !Objects.equals(playerX, name)){
                        db.updatePlayerData(id, name, playerO, winner, "false");
                        gameId = id;
                        return;
                    }
                    else if(Objects.equals(playerO, "none") && !Objects.equals(playerO, name)){
                        db.updatePlayerData(id, playerX, name, winner, "false");
                        gameId = id;
                        return;
                    }
                }

            }

            db.insertPlayerData(name, "none", "none", "true");
            fetchGameId();
        }

    }

    private boolean getWaitingStatus() throws SQLException {
        HashMap<String, List<String>> playerData = db.getPlayerData();

        /*ArrayList<String> playerValuesArr = new ArrayList<>();

        for (String key : playerData.keySet()){
            playerValuesArr.add(key + " " + playerData.get(key).toString().replace("[", "").replace("]", "").replace(",", ""));
        }

        String arrayValue = playerValuesArr.get(playerValuesArr.size()-1);

        String[] array = arrayValue.split(" ");

        return array[4];*/

        ArrayList<String> dbValues = new ArrayList<>();
        Object[] keys = playerData.keySet().toArray();

        for (int j = 0; j < keys.length; j++){
            /*List<String> innerList = playerData.get(keys[j]);
            System.out.println(playerData);*/
            int dbGameId = Integer.parseInt((String) keys[j]);

            if(gameId == dbGameId){
                dbValues.add(playerData.get(keys[j]).toString().replace("[", "").replace("]", "").replace(",", ""));
                break;
            }
        }


        String arrayValue = dbValues.get(dbValues.size()-1);
        String[] array = arrayValue.split(" ");
        return Boolean.parseBoolean(array[3]);
    }

    private void fetchGameId() throws SQLException {
        HashMap<String, List<String>> playerData = db.getPlayerData();

        ArrayList<String> playerValuesArr = new ArrayList<>();

        if (playerData != null){

            for (String key : playerData.keySet()){
                playerValuesArr.add(key + " " + playerData.get(key).toString().replace("[", "").replace("]", "").replace(",", ""));
            }

            for (int i = 0; i < playerValuesArr.size(); i++){
                String arrVal = playerValuesArr.get(i);
                String[] convArr = arrVal.split(" ");

                int id = Integer.parseInt(convArr[0]);
                String playerX = convArr[1];
                boolean isWaiting = Boolean.parseBoolean(convArr[4]);

                if(Objects.equals(playerX, playerName) && isWaiting){
                    gameId = id;
                }

            }
        }
    }

    public int getGameId(){
        return gameId;
    }

}
