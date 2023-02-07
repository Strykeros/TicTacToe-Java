package logic;
import layout.Grid;

import java.sql.SQLException;
import java.util.*;

public class PlayerTurn {
    private Database db = new Database();
    private String player;
    private int currentGameId;

    public PlayerTurn(String playerName, int gameId){
        this.currentGameId = gameId;
        this.player = playerName;
    }

    public String checkTurn() throws SQLException {

        HashMap<String, List<String>> gameData = db.getGameTurns();

        String turn;
        if(gameData == null){
            turn = "X";
        }
        else {

            ArrayList<String> dbValues = new ArrayList<>();
            Object[] keys = gameData.keySet().toArray();

            for (int j = 0; j < keys.length; j++){
                List<String> innerList = gameData.get(keys[j]);
                int dbGameId = Integer.parseInt(innerList.get(0));

                if(currentGameId == dbGameId){
                    dbValues.add(gameData.get(keys[j]).toString().replace("[", "").replace("]", "").replace(",", ""));

                }
            }

            if(dbValues.isEmpty()){
                turn = "X";
            }else {
                String arrayValue = dbValues.get(dbValues.size()-1);
                String[] array = arrayValue.split(" ");
                turn = array[3];
            }

        }

        return turn;

    }

    public String getNextTurn() throws SQLException {

        String nextTurn;
        HashMap<String, List<String>> gameData = db.getGameTurns();

        if(gameData == null){
            nextTurn = "O";
        }
        else {

            ArrayList<String> dbValues = new ArrayList<>();
            Object[] keys = gameData.keySet().toArray();

            for (int j = 0; j < keys.length; j++){
                List<String> innerList = gameData.get(keys[j]);
                int dbGameId = Integer.parseInt(innerList.get(0));

                if(currentGameId == dbGameId){
                    dbValues.add(gameData.get(keys[j]).toString().replace("[", "").replace("]", "").replace(",", ""));

                }
            }

            if(dbValues.isEmpty()){
                nextTurn = "O";
            }else {
                String arrayValue = dbValues.get(dbValues.size()-1);

                String[] gmValsArr = arrayValue.split(" ");

                if(Objects.equals(gmValsArr[3], "X")){
                    nextTurn = "O";
                }else {
                    nextTurn = "X";
                }
            }



        }
        return nextTurn;
    }

}
