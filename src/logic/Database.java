package logic;

import java.sql.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Database {

    private static Connection conn =  null;

    // Connect to the Database
    public void connectToDB(){
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:src/databases/game_data.db");
            createTurnTable(conn);
            createPlayerTable(conn);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Create a "turns" table in the database if it doesn't exist yet.
    private void createTurnTable(Connection conn) throws SQLException {
        String createTable = "" + "CREATE TABLE IF NOT EXISTS turns " +
                "( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "game_id INTEGER NOT NULL, "+
                "clicked_btn TEXT NOT NULL," +
                "btn_value TEXT NOT NULL," +
                "next_turn TEXT NOT NULL" +
                "); " +
                "";

        Statement statement = conn.createStatement();
        statement.execute(createTable);
        statement.close();
    }

    // Create a "games" table in the database if it doesn't exist yet.
    private void createPlayerTable(Connection conn) throws SQLException {
        String createTable = "" + "CREATE TABLE IF NOT EXISTS games " +
                "( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "player_x TEXT, " +
                "player_o TEXT, " +
                "winner TEXT, " +
                "waiting_opponent TEXT" +
                "); " +
                "";

        Statement statement = conn.createStatement();
        statement.execute(createTable);
        statement.close();
    }

    // Insert data into the "turns" table in the database.
    public void insertGameTurns(String gameId, String clickedBtnName, String btnValue, String nextTurn) throws SQLException{
        String insertData = " INSERT INTO turns (game_id, clicked_btn, btn_value, next_turn) VALUES(?, ?, ?, ?)";
        PreparedStatement prepState = conn.prepareStatement(insertData);
        prepState.setString(1, gameId);
        prepState.setString(2, clickedBtnName);
        prepState.setString(3, btnValue);
        prepState.setString(4, nextTurn);
        prepState.executeUpdate();
        prepState.close();
    }

    // Retrieve data from the "turns" table in the database and return a HashMap.
    public HashMap<String, List<String>> getGameTurns() throws SQLException {
        HashMap<String, List<String>> gameData = new HashMap<String, List<String>>();
        String selectData = "SELECT * FROM turns";
        Statement statement = conn.createStatement();
        ResultSet results = statement.executeQuery(selectData);

        while (results.next()){
            String id = results.getString("id");
            String game_id = results.getString("game_id");
            String btn = results.getString("clicked_btn");
            String btn_value = results.getString("btn_value");
            String next_turn = results.getString("next_turn");

            gameData.put(id, Arrays.asList(game_id, btn, btn_value, next_turn) );
        }

        statement.close();

        if(gameData.isEmpty()){
            return null;
        }else {
            return gameData;
        }
    }

    // Retrieve data from the "games" table in the database and return a HashMap.
    public HashMap<String, List<String>> getPlayerData() throws SQLException{

        HashMap<String, List<String>> playerData = new HashMap<String, List<String>>();

        String selectData = "SELECT * FROM games";
        Statement statement = conn.createStatement();
        ResultSet results = statement.executeQuery(selectData);

        while (results.next()){
            String id = results.getString("id");
            String playerX = results.getString("player_x");
            String playerO = results.getString("player_o");
            String winner = results.getString("winner");
            String waitingOpponent = results.getString("waiting_opponent");

            playerData.put(id, Arrays.asList(playerX, playerO, winner, waitingOpponent));
        }

        statement.close();

        if(playerData.isEmpty()){
            return null;
        }else {
            return playerData;
        }

    }

    // Insert data in the "games" table.
    public void insertPlayerData(String playerX, String playerO, String winner, String waitingOpponent) throws SQLException{
        String insertData = " INSERT INTO games (player_x, player_o, winner, waiting_opponent) VALUES(?, ?, ?, ?)";

        PreparedStatement prepState = conn.prepareStatement(insertData);
        prepState.setString(1, playerX);
        prepState.setString(2, playerO);
        prepState.setString(3, winner);
        prepState.setString(4, waitingOpponent);

        prepState.executeUpdate();
        prepState.close();
    }

    // Update data in the "games" table.
    public void updatePlayerData(int id, String player_x, String player_o, String winner, String waiting_opponent) throws SQLException{
        String updateData = " UPDATE games SET player_x = ?, player_o = ?, winner = ?, waiting_opponent = ? WHERE id = ?";

        PreparedStatement prepState = conn.prepareStatement(updateData);
        prepState.setString(1, player_x);
        prepState.setString(2, player_o);
        prepState.setString(3, winner);
        prepState.setString(4, waiting_opponent);
        prepState.setInt(5, id);

        prepState.executeUpdate();
        prepState.close();
    }

}
