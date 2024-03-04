/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//  OBJECTIVE 10: A means of viewing the rankings table of the players 
package badmintonSystem;

// required imports
import java.awt.Image;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Neka
 */
public class mpRankings extends javax.swing.JFrame {
    // global variables
    int idDeleted = 0;
    private static String actionChosen = null;
    
    msgDialog msgDlg = new msgDialog(new javax.swing.JFrame(),true);

    /**
     * Creates new form mpRankings
     */
    public mpRankings() {
        initComponents();
        setRank();
    }
    
    // procedure to send the SQL queries to select from multiple tables and order (descending) based on highest rank Points
    public void setRank() {
        String SRQ1 = "SELECT * " +
        "FROM `player`, `playerStatistics`, `playerForm` " +
        "WHERE player.player_id = playerStatistics.playerOfStats_id " +
        "AND player.player_id = playerForm.playerOfForm_id AND playerForm.playerOfForm_id = playerStatistics.playerOfStats_id ";
        
        String SRQ2 = "SELECT * " +
        "FROM `player`, `playerStatistics`, `playerForm` " +
        "WHERE player.player_id = playerStatistics.playerOfStats_id " +
        "AND player.player_id = playerForm.playerOfForm_id AND playerForm.playerOfForm_id = playerStatistics.playerOfStats_id " +
        "ORDER BY playerStatistics.rankPoints DESC;";

        
        showRankingsInJTable(SRQ1,SRQ2);
    }
    
    //
    public ArrayList<badmintonPlayer> getPlayersList(String orderQuery)
    {
        // instantiate new array list of the class, badmintonPlayer
        ArrayList<badmintonPlayer> playerList = new ArrayList<badmintonPlayer>();
        // return the connection by calling function in the class, SQLConnection
        Connection connection = SQLConnection.getConnection();

        Statement st = null;
        ResultSet rs = null;
        
        try {
            st = connection.createStatement();
            rs = st.executeQuery(orderQuery);
            
            badmintonPlayer player;
            
            while (rs.next()) {
                player = new badmintonPlayer(rs.getInt("player_id"),rs.getString("firstName"),rs.getString("lastName"),rs.getString("gender"),rs.getString("dateOfBirth"),rs.getString("hand"),rs.getString("status"),rs.getInt("ranking"),rs.getInt("oldRanking"),rs.getDouble("rankPoints"),rs.getInt("singleMatchWins"),rs.getInt("singleMatchLosses"),rs.getInt("doubleMatchWins"),rs.getInt("doubleMatchLosses"),rs.getString("form"),rs.getInt("balance"),rs.getInt("singleTournamentWins"),rs.getInt("doubleTournamentWins"),rs.getString("linkedWithUser"));
                playerList.add(player);
                System.out.println("player added: " + player.getFirstName() + " " + player.getLastName());
            }
        }
        // exception handling of SQL errors
        catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // procedure to close connection
            SQLConnection.closeConnection(connection, rs, null, st);
        }
        return playerList;
    }
    
    // get list of rankings from mySQL database
    public void showRankingsInJTable(String findOldRanksQuery, String orderQuery)
    {
        //ArrayList<badmintonPlayer> oldList = getPlayersList(findOldRanksQuery);
        
        // get returned array list from called function
        ArrayList<badmintonPlayer> list = getPlayersList(orderQuery);
        
        // array of the column names of the table
        String[] columnName = {"RK","PLAYER","PLAYERID","POS","RP","SMW","SML","SMP","DMW","DML","DMP","BAL","STW","DTW","FORM"};
        
        // create a two-dimensional array for the rows, columns on the table
        Object[][] rows = new Object[list.size()][15];
        int rankPos = 0;
        // display the contents of the player for each row of the table (all of the players' statistics are ranked in order)
        for (int i = 0; i < list.size(); i++) {
            // increment the rankPos variable in each iteration to create the rank from #1 to the last player of the rank
            rankPos = rankPos + 1;
            rows[i][0] = rankPos;
            // getter methods
            rows[i][1] = list.get(i).getFirstName() + " " + list.get(i).getLastName();
            rows[i][2] = list.get(i).getID();
            if (list.get(i).getStatistics().getMovePosition() != null) {
                ImageIcon imageIcon;
                Image image;
                if (list.get(i).getStatistics().getMovePosition().equals("UP")) {
                    imageIcon = new ImageIcon(getClass().getResource("/systemIcons/posUPicon.png"));
                    image = imageIcon.getImage().getScaledInstance(25, 20, Image.SCALE_SMOOTH);
                    rows[i][3] = image;
                } else {
                    // the movePosition must be DOWN
                    imageIcon = new ImageIcon(getClass().getResource("/systemIcons/posDOWNicon.png"));
                    image = imageIcon.getImage().getScaledInstance(25, 20, Image.SCALE_SMOOTH);
                    rows[i][3] = image;
                }              
            } else {
                rows[i][3] = "-";
            }
            // retrieve the statistics of the player using the getter methods from PlayerStatistics which is within the class, badmintonPlayer
            rows[i][4] = list.get(i).getStatistics().getRankPoints();
            rows[i][5] = list.get(i).getStatistics().getSingleMatchWins();
            rows[i][6] = list.get(i).getStatistics().getSingleMatchLosses();
            rows[i][7] = list.get(i).getStatistics().getSingleMatchWins() + list.get(i).getStatistics().getSingleMatchLosses();
            rows[i][8] = list.get(i).getStatistics().getDoubleMatchWins();
            rows[i][9] = list.get(i).getStatistics().getDoubleMatchLosses();
            rows[i][10] = list.get(i).getStatistics().getDoubleMatchWins() + list.get(i).getStatistics().getDoubleMatchLosses();
            rows[i][11] = list.get(i).getStatistics().getBalance();
            rows[i][12] = list.get(i).getStatistics().getSingleTournamentWins();
            rows[i][13] = list.get(i).getStatistics().getDoubleTournamentWins();
            rows[i][14] = list.get(i).getStatistics().getForm();
        }
        
        // instantiate a new class of theModel, where the model, row height and column width (for column 3) are set
        theModel model = new theModel(rows, columnName);
        tblRankings.setModel(model);
        tblRankings.setRowHeight(25);
        tblRankings.getColumnModel().getColumn(3).setPreferredWidth(20);
        
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel = new javax.swing.JPanel();
        btnExit = new javax.swing.JToggleButton();
        mpIcon = new javax.swing.JLabel();
        topBar = new javax.swing.JPanel();
        mainMenuLabel = new javax.swing.JLabel();
        LogLabel = new javax.swing.JLabel();
        userLoggedOn = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblRankings = new javax.swing.JTable();
        btnRefresh = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnViewStats = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1070, 530));
        setResizable(false);
        getContentPane().setLayout(null);

        jPanel.setBackground(new java.awt.Color(0, 51, 102));
        jPanel.setPreferredSize(new java.awt.Dimension(1070, 530));

        btnExit.setBackground(new java.awt.Color(204, 0, 0));
        btnExit.setFont(new java.awt.Font("Adobe Gothic Std", 0, 13)); // NOI18N
        btnExit.setForeground(new java.awt.Color(255, 255, 255));
        btnExit.setText("BACK TO MANAGE PLAYERS");
        btnExit.setAlignmentY(0.0F);
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });

        mpIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/systemIcons/mpIcon.png"))); // NOI18N

        topBar.setBackground(new java.awt.Color(255, 51, 0));

        mainMenuLabel.setFont(new java.awt.Font("Adobe Gothic Std", 0, 18)); // NOI18N
        mainMenuLabel.setForeground(new java.awt.Color(255, 255, 255));
        mainMenuLabel.setText("CLUB BADMINTON | RANKINGS");

        LogLabel.setFont(new java.awt.Font("Adobe Gothic Std", 0, 14)); // NOI18N
        LogLabel.setForeground(new java.awt.Color(255, 255, 255));
        LogLabel.setText("LOGGED ON AS: ");

        userLoggedOn.setFont(new java.awt.Font("Adobe Gothic Std", 0, 14)); // NOI18N
        userLoggedOn.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout topBarLayout = new javax.swing.GroupLayout(topBar);
        topBar.setLayout(topBarLayout);
        topBarLayout.setHorizontalGroup(
            topBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(topBarLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainMenuLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 372, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(261, 261, 261)
                .addComponent(LogLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(userLoggedOn, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );
        topBarLayout.setVerticalGroup(
            topBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(topBarLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(topBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(userLoggedOn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(topBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(mainMenuLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(LogLabel)))
                .addContainerGap())
        );

        userLoggedOn.setText(SessionManager.getUsername());

        jPanel1.setBackground(new java.awt.Color(0, 51, 102));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "RANKING", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 13), new java.awt.Color(255, 51, 0))); // NOI18N

        tblRankings.setBackground(new java.awt.Color(0, 51, 102));
        tblRankings.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tblRankings.setForeground(new java.awt.Color(255, 255, 255));
        tblRankings.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "RK", "PLAYER", "PLAYERID", "POS", "RP", "SMW", "SML", "SMP", "DMW", "DML", "DMP", "BAL", "STW", "DTW", "FORM"
            }
        ));
        tblRankings.setGridColor(new java.awt.Color(255, 0, 0));
        tblRankings.setSelectionBackground(new java.awt.Color(0, 51, 153));
        tblRankings.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblRankingsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblRankings);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 997, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE)
                .addContainerGap())
        );

        btnRefresh.setBackground(new java.awt.Color(255, 51, 0));
        btnRefresh.setFont(new java.awt.Font("Adobe Gothic Std", 0, 13)); // NOI18N
        btnRefresh.setForeground(new java.awt.Color(255, 255, 255));
        btnRefresh.setText("REFRESH RANKINGS");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        btnUpdate.setBackground(new java.awt.Color(255, 51, 0));
        btnUpdate.setFont(new java.awt.Font("Adobe Gothic Std", 0, 13)); // NOI18N
        btnUpdate.setForeground(new java.awt.Color(255, 255, 255));
        btnUpdate.setText("EDIT PLAYER STATISTICS");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnViewStats.setBackground(new java.awt.Color(255, 51, 0));
        btnViewStats.setFont(new java.awt.Font("Adobe Gothic Std", 0, 13)); // NOI18N
        btnViewStats.setForeground(new java.awt.Color(255, 255, 255));
        btnViewStats.setText("VIEW SELECTED PLAYER");
        btnViewStats.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewStatsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelLayout = new javax.swing.GroupLayout(jPanel);
        jPanel.setLayout(jPanelLayout);
        jPanelLayout.setHorizontalGroup(
            jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(topBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanelLayout.createSequentialGroup()
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelLayout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(mpIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(39, 39, 39)
                        .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnViewStats, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)
                        .addComponent(btnExit)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanelLayout.setVerticalGroup(
            jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLayout.createSequentialGroup()
                .addComponent(topBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnUpdate)
                        .addComponent(btnRefresh)
                        .addComponent(btnViewStats))
                    .addComponent(mpIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(31, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel);
        jPanel.setBounds(0, 6, 1040, 530);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        // opens manage players window and closes this window
        mpWindow mpW = new mpWindow();
        mpW.setVisible(true);
        mpW.pack();
        mpW.setLocationRelativeTo(null);
        mpW.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.dispose();
    }//GEN-LAST:event_btnExitActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        // refreshes the table by clearing the contents of it and re-adding the contents to its updated version
        DefaultTableModel model = (DefaultTableModel)tblRankings.getModel();
        model.setRowCount(0);
        setRank();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // open the player statistics window to edit the player's statistics and quit this form
        
        // only allow the user to edit if a player from the table was actually selected (0 as an ID means no player was selected)
        if (badmintonPlayer.getSelectedPlayerID() != 0) {
            actionChosen = "UPDATE";
            mpPlayerStats mps = new mpPlayerStats(actionChosen);
            mps.setVisible(true);
            mps.pack();
            mps.setLocationRelativeTo(null);
            mps.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.dispose();
        } else {
            msgDlg.setMessage("Must select a player's statistics to update");
            msgDlg.setVisible(true);
        }
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void tblRankingsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblRankingsMouseClicked
        // display selected row in JTextFields
        int i = tblRankings.getSelectedRow();
        theModel model = (theModel) tblRankings.getModel();
        
        // if table is empty
        if (model.getValueAt(1,0) == null) {
            System.out.println("empty table");
        } else {
            badmintonPlayer.selectPlayerID((int) model.getValueAt(i,2));
        }
        
        // get the ID selected so that it can be used if a window is opened to edit or view the player


    }//GEN-LAST:event_tblRankingsMouseClicked

    private void btnViewStatsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewStatsActionPerformed
        // open the player statistics window to view the player's statistics and quit this form
        
        // only allow the user to view if a player from the table was actually selected (0 as an ID means no player was selected)
        if (badmintonPlayer.getSelectedPlayerID() != 0) {
            actionChosen = "VIEW";
            mpPlayerStats mps = new mpPlayerStats(actionChosen);
            mps.setVisible(true);
            mps.pack();
            mps.setLocationRelativeTo(null);
            mps.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.dispose();
        } else {
            msgDlg.setMessage("Must select a player's statistics to update");
            msgDlg.setVisible(true);
        }
    }//GEN-LAST:event_btnViewStatsActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(mpRankings.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(mpRankings.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(mpRankings.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(mpRankings.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new mpRankings().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel LogLabel;
    private javax.swing.JToggleButton btnExit;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JButton btnViewStats;
    private javax.swing.JPanel jPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel mainMenuLabel;
    private javax.swing.JLabel mpIcon;
    private javax.swing.JTable tblRankings;
    private javax.swing.JPanel topBar;
    public javax.swing.JLabel userLoggedOn;
    // End of variables declaration//GEN-END:variables
}
