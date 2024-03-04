/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package badmintonSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Neka
 */
public class competitionRoundRobin extends javax.swing.JFrame {
    
    

    /**
     * Creates new form competitionsWindow
     */
    public competitionRoundRobin() {
        initComponents();
        showSelectedTournamentDetails();
        displayRound(1,"FIRST");
    }
    
     public void showSelectedTournamentDetails() {
        // SEARCH FOR FIELDS IN SELECTED TOURNAMENT IN PLAYER TABLE
        
        int tournamentID = 0;
        
        String tournamentName = "";
        String tournamentCategory = "";
        int noOfPlayers = 0;
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String queryA = "SELECT * FROM tournament WHERE tournament_id = ?";

            ps = SQLConnection.getConnection().prepareStatement(queryA);

            ps.setInt(1, badmintonTournament.getSelectedTournamentID());           
            rs = ps.executeQuery();

            while (rs.next()) {
                tournamentID = rs.getInt("tournament_id");
                tournamentName = rs.getString("tournamentName");
                tournamentCategory = rs.getString("tournamentCategory");
                noOfPlayers = rs.getInt("noOfPlayers");

            }             

            // convert integer variables to string to display on fields
            String IDShown = Integer.toString(tournamentID);
            String nopString = Integer.toString(noOfPlayers);
            
            // set fields to selected record
            tournamentIDField.setText(IDShown);
            tournamentNameField.setText(tournamentName);
            tournamentNOPField.setText(nopString);
            categoryField.setText(tournamentCategory);
            
            
            
        } catch (SQLException ex) {
            Logger.getLogger(competitionRoundRobin.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            // call procedure to close connection
            SQLConnection.closeConnection(SQLConnection.getConnection(), rs, ps, null);
        }
    }
    
    // procedure to display the contents of the current round 
    public void displayRound(int currentRound, String action) {
        // number of rounds in the round-robin tournament
        int numOfRounds = Integer.valueOf(tournamentNOPField.getText()) - 1;
        // if the action was "NEXT" then go forward by one
        if (action.equals("NEXT")) {
            // ensure the round doesn't exceed the number of rounds
            currentRound = currentRound + 1;
            if (currentRound > numOfRounds) {
                currentRound = numOfRounds;
            }
            getMatchesFromRound(currentRound);
        // if the action was "FIRST" then go to first round
        } else if (action.equals("FIRST")) {
            currentRound = 1;
            getMatchesFromRound(currentRound);
        // if the action was "PREVIOUS" then go backwards by one    
        } else if (action.equals("PREVIOUS")) {
            currentRound = currentRound - 1;
            // ensure that the round doesn't go to zero
            if (currentRound < 1) {
                currentRound = 1;
            }
            getMatchesFromRound(currentRound);
        // if the action was "LAST" then go to the last round       
        } else if (action.equals("LAST")) {
            currentRound = numOfRounds;
            getMatchesFromRound(currentRound);
        }
        roundLabel.setText("ROUND " + currentRound);
    }
    
    // procedure to retrieve the matches from the current round
    public void getMatchesFromRound(int currentRound) {
        String query = "SELECT * FROM `tournamentHasFixture` AND `fixture` WHERE tournamentHasFixture.roundNo = " + currentRound + " AND tournamentHasFixture.tournamentFound_id = " + badmintonTournament.getSelectedTournamentID() + 
                " AND tournamentHasFixture.fixtureOfTournament_id = fixture.fixture_id";
        Statement st = null;
        ResultSet rs = null;
       
        
        ArrayList<badmintonMatch> list = new ArrayList<badmintonMatch>();
        Connection connection = SQLConnection.getConnection();
        try {
            st = connection.createStatement();
            rs = st.executeQuery(query);
            
            badmintonMatch match;
            
            while (rs.next()) {
                match = new badmintonMatch(rs.getInt("fixture.fixture_id"),rs.getInt("fixture.sideAPlayer1"),rs.getInt("fixture.sideAPlayer2"),rs.getInt("fixture.sideBPlayer1"),rs.getInt("fixture.sideBPlayer2"),rs.getString("fixture.sideAScore"),rs.getString("fixture.sideBScore"),rs.getString("fixture.typeOfFixture"),rs.getString("fixture.venueOfFixture"),rs.getInt("fixture.courtNo"),rs.getString("fixture.dateOfFixture"),rs.getString("fixture.typeOfFixture"),rs.getString("fixture.comments"),rs.getBoolean("fixture.fixtureCompleted"),rs.getInt("fixture.tournamentOfFixture_id"));
                list.add(match);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // call procedure to close connection
            SQLConnection.closeConnection(connection, rs, null, st);
        }
        
        DefaultTableModel model = (DefaultTableModel)tblFixtures.getModel();
        model.setRowCount(0);
        
        Object[] row = new Object[6];
        for (int i = 0; i < list.size(); i++) {         
            row[0] = list.get(i).getID();
            row[1] = retrieveNamesOfPlayers(list.get(i).getSideAP1ID());
            row[2] = retrieveNamesOfPlayers(list.get(i).getSideAP2ID());

            if ((list.get(i).getSideAScore()) != null) {

                String sideAGamePoints[] = list.get(i).getSideAScore().split(",");
                row[3] = sideAGamePoints[0];
                row[4] = sideAGamePoints[1];
                row[5] = sideAGamePoints[2];
            }        

            model.addRow(row);

            row[0] = list.get(i).getID();
            row[1] = retrieveNamesOfPlayers(list.get(i).getSideBP1ID());
            row[2] = retrieveNamesOfPlayers(list.get(i).getSideBP2ID());

            if ((list.get(i).getSideBScore()) != null) {

                String sideBGamePoints[] = list.get(i).getSideBScore().split(",");
                row[3] = sideBGamePoints[0];
                row[4] = sideBGamePoints[1];
                row[5] = sideBGamePoints[2];
            }        

            model.addRow(row);
        }
    }
    
    // selects from tournamentPoints
    public void displayLeagueTable() {
        String query = "SELECT * FROM `tournamentPoints`, `player` WHERE tournamentPoints.tournamentPlayerIsIn_id =" + badmintonTournament.getSelectedTournamentID() +
                " AND tournamentPoints.tournamentPlayerIsIn_id = player.player_id" +
                " ORDER BY tournamentPoints.tournamentPoints DESC, tournamentPoints.matchGamesWon DESC, tournamentPoints.gamePointsWon DESC";
        Statement st = null;
        ResultSet rs = null;
       
        
        ArrayList<badmintonPlayer> list = new ArrayList<badmintonPlayer>();
        Connection connection = SQLConnection.getConnection();
        try {
            st = connection.createStatement();
            rs = st.executeQuery(query);
            
            badmintonPlayer player;
            
            while (rs.next()) {
                player = new badmintonPlayer(rs.getInt("player_id"),rs.getString("firstName"),rs.getString("lastName"),rs.getString("gender"),rs.getString("dateOfBirth"),rs.getString("hand"),rs.getString("status"),rs.getInt("ranking"),rs.getInt("oldRanking"),rs.getDouble("rankPoints"),rs.getInt("singleMatchWins"),rs.getInt("singleMatchLosses"),rs.getInt("doubleMatchWins"),rs.getInt("doubleMatchLosses"),rs.getString("form"),rs.getInt("balance"),rs.getInt("singleTournamentWins"),rs.getInt("doubleTournamentWins"),rs.getString("linkedWithUser"));
                list.add(player);
                System.out.println("player added: " + player.getFirstName() + " " + player.getLastName());
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // call procedure to close connection
            SQLConnection.closeConnection(connection, rs, null, st);
        }
        
    }
    
    // 
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
                
                rs = pst.executeQuery(query);

                while (rs.next()) {
                    firstNameFound = rs.getString("player.firstName");
                    lastNameFound = rs.getString("player.lastName");
                }
                fullName = firstNameFound + " " + lastNameFound;
            }
            catch (SQLException e) {
                e.printStackTrace();
            } finally {
                // call procedure to close connection
                SQLConnection.closeConnection(connection, rs, pst, null);
            }
        }
        return fullName;
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
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        tournamentIDField = new javax.swing.JTextField();
        tournamentNameField = new javax.swing.JTextField();
        tournamentNOPField = new javax.swing.JTextField();
        btnViewMatch = new javax.swing.JButton();
        btnEditMatch = new javax.swing.JButton();
        categoryField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        leagueTable = new javax.swing.JTable();
        roundLabel = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        btnPreviousRound = new javax.swing.JButton();
        btnNextRound = new javax.swing.JButton();
        btnLastRound = new javax.swing.JButton();
        btnFirstRound = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblFixtures = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1190, 539));
        setResizable(false);
        getContentPane().setLayout(null);

        jPanel.setBackground(new java.awt.Color(0, 51, 102));
        jPanel.setPreferredSize(new java.awt.Dimension(1070, 539));

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
                .addContainerGap(169, Short.MAX_VALUE))
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

        jLabel1.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("TOURNAMENT NAME:");

        jLabel2.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("NUMBER OF PLAYERS:");

        jLabel3.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("TOURNAMENT ID:");

        tournamentIDField.setEditable(false);
        tournamentIDField.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        tournamentIDField.setText("5");

        tournamentNameField.setEditable(false);
        tournamentNameField.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        tournamentNameField.setText("Surrey Beginners Country Cup");

        tournamentNOPField.setEditable(false);
        tournamentNOPField.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        tournamentNOPField.setText("0");

        btnViewMatch.setBackground(new java.awt.Color(255, 51, 0));
        btnViewMatch.setFont(new java.awt.Font("Adobe Gothic Std", 0, 11)); // NOI18N
        btnViewMatch.setForeground(new java.awt.Color(255, 255, 255));
        btnViewMatch.setText("VIEW SELECTED MATCH");
        btnViewMatch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewMatchActionPerformed(evt);
            }
        });

        btnEditMatch.setBackground(new java.awt.Color(255, 51, 0));
        btnEditMatch.setFont(new java.awt.Font("Adobe Gothic Std", 0, 11)); // NOI18N
        btnEditMatch.setForeground(new java.awt.Color(255, 255, 255));
        btnEditMatch.setText("EDIT SELECTED MATCH");

        categoryField.setEditable(false);
        categoryField.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        categoryField.setText("singles");
        categoryField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                categoryFieldActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("CATEGORY:");

        leagueTable.setBackground(new java.awt.Color(0, 51, 102));
        leagueTable.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        leagueTable.setForeground(new java.awt.Color(255, 255, 255));
        leagueTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "POS", "NAME", "W", "L", "GP", "GW", "PTS"
            }
        ));
        leagueTable.setGridColor(new java.awt.Color(255, 0, 0));
        leagueTable.setSelectionBackground(new java.awt.Color(0, 51, 153));
        leagueTable.setShowGrid(true);
        leagueTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                leagueTableMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(leagueTable);

        roundLabel.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        roundLabel.setForeground(new java.awt.Color(255, 255, 255));
        roundLabel.setText("ROUND 1");

        jLabel7.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("LEAGUE TABLE:");

        btnPreviousRound.setBackground(new java.awt.Color(255, 51, 0));
        btnPreviousRound.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        btnPreviousRound.setForeground(new java.awt.Color(255, 255, 255));
        btnPreviousRound.setText("<");
        btnPreviousRound.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviousRoundActionPerformed(evt);
            }
        });

        btnNextRound.setBackground(new java.awt.Color(255, 51, 0));
        btnNextRound.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        btnNextRound.setForeground(new java.awt.Color(255, 255, 255));
        btnNextRound.setText(">");
        btnNextRound.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextRoundActionPerformed(evt);
            }
        });

        btnLastRound.setBackground(new java.awt.Color(255, 51, 0));
        btnLastRound.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        btnLastRound.setForeground(new java.awt.Color(255, 255, 255));
        btnLastRound.setText(">|");
        btnLastRound.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLastRoundActionPerformed(evt);
            }
        });

        btnFirstRound.setBackground(new java.awt.Color(255, 51, 0));
        btnFirstRound.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        btnFirstRound.setForeground(new java.awt.Color(255, 255, 255));
        btnFirstRound.setText("|<");
        btnFirstRound.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFirstRoundActionPerformed(evt);
            }
        });

        tblFixtures.setBackground(new java.awt.Color(0, 51, 102));
        tblFixtures.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tblFixtures.setForeground(new java.awt.Color(255, 255, 255));
        tblFixtures.setModel(new javax.swing.table.DefaultTableModel(
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
        tblFixtures.setGridColor(new java.awt.Color(255, 0, 0));
        tblFixtures.setSelectionBackground(new java.awt.Color(0, 51, 153));
        tblFixtures.setShowGrid(true);
        tblFixtures.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblFixturesMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tblFixtures);

        javax.swing.GroupLayout jPanelLayout = new javax.swing.GroupLayout(jPanel);
        jPanel.setLayout(jPanelLayout);
        jPanelLayout.setHorizontalGroup(
            jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(topBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanelLayout.createSequentialGroup()
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addGroup(jPanelLayout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(tournamentIDField, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanelLayout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(competitionsIcon))
                    .addGroup(jPanelLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(categoryField, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addGap(12, 12, 12)
                        .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tournamentNOPField, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tournamentNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnViewMatch, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnEditMatch, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(jPanelLayout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(roundLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanelLayout.createSequentialGroup()
                                .addComponent(btnFirstRound)
                                .addGap(92, 92, 92)
                                .addComponent(btnPreviousRound)
                                .addGap(79, 79, 79)
                                .addComponent(btnNextRound)
                                .addGap(105, 105, 105)
                                .addComponent(btnLastRound)))
                        .addGap(27, 27, 27)
                        .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 427, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanelLayout.createSequentialGroup()
                    .addGap(134, 134, 134)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 578, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(478, Short.MAX_VALUE)))
        );
        jPanelLayout.setVerticalGroup(
            jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLayout.createSequentialGroup()
                .addComponent(topBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanelLayout.createSequentialGroup()
                            .addGap(17, 17, 17)
                            .addComponent(jLabel1)
                            .addGap(106, 106, 106))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelLayout.createSequentialGroup()
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(tournamentNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(jPanelLayout.createSequentialGroup()
                                    .addComponent(competitionsIcon)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                                .addGroup(jPanelLayout.createSequentialGroup()
                                    .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel2)
                                        .addComponent(tournamentNOPField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(roundLabel)
                                        .addComponent(jLabel7))
                                    .addGap(18, 18, 18)))))
                    .addGroup(jPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnEditMatch, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnViewMatch, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelLayout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tournamentIDField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(categoryField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnPreviousRound)
                    .addComponent(btnNextRound)
                    .addComponent(btnLastRound)
                    .addComponent(btnFirstRound))
                .addContainerGap(50, Short.MAX_VALUE))
            .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanelLayout.createSequentialGroup()
                    .addGap(185, 185, 185)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(98, Short.MAX_VALUE)))
        );

        getContentPane().add(jPanel);
        jPanel.setBounds(0, 6, 1190, 540);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        // quit this form and return back to the competitions window
        competitionsWindow cW = new competitionsWindow();
        cW.setVisible(true);
        cW.pack();
        cW.setLocationRelativeTo(null);
        cW.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.dispose();
    }//GEN-LAST:event_btnExitActionPerformed

    private void categoryFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_categoryFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_categoryFieldActionPerformed

    private void btnViewMatchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewMatchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnViewMatchActionPerformed

    private void leagueTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_leagueTableMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_leagueTableMouseClicked

    private void btnFirstRoundActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFirstRoundActionPerformed
        // remove the "ROUND" from the string and trim any whitespace to only retrieve number
        String numberOnly = (roundLabel.getText().replace("ROUND", "")).trim();
        // convert string number to integer
        int roundNumber = Integer.valueOf(numberOnly);
        
        // call display round with the parameter "FIRST"
        displayRound(roundNumber, "FIRST");
    }//GEN-LAST:event_btnFirstRoundActionPerformed

    private void btnPreviousRoundActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousRoundActionPerformed
        // remove the "ROUND" from the string and trim any whitespace to only retrieve number
        String numberOnly = (roundLabel.getText().replace("ROUND", "")).trim();
        // convert string number to integer
        int roundNumber = Integer.valueOf(numberOnly);
        
        // call display round with the parameter "FIRST"
        displayRound(roundNumber, "PREVIOUS");
    }//GEN-LAST:event_btnPreviousRoundActionPerformed

    private void btnNextRoundActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextRoundActionPerformed
        // remove the "ROUND" from the string and trim any whitespace to only retrieve number
        String numberOnly = (roundLabel.getText().replace("ROUND", "")).trim();
        // convert string number to integer
        int roundNumber = Integer.valueOf(numberOnly);
        
        // call display round with the parameter "FIRST"
        displayRound(roundNumber, "NEXT");
    }//GEN-LAST:event_btnNextRoundActionPerformed

    private void btnLastRoundActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLastRoundActionPerformed
        // remove the "ROUND" from the string and trim any whitespace to only retrieve number
        String numberOnly = (roundLabel.getText().replace("ROUND", "")).trim();
        // convert string number to integer
        int roundNumber = Integer.valueOf(numberOnly);
        
        // call display round with the parameter "FIRST"
        displayRound(roundNumber, "LAST");
    }//GEN-LAST:event_btnLastRoundActionPerformed

    private void tblFixturesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblFixturesMouseClicked
        // display selected row in JTextFields
        int i = tblFixtures.getSelectedRow();
        DefaultTableModel model = (DefaultTableModel) tblFixtures.getModel();

        // if table is empty
        if (model.getValueAt(1,0).toString() == null) {
            System.out.println("empty table");
        }

        // get the ID selected so that it can be used if a window is opened to edit or delete the player
        badmintonMatch.selectResultID((int) model.getValueAt(i,0));
    }//GEN-LAST:event_tblFixturesMouseClicked

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
            java.util.logging.Logger.getLogger(competitionRoundRobin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(competitionRoundRobin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(competitionRoundRobin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(competitionRoundRobin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new competitionRoundRobin().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel LogLabel;
    private javax.swing.JButton btnEditMatch;
    private javax.swing.JToggleButton btnExit;
    private javax.swing.JButton btnFirstRound;
    private javax.swing.JButton btnLastRound;
    private javax.swing.JButton btnNextRound;
    private javax.swing.JButton btnPreviousRound;
    private javax.swing.JButton btnViewMatch;
    private javax.swing.JTextField categoryField;
    private javax.swing.JLabel competitionsIcon;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable leagueTable;
    private javax.swing.JLabel mainMenuLabel;
    private javax.swing.JLabel roundLabel;
    private javax.swing.JTable tblFixtures;
    private javax.swing.JPanel topBar;
    private javax.swing.JTextField tournamentIDField;
    private javax.swing.JTextField tournamentNOPField;
    private javax.swing.JTextField tournamentNameField;
    public javax.swing.JLabel userLoggedOn;
    // End of variables declaration//GEN-END:variables
}
