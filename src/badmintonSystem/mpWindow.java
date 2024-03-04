/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
// OBJECTIVE 5:	A means of sorting out levels of access for each user 
package badmintonSystem;

// required imports
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JFrame;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author Neka
 */
public class mpWindow extends javax.swing.JFrame {
    // global variables
    int idDeleted = 0;
    private static String actionChosen = null;
    
    msgDialog msgDlg = new msgDialog(new javax.swing.JFrame(),true);

    /**
     * Creates new form mpWindow
     */
    public mpWindow() {
        initComponents();
        DefaultTableModel model = (DefaultTableModel)tblPlayers.getModel();
        model.setRowCount(0);
        showPlayersInJTable();
    }
    
    // function to return the array list of players using SQL select query of multiple tables
    public ArrayList<badmintonPlayer> getPlayersList()
    {
        ArrayList<badmintonPlayer> playerList = new ArrayList<badmintonPlayer>();
        Connection connection = SQLConnection.getConnection();
        
        // query used to select the records 
        String query = "SELECT * FROM `player`, `playerStatistics`, `playerForm` WHERE player.player_id = playerStatistics.playerOfStats_id " +
                "AND player.player_id = playerForm.playerOfForm_id AND playerForm.playerOfForm_id = playerStatistics.playerOfStats_id";
        Statement st = null;
        ResultSet rs = null;
        
        try {
            st = connection.createStatement();
            rs = st.executeQuery(query);
            
            // use of OOP to get an arraylist of badmintonPlayer objects using SQL
            badmintonPlayer player;
            
            // while records are being searched, instantiate a new player and add into array list
            while (rs.next()) {
                player = new badmintonPlayer(rs.getInt("player_id"),rs.getString("firstName"),rs.getString("lastName"),rs.getString("gender"),rs.getString("dateOfBirth"),rs.getString("hand"),rs.getString("status"),rs.getInt("ranking"),rs.getInt("oldRanking"),rs.getDouble("rankPoints"),rs.getInt("singleMatchWins"),rs.getInt("singleMatchLosses"),rs.getInt("doubleMatchWins"),rs.getInt("doubleMatchLosses"),rs.getString("form"),rs.getInt("balance"),rs.getInt("singleTournamentWins"),rs.getInt("doubleTournamentWins"),rs.getString("linkedWithUser"));
                playerList.add(player);
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
    
    // get list of players from mySQL database
    public void showPlayersInJTable()
    {
        // returned array list
        ArrayList<badmintonPlayer> list = getPlayersList();
        DefaultTableModel model = (DefaultTableModel)tblPlayers.getModel();
        // object array to use as 'rows' in FOR loop
        Object[] row = new Object[5];
        // FOR loop - for every item in the list, insert a new row with its contents
        for (int i = 0; i < list.size(); i++) {
            row[0] = list.get(i).getID();
            row[1] = list.get(i).getFirstName();
            row[2] = list.get(i).getLastName();
            row[3] = list.get(i).getHand();
            row[4] = list.get(i).getStatus();
            
            model.addRow(row);
        }
    }
    
    // update the audit log 
    public void updateAuditLog(String message, String firstName, String lastName) {
        // SET TIMESTAMP FOR SQL QUERY
        Date date = new Date();
        long time = date.getTime();
        Timestamp updateTimestamp = new Timestamp(time);
        
        // get the user from method in class, SessionManager
        String userWhoChanged = SessionManager.getUsername();
        int userWhoChangedId = SessionManager.getUserID();
        String change = (message + " player: " + firstName + " " + lastName);
        
        // get the returned connection from the function called in the class, SQLConnection
        Connection con = SQLConnection.getConnection();
        Statement st = null;
        if (message.equals("Deleted")) { // DELETED PLAYER LOG
            try {
                st = con.createStatement();
                if ((st.executeUpdate("INSERT INTO `change`(`change`, `timeOfChange`, `userWhoChanged`, `itemAffected`, `user_id`) VALUES ('"+change+"','"+updateTimestamp+"','"+userWhoChanged+"','" + "deleted player was ID: "+idDeleted+"','" +userWhoChangedId + "')")) == 1)
                {
                    System.out.println("AUDIT LOG UPDATED");    
                } else {
                    System.out.println("Failed to update into audit log.");
                }           
            // exception handling of SQL errors
            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                // procedure to close connection
                SQLConnection.closeConnection(con, null, null, st);
            }
        }
    }
        
    // execute the SQL query
    public void executeSQLQuery(String query, String message, String firstName, String lastName) {
        // get the returned connection from the function called in the class, SQLConnection
        Connection con = SQLConnection.getConnection();
        Statement st = null;
        try {
            st = con.createStatement();
            if ((st.executeUpdate(query)) == 1)
            {
                msgDlg.setMessage("Data " + message + " successfully");
                msgDlg.setVisible(true);
                // update audit log
                updateAuditLog(message, firstName, lastName);
                // refresh tblPlayers data
               DefaultTableModel model = (DefaultTableModel) tblPlayers.getModel();
               model.setRowCount(0);
               showPlayersInJTable();
               
            } else {
                msgDlg.setMessage("Data not " + message);
                msgDlg.setVisible(true);
            }   
        // exception handling of SQL errors
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
           // procedure to close connection
           SQLConnection.closeConnection(con, null, null, st);
        }
        
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
        tblPlayers = new javax.swing.JTable();
        btnRankings = new javax.swing.JButton();
        btnAdd = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        btnView = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1070, 530));
        setResizable(false);
        getContentPane().setLayout(null);

        jPanel.setBackground(new java.awt.Color(0, 51, 102));
        jPanel.setPreferredSize(new java.awt.Dimension(1070, 530));

        btnExit.setBackground(new java.awt.Color(204, 0, 0));
        btnExit.setFont(new java.awt.Font("Adobe Gothic Std", 0, 13)); // NOI18N
        btnExit.setForeground(new java.awt.Color(255, 255, 255));
        btnExit.setText("MAIN MENU");
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
        mainMenuLabel.setText("CLUB BADMINTON | MANAGE PLAYERS");

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
                .addContainerGap(49, Short.MAX_VALUE))
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
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "SELECT PLAYER", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 13), new java.awt.Color(255, 51, 0))); // NOI18N

        tblPlayers.setBackground(new java.awt.Color(0, 51, 102));
        tblPlayers.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tblPlayers.setForeground(new java.awt.Color(255, 255, 255));
        tblPlayers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID", "firstName", "lastName", "hand", "status"
            }
        ));
        tblPlayers.setGridColor(new java.awt.Color(255, 0, 0));
        tblPlayers.setSelectionBackground(new java.awt.Color(0, 51, 153));
        tblPlayers.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPlayersMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblPlayers);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 616, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        btnRankings.setBackground(new java.awt.Color(255, 51, 0));
        btnRankings.setFont(new java.awt.Font("Adobe Gothic Std", 0, 13)); // NOI18N
        btnRankings.setForeground(new java.awt.Color(255, 255, 255));
        btnRankings.setText("VIEW RANKINGS");
        btnRankings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRankingsActionPerformed(evt);
            }
        });

        btnAdd.setBackground(new java.awt.Color(255, 51, 0));
        btnAdd.setFont(new java.awt.Font("Adobe Gothic Std", 0, 13)); // NOI18N
        btnAdd.setForeground(new java.awt.Color(255, 255, 255));
        btnAdd.setText("ADD PLAYER");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        btnUpdate.setBackground(new java.awt.Color(255, 51, 0));
        btnUpdate.setFont(new java.awt.Font("Adobe Gothic Std", 0, 13)); // NOI18N
        btnUpdate.setForeground(new java.awt.Color(255, 255, 255));
        btnUpdate.setText("EDIT PLAYER");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnDelete.setBackground(new java.awt.Color(255, 51, 0));
        btnDelete.setFont(new java.awt.Font("Adobe Gothic Std", 0, 13)); // NOI18N
        btnDelete.setForeground(new java.awt.Color(255, 255, 255));
        btnDelete.setText("DELETE PLAYER");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnRefresh.setBackground(new java.awt.Color(255, 51, 0));
        btnRefresh.setFont(new java.awt.Font("Adobe Gothic Std", 0, 13)); // NOI18N
        btnRefresh.setForeground(new java.awt.Color(255, 255, 255));
        btnRefresh.setText("REFRESH TABLE");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        btnView.setBackground(new java.awt.Color(255, 51, 0));
        btnView.setFont(new java.awt.Font("Adobe Gothic Std", 0, 13)); // NOI18N
        btnView.setForeground(new java.awt.Color(255, 255, 255));
        btnView.setText("VIEW SELECTED PLAYER");
        btnView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelLayout = new javax.swing.GroupLayout(jPanel);
        jPanel.setLayout(jPanelLayout);
        jPanelLayout.setHorizontalGroup(
            jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(topBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mpIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnRefresh, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnView, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnRankings, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAdd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnUpdate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnDelete, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(50, 50, 50))
        );
        jPanelLayout.setVerticalGroup(
            jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLayout.createSequentialGroup()
                .addComponent(topBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelLayout.createSequentialGroup()
                        .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanelLayout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(mpIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(43, 43, 43))
                            .addGroup(jPanelLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnView, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnRankings)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                        .addComponent(btnAdd)
                        .addGap(18, 18, 18)
                        .addComponent(btnUpdate)
                        .addGap(18, 18, 18)
                        .addComponent(btnDelete))
                    .addGroup(jPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(btnRefresh)
                .addContainerGap(119, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel);
        jPanel.setBounds(0, 6, 1070, 530);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        // quit menu, return back to login
        badmintonMenu bMenu = new badmintonMenu();
        bMenu.setVisible(true);
        bMenu.pack();
        bMenu.setLocationRelativeTo(null);
        bMenu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.dispose();
    }//GEN-LAST:event_btnExitActionPerformed

    private void btnRankingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRankingsActionPerformed
         // makes sure no player is selected
        badmintonPlayer.selectPlayerID(0);
        // open rankings window
        mpRankings mR = new mpRankings();
        mR.setVisible(true);
        mR.pack();
        mR.setLocationRelativeTo(null);
        mR.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.dispose();
    }//GEN-LAST:event_btnRankingsActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed

        // makes sure no player is selected
        badmintonPlayer.selectPlayerID(0);
        // open customise window
        actionChosen = "INSERT";
        mpCustomisePlayer cp = new mpCustomisePlayer(actionChosen);
        cp.setVisible(true);
        cp.pack();
        cp.setLocationRelativeTo(null);
        cp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.dispose();
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // open customise window, return back to login
        actionChosen = "UPDATE/DELETE";
        mpCustomisePlayer cp = new mpCustomisePlayer(actionChosen);
        cp.setVisible(true);
        cp.pack();
        cp.setLocationRelativeTo(null);
        cp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.dispose();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // get table model from the table of players
        TableModel model = tblPlayers.getModel();
        // don't allow a player to be deleted from the table if no row was selected from the table
        if (model.getValueAt(1,0).toString() == null) {
            msgDlg.setMessage("No selected player to delete");
            msgDlg.setVisible(true);
            System.out.println("empty table, no player to delete");
        } else {
            int i = tblPlayers.getSelectedRow();
            String firstNameSelected = model.getValueAt(i, 1).toString();
            String lastNameSelected = model.getValueAt(i, 2).toString();
            // get confirmation from the user if they want to commit to this decision to delete the player
            msgDlg.setMessage("Confirm Delete Player ?");
            msgDlg.setVisible(true);
            // get the return status (if the user clicked "OK" in the messageDialog then returnStatus = 1, else returnStatus = 0)
            int confirmDelete = msgDlg.getReturnStatus();
            if (confirmDelete == 1 ) {
                // if table is empty
                int IDSelected = badmintonPlayer.getSelectedPlayerID();
                if (IDSelected == 0) {
                    msgDlg.setMessage("No selected player to delete");
                    msgDlg.setVisible(true);
                } else {
                    // query to delete the player from the system
                    String query = "DELETE FROM `player` WHERE player_id = " + IDSelected;
                    idDeleted = IDSelected;            
                    executeSQLQuery(query,"Deleted",firstNameSelected,lastNameSelected);
                }
            } else {
                System.out.println("Delete Player Cancelled");
            }
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void tblPlayersMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPlayersMouseClicked
        // display selected row in JTextFields
        int i = tblPlayers.getSelectedRow();
        DefaultTableModel model = (DefaultTableModel) tblPlayers.getModel();
        
        // if table is empty
        if (model.getValueAt(1,0).toString() == null) {
            System.out.println("empty table");
        }
        
        // get the ID selected so that it can be used if a window is opened to edit or delete the player
        badmintonPlayer.selectPlayerID((int) model.getValueAt(i,0));

    }//GEN-LAST:event_tblPlayersMouseClicked

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        // get the model from the table, set row count to 0, re-call the method to show players in the table
        DefaultTableModel model = (DefaultTableModel)tblPlayers.getModel();
        model.setRowCount(0);
        showPlayersInJTable();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void btnViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewActionPerformed
        // open mpCustomisePlayer form to view the player (not edit details about them), and close this form
        actionChosen = "VIEW";
        mpCustomisePlayer cp = new mpCustomisePlayer(actionChosen);
        cp.setVisible(true);
        cp.pack();
        cp.setLocationRelativeTo(null);
        cp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.dispose();
    }//GEN-LAST:event_btnViewActionPerformed

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
            java.util.logging.Logger.getLogger(mpWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(mpWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(mpWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(mpWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new mpWindow().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel LogLabel;
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnDelete;
    private javax.swing.JToggleButton btnExit;
    private javax.swing.JButton btnRankings;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JButton btnView;
    private javax.swing.JPanel jPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel mainMenuLabel;
    private javax.swing.JLabel mpIcon;
    private javax.swing.JTable tblPlayers;
    private javax.swing.JPanel topBar;
    public javax.swing.JLabel userLoggedOn;
    // End of variables declaration//GEN-END:variables
}
