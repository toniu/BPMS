/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
// OBJECTIVE 4: An effective method for match-making for singles and doubles matches for tournaments 
package badmintonSystem;

// required imports
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;



/**
 *
 * @author Neka
 */
public class competitionCustomise extends javax.swing.JFrame {
    
    // message dialog
    msgDialog msgDlg = new msgDialog(new javax.swing.JFrame(),true);
   
    // global variables

    String fixtureAction = null;
    String matchStatus = null;


    /**
     * Creates new form mpWindow
     */
    
    public competitionCustomise() {
        initComponents();
    }
    
    public competitionCustomise(String actionChosen, String typeOfTournament, String categoryOfTournament) {
        initComponents();
        
        if (actionChosen.equals("VIEW")) {
            // typeOfAction is VIEW so...
            // disable Add / Delete / Update player if the user is planning to view the selected player
            btnAdd.setVisible(false);
            btnAdd.setEnabled(false);   
            btnDelete.setVisible(false);
            btnDelete.setEnabled(false);

            fixtureAction = actionChosen;
            // fields and comboBoxes to disable
            showSelectedTournamentDetails();
            
            // fields or comboBoxes to set as uneditable           
        } else {
            // typeOfAction is INSERT so...
            // disable Delete / Update player if the user is planning to add a new player
            btnDelete.setVisible(false);
            btnDelete.setEnabled(false);
            
            fixtureAction = actionChosen;
            
            // set selected details to null 
            setSelectedDetails(null, null);
        }
    }
    
   
   
   // procedure to set the textfields of the form
  public void setSelectedDetails(String typeOfTournament, String categoryOfTournament) {
      // if the user chose players from the other form then set the player textfields as the selected players
    if (chooseTournamentType.getReturnStatus() == 1) {
       typeField.setText(chooseTournamentType.getSelectedType());
       categoryField.setText(chooseTournamentType.getSelectedCategory());
       
       int tournamentSize = badmintonTournament.getPlayerList().size();
       noOfPlayersField.setText(Integer.toString(tournamentSize));
       
        // use the DLM to set the list as the current playerlist of the tournament being edited
       DefaultListModel DLM = new DefaultListModel();
       for (int i = 0; i < tournamentSize; i++) {
           DLM.addElement(badmintonTournament.getPlayerList().get(i));         
       }
       playerJList.setModel(DLM);
    } else {
        // else set to null
       typeField.setText(typeOfTournament);
       categoryField.setText(categoryOfTournament);
    }
  }
  
  // function to retrieve names of players using given player ID
  public String retrieveNamesOfPlayers(int searchID) {
      // assign the variables
        String firstNameFound = "";
        String lastNameFound = "";
        String fullNameFound = "";
      // players cannot have an ID of 0
        if (searchID != 0) {
            Connection connection = SQLConnection.getConnection();

            String query = "SELECT * FROM `player`, `tournamentPoints` WHERE tournamentPoints.playerOfTournament_id = player.player_id AND player.player_id = " + searchID;
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
                fullNameFound = firstNameFound + " " + lastNameFound;
            }
            catch (SQLException e) {
                e.printStackTrace();
            } finally {
                // call procedure to close connection
                SQLConnection.closeConnection(connection, rs, pst, null);
            }
        }
        return fullNameFound;
    }
    
  
  public void retrieveListOfPlayers(int tournamentID) {
      DefaultListModel retrievedDLM = new DefaultListModel();
      PreparedStatement ps = null;
      ResultSet rs = null;
      try {
            String queryA = "SELECT * FROM tournamentPoints WHERE tournament_id = ?";

            ps = SQLConnection.getConnection().prepareStatement(queryA);
            
            ps.setInt(1, badmintonTournament.getSelectedTournamentID());           
            rs = ps.executeQuery();
            
            while (rs.next()) {
                retrievedDLM.addElement("[" + rs.getInt(2) + "] " + retrieveNamesOfPlayers(rs.getInt(2)));
            }  
            
            badmintonTournament.setPlayerList(retrievedDLM);
            playerJList.setModel(retrievedDLM);
          
      } catch (SQLException ex) {
          Logger.getLogger(competitionCustomise.class.getName()).log(Level.SEVERE, null, ex);
      } finally {
          // call procedure to close connection
          SQLConnection.closeConnection(SQLConnection.getConnection(), rs, ps, null);
      }
  }
  
  // function to check if tournament name already exists
    public boolean checkIfTournamentNameExists(String name)
    {
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean checkName = false;
        
        String queryCheck = "SELECT * FROM `tournament` WHERE `tournamentName` = ?";
        
        try {
            ps = SQLConnection.getConnection().prepareStatement(queryCheck);
            ps.setString(1, name);
            
            rs = ps.executeQuery();
            
            if(rs.next())
            {
                checkName = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(competitionCustomise.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            // call procedure to close connection
            SQLConnection.closeConnection(SQLConnection.getConnection(), rs, ps, null);
        }
        return checkName;
    }


   
    public void showSelectedTournamentDetails() {
        // SEARCH FOR FIELDS IN SELECTED TOURNAMENT IN PLAYER TABLE
        
        int tournamentID = 0;
        
        String tournamentName = "";
        String tournamentCategory = "";
        String tournamentType = "";
        int noOfPlayers = 0;
        int winnerOfTournamentID = 0;
        boolean tournamentCompleted = false;
        String dateStarted = null;
        String dateCompleted = null;

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
                tournamentType = rs.getString("tournamentType");
                noOfPlayers = rs.getInt("noOfPlayers");
                winnerOfTournamentID = rs.getInt("winnerOfTournamentID");
                tournamentCompleted = rs.getBoolean("tournamentCompleted");
                dateStarted = rs.getString("dateStarted");
                dateCompleted = rs.getString("dateCompleted");

            }             

            // convert integer variables to string to display on fields
            String IDShown = Integer.toString(tournamentID);
            String nopString = Integer.toString(noOfPlayers);
            String winnerIDShown = Integer.toString(winnerOfTournamentID);
            
            // set the JList to the model created when the list of players in the specific tournament is queried
            retrieveListOfPlayers(tournamentID);
            // set fields to selected record
            tournamentIDField.setText(IDShown);
            tournamentNameField.setText(tournamentName);
            noOfPlayersField.setText(nopString);
            
            // call sub-routine to set tournament type and tournament cateogyr details depending on the scenario
            setSelectedDetails(tournamentType, tournamentCategory);
            
            // convert from string to java.util.Date to show on the comboboxes, dateStarted and dateCompleted.
            if (dateStarted != null) {
                try {
                    Date convertedDate = new SimpleDateFormat("dd-MM-yyyy").parse(dateStarted);
                    dcStartedCombo.setDate(convertedDate);
                } catch (ParseException ex) {
                    Logger.getLogger(competitionCustomise.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(competitionCustomise.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
           // call procedure to close connection
           SQLConnection.closeConnection(SQLConnection.getConnection(), rs, ps, null);
        }
    }
    
    // update the audit log 
    public void updateAuditLog(String message) {
        // SET TIMESTAMP FOR SQL QUERY
        Date date = new Date();
        long time = date.getTime();
        Timestamp updateTimestamp = new Timestamp(time);
        
        String userWhoChanged = SessionManager.getUsername();
        String change = (message + " tournament: ");    
        
        Statement st = null;
        Connection con = SQLConnection.getConnection();
        
        if (message.equals("Inserted")) {           
            try {
                st = con.createStatement();
                if ((st.executeUpdate("INSERT INTO `change`(`change`, `timeOfChange`, `userWhoChanged`, `itemAffected`, `user_id`) VALUES ('"+change+"','"+updateTimestamp+"','"+userWhoChanged + "','"  + "','" + SessionManager.getUserID() + "')")) == 1)
                {
                    System.out.println("AUDIT LOG UPDATED");    
                } else {
                    System.out.println("Failed to update into audit log.");
                }           
            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                // call procedure to close connection
                SQLConnection.closeConnection(con, null, null, st);
            }           
        } else  { // deleted tournament
            try {
                st = con.createStatement();
                if ((st.executeUpdate("INSERT INTO `change`(`change`, `timeOfChange`, `userWhoChanged`, `itemAffected`, `user_id`) VALUES ('"+change+"','"+updateTimestamp+"','"+userWhoChanged + "','" + "deleted tournament was ID: " + badmintonTournament.getSelectedTournamentID() + "','" + SessionManager.getUserID() + "')")) == 1)
                {
                    System.out.println("AUDIT LOG UPDATED");    
                } else {
                    System.out.println("Failed to update into audit log.");
                }           
            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                // call procedure to close connection
                SQLConnection.closeConnection(con, null, null, st);
            }
        }
    }
    
    // execute the SQL query
    public void executeSQLQuery(String query, String message) {
        Connection con = SQLConnection.getConnection();
        Statement st = null;
        try {
            st = con.createStatement();
            if ((st.executeUpdate(query)) == 1)
            {
                msgDlg.setMessage("Data " + message + " successfully");
                msgDlg.setVisible(true);
                // update audit log
                updateAuditLog(message);           
            } else {
                displayErrorMessage("Data not " + message);
            }           
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            // call procedure to close connection
            SQLConnection.closeConnection(con, null, null, st);
        }   
    }   

    
    public void quitWindow() {
        // opens main compeitions window
        competitionsWindow cw = new competitionsWindow();
        cw.setVisible(true);
        cw.pack();
        cw.setLocationRelativeTo(null);
        cw.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.dispose();
    }
    
  
    
    public void createRoundRobinSchedule(String tournamentName) {
        
        // the amount of days between each round
        int breakDays = (int) breakDaysCombo.getSelectedItem();           
        
        // conversion of Date to string of dateStarted
        Date dateInput = dcStartedCombo.getDate();
        DateFormat dayFormat = new SimpleDateFormat("dd-MM-yyyy");
        String dateString;
        
        // conversion of Date to string of earliest date and latest date
        Date earliestTimeInput = (Date) earliestTimeSpinner.getValue();
        Date latestTimeInput = (Date) latestTimeSpinner.getValue();
        DateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String earliestTimeString = timeFormat.format(earliestTimeInput);
        String latestTimeString = timeFormat.format(latestTimeInput);
                
        List listPlayers = badmintonTournament.getPlayerList(); // retrieve tournament player list
        int tournamentSize = listPlayers.size(); // size of tournament
        String playerIDString;
        int playerID;
        
        // call function to return the tournament ID of recently inserted tournament
        int tournamentID = retrieveTournamentID(tournamentName);
        String categoryOfTournament = categoryField.getText();
        Connection con = SQLConnection.getConnection();
        
        // insert each player into table, tournamentPoints with the tournament ID, player ID and a created team ID for each player
        String insertTournamentPoints = "INSERT INTO `tournamentPoints`(`tournamentPlayerIsIn_id`, `playerOfTournament_id`, `team_id`, `position`) VALUES ('?','?','?','?')";
        // surround try and catch statement for SQL exception handling
        try {
            if (categoryOfTournament.equals("singles")) {
                // ?: parameter as a placeholder
                PreparedStatement pst = con.prepareStatement(insertTournamentPoints);

                // loop to bind data/parameters to the prepared statement for each player in the list
                for (int nextRow = 0; nextRow < tournamentSize; nextRow++) {

                        // use of regex expression and trim to only retrieve the number (the ID number) from the string
                        playerIDString = listPlayers.get(nextRow).toString().replaceAll("[^0-9]+", " ").trim();
                        playerID = Integer.valueOf(playerIDString);

                        // batch processing of SQL insert statements for faster execution              
                        pst.setInt(1, tournamentID);
                        pst.setInt(2, playerID);
                        pst.setInt(3, (nextRow + 1));
                        pst.setInt(4, (nextRow + 1));                        
                        pst.addBatch();

                        System.out.println("Tournament points for tournament ID: " + tournamentID + ", inserted");
                }
                pst.executeBatch();

                pst.close();
                con.close();


                int rounds = (tournamentSize - 1); // rounds needed to complete round-robin tournament
                int halfSize = tournamentSize / 2; // mid of tournament size

                List singlesPlayers = new ArrayList(listPlayers); // Duplicate the list and remove the first player from arrayList
                singlesPlayers.remove(0);

                int singlesPlayersSize = singlesPlayers.size();

                // insert query to generate fixtures into 
                // ?: parameter as a placeholder
                String insertSinglesFixture = "INSERT INTO `fixture`(`sideAPlayer1`, `sideBPlayer1`, `typeOfFixture`, `venueOfFixture`, `dateOfFixture`, `fixtureCompleted`) VALUES ('?','?','?','?','?','?','?','?')";
                PreparedStatement fixturePst = con.prepareStatement(insertSinglesFixture, Statement.RETURN_GENERATED_KEYS);

                // calendar variable of the date which will be incremated by the amount of breakdays set by user PER ROUND         
                Calendar c = Calendar.getInstance();
                c.setTime(dateInput);       
                
                
                String player1IDString;
                int player1ID;
                String player2IDString;
                int player2ID;
                
                // stack to get the player ID's involved in the fixtures once the fixtures are generated
                Stack <Integer> playerinFixtureIDStack = new Stack <Integer>();
                // loop to generate badminton singles matches for each round    
                
                for (int day = 0; day < rounds; day++)
                {
                    // conversion of date 
                    dateString = dayFormat.format(c);
                    
                    // new round
                    System.out.println("Round " + (day + 1) + " ========");

                    int playerIdx = day % singlesPlayersSize;
                    
                    player1IDString = singlesPlayers.get(playerIdx).toString().replaceAll("[^0-9]+", " ").trim();
                    player1ID = Integer.valueOf(player1IDString);
                    
                    player2IDString = listPlayers.get(0).toString().replaceAll("[^0-9]+", " ").trim();
                    player2ID = Integer.valueOf(player2IDString);
                   
                    // output the match involving first player in list for every round
                    System.out.println(singlesPlayers.get(playerIdx) + " vs. " + listPlayers.get(0));
                    
                    // push player IDs into stack to know the player IDs for each fixture
                    playerinFixtureIDStack.push(player1ID);
                    playerinFixtureIDStack.push(player2ID);
                        
                    // set parameters for each batch added
                    fixturePst.setInt(1, player1ID);
                    fixturePst.setInt(2, player2ID);
                    fixturePst.setString(3, "singles");
                    fixturePst.setString(4, "Local gym");
                    fixturePst.setString(5, dateString);
                    fixturePst.setBoolean(6, false);
                    
                    fixturePst.addBatch();
                    

                    for (int idx = 1; idx < halfSize; idx++)
                    {               
                        int firstPlayer = (day + idx) % singlesPlayersSize;
                        int secondPlayer = (day  + singlesPlayersSize - idx) % singlesPlayersSize;
                        
                        player1IDString = singlesPlayers.get(firstPlayer).toString().replaceAll("[^0-9]+", " ").trim();
                        player1ID = Integer.valueOf(player1IDString);

                        player2IDString = singlesPlayers.get(secondPlayer).toString().replaceAll("[^0-9]+", " ").trim();
                        player2ID = Integer.valueOf(player2IDString);

                        // output the match involving other players in list for every round
                        System.out.println(singlesPlayers.get(firstPlayer) + " vs. " + singlesPlayers.get(secondPlayer));
                        
                        // push player IDs into stack to know the player IDs for each fixture
                        playerinFixtureIDStack.push(player1ID);
                        playerinFixtureIDStack.push(player2ID);
                        
                        // set parameters for each batch added
                        fixturePst.setInt(1, player1ID);
                        fixturePst.setInt(2, player2ID);
                        fixturePst.setString(3, "singles");
                        fixturePst.setString(4, "Local gym");
                        fixturePst.setString(5, dateString);
                        fixturePst.setBoolean(6, false);
                        
                        fixturePst.addBatch();
                    }
                    c.add(Calendar.DATE, breakDays);
                }
                int[] insertedRows = fixturePst.executeBatch();
                // get array of generated keys (the fixture IDs generated)
                ResultSet rs = fixturePst.getGeneratedKeys();
                Stack <Integer> fixtureIDStack = new Stack <Integer>();
                String insertTournamentHasFixture = "INSERT INTO `tournamentHasFixture`(`tournamentFound_id`, `fixtureOfTournament_id`, `roundNo`, `matchNo`) VALUES ('?','?','?','?')";    
                PreparedStatement tournamentHasFixturePst = con.prepareStatement(insertTournamentHasFixture);
             
                while (rs.next()) {
                    int generatedKey = rs.getInt(1);
                    fixtureIDStack.push(generatedKey);
                    System.out.println("Automatically generated fixture key value into tournamentHasFixture = " + generatedKey);                    
                }
                
                int incrementMatchNo = 0;
                int incrementRound = 0;
                // loop to insert into table, tournamentHasFixture
                for (int i = 0; i < fixtureIDStack.size(); i++) {
                    if ((incrementMatchNo % 2) == 0) {
                        incrementRound++;
                    }
                    System.out.println("Round " + (incrementRound) + ": " + (incrementMatchNo + 1));
                    tournamentHasFixturePst.setInt(1, tournamentID);
                    tournamentHasFixturePst.setInt(2, (fixtureIDStack.get(i)));
                    tournamentHasFixturePst.setInt(3, incrementRound + 1);
                    tournamentHasFixturePst.setInt(4, (incrementMatchNo));
                    tournamentHasFixturePst.addBatch();
                    incrementMatchNo++;
                }
                tournamentHasFixturePst.executeBatch();
                System.out.println("Inserted tournamentHasFixture");
                
                
                String insertFixtureHasPlayers = "INSERT INTO `fixtureHasPlayers`(`fixture_id`, `player_id`) VALUES ('?','?')";  
                PreparedStatement fixtureHasPlayersPst = con.prepareStatement(insertFixtureHasPlayers);
                
                // use of integer stack
                Stack <Integer> numStack = new Stack <Integer>();
                
                int nextMatch = 0;
                for (int i = 0; i < playerinFixtureIDStack.size(); i++) {
                     if (i % 2 == 0) {
                        numStack.push(i);
                    } else {
                        numStack.push(numStack.get(i - 1));
                    }

                    // the calculation iterated to get the 1, 1, 2, 2, 3, 3 etc. sequence with help of stacks
                    nextMatch = numStack.get(i) / 2 + 1;
                    
                    fixtureHasPlayersPst.setInt(1, fixtureIDStack.get(nextMatch - 1));
                    fixtureHasPlayersPst.setInt(2, playerinFixtureIDStack.get(i));
                    // set parameters and then add to batch
                    tournamentHasFixturePst.addBatch();
                    
                    System.out.println(" Next record:  FIXTURE ID: " + fixtureIDStack.get(nextMatch - 1) + " | PLAYER ID: " + playerinFixtureIDStack.get(i));
                    nextMatch++;
                }
                
                // execute the batch
                tournamentHasFixturePst.executeBatch();
                System.out.println("Inserted tournamentHasFixture");
                
                tournamentHasFixturePst.close();
                con.close();
                
            } else {
                // try and catch for the doubles round robin
                try {

                    // ?: parameter as a placeholder
                    PreparedStatement pst = con.prepareStatement(insertTournamentPoints);
                    // use of stack to get 1, 1, 2, 2, 3, 3 etc. sequence (this is required to assign the same team ID for two players to form a doubles team)
                    /**
                     * e.g. player 1 and player 2 in the tournament List will be regarded as ONE TEAM
                     * thus will both have the same team ID, 1.
                     */
                    Stack <Integer> numStack = new Stack<Integer>();
                    int teamID;

                    // loop to bind data/parameters to the prepared statement for each player in the list
                    for (int nextRow = 0; nextRow < tournamentSize; nextRow++) {
                            if (nextRow % 2 == 0) {
                                numStack.push(nextRow);
                            } else {
                                numStack.push(numStack.get(nextRow - 1));
                            }

                            // the calculation repeated to get the 1, 1, 2, 2, 3, 3 etc. sequence
                            teamID = numStack.get(nextRow) / 2 + 1;

                            // use of regex expression and trim to only retrieve the number (the ID number) from the string
                            playerIDString = listPlayers.get(nextRow).toString().replaceAll("[^0-9]+", " ").trim();
                            playerID = Integer.valueOf(playerIDString);

                            // batch processing of SQL insert statements for faster execution
                            pst.setInt(1, tournamentID);
                            pst.setInt(2, playerID);
                            pst.setInt(3, teamID);
                            pst.setInt(4, teamID); // initial positions of the players
                            pst.addBatch();

                            System.out.println("Tournament points for tournament ID: " + tournamentID + ", inserted, PLAYER NAME: " + listPlayers.get(nextRow) + ", ID: " + playerIDString + ", TEAM ID: " + (teamID));
                    }
                    pst.executeBatch();

                    pst.close();
                    con.close();
                } catch (SQLException ex) {
                    Logger.getLogger(competitionCustomise.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    // call procedure to close connection
                    SQLConnection.closeConnection(con, null, null, null);
                }

                int rounds = (tournamentSize - 1); // rounds needed to complete round-robin tournament
                int halfSize = tournamentSize / 2; // mid of tournament size

                List teams = new ArrayList(listPlayers); // Duplicate the list and remove the first team from arrayList
                teams.remove(0);
                int teamsSize = teams.size();

                DefaultListModel doublesTeams = new DefaultListModel();

                // loop to create the badminton doubles teams (join every 2 players in the list as one pair, a doubles team)
                for (int i = 0; i < halfSize; i++) {
                    doublesTeams.addElement(listPlayers.get(i*2) + ", " + listPlayers.get((i*2)+1));
                    System.out.println(doublesTeams.get(i));          
                }
                
                // set the player list of the tournament to the doubles teams created
                badmintonTournament.setPlayerList(doublesTeams);

                // loop to generate badminton doubles matches for each round
                for (int day = 0; day < rounds; day++)
                {
                    // new round
                    System.out.println("Round " + (day + 1) + " ========");

                    int teamIdx = day % teamsSize;

                    // output the match involving first team in list for every round
                    System.out.println(teams.get(teamIdx) + " vs. " + listPlayers.get(0));
                    

                    for (int idx = 1; idx < halfSize; idx++)
                    {               
                        int firstTeam = (day + idx) % teamsSize;
                        int secondTeam = (day  + teamsSize - idx) % teamsSize;

                        // output the match involving other teams in list for every round
                        System.out.println(teams.get(firstTeam) + " vs. " + teams.get(secondTeam));
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(competitionCustomise.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            // call procedure to close connection
            SQLConnection.closeConnection(con, null, null, null);
        }
    }
    
   
    
    public int retrieveTournamentID(String tournamentName) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int id = 0;
        
        String queryCheck = "SELECT * FROM `tournament` WHERE `tournamentName` = ?";
        
        
        try {
            ps = SQLConnection.getConnection().prepareStatement(queryCheck);
            ps.setString(1, tournamentName);
            
            rs = ps.executeQuery();
            
            while (rs.next())
            {
                id = rs.getInt("tournament_id");
            }
        } catch (SQLException ex) {
            Logger.getLogger(competitionCustomise.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            // call procedure to close connection
            SQLConnection.closeConnection(SQLConnection.getConnection(), rs, ps, null);
        }
        return id;
        
    }
    
    public boolean validationCheck() {
        boolean isValid = true;
        String tournamentCategory = categoryField.getText();
        String tournamentName = tournamentNameField.getText();
        String tournamentType = typeField.getText();

        String nopInput = noOfPlayersField.getText();       

        Date dateInput = dcStartedCombo.getDate();
        DateFormat dayFormat = new SimpleDateFormat("dd-MM-yyyy");
        String dateString = dayFormat.format(dateInput);
        
        Date earliestTimeInput = (Date) earliestTimeSpinner.getValue();
        Date latestTimeInput = (Date) latestTimeSpinner.getValue();
        DateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String earliestTimeString = timeFormat.format(earliestTimeInput);
        String latestTimeString = timeFormat.format(latestTimeInput);
        
        // check if fields are blank
        
        if (dateString == null || earliestTimeString == null || latestTimeString == null || nopInput.equals("") || tournamentCategory.equals("") || tournamentName.equals("") || tournamentType.equals("")) {
            isValid = false;
            displayErrorMessage("INVALID - Fields for earliest time, latest time, date started, number of players, tournament category, tournament name or tournament type is blank");
        }
        
        // check that the difference between the earliest and latest time is at least two hours
        if (isValid) {
            if (earliestTimeInput.getTime() >= latestTimeInput.getTime()) {
                isValid = false;
                displayErrorMessage("INVALID - earliest time is later or the same as latest time");
            } else {
                long timeDifference = latestTimeInput.getTime() - earliestTimeInput.getTime();
                // time difference between earliest and latest times must be at least 2 hours
                if (timeDifference >= 7200000) {
                    isValid = true;
                } else {
                    isValid = false;
                    displayErrorMessage("INVALID - time difference between earliest and latest times must be at least 2 hours");
                }
            }
        }
        
        boolean alreadyExists = checkIfTournamentNameExists(tournamentName);
        if (alreadyExists) {
            isValid = false;
            displayErrorMessage("INVALID - Duplicate tournament name (this name already exists)");
        } else {
            isValid = true;
        }
        
        return isValid;
    }
    
    public void displayErrorMessage(String errorMessage) {
       msgDlg.setMessage(errorMessage);
       msgDlg.setVisible(true);
    }
   
    /**
     * This method is called from within the constructor to initialise the form.
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
        plFixture = new javax.swing.JPanel();
        playerIcon = new javax.swing.JLabel();
        sideAP1Label = new javax.swing.JLabel();
        sideAP2Label = new javax.swing.JLabel();
        timeHelpIcon = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        tournamentIDField = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        tournamentNameField = new javax.swing.JTextField();
        typeField = new javax.swing.JTextField();
        categoryField = new javax.swing.JTextField();
        btnSelectPlayers = new javax.swing.JButton();
        sideAP2Label1 = new javax.swing.JLabel();
        noOfPlayersField = new javax.swing.JTextField();
        sideAP2Label2 = new javax.swing.JLabel();
        sideAP2Label4 = new javax.swing.JLabel();
        winnerOfTournamentFIeld = new javax.swing.JTextField();
        dcStartedCombo = new com.toedter.calendar.JDateChooser();
        sideAP2Label5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        playerJList = new javax.swing.JList<>();
        sideAP2Label3 = new javax.swing.JLabel();
        breakDaysCombo = new javax.swing.JComboBox<>();
        sideAP2Label6 = new javax.swing.JLabel();
        sideAP2Label7 = new javax.swing.JLabel();
        helpIcon1 = new javax.swing.JLabel();
        Date date = new Date();
        SpinnerDateModel sm =
        new SpinnerDateModel (date, null, null, Calendar.HOUR_OF_DAY);
        earliestTimeSpinner = new javax.swing.JSpinner(sm);
        SpinnerDateModel sm2 =
        new SpinnerDateModel (date, null, null, Calendar.HOUR_OF_DAY);
        latestTimeSpinner = new javax.swing.JSpinner(sm2);
        btnAdd = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1162, 650));
        setResizable(false);
        getContentPane().setLayout(null);

        jPanel.setBackground(new java.awt.Color(0, 51, 102));
        jPanel.setPreferredSize(new java.awt.Dimension(1162, 650));

        btnExit.setBackground(new java.awt.Color(204, 0, 0));
        btnExit.setFont(new java.awt.Font("Adobe Gothic Std", 0, 13)); // NOI18N
        btnExit.setForeground(new java.awt.Color(255, 255, 255));
        btnExit.setText("RETURN");
        btnExit.setAlignmentY(0.0F);
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });

        mpIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/systemIcons/compIcon.png"))); // NOI18N

        topBar.setBackground(new java.awt.Color(255, 51, 0));

        mainMenuLabel.setFont(new java.awt.Font("Adobe Gothic Std", 0, 18)); // NOI18N
        mainMenuLabel.setForeground(new java.awt.Color(255, 255, 255));
        mainMenuLabel.setText("CLUB BADMINTON | CUSTOMISE TOURNAMENT");

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
                .addComponent(mainMenuLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 402, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(231, 231, 231)
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

        plFixture.setBackground(new java.awt.Color(0, 51, 102));
        plFixture.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "CUSTOMISE TOURNAMENT", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 13), new java.awt.Color(255, 51, 0))); // NOI18N

        playerIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/systemIcons/compIcon.png"))); // NOI18N

        sideAP1Label.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        sideAP1Label.setForeground(new java.awt.Color(255, 255, 255));
        sideAP1Label.setText("TOURNAMENT NAME:");

        sideAP2Label.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        sideAP2Label.setForeground(new java.awt.Color(255, 255, 255));
        sideAP2Label.setText("TOURNAMENT TYPE:");

        timeHelpIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/systemIcons/helpIcon.png"))); // NOI18N
        timeHelpIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                timeHelpIconMouseClicked(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("TOURNAMENT ID:");

        tournamentIDField.setEditable(false);
        tournamentIDField.setBackground(new java.awt.Color(255, 204, 204));

        jLabel15.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText("TOURNAMENT CATEGORY:");

        tournamentNameField.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N

        typeField.setEditable(false);
        typeField.setBackground(new java.awt.Color(255, 204, 204));
        typeField.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N

        categoryField.setEditable(false);
        categoryField.setBackground(new java.awt.Color(255, 204, 204));
        categoryField.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N

        btnSelectPlayers.setBackground(new java.awt.Color(255, 51, 0));
        btnSelectPlayers.setFont(new java.awt.Font("Adobe Gothic Std", 0, 13)); // NOI18N
        btnSelectPlayers.setForeground(new java.awt.Color(255, 255, 255));
        btnSelectPlayers.setText("SELECT PLAYERS");
        btnSelectPlayers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectPlayersActionPerformed(evt);
            }
        });

        sideAP2Label1.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        sideAP2Label1.setForeground(new java.awt.Color(255, 255, 255));
        sideAP2Label1.setText("NUMBER OF PLAYERS:");

        noOfPlayersField.setEditable(false);
        noOfPlayersField.setBackground(new java.awt.Color(255, 204, 204));
        noOfPlayersField.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N

        sideAP2Label2.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        sideAP2Label2.setForeground(new java.awt.Color(255, 255, 255));
        sideAP2Label2.setText("DATE STARTED:");

        sideAP2Label4.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        sideAP2Label4.setForeground(new java.awt.Color(255, 255, 255));
        sideAP2Label4.setText("WINNER OF TOURNAMENT:");

        winnerOfTournamentFIeld.setEditable(false);
        winnerOfTournamentFIeld.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N

        dcStartedCombo.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N

        sideAP2Label5.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        sideAP2Label5.setForeground(new java.awt.Color(255, 255, 255));
        sideAP2Label5.setText("LIST OF PARTICIPANTS:");

        playerJList.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jScrollPane1.setViewportView(playerJList);

        sideAP2Label3.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        sideAP2Label3.setForeground(new java.awt.Color(255, 255, 255));
        sideAP2Label3.setText("NUMBER OF BREAK DAYS BETWEEN EACH ROUND:");

        breakDaysCombo.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        breakDaysCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14" }));

        sideAP2Label6.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        sideAP2Label6.setForeground(new java.awt.Color(255, 255, 255));
        sideAP2Label6.setText("EARLIEST TIMES:");

        sideAP2Label7.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        sideAP2Label7.setForeground(new java.awt.Color(255, 255, 255));
        sideAP2Label7.setText("LATEST TIMES:");

        helpIcon1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/systemIcons/helpIcon.png"))); // NOI18N
        helpIcon1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                helpIcon1MouseClicked(evt);
            }
        });

        JSpinner.DateEditor de = new JSpinner.DateEditor(earliestTimeSpinner, "HH:mm");
        earliestTimeSpinner.setEditor(de);
        earliestTimeSpinner.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N

        JSpinner.DateEditor de2 = new JSpinner.DateEditor(latestTimeSpinner, "HH:mm");
        latestTimeSpinner.setEditor(de2);
        latestTimeSpinner.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N

        javax.swing.GroupLayout plFixtureLayout = new javax.swing.GroupLayout(plFixture);
        plFixture.setLayout(plFixtureLayout);
        plFixtureLayout.setHorizontalGroup(
            plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, plFixtureLayout.createSequentialGroup()
                .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, plFixtureLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(plFixtureLayout.createSequentialGroup()
                                .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(plFixtureLayout.createSequentialGroup()
                                        .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel9)
                                            .addComponent(tournamentIDField, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(92, 92, 92)
                                        .addComponent(sideAP2Label, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(sideAP2Label5, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(playerIcon, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(typeField))
                            .addGroup(plFixtureLayout.createSequentialGroup()
                                .addComponent(jScrollPane1)
                                .addGap(12, 12, 12)
                                .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(plFixtureLayout.createSequentialGroup()
                                        .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel15)
                                            .addComponent(categoryField, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(18, 18, 18)
                                        .addComponent(btnSelectPlayers, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(9, 9, 9))
                                    .addGroup(plFixtureLayout.createSequentialGroup()
                                        .addComponent(sideAP2Label4)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(winnerOfTournamentFIeld, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(plFixtureLayout.createSequentialGroup()
                                        .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(sideAP2Label2)
                                            .addComponent(sideAP2Label1)
                                            .addGroup(plFixtureLayout.createSequentialGroup()
                                                .addComponent(timeHelpIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(41, 41, 41)
                                                .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                    .addComponent(sideAP2Label7)
                                                    .addComponent(sideAP2Label6))))
                                        .addGap(23, 23, 23)
                                        .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(noOfPlayersField, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(dcStartedCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(earliestTimeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(latestTimeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(plFixtureLayout.createSequentialGroup()
                                        .addComponent(sideAP2Label3, javax.swing.GroupLayout.PREFERRED_SIZE, 335, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(breakDaysCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, plFixtureLayout.createSequentialGroup()
                        .addGap(280, 280, 280)
                        .addComponent(sideAP1Label)
                        .addGap(18, 18, 18)
                        .addComponent(tournamentNameField)))
                .addGap(37, 37, 37))
            .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(plFixtureLayout.createSequentialGroup()
                    .addGap(290, 290, 290)
                    .addComponent(helpIcon1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(484, Short.MAX_VALUE)))
        );
        plFixtureLayout.setVerticalGroup(
            plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, plFixtureLayout.createSequentialGroup()
                .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(plFixtureLayout.createSequentialGroup()
                        .addComponent(playerIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tournamentIDField, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(plFixtureLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(categoryField, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnSelectPlayers))
                        .addGap(35, 35, 35)
                        .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(sideAP1Label)
                            .addComponent(tournamentNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(sideAP2Label)
                            .addComponent(typeField, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sideAP2Label5)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(noOfPlayersField, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(sideAP2Label1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(plFixtureLayout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addContainerGap())
                    .addGroup(plFixtureLayout.createSequentialGroup()
                        .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dcStartedCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(plFixtureLayout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(sideAP2Label2)))
                        .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(plFixtureLayout.createSequentialGroup()
                                .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(plFixtureLayout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addComponent(sideAP2Label6))
                                    .addGroup(plFixtureLayout.createSequentialGroup()
                                        .addGap(12, 12, 12)
                                        .addComponent(earliestTimeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(latestTimeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(sideAP2Label7)))
                            .addGroup(plFixtureLayout.createSequentialGroup()
                                .addGap(50, 50, 50)
                                .addComponent(timeHelpIcon)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 47, Short.MAX_VALUE)
                        .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(sideAP2Label4)
                            .addComponent(winnerOfTournamentFIeld, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(20, 20, 20)
                        .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(sideAP2Label3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(breakDaysCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18))))
            .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(plFixtureLayout.createSequentialGroup()
                    .addGap(39, 39, 39)
                    .addComponent(helpIcon1)
                    .addContainerGap(441, Short.MAX_VALUE)))
        );

        btnAdd.setBackground(new java.awt.Color(255, 51, 0));
        btnAdd.setFont(new java.awt.Font("Adobe Gothic Std", 0, 13)); // NOI18N
        btnAdd.setForeground(new java.awt.Color(255, 255, 255));
        btnAdd.setText("ADD TOURNAMENT");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        btnDelete.setBackground(new java.awt.Color(255, 51, 0));
        btnDelete.setFont(new java.awt.Font("Adobe Gothic Std", 0, 13)); // NOI18N
        btnDelete.setForeground(new java.awt.Color(255, 255, 255));
        btnDelete.setText("DELETE TOURNAMENT");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
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
                .addComponent(plFixture, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnDelete)
                    .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        jPanelLayout.setVerticalGroup(
            jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLayout.createSequentialGroup()
                .addComponent(topBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelLayout.createSequentialGroup()
                        .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelLayout.createSequentialGroup()
                                .addGap(8, 8, 8)
                                .addComponent(mpIcon))
                            .addComponent(btnExit))
                        .addGap(13, 13, 13)
                        .addComponent(btnAdd)
                        .addGap(18, 18, 18)
                        .addComponent(btnDelete))
                    .addComponent(plFixture, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(38, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel);
        jPanel.setBounds(0, 6, 1160, 640);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        // quits form
        quitWindow();
    }//GEN-LAST:event_btnExitActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        // confirmation to add tournament
        msgDlg.setMessage("Confirm Add Tournament");
        msgDlg.setVisible(true);
        int confirmDelete = msgDlg.getReturnStatus();
        if (confirmDelete == 1 ) {
            boolean validCheck = validationCheck();
            
            String tournamentCategory = categoryField.getText();
            String tournamentName = tournamentNameField.getText();
            String tournamentType = typeField.getText();
            
            String nopInput = noOfPlayersField.getText();
            int noOfPlayers = Integer.valueOf(nopInput);
            
            int breakDays = Integer.valueOf(breakDaysCombo.getSelectedItem().toString());           
            
            Date dateInput = dcStartedCombo.getDate();
            DateFormat dayFormat = new SimpleDateFormat("dd-MM-yyyy");
            String dateString = dayFormat.format(dateInput);
                     
            /*          
            Calendar c = Calendar.getInstance();
            c.setTime(dateInput);
            c.add(Calendar.DATE, breakDays);*/
            
            // execute query to find the maximum tournament ID to then use to increment for the new tournament record about to be inserted
            // includes the aggregate SQL function, MAX()
            String findMaxIDquery = "SELECT tournament_id from `tournament` WHERE tournament_id = (SELECT MAX(tournament_id) FROM badmintonSchema.tournament)";
            
            PreparedStatement ps = null;
            ResultSet rs = null;
            // tournament ID of last record
            int IDOfLastRecord = 0;
            try {
                ps = SQLConnection.getConnection().prepareStatement(findMaxIDquery);

                rs = ps.executeQuery();

                while (rs.next())
                {
                    IDOfLastRecord = rs.getInt("tournament_id");
                }
            // exception handling of SQL errors
            } catch (SQLException ex) {
                Logger.getLogger(competitionCustomise.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                // call procedure to close connection
                SQLConnection.closeConnection(SQLConnection.getConnection(), rs, ps, null);
            }
            
            
            if (validCheck) {
                // only allow the insert query if the ID was not 0
                if (IDOfLastRecord != 0) {
                    IDOfLastRecord = IDOfLastRecord + 1;
                    String newTournament = "INSERT INTO `tournament`(`tournament_id`,`tournamentName`, `tournamentCategory`, `tournamentType`, `noOfPlayers`, `tournamentCompleted`, `dateStarted`) VALUES ('"+IDOfLastRecord+"','"+tournamentName+"','"+tournamentCategory+"','"+tournamentType+"','"+noOfPlayers+"','"+ (0) +"','"+dateString+"')";
                    executeSQLQuery(newTournament,"Inserted"); 
                    // check which type of tournament it is to determine the schedule
                    if (tournamentType.equals("round-robin")) {
                        // call procedure to create round robin schedule of the tournament
                        createRoundRobinSchedule(tournamentName);
                    }
                    quitWindow();  
                }
            }
        }       
    }//GEN-LAST:event_btnAddActionPerformed

    
 
    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        String deleteQuery = "DELETE FROM `tournament` WHERE tournament_id = " + badmintonTournament.getSelectedTournamentID();  
        
        // confirmation to delete the selected tournament ?
        msgDlg.setMessage("Confirm Delete Tournament");
        msgDlg.setVisible(true);
        int confirmDelete = msgDlg.getReturnStatus();
        if (confirmDelete == 1 ) {    
            if (SessionManager.getUserRole().equals("admin")) {
                executeSQLQuery(deleteQuery,"Deleted");
                quitWindow();
            } else {
                msgDlg.setMessage("Must be admin to delete a tournament");
                msgDlg.setVisible(true);
            }
        }
        
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void timeHelpIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_timeHelpIconMouseClicked
        // help guide
        msgDlg.setMessage(" Pick the earliest time and latest time you want matches to be scheduled for, ensure that there is at least 2 hours between the two times");
        msgDlg.setVisible(true);
    }//GEN-LAST:event_timeHelpIconMouseClicked

    private void btnSelectPlayersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectPlayersActionPerformed
        String tournamentName = tournamentNameField.getText();
        String typeOfTournament = typeField.getText();
        String categoryOfTournament = categoryField.getText();
        
        chooseTournamentType ctt = new chooseTournamentType(tournamentName, typeOfTournament, categoryOfTournament);
        ctt.setVisible(true);
        ctt.pack();
        ctt.setLocationRelativeTo(null);
        ctt.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.dispose();
    }//GEN-LAST:event_btnSelectPlayersActionPerformed

    private void helpIcon1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_helpIcon1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_helpIcon1MouseClicked

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
            java.util.logging.Logger.getLogger(competitionCustomise.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(competitionCustomise.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(competitionCustomise.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(competitionCustomise.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new competitionCustomise().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel LogLabel;
    private javax.swing.JComboBox<String> breakDaysCombo;
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnDelete;
    private javax.swing.JToggleButton btnExit;
    private javax.swing.JButton btnSelectPlayers;
    private javax.swing.JTextField categoryField;
    private com.toedter.calendar.JDateChooser dcStartedCombo;
    private javax.swing.JSpinner earliestTimeSpinner;
    private javax.swing.JLabel helpIcon1;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSpinner latestTimeSpinner;
    private javax.swing.JLabel mainMenuLabel;
    private javax.swing.JLabel mpIcon;
    private javax.swing.JTextField noOfPlayersField;
    private javax.swing.JPanel plFixture;
    private javax.swing.JLabel playerIcon;
    private javax.swing.JList<String> playerJList;
    private javax.swing.JLabel sideAP1Label;
    private javax.swing.JLabel sideAP2Label;
    private javax.swing.JLabel sideAP2Label1;
    private javax.swing.JLabel sideAP2Label2;
    private javax.swing.JLabel sideAP2Label3;
    private javax.swing.JLabel sideAP2Label4;
    private javax.swing.JLabel sideAP2Label5;
    private javax.swing.JLabel sideAP2Label6;
    private javax.swing.JLabel sideAP2Label7;
    private javax.swing.JLabel timeHelpIcon;
    private javax.swing.JPanel topBar;
    private javax.swing.JTextField tournamentIDField;
    private javax.swing.JTextField tournamentNameField;
    private javax.swing.JTextField typeField;
    public javax.swing.JLabel userLoggedOn;
    private javax.swing.JTextField winnerOfTournamentFIeld;
    // End of variables declaration//GEN-END:variables
}
