package layout;

import logic.Database;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public class SideMenu {

    private static JPanel mainPanel;
    private static String startTxtVal = "Waiting for opponent";
    private static String turnTxtVal = "'s turn";
    private static String currentPlayer;
    private static JLabel turnTxt;
    private static JButton leaderboardBtn;
    private static int currentGameId;
    private Database db = new Database();

    public SideMenu(JPanel panel){
        mainPanel = panel;

    }

    // Main function that initializes Label and leaderboard button
    public void initSidePanel(String player, int gameId) {

        currentGameId = gameId;
        currentPlayer = player;
        playerTurnTxt();
        leaderboardBtn();
    }

    // Create and add Label to the side panel
    private void playerTurnTxt() {

        GridBagConstraints constraints = new GridBagConstraints();
        turnTxt = new JLabel();
        Font font = new Font("Arial", Font.BOLD, 18);
        turnTxt.setFont(font);

        turnTxt.setText(startTxtVal);
        turnTxt.setSize(450, 40);
        turnTxt.setBorder(new EmptyBorder(20, 20, 20, 20));

        mainPanel.add(turnTxt, constraints);

    }

    // Create and add leaderboard button to the side panel
    private void leaderboardBtn(){
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = 1;
        leaderboardBtn = new JButton();
        Font font = new Font("Arial", Font.BOLD, 18);
        leaderboardBtn.setFont(font);
        leaderboardBtn.setText("Leaderboard");
        int marginSize = 30;
        constraints.insets = new Insets(marginSize, marginSize, marginSize, marginSize);

        leaderboardBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    getStats();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        mainPanel.add(leaderboardBtn, constraints);
    }

    // Update Label text based on the state of the game
    public void changeTxtValue(String player, boolean gameOver) throws SQLException {


        String playerName = getUsername(player);

        if(gameOver){

            if(Objects.equals(player, "draw")){
                turnTxt.setText("It's a draw!");
            }
            else {
                turnTxt.setText("The winner is " + playerName);
            }

        }
        else {
            if(Objects.equals(player, "X")){
                String playr = getUsername("O");

                if(Objects.equals(playr, currentPlayer)){
                    turnTxt.setText("Your turn");
                }else {
                    turnTxt.setText(playr + "'s turn");
                }

            }else {
                String playr = getUsername("X");
                if(Objects.equals(playr, currentPlayer)){
                    turnTxt.setText("Your turn");
                }else {
                    turnTxt.setText(playr + "'s turn");
                }
            }
        }

    }

    // Get username from database based on the "X" or "O" symbol
    public String getUsername(String player) throws SQLException {
        HashMap<String, List<String>> playerData = db.getPlayerData();

        ArrayList<String> playerValuesArr = new ArrayList<>();

        ArrayList<String> keys = new ArrayList<>(playerData.keySet());

        if(playerData != null){

            for (int j = 0; j < keys.size(); j++){
                int dbGameId = Integer.parseInt(keys.get(j));

                if(currentGameId == dbGameId){
                    playerValuesArr.add(dbGameId + " " + playerData.get(keys.get(j)).toString().replace("[", "").replace("]", "").replace(",", ""));

                }
            }
            String arrayValue = playerValuesArr.get(playerValuesArr.size()-1);
            String[] array = arrayValue.split(" ");

            String playerX = array[1];
            String playerO = array[2];


            if(Objects.equals(player, "X")){
                return playerX;
            }else {
                return playerO;
            }

        }

        return null;
    }

    // Change initial label text based on the player's turn
    public void changeInitialText(String user) throws SQLException {

        String playerX = getUsername("X");
        String playerO = getUsername("O");

        if(Objects.equals(user, playerX)){
            String txtValue = "<html>Your turn<br/><br/>Your opponent is " + playerO +" </html>";
            turnTxt.setText(txtValue);
        }else {
            turnTxt.setText(playerX + turnTxtVal);
        }
    }

    // Get a list of winners from the database
    private void getStats() throws SQLException {

        HashMap<String, List<String>> playerData = db.getPlayerData();

        ArrayList<String> playerValuesArr = new ArrayList<>();
        ArrayList<String> winners = new ArrayList<>();
        HashMap<String, Integer> winnerCount = new HashMap<>();

        if (playerData != null){
            for (String key : playerData.keySet()){
                playerValuesArr.add(key + " " + playerData.get(key).toString().replace("[", "").replace("]", "").replace(",", ""));
            }

            for (String arrayValue : playerValuesArr) {
                String[] array = arrayValue.split(" ");

                if (!Objects.equals(array[3], "none") && !Objects.equals(array[3], "draw"))
                    winners.add(array[3]);
            }

            for (String str : winners) {
                if (winnerCount.containsKey(str)) {
                    winnerCount.put(str, winnerCount.get(str) + 1);
                } else {
                    winnerCount.put(str, 1);
                }
            }

            showGameData(winnerCount);
        }
        else {
            JOptionPane.showMessageDialog(null, "The leaderboard is empty!", "Leaderboard", JOptionPane.PLAIN_MESSAGE);
        }

    }

    // Display the winners on the leaderboard
    private void showGameData(HashMap<String, Integer> winners){
        String[] columns = {"Place", "Player", "Wins"};

        ArrayList<Object[]> dataList = new ArrayList<>();

        winners.forEach((key, value) -> {
            Object[] record = {key, value};
            dataList.add(record);
        });

        Object[][] data = new Object[dataList.size()][];
        dataList.toArray(data);

        Arrays.sort(data, new Comparator<Object[]>() {
            @Override
            public int compare(Object[] arrA, Object[] arrB) {
                Integer arrValA = (Integer) arrA[1];
                Integer arrValB = (Integer) arrB[1];
                return arrValB.compareTo(arrValA);
            }
        });

        for (int i = 0; i < data.length; i++) {
            String place = i + 1 + ".";
            Object[] record = data[i];
            Object[] newRecord = {place, record[0], record[1]};
            data[i] = newRecord;
        }

        DefaultTableModel model = new DefaultTableModel(data, columns);
        JTable table = new JTable(model);

        JOptionPane.showMessageDialog(null, new JScrollPane(table), "Leaderboard", JOptionPane.PLAIN_MESSAGE);
    }

}
