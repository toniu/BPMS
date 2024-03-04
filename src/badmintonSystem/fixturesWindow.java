/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
// OBJECTIVE 3: means of viewing the information about competitions and tournaments / a means of viewing match results
package badmintonSystem;

// required imports
import java.sql.Connection;
import java.sql.PreparedStatement;
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
public class fixturesWindow extends javax.swing.JFrame {
    
    String actionChosen = null;

    /**
     * Creates new form fixturesWindow
     */
    public fixturesWindow() {
        initComponents();
        DefaultTableModel fixturesModel = (DefaultTableModel)tblFixtures.getModel();
        DefaultTableModel resultsModel = (DefaultTableModel)tblResults.getModel();
        fixturesModel.setRowCount(0);
        resultsModel.setRowCount(0);
        showFixturesInJTable();
        showResultsInJTable();
    }
    
    // procedure to get the list of tournaments  / 
    public ArrayList<badmintonMatch> getMatchesList()
    {
        // call function to return a new arrayList of matches from mySQL table, 'fixture' 
        ArrayList<badmintonMatch> matchList = new ArrayList<badmintonMatch>();
        // call function to return connection  
        Connection connection = SQLConnection.getConnection();
        
        // query to be executed, select all columns from table 'tournament'
        String query = "SELECT * FROM `fixture`";
        Statement st = null;
        ResultSet rs = null;
        
        try {
            // create the statement
            st = connection.createStatement();
            // execute the query
            rs = st.executeQuery(query);
            
            badmintonMatch match;
            
            /** 
             * while each row of the mySQL table is being selected, instantiate a new 'fixture' with the  
             * columns being retrieved as the parameters 
             */	          

            while (rs.next()) {
                match = new badmintonMatch(rs.getInt("fixture_id"),rs.getInt("sideAPlayer1"),rs.getInt("sideAPlayer2"),rs.getInt("sideBPlayer1"),rs.getInt("sideBPlayer2"),rs.getString("sideAScore"),rs.getString("sideBScore"),rs.getString("typeOfFixture"),rs.getString("venueOfFixture"),rs.getInt("courtNo"),rs.getString("dateOfFixture"),rs.getString("typeOfFixture"),rs.getString("comments"),rs.getBoolean("fixtureCompleted"),rs.getInt("tournamentOfFixture_id"));
                matchList.add(match);
            }
        }
        // exception handling of SQL errors
        catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // call procedure to close the connection    
            SQLConnection.closeConnection(connection, rs, null, st);
        }
        return matchList;
    }
    
    // procedure to retrieve the full names of players with the given player ID as the parameter
    public String retrieveNamesOfPlayers(int searchID) {
        String firstNameFound = "";
        String lastNameFound = "";
        String fullName = "";
        if (searchID != 0) {
            Connection connection = SQLConnection.getConnection();

            String query = "SELECT * FROM `player`, `fixtureHasPlayers` WHERE fixtureHasPlayers.player_id = player.player_id AND player.player_id = " + searchID;
            PreparedStatement pst = null;
            ResultSet rs = null;

            try {
                pst = connection.prepareStatement(query);
                //pst.setInt(1, searchID);
                
                rs = pst.executeQuery(query);

                while (rs.next()) {
                    firstNameFound = rs.getString("player.firstName");
                    lastNameFound = rs.getString("player.lastName");
                }
                fullName = firstNameFound + " " + lastNameFound;
            }
            // exception handling of SQL errors
            catch (SQLException e) {
                e.printStackTrace();
            } finally {
                // call procedure to close the connection    
                SQLConnection.closeConnection(connection, rs, pst, null);
            }
        }
        return fullName;
    }
    
    
    
    // get list of fixtures from mySQL database
    public void showFixturesInJTable()
    {
        // returned array list from calling function to get list of badmintom matches
        ArrayList<badmintonMatch> list = getMatchesList();
        // use DLM to get model of table
        DefaultTableModel model = (DefaultTableModel)tblFixtures.getModel();
        Object[] row = new Object[11];
        // for each row, add the contents of each fixture
        for (int i = 0; i < list.size(); i++) {
            row[0] = list.get(i).getID();
            row[1] = retrieveNamesOfPlayers(list.get(i).getSideAP1ID());
            row[2] = retrieveNamesOfPlayers(list.get(i).getSideAP2ID());
            row[3] = "vs";
            row[4] = retrieveNamesOfPlayers(list.get(i).getSideBP1ID());
            row[5] = retrieveNamesOfPlayers(list.get(i).getSideBP2ID());
            row[6] = list.get(i).getTypeOfMatch();
            row[7] = list.get(i).getVenueOfMatch();
            row[8] = list.get(i).getCourtNo();
            row[9] = list.get(i).getDateOfMatch();
            row[10] = list.get(i).getTimeOfMatch();
            
            model.addRow(row);
        }
    }
    
    // get list of results from mySQL database
    public void showResultsInJTable()
    {
        // returned array list from calling function to get list of badmintom matches
        ArrayList<badmintonMatch> list = getMatchesList();
        // use DLM to get model of table
        DefaultTableModel model = (DefaultTableModel)tblResults.getModel();
        Object[] row = new Object[6];
        for (int i = 0; i < list.size(); i++) {
            // fixture completed means that the badminton match is a result
            if (list.get(i).getFixtureCompleted() == true) {
                row[0] = list.get(i).getID();
                row[1] = retrieveNamesOfPlayers(list.get(i).getSideAP1ID());
                row[2] = retrieveNamesOfPlayers(list.get(i).getSideAP2ID());
                
                // convert the score string to a set of three integers
                if ((list.get(i).getSideAScore()) != null) {

                    // splits the string and places the gamePoints for Game 1, Game 2 and Game 3
                    /**
                     * e.g. "5,21,21" would convert to:
                     * GAME 1 SCORE: 5
                     * GAME 2 SCORE: 21
                     * GAME 3 SCORE: 21
                     */
                    String sideAGamePoints[] = list.get(i).getSideAScore().split(",");
                    row[3] = sideAGamePoints[0];
                    row[4] = sideAGamePoints[1];
                    row[5] = sideAGamePoints[2];
                }        
                
                // add row to the table
                model.addRow(row);

                row[0] = list.get(i).getID();
                row[1] = retrieveNamesOfPlayers(list.get(i).getSideBP1ID());
                row[2] = retrieveNamesOfPlayers(list.get(i).getSideBP2ID());
                
                // convert the score string to a set of three integers
                if ((list.get(i).getSideBScore()) != null) {

                    String sideBGamePoints[] = list.get(i).getSideBScore().split(",");
                    row[3] = sideBGamePoints[0];
                    row[4] = sideBGamePoints[1];
                    row[5] = sideBGamePoints[2];
                }        
                
                // add the row to the table
                model.addRow(row);
            }
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
        fixtureIcon = new javax.swing.JLabel();
        topBar = new javax.swing.JPanel();
        mainMenuLabel = new javax.swing.JLabel();
        LogLabel = new javax.swing.JLabel();
        userLoggedOn = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        btnAddFixture = new javax.swing.JButton();
        btnEditFixture = new javax.swing.JButton();
        btnViewFixture = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblFixtures = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblResults = new javax.swing.JTable();
        btnViewResult = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1180, 530));
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

        fixtureIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/systemIcons/fixtureIcon.png"))); // NOI18N

        topBar.setBackground(new java.awt.Color(255, 51, 0));

        mainMenuLabel.setFont(new java.awt.Font("Adobe Gothic Std", 0, 18)); // NOI18N
        mainMenuLabel.setForeground(new java.awt.Color(255, 255, 255));
        mainMenuLabel.setText("CLUB BADMINTON | FIXTURES");

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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        jLabel2.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("FIXTURES:");

        jLabel3.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("RESULTS:");

        btnAddFixture.setBackground(new java.awt.Color(255, 51, 0));
        btnAddFixture.setFont(new java.awt.Font("Adobe Gothic Std", 0, 13)); // NOI18N
        btnAddFixture.setForeground(new java.awt.Color(255, 255, 255));
        btnAddFixture.setText("ADD NEW FIXTURE");
        btnAddFixture.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddFixtureActionPerformed(evt);
            }
        });

        btnEditFixture.setBackground(new java.awt.Color(255, 51, 0));
        btnEditFixture.setFont(new java.awt.Font("Adobe Gothic Std", 0, 13)); // NOI18N
        btnEditFixture.setForeground(new java.awt.Color(255, 255, 255));
        btnEditFixture.setText("EDIT SELECTED FIXTURE");
        btnEditFixture.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditFixtureActionPerformed(evt);
            }
        });

        btnViewFixture.setBackground(new java.awt.Color(255, 51, 0));
        btnViewFixture.setFont(new java.awt.Font("Adobe Gothic Std", 0, 13)); // NOI18N
        btnViewFixture.setForeground(new java.awt.Color(255, 255, 255));
        btnViewFixture.setText("VIEW SELECTED FIXTURE");
        btnViewFixture.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewFixtureActionPerformed(evt);
            }
        });

        tblFixtures.setBackground(new java.awt.Color(0, 51, 102));
        tblFixtures.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tblFixtures.setForeground(new java.awt.Color(255, 255, 255));
        tblFixtures.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "sideAPlayer1", "sideAPlayer2", "", "sideBPlayer1", "sideBPlayer2", "type", "venue", "court", "date", "time"
            }
        ));
        tblFixtures.setGridColor(new java.awt.Color(255, 0, 0));
        tblFixtures.setSelectionBackground(new java.awt.Color(0, 51, 153));
        tblFixtures.setShowGrid(true);
        tblFixtures.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblFixturesMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblFixtures);

        tblResults.setBackground(new java.awt.Color(0, 51, 102));
        tblResults.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tblResults.setForeground(new java.awt.Color(255, 255, 255));
        tblResults.setModel(new javax.swing.table.DefaultTableModel(
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
                "ID", "side", "", "game1Score", "game2Score", "game3Score"
            }
        ));
        tblResults.setGridColor(new java.awt.Color(255, 0, 0));
        tblResults.setSelectionBackground(new java.awt.Color(0, 51, 153));
        tblResults.setShowGrid(true);
        tblResults.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblResultsMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tblResults);

        btnViewResult.setBackground(new java.awt.Color(255, 51, 0));
        btnViewResult.setFont(new java.awt.Font("Adobe Gothic Std", 0, 13)); // NOI18N
        btnViewResult.setForeground(new java.awt.Color(255, 255, 255));
        btnViewResult.setText("VIEW SELECTED RESULT");
        btnViewResult.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewResultActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelLayout = new javax.swing.GroupLayout(jPanel);
        jPanel.setLayout(jPanelLayout);
        jPanelLayout.setHorizontalGroup(
            jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(topBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanelLayout.createSequentialGroup()
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(fixtureIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 824, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanelLayout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 922, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 3, Short.MAX_VALUE)
                        .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnViewFixture, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnExit, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnAddFixture, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnEditFixture, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(20, 20, 20))
                    .addGroup(jPanelLayout.createSequentialGroup()
                        .addComponent(btnViewResult, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanelLayout.setVerticalGroup(
            jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLayout.createSequentialGroup()
                .addComponent(topBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(fixtureIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanelLayout.createSequentialGroup()
                                .addComponent(btnViewFixture)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnAddFixture)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnEditFixture)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelLayout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addComponent(btnViewResult)))
                .addContainerGap(43, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel);
        jPanel.setBounds(0, 6, 1180, 530);

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

    private void btnAddFixtureActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddFixtureActionPerformed
        // open customise match with parameter "INSERT"
        actionChosen = "INSERT";
        fixtureCustomise fc = new fixtureCustomise(actionChosen,"fixture");
        fc.setVisible(true);
        fc.pack();
        fc.setLocationRelativeTo(null);
        fc.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.dispose();
    }//GEN-LAST:event_btnAddFixtureActionPerformed

    private void btnEditFixtureActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditFixtureActionPerformed
        // open customise match with parameter "UPDATE/DELETE"
        actionChosen = "UPDATE/DELETE";
        fixtureCustomise fc = new fixtureCustomise(actionChosen,"fixture");
        fc.setVisible(true);
        fc.pack();
        fc.setLocationRelativeTo(null);
        fc.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.dispose();
    }//GEN-LAST:event_btnEditFixtureActionPerformed

    private void btnViewFixtureActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewFixtureActionPerformed
        // open customise match window with parameter "VIEW"
        actionChosen = "VIEW";
        fixtureCustomise fc = new fixtureCustomise(actionChosen,"fixture");
        fc.setVisible(true);
        fc.pack();
        fc.setLocationRelativeTo(null);
        fc.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.dispose();
    }//GEN-LAST:event_btnViewFixtureActionPerformed

    private void tblFixturesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblFixturesMouseClicked
        // display selected row in JTextFields
        int i = tblFixtures.getSelectedRow();
        DefaultTableModel model = (DefaultTableModel) tblFixtures.getModel();
        
        // if table is empty
        if (model.getValueAt(1,0).toString() == null) {
            System.out.println("empty table");
        }
        
        // get the ID selected so that it can be used if a window is opened to edit or delete the match
        badmintonMatch.selectFixtureID((int) model.getValueAt(i,0));
    }//GEN-LAST:event_tblFixturesMouseClicked

    private void tblResultsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblResultsMouseClicked
        // display selected row in JTextFields
        int i = tblResults.getSelectedRow();
        DefaultTableModel model = (DefaultTableModel) tblResults.getModel();
        
        // if table is empty
        if (model.getValueAt(1,0).toString() == null) {
            System.out.println("empty table");
        }
        
        // get the ID selected so that it can be used if a window is opened to edit or delete the match
        badmintonMatch.selectResultID((int) model.getValueAt(i,0));
    }//GEN-LAST:event_tblResultsMouseClicked

    private void btnViewResultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewResultActionPerformed
        // open customise match window with parameter "VIEW"
        actionChosen = "VIEW";
        fixtureCustomise fc = new fixtureCustomise(actionChosen,"result");
        fc.setVisible(true);
        fc.pack();
        fc.setLocationRelativeTo(null);
        fc.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.dispose();
    }//GEN-LAST:event_btnViewResultActionPerformed

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
            java.util.logging.Logger.getLogger(fixturesWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(fixturesWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(fixturesWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(fixturesWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new fixturesWindow().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel LogLabel;
    private javax.swing.JButton btnAddFixture;
    private javax.swing.JButton btnEditFixture;
    private javax.swing.JToggleButton btnExit;
    private javax.swing.JButton btnViewFixture;
    private javax.swing.JButton btnViewResult;
    private javax.swing.JLabel fixtureIcon;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel mainMenuLabel;
    private javax.swing.JTable tblFixtures;
    private javax.swing.JTable tblResults;
    private javax.swing.JPanel topBar;
    public javax.swing.JLabel userLoggedOn;
    // End of variables declaration//GEN-END:variables
}
