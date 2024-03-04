/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
// OBJECTIVE 3: means of viewing the information about competitions and tournaments / a means of viewing match results
package badmintonSystem;

// required imports
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Neka
 */
public class competitionsWindow extends javax.swing.JFrame {
    // message dialog
    msgDialog msgDlg = new msgDialog(new javax.swing.JFrame(),true);

    /**
     * Creates new form competitionsWindow
     */
    public competitionsWindow() {
        initComponents();
        
        // get models of the table and convert to DTM
        DefaultTableModel ongoingTournamentsModel = (DefaultTableModel)tblOT.getModel();
        DefaultTableModel finishedTournamentsModel = (DefaultTableModel)tblFT.getModel();
        // set the row count of table to zero
        ongoingTournamentsModel.setRowCount(0);
        finishedTournamentsModel.setRowCount(0);
        // call procedures to show contents in the tables
        showOTsInJTable();
        showFTsInJTable();
    }
    
    public void showOTsInJTable() {
        // returned array list
        ArrayList<badmintonTournament> list = getTournamentsList();
        DefaultTableModel model = (DefaultTableModel)tblOT.getModel();
        // object array to use as 'rows' in FOR loop
        Object[] row = new Object[5];
        for (int i = 0; i < list.size(); i++) {
            // FOR loop - for every item in the list, insert a new row with its contents
            if (list.get(i).getTournamentCompleted() == false) {
                row[0] = list.get(i).getID();
                row[1] = list.get(i).getTournamentName();
                row[2] = list.get(i).getTournamentType();
                row[3] = list.get(i).getTournamentCategory();
                row[4] = list.get(i).getNoOfPlayers();
                
                model.addRow(row);
            }
        }
    }
    
    public void showFTsInJTable() {
        // returned array list
        ArrayList<badmintonTournament> list = getTournamentsList();
        DefaultTableModel model = (DefaultTableModel)tblFT.getModel();
        // object array to use as 'rows' in FOR loop
        Object[] row = new Object[6];
        for (int i = 0; i < list.size(); i++) {
            // FOR loop - for every item in the list, insert a new row with its contents
            if (list.get(i).getTournamentCompleted() == true) {
                row[0] = list.get(i).getID();
                row[1] = list.get(i).getTournamentName();
                row[2] = list.get(i).getTournamentType();
                row[3] = list.get(i).getTournamentCategory();
                row[4] = list.get(i).getNoOfPlayers();
                row[5] = list.get(i).getDateCompleted();
                
                model.addRow(row);
            }
        }
    }
    
    // procedure to get the list of tournaments
    public ArrayList<badmintonTournament> getTournamentsList()
    {
        // call function to return a new arrayList of logs from mySQL table, 'tournament'
        ArrayList<badmintonTournament> tournamentList = new ArrayList<badmintonTournament>();
        // call function to return connection
        Connection connection = SQLConnection.getConnection();
        
        // query to be executed, select all columns from table 'tournament'
        String query = "SELECT * FROM `tournament`";
        Statement st = null;
        ResultSet rs = null;
        
        try {
            // create the statement
            st = connection.createStatement();
            // execute the query 
            rs = st.executeQuery(query);

            
            badmintonTournament tournament;
            
            /** while each row of the mySQL table is being selected, instantiate a new 'tournament' with the 
             * columns being retrieved as the parameters
             */
            
            while (rs.next()) {
                tournament = new badmintonTournament(rs.getInt("tournament_id"),rs.getString("tournamentName"),rs.getString("tournamentCategory"),rs.getString("tournamentType"),rs.getInt("noOfPlayers"),rs.getString("winnerOfTournamentID"),rs.getBoolean("tournamentCompleted"),rs.getString("dateStarted"),rs.getString("dateCompleted"));
                tournamentList.add(tournament);
            }
        }
        // exception handling of SQL errors
        catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // call procedure to close connection
            SQLConnection.closeConnection(connection, rs, null, st);
        }
        // return the array list
        return tournamentList;
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
        competitionsIcon = new javax.swing.JLabel();
        topBar = new javax.swing.JPanel();
        mainMenuLabel = new javax.swing.JLabel();
        LogLabel = new javax.swing.JLabel();
        userLoggedOn = new javax.swing.JLabel();
        btnDeleteTournament = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblOT = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblFT = new javax.swing.JTable();
        btnCreateTournament = new javax.swing.JButton();
        btnViewTournament = new javax.swing.JButton();

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

        competitionsIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/systemIcons/compIcon.png"))); // NOI18N

        topBar.setBackground(new java.awt.Color(255, 51, 0));

        mainMenuLabel.setFont(new java.awt.Font("Adobe Gothic Std", 0, 18)); // NOI18N
        mainMenuLabel.setForeground(new java.awt.Color(255, 255, 255));
        mainMenuLabel.setText("CLUB BADMINTON | COMPETITIONS");

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

        btnDeleteTournament.setBackground(new java.awt.Color(255, 51, 0));
        btnDeleteTournament.setFont(new java.awt.Font("Adobe Gothic Std", 0, 13)); // NOI18N
        btnDeleteTournament.setForeground(new java.awt.Color(255, 255, 255));
        btnDeleteTournament.setText("DELETE TOURNAMENT");

        jLabel2.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("ONGOING TOURNAMENTS:");

        jLabel3.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("FINISHED TOURNAMENTS:");

        tblOT.setBackground(new java.awt.Color(0, 51, 102));
        tblOT.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tblOT.setForeground(new java.awt.Color(255, 255, 255));
        tblOT.setModel(new javax.swing.table.DefaultTableModel(
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
                "ID", "name", "type", "structure", "amountOfPlayers"
            }
        ));
        tblOT.setGridColor(new java.awt.Color(255, 0, 0));
        tblOT.setSelectionBackground(new java.awt.Color(0, 51, 153));
        tblOT.setShowGrid(true);
        tblOT.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblOTMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblOT);

        tblFT.setBackground(new java.awt.Color(0, 51, 102));
        tblFT.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tblFT.setForeground(new java.awt.Color(255, 255, 255));
        tblFT.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "name", "type", "structure", "amountOfPlayers", "dateFinished"
            }
        ));
        tblFT.setGridColor(new java.awt.Color(255, 0, 0));
        tblFT.setSelectionBackground(new java.awt.Color(0, 51, 153));
        tblFT.setShowGrid(true);
        tblFT.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblFTMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblFT);

        btnCreateTournament.setBackground(new java.awt.Color(255, 51, 0));
        btnCreateTournament.setFont(new java.awt.Font("Adobe Gothic Std", 0, 13)); // NOI18N
        btnCreateTournament.setForeground(new java.awt.Color(255, 255, 255));
        btnCreateTournament.setText("CREATE NEW TOURNAMENT");
        btnCreateTournament.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateTournamentActionPerformed(evt);
            }
        });

        btnViewTournament.setBackground(new java.awt.Color(255, 51, 0));
        btnViewTournament.setFont(new java.awt.Font("Adobe Gothic Std", 0, 13)); // NOI18N
        btnViewTournament.setForeground(new java.awt.Color(255, 255, 255));
        btnViewTournament.setText("VIEW TOURNAMENT");
        btnViewTournament.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewTournamentActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelLayout = new javax.swing.GroupLayout(jPanel);
        jPanel.setLayout(jPanelLayout);
        jPanelLayout.setHorizontalGroup(
            jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(topBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelLayout.createSequentialGroup()
                        .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanelLayout.createSequentialGroup()
                                .addComponent(competitionsIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanelLayout.createSequentialGroup()
                                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnDeleteTournament, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(jPanelLayout.createSequentialGroup()
                                .addGap(15, 15, 15)
                                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 699, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 596, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnCreateTournament, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnViewTournament, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(74, 74, 74)))
                .addContainerGap())
        );
        jPanelLayout.setVerticalGroup(
            jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLayout.createSequentialGroup()
                .addComponent(topBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelLayout.createSequentialGroup()
                        .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelLayout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(competitionsIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanelLayout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addGap(18, 18, 18)
                                        .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelLayout.createSequentialGroup()
                                                .addComponent(btnViewTournament)
                                                .addGap(18, 18, 18)
                                                .addComponent(btnCreateTournament)
                                                .addGap(22, 22, 22))
                                            .addGroup(jPanelLayout.createSequentialGroup()
                                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(jLabel3))))))
                            .addGroup(jPanelLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(62, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnDeleteTournament)
                        .addGap(214, 214, 214))))
        );

        getContentPane().add(jPanel);
        jPanel.setBounds(0, 6, 1070, 530);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        // quit the competitions window and return back to main menu
        badmintonMenu bMenu = new badmintonMenu();
        bMenu.setVisible(true);
        bMenu.pack();
        bMenu.setLocationRelativeTo(null);
        bMenu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.dispose();
    }//GEN-LAST:event_btnExitActionPerformed

    private void tblOTMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblOTMouseClicked
        // display selected row in JTextFields
        int i = tblOT.getSelectedRow();
        DefaultTableModel model = (DefaultTableModel) tblOT.getModel();
        
        // if table is empty
        if (model.getValueAt(1,0) == null) {
            System.out.println("empty table");
        }
        
        // get the ID selected so that it can be used if a window is opened to edit or delete the player
        badmintonTournament.selectTournamentID((int) model.getValueAt(i,0));
    }//GEN-LAST:event_tblOTMouseClicked

    private void tblFTMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblFTMouseClicked
        // display selected row in JTextFields
        int i = tblFT.getSelectedRow();
        DefaultTableModel model = (DefaultTableModel) tblFT.getModel();
        
        // if table is empty
        if (model.getValueAt(1,0) == null) {
            System.out.println("empty table");
        }
        
        // get the ID selected so that it can be used if a window is opened to edit or delete the player
        badmintonTournament.selectTournamentID((int) model.getValueAt(i,0));
    }//GEN-LAST:event_tblFTMouseClicked

    private void btnViewTournamentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewTournamentActionPerformed
        
        if (badmintonTournament.getSelectedTournamentID() != 0) {
            // view tournament that is selected
            competitionRoundRobin cRR = new competitionRoundRobin();
            cRR.setVisible(true);
            cRR.pack();
            cRR.setLocationRelativeTo(null);
            cRR.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.dispose();
        } else {
            msgDlg.setMessage("Must select a tournament");
            msgDlg.setVisible(true);
        }
    }//GEN-LAST:event_btnViewTournamentActionPerformed

    private void btnCreateTournamentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateTournamentActionPerformed
        // set selected ID to 0 to ensure no tournament was selected
        badmintonTournament.selectTournamentID(0);
        
        // open competitionCustomise form to create a new tournament and quit this form
        competitionCustomise cc = new competitionCustomise("INSERT",null,null);
        cc.setVisible(true);
        cc.pack();
        cc.setLocationRelativeTo(null);
        cc.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.dispose();
    }//GEN-LAST:event_btnCreateTournamentActionPerformed

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
            java.util.logging.Logger.getLogger(competitionsWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(competitionsWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(competitionsWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(competitionsWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new competitionsWindow().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel LogLabel;
    private javax.swing.JButton btnCreateTournament;
    private javax.swing.JButton btnDeleteTournament;
    private javax.swing.JToggleButton btnExit;
    private javax.swing.JButton btnViewTournament;
    private javax.swing.JLabel competitionsIcon;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel mainMenuLabel;
    private javax.swing.JTable tblFT;
    private javax.swing.JTable tblOT;
    private javax.swing.JPanel topBar;
    public javax.swing.JLabel userLoggedOn;
    // End of variables declaration//GEN-END:variables
}
