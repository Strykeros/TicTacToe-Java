package logic;

import layout.SideMenu;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public class CheckWinner {

    private static JPanel mainPanel;
    private Database db = new Database();
    private SideMenu menu;
    private static String playerName;
    private static boolean winnerExists = false;

    private static int[][] winCombos = {
            {1, 2, 3}, {4, 5, 6}, {7, 8, 9},
            {1, 4, 7}, {2, 5, 8}, {3, 6, 9},
            {1, 5, 9}, {3, 5, 7}
    };

    public CheckWinner(JPanel panel, String player){
        mainPanel = panel;
        playerName = player;
    }

    // Check for the winner on each turn
    public String checkForWin() throws SQLException {
        HashMap<String, String> btns = new HashMap<>();
        Component[] childs = mainPanel.getComponents();
        menu = new SideMenu(mainPanel);

        for (int i = 0; i < childs.length; i++){
            if(childs[i] instanceof JButton){
                String btnName = childs[i].getName();
                String btnTxt = ((JButton) childs[i]).getText();
                btns.put(btnName, btnTxt);
            }
        }

        for (int i = 0; i < winCombos.length; i++){

            int j = 0;

            String btnName1 = "Button" + winCombos[i][j];
            String btn1 = btns.get(btnName1);
            String btnName2 = "Button" + winCombos[i][j+1];
            String btn2 = btns.get(btnName2);
            String btnName3 = "Button" + winCombos[i][j+2];
            String btn3 = btns.get(btnName3);


            if(Objects.equals(btn1, btn2) &&
                    Objects.equals(btn2, btn3) &&
                    Objects.equals(btn1, btn3) &&
                    !Objects.equals(btn1, "")  &&
                    !Objects.equals(btn2, "")  &&
                    !Objects.equals(btn3, "")){
                 insertData(btn1);
                 winnerExists = true;
                 return btn1;
            }

        }
        return null;
    }

    // Check for draw on each turn
    public String checkForDraw() throws SQLException {
        HashMap<String, String> btns = new HashMap<>();
        Component[] childs = mainPanel.getComponents();


        for (int i = 0; i < childs.length; i++){
            if(childs[i] instanceof JButton){
                String btnName = childs[i].getName();
                String btnTxt = ((JButton) childs[i]).getText();
                btns.put(btnName, btnTxt);
            }
        }

        boolean gridIsFull = btns.containsValue("");

        if(!gridIsFull && !winnerExists){
            String status = "draw";
            insertData(status);
            return status;
        }

        return null;

    }

    // Insert the winner or "draw" in the database
    private void insertData(String gameStatus) throws SQLException {
        Matchmaking matchmaking = new Matchmaking(mainPanel, playerName);
        int currentGameId = matchmaking.getGameId();
        HashMap<String, List<String>> playerData = db.getPlayerData();

        ArrayList<String> keys = new ArrayList<>(playerData.keySet());
        ArrayList<String> dbValues = new ArrayList<>();


        for (int j = 0; j < keys.size(); j++){
            int dbGameId = Integer.parseInt(keys.get(j));

            if(currentGameId == dbGameId){
                dbValues.add(dbGameId + " " + playerData.get(keys.get(j)).toString().replace("[", "").replace("]", "").replace(",", ""));

            }
        }

        String arrayValue = dbValues.get(dbValues.size()-1);

        String[] convArr = arrayValue.split(" ");

        int id = Integer.parseInt(convArr[0]);
        String playerX = convArr[1];
        String playerO = convArr[2];
        boolean isWaiting = Boolean.parseBoolean(convArr[4]);

        if(Objects.equals(gameStatus, "X")){
            db.updatePlayerData(id, playerX, playerO, playerX, String.valueOf(isWaiting));
        }else if(Objects.equals(gameStatus, "O")){
            db.updatePlayerData(id, playerX, playerO, playerO, String.valueOf(isWaiting));
        }else {
            db.updatePlayerData(id, playerX, playerO, gameStatus, String.valueOf(isWaiting));
        }

    }

}
