/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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
public class muWindow extends javax.swing.JFrame {
    // global variables
    int idDeleted = 0;
    private static String actionChosen = null;
    
    msgDialog msgDlg = new msgDialog(new javax.swing.JFrame(),true);

    /** Creates new form muWindow */
    public muWindow() {
        initComponents();
        DefaultTableModel model = (DefaultTableModel)tblUsers.getModel();
        model.setRowCount(0);
        showUsersInJTable();
        // only show the delete button if the user logged in is an admin
        if (!SessionManager.getUserRole().equals("admin")) {
            btnDelete.setVisible(false);
            btnDelete.setEnabled(false);
        }
    }
    
    // procedure to get the list of users
    public ArrayList<badmintonUser> getUsersList()
    {
        // call function to return a new arrayList of users from mySQL table, 'user'
        ArrayList<badmintonUser> userList = new ArrayList<badmintonUser>();
        // call function to return connection
        Connection connection = SQLConnection.getConnection();
        
        // query to be executed, select all columns from table 'user'
        String query = "SELECT * FROM `user` ";
        Statement st = null;
        ResultSet rs = null;
        
        try {
            // create the statement
            st = connection.createStatement();
            // execute the query 
            rs = st.executeQuery(query);

            
            badmintonUser user;
            
            /** while each row of the mySQL table is being selected, instantiate a new log with the 
             * columns being retrieved as the parameters
             */

            while (rs.next()) {
                user = new badmintonUser(rs.getInt("user_id"),rs.getString("username"),rs.getString("password"),rs.getString("firstName"),rs.getString("lastName"),rs.getString("gender"),rs.getString("dateOfBirth"),rs.getString("verified"));
                userList.add(user);
            }
        }
        // exception handling to handle SQL errors
        catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // procedure to close connection
            SQLConnection.closeConnection(connection, rs, null, st);
        }
        // return the array list
        return userList;
    }
    
    // get list of bookings from mySQL database
    public void showUsersInJTable()
    {
        // returned array list
        ArrayList<badmintonUser> list = getUsersList();
        DefaultTableModel model = (DefaultTableModel)tblUsers.getModel();
        // object array to use as 'rows' in FOR loop
        Object[] row = new Object[5];
        // FOR loop - for every item in the list, insert a new row with its contents
        for (int i = 0; i < list.size(); i++) {
            row[0] = list.get(i).getID();
            row[1] = list.get(i).getFirstName();
            row[2] = list.get(i).getLastName();
            
            model.addRow(row);
        }
    }
    
    // update the audit log 
    public void updateAuditLog(String message, String firstNameInput, String lastNameInput) {
        // SET TIMESTAMP FOR SQL QUERY
        Date date = new Date();
        long time = date.getTime();
        Timestamp updateTimestamp = new Timestamp(time);
        
        // get the user from method in class, SessionManager
        String userWhoChanged = SessionManager.getUsername();
        String change = (message + " user: " + firstNameInput + " " + lastNameInput);
        
        Statement st = null;
        if (message.equals("Deleted")) { // DELETED user LOG
            // get returned connection from calling the function in the class, SQLConnection
            Connection con = SQLConnection.getConnection();
            try {
                st = con.createStatement();
                if ((st.executeUpdate("INSERT INTO `change`(`change`, `timeOfChange`, `userWhoChanged`, `itemAffected`) VALUES ('"+change+"','"+updateTimestamp+"','"+userWhoChanged+"','" + "deleted player was ID: "+idDeleted+"')")) == 1)
                {
                    System.out.println("AUDIT LOG UPDATED");    
                } else {
                    System.out.println("Failed to update into audit log.");
                }           
            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                // procedure to close connection
                SQLConnection.closeConnection(con, null, null, st);
            }
        }
    }
        
    // execute the SQL query
    public void executeSQLQuery(String query, String message, String firstNameInput, String lastNameInput) {
        // get returned connection from calling the function in the class, SQLConnection
        Connection con = SQLConnection.getConnection();
        Statement st = null;
        try {
            st = con.createStatement();
            if ((st.executeUpdate(query)) == 1)
            {
                msgDlg.setMessage("Data " + message + " successfully");
                msgDlg.setVisible(true);
                // update audit log
                updateAuditLog(message, firstNameInput, lastNameInput);
                // refresh tblUsers data
               DefaultTableModel model = (DefaultTableModel) tblUsers.getModel();
               model.setRowCount(0);
               showUsersInJTable();
               
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

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel = new javax.swing.JPanel();
        btnExit = new javax.swing.JToggleButton();
        muIcon = new javax.swing.JLabel();
        topBar = new javax.swing.JPanel();
        mainMenuLabel = new javax.swing.JLabel();
        LogLabel = new javax.swing.JLabel();
        userLoggedOn = new javax.swing.JLabel();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblUsers = new javax.swing.JTable();
        btnView = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1043, 530));
        setResizable(false);
        getContentPane().setLayout(null);

        jPanel.setBackground(new java.awt.Color(0, 51, 102));
        jPanel.setPreferredSize(new java.awt.Dimension(1043, 530));

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

        muIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/systemIcons/muIcon.png"))); // NOI18N

        topBar.setBackground(new java.awt.Color(255, 51, 0));

        mainMenuLabel.setFont(new java.awt.Font("Adobe Gothic Std", 0, 18)); // NOI18N
        mainMenuLabel.setForeground(new java.awt.Color(255, 255, 255));
        mainMenuLabel.setText("CLUB BADMINTON | MANAGE USERS");

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

        btnUpdate.setBackground(new java.awt.Color(255, 51, 0));
        btnUpdate.setFont(new java.awt.Font("Adobe Gothic Std", 0, 13)); // NOI18N
        btnUpdate.setForeground(new java.awt.Color(255, 255, 255));
        btnUpdate.setText("EDIT USER");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnDelete.setBackground(new java.awt.Color(255, 51, 0));
        btnDelete.setFont(new java.awt.Font("Adobe Gothic Std", 0, 13)); // NOI18N
        btnDelete.setForeground(new java.awt.Color(255, 255, 255));
        btnDelete.setText("DELETE USER");
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

        jPanel1.setBackground(new java.awt.Color(0, 51, 102));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "SELECT USER", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 13), new java.awt.Color(255, 51, 0))); // NOI18N

        tblUsers.setBackground(new java.awt.Color(0, 51, 102));
        tblUsers.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tblUsers.setForeground(new java.awt.Color(255, 255, 255));
        tblUsers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "ID", "firstName", "lastName"
            }
        ));
        tblUsers.setGridColor(new java.awt.Color(255, 0, 0));
        tblUsers.setSelectionBackground(new java.awt.Color(0, 51, 153));
        tblUsers.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblUsersMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblUsers);

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

        btnView.setBackground(new java.awt.Color(255, 51, 0));
        btnView.setFont(new java.awt.Font("Adobe Gothic Std", 0, 13)); // NOI18N
        btnView.setForeground(new java.awt.Color(255, 255, 255));
        btnView.setText("VIEW USER");
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
                .addComponent(muIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelLayout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnUpdate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnRefresh, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnView, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnDelete, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelLayout.setVerticalGroup(
            jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLayout.createSequentialGroup()
                .addComponent(topBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelLayout.createSequentialGroup()
                        .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(41, 41, 41)
                        .addComponent(btnDelete)
                        .addGap(18, 18, 18)
                        .addComponent(btnUpdate)
                        .addGap(18, 18, 18)
                        .addComponent(btnView, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(btnRefresh))
                    .addComponent(muIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(176, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel);
        jPanel.setBounds(0, 6, 1040, 530);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        // quit this form and return back to main menu
        badmintonMenu bMenu = new badmintonMenu();
        bMenu.setVisible(true);
        bMenu.pack();
        bMenu.setLocationRelativeTo(null);
        bMenu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.dispose();
    }//GEN-LAST:event_btnExitActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // close this form, open the muCustomiseUser to update the user with parameter "UPDATE/DELETE"
        System.out.println("SessionManager ID: " + SessionManager.getUserID());
        System.out.println("selectedUser ID: " + badmintonUser.getSelectedUserID());
        // only allow the user to update users' details if they are an ADMIN or if they are editing their own user profile
        if (SessionManager.getUserRole().equals("admin") || SessionManager.getUserID() == badmintonUser.getSelectedUserID()) {
            actionChosen = "UPDATE/DELETE";
            muCustomiseUser mucu = new muCustomiseUser(actionChosen);
            mucu.setVisible(true);
            mucu.pack();
            mucu.setLocationRelativeTo(null);
            mucu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.dispose();
        } else {
            msgDlg.setMessage("Must select your own user profile only");
            msgDlg.setVisible(true);
        }
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // reject action if no user is selected from table
        TableModel model = tblUsers.getModel();
        if (model.getValueAt(1,0).toString() == null) {
            msgDlg.setMessage("No selected user to delete");
            msgDlg.setVisible(true);
            System.out.println("empty table, no player to delete");
        } else {
            int i = tblUsers.getSelectedRow();
            String firstNameSelected = model.getValueAt(i, 1).toString();
            String lastNameSelected = model.getValueAt(i, 2).toString();
            msgDlg.setMessage("Confirm Delete User ?");
            msgDlg.setVisible(true);
            // get returnStatus from msgDlg (OK = 1, CANCEL = 0)
            int confirmDelete = msgDlg.getReturnStatus();
            if (confirmDelete == 1 ) {
                // if table is empty
                // get selected user's ID from method in class, badmintonUser
                int IDSelected = badmintonUser.getSelectedUserID();
                if (IDSelected == 0) {
                    msgDlg.setMessage("No selected user to delete");
                    msgDlg.setVisible(true);
                } else {
                    String query = "DELETE FROM `user` WHERE user_id = " + IDSelected;
                    idDeleted = IDSelected;
                    executeSQLQuery(query,"Deleted", firstNameSelected, lastNameSelected);
                }
            } else {
                System.out.println("Delete User Cancelled");
            }
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        // refresh the table, set row count to zero and re-call the procedure to show users in the table
        DefaultTableModel model = (DefaultTableModel)tblUsers.getModel();
        model.setRowCount(0);
        showUsersInJTable();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void tblUsersMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblUsersMouseClicked
        // display selected row in JTextFields
        int i = tblUsers.getSelectedRow();
        DefaultTableModel model = (DefaultTableModel) tblUsers.getModel();

        // if table is empty
        if (model.getValueAt(i,0).toString() == null) {
            System.out.println("empty table");
        }

        // get the ID selected so that it can be used if a window is opened to edit or delete the player
        badmintonUser.selectUserID((int) model.getValueAt(i,0));
    }//GEN-LAST:event_tblUsersMouseClicked

    private void btnViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewActionPerformed
        // users can only view other user profiles if they are ADMIN, otherwise they can only view their own user profile
        if (SessionManager.getUserRole().equals("admin") || SessionManager.getUserID() == badmintonPlayer.getSelectedUserID()) {
            // close this form, open the muCustomiseUser to view the user with parameter "VIEW"
            actionChosen = "VIEW";
            muCustomiseUser mucu = new muCustomiseUser(actionChosen);
            mucu.setVisible(true);
            mucu.pack();
            mucu.setLocationRelativeTo(null);
            mucu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.dispose();         
        } else {
            msgDlg.setMessage("Must select your own user profile only OR must be admin to view other user's profiles");
            msgDlg.setVisible(true);
        }
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
            java.util.logging.Logger.getLogger(muWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(muWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(muWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(muWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new muWindow().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel LogLabel;
    private javax.swing.JButton btnDelete;
    private javax.swing.JToggleButton btnExit;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JButton btnView;
    private javax.swing.JPanel jPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel mainMenuLabel;
    private javax.swing.JLabel muIcon;
    private javax.swing.JTable tblUsers;
    private javax.swing.JPanel topBar;
    public javax.swing.JLabel userLoggedOn;
    // End of variables declaration//GEN-END:variables

}
