/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
// OBJECTIVE 8: A means of adding/editing match results 
package badmintonSystem;

// required imports
import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;

/**
 *
 * @author Neka
 */
public class fixtureCustomise extends javax.swing.JFrame {
    
    // message dialog
    msgDialog msgDlg = new msgDialog(new javax.swing.JFrame(),true);
   
    // global variables
    int selectedID = 0;
    int deletedID = 0;
    String fixtureAction = null;
    String matchStatus = null;
    
    // arrays of items used for comparison if a MATCH UPDATE is being made
    String[] oldItems = new String[12];
    String[] newItems = new String[12];

    /**
     * Creates new form mpWindow
     */
    
    public fixtureCustomise() {
        initComponents();
    }
    
    public fixtureCustomise(String actionChosen, String match) {
        matchStatus = match;
        if (match.equals("fixture")) {
            selectedID = badmintonMatch.getSelectedFixtureID();
        } else {
            selectedID = badmintonMatch.getSelectedResultID();
        }
        initComponents();
        recalculateCommentsLength();
        
        if (actionChosen.equals("UPDATE/DELETE")) {
            // disable Add player if the user is planning to update or delete the player
            btnAdd.setVisible(false);
            btnAdd.setEnabled(false);
            
            fixtureAction = actionChosen;
            
            // call subroutine to show details of selected player;
            showSelectedFixtureDetails();    
            
            // fields and comboBoxes to enable
            dcCombo.setEnabled(true);
            commentsField.setEditable(true);   
        } else if (actionChosen.equals("VIEW")) {
            // typeOfAction is VIEW so...
            // disable Add / Delete / Update player if the user is planning to view the selected player
            btnAdd.setVisible(false);
            btnAdd.setEnabled(false);   
            btnDelete.setVisible(false);
            btnDelete.setEnabled(false);
            btnUpdate.setVisible(false);
            btnUpdate.setEnabled(false);   
            
            fixtureAction = actionChosen;
            // fields and comboBoxes to disable
            dcCombo.setEnabled(false);
            commentsField.setEditable(false);
            showSelectedFixtureDetails();
            
            // fields or comboBoxes to set as uneditable           
        } else {
            // typeOfAction is INSERT so...
            // disable Delete / Update player if the user is planning to add a new player
            btnDelete.setVisible(false);
            btnDelete.setEnabled(false);
            
            fixtureAction = actionChosen;
            
            btnUpdate.setVisible(false);
            btnUpdate.setEnabled(false);  
            setSelectedDetails(null, null, null, null, null);
        }
    }
    
     //  validation methods
    
   
   public void recalculateCommentsLength() {
       // recalculates the comments length and sets the comments field as the value
       int currentCharacterCount = commentsField.getText().length();
       ccLabel.setText(currentCharacterCount + "/250");
       // change the colour of the text to red if the character count exceeds the limit of 250
       if (currentCharacterCount > 250) {
           ccLabel.setForeground(Color.red);
       } else {
           ccLabel.setForeground(Color.white);
       }
   }
   
   // sets the textfields of the form based on the user's current choices after re-opening this form
  public void setSelectedDetails(String typeOfFixture, String sideAPlayer1, String sideAPlayer2, String sideBPlayer1, String sideBPlayer2) {
    if (choosePlayerList.getReturnStatus() == 1) {
       typeField.setText(choosePlayerList.getSelectedType());
       sideAP1Field.setText(choosePlayerList.getSelectedAP1());
       sideAP2Field.setText(choosePlayerList.getSelectedAP2());
       sideBP1Field.setText(choosePlayerList.getSelectedBP1());
       sideBP2Field.setText(choosePlayerList.getSelectedBP2()); 
    } else {
       typeField.setText(typeOfFixture);
       sideAP1Field.setText(sideAPlayer1);
       sideAP2Field.setText(sideAPlayer2);
       sideBP1Field.setText(sideBPlayer1);
       sideBP2Field.setText(sideBPlayer2); 
    }
      
  }


   // sets the textfields of the form based on the details of the selected tournament
    public void showSelectedFixtureDetails() {
        // SEARCH FOR FIELDS IN SELECTED FIXTURE IN FIXTURE TABLE
        
        int fixtureID = 0;
        int tournamentID = 0;
        
        String sideAPlayer1 = null;
        String sideAPlayer2 = null;
        String sideBPlayer1 = null;
        String sideBPlayer2 = null;

        String sideAScore = null;
        String sideBScore = null;
        
        String typeOfFixture = null;
        String venueOfFixture = "";
        int courtNo = 0;
        
        String dateOfFixture = null;
        String timeOfFixture = null;
        
        String comments = "";
        boolean fixtureCompleted = false;
        
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String queryA = "SELECT * FROM fixture WHERE fixture_id = ?";

            ps = SQLConnection.getConnection().prepareStatement(queryA);

            ps.setInt(1, selectedID);           
            rs = ps.executeQuery();

            while (rs.next()) {
                fixtureID = rs.getInt("fixture_id");
                sideAPlayer1 = rs.getString("sideAPlayer1");
                sideAPlayer2 = rs.getString("sideAPlayer2");
                sideBPlayer1 = rs.getString("sideBPlayer1");
                sideBPlayer2 = rs.getString("sideBPlayer2");
                sideAScore = rs.getString("sideAScore");
                sideBScore = rs.getString("sideBScore");
                typeOfFixture = rs.getString("typeOfFixture");
                venueOfFixture = rs.getString("venueOfFixture");
                courtNo = rs.getInt("courtNo");
                dateOfFixture = rs.getString("dateOfFixture");
                timeOfFixture = rs.getString("timeOfFixture");
                comments = rs.getString("comments");
                fixtureCompleted = rs.getBoolean("fixtureCompleted");
                tournamentID = rs.getInt("tournamentOfFixture_id");

            }
            
            if (fixtureCompleted) {
                matchStatus = "result";
            }

            // convert integer variables to string to display on fields
            String IDShown = Integer.toString(selectedID);
            String courtNoString = Integer.toString(courtNo);
            
            // SET FIELDS TO SELECTED RECORD
            IDField.setText(IDShown);
            
            setSelectedDetails(typeOfFixture, sideAPlayer1, sideAPlayer2, sideBPlayer1, sideBPlayer2);
            
            
            if ((sideAScore != null) && (sideBScore != null)) {
                
                String sideAGamePoints[] = sideAScore.split(",");
                sideAG1Field.setSelectedItem(sideAGamePoints[0]);
                sideAG2Field.setSelectedItem(sideAGamePoints[1]);
                sideAG3Field.setSelectedItem(sideAGamePoints[2]);

                String sideBGamePoints[] = sideBScore.split(",");
                sideBG1Field.setSelectedItem(sideBGamePoints[0]);
                sideBG2Field.setSelectedItem(sideBGamePoints[1]);
                sideBG3Field.setSelectedItem(sideBGamePoints[2]);
            }   
            
            venueField.setText(venueOfFixture);
            courtNoField.setText(courtNoString);
            // CONVERT FROM STRING TO JAVA.UTIL.DATE TO SHOW ON DCCOMBO
            if (dateOfFixture != null ) {
                try {
                    Date convertedDate = new SimpleDateFormat("dd-MM-yyyy").parse(dateOfFixture);
                    dcCombo.setDate(convertedDate);
                } catch (ParseException ex) {
                    Logger.getLogger(fixtureCustomise.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            // CONVERT FROM STRING TO JAVA.UTIL.DATE TO SHOW ON TIME SPINNER
            if (timeOfFixture != null) {
                DateFormat tsSDF = new SimpleDateFormat("hh:mm");
                try {
                    Date convertedTime = tsSDF.parse(timeOfFixture);
                    Object timeValue = convertedTime;
                    timeSpinner.setValue(timeValue); 
                } catch (ParseException ex) {
                    Logger.getLogger(fixtureCustomise.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            commentsField.setText(comments);

            getOldItems(sideAPlayer1,sideAPlayer2,sideBPlayer1,sideBPlayer2,sideAScore,sideBScore,typeOfFixture,venueOfFixture,courtNoString,dateOfFixture,timeOfFixture,comments);


        } catch (SQLException ex) {
            Logger.getLogger(fixtureCustomise.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            // call procedure to close the connection  
            SQLConnection.closeConnection(SQLConnection.getConnection(), rs, ps, null);  
        }
    }
    
    // procedure to display the error message on message dialog
    public void displayErrorMessage(String errorMessage) {
       msgDlg.setMessage(errorMessage);
       msgDlg.setVisible(true);
   }
    
    // a validation function to check if the inputs are valid
    public boolean validationCheck() {
        boolean isValid = true;
        String courtNoInput = courtNoField.getText();
        String venueInput = venueField.getText();
        
        // simpleDateFormats to format the date and time into a string
        DateFormat dayFormat = new SimpleDateFormat("dd-MM-yyyy");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm");
        
        String dateString = null;
        String timeString = null;
        
        Date dateInput = dcCombo.getDate();
        Date timeInput = (Date) timeSpinner.getValue();
        
        if (dateInput != null) {
            dateString = dayFormat.format(dateInput);
        }
        if (timeInput != null) {
            timeString = timeFormat.format(timeInput);
        }
        
        
        // check if fields are blank
        
        if (timeString == null || dateString == null || courtNoInput.equals("") || venueInput.equals("")) {
            isValid = false;
            displayErrorMessage("INVALID - Fields for time, date, court No or venue is blank");
        }
        
        // check if comments character count is within the limit
        if (commentsField.getText().length() > 250) {
            isValid = false;
            displayErrorMessage("INVALID - Character count of comments exceeded limit (250 characters)");
        }
        // check if the type of fixture field is blank
        if (typeField.getText().equals("")) {
            isValid = false;
            displayErrorMessage("INVALID - Type of fixture field blank");
        } else {
            if (typeField.getText().equals("singles")) {
                // check if there the first player of each team is not blank and that the other player fields are blank
                if (sideAP1Field.getText().equals("") || sideBP1Field.getText().equals("") || !(sideAP2Field.getText().equals("")) || !(sideBP2Field.getText().equals(""))) {
                    isValid = false;
                    displayErrorMessage("INVALID - One or more of the fields for the players is blank/ OR two players of a side found present for a SINGLES match");
                }
            } else {
                // check if any of the player fields are blank
                if (sideAP1Field.getText().equals("") || sideBP1Field.getText().equals("") || sideAP2Field.getText().equals("") || sideBP2Field.getText().equals("")) {
                    isValid = false;
                    displayErrorMessage("INVALID - One or more of the fields for the players is blank");
                }
            }
        }
        
        if (sideAG1Field.getSelectedItem().toString().equals("") || sideAG2Field.getSelectedItem().toString().equals("") || sideAG3Field.getSelectedItem().toString().equals("") || sideBG1Field.getSelectedItem().toString().equals("") || sideBG2Field.getSelectedItem().toString().equals("") || sideBG3Field.getSelectedItem().toString().equals("") ) {
            matchStatus = "fixture";
        } else {
            matchStatus = "result";
            // validate game score
            int sideAGP = 0;
            int sideBGP = 0;
            
            // sort the scores for each game from each side as integer arrays
            int sideAGamePoints[] = {Integer.valueOf(sideAG1Field.getSelectedItem().toString()), Integer.valueOf(sideAG2Field.getSelectedItem().toString()), Integer.valueOf(sideAG3Field.getSelectedItem().toString())};
            int sideBGamePoints[] = {Integer.valueOf(sideBG1Field.getSelectedItem().toString()), Integer.valueOf(sideBG2Field.getSelectedItem().toString()), Integer.valueOf(sideBG3Field.getSelectedItem().toString())};
            
            /** loop to check if the format for the score of the badminton game is correct
             * a match score can either finish as SIDE A: 21-21-0     vs. SIDE B: X-X-0
             * SIDE A: 21-X-21     vs. SIDE B: X-21-X
             * SIDE A: X-21-X     vs. SIDE B: 21-X-21
             * SIDE A: X-X-0     vs. SIDE B: 21-21-0
             * where X is a number between 1-20 
             */
            for (int i = 0; i < sideAGamePoints.length; i++) {
                // if side A has more points than side B in a game
                if (sideAGamePoints[i] > sideBGamePoints[i]) {
                    sideAGP = sideAGP + 1;
                    if (sideAGamePoints[i] != 21) {
                        isValid = false;
                        displayErrorMessage("INVALID - The number must be '21' points when indicating which side has won a game in the match");
                    }
                // if side B has more points than side a in a game
                } else if (sideBGamePoints[i] > sideAGamePoints[i]) {
                    sideBGP = sideBGP + 1;
                    if (sideBGamePoints[i] != 21) {
                        isValid = false;
                        displayErrorMessage("INVALID - The number must be '21' points when indicating which side has won a game in the match");
                    }
                } else {
                // if both sides have the same points in a game (this can only be the case when its 0-0 at game 3)
                    if (((sideAGP == 2) && (sideBGP == 0)) || ((sideBGP == 2) && (sideAGP == 0))) {
                        isValid = true;
                    } else {
                        isValid = false;
                        displayErrorMessage("INVALID - Both sides cannot have the same score of the same fixture unless it is 0-0 for Game 3 and a player has won the first two games ");
                    }
                }
                System.out.println("GAME " + (i+1));
                System.out.println("Side A game points: " + sideAGP);
                System.out.println("Side B game points: " + sideBGP);
            }
        }
        return isValid;
    }
    
    // use of an array to retrieve old items
    public void getOldItems(String sideAPlayer1, String sideAPlayer2, String sideBPlayer1, String sideBPlayer2, String sideAScore, String sideBScore, String typeOfFixture, String venueOfFixture, String courtNo, String dateOfFixture, String timeOfFixture, String comments) {
        oldItems[0] = sideAPlayer1;
        oldItems[1] = sideAPlayer2;
        oldItems[2] = sideBPlayer1;
        oldItems[3] = sideBPlayer2;
        oldItems[4] = sideAScore;
        oldItems[5] = sideBScore;
        oldItems[6] = typeOfFixture;
        oldItems[7] = venueOfFixture;
        oldItems[8] = courtNo;
        oldItems[9] = dateOfFixture;
        oldItems[10] = timeOfFixture;
        oldItems[11] = comments;
    
    }
    
    // use of array to retieve new items if an UPDATE has been made
    public void getNewItems(String sideAPlayer1, String sideAPlayer2, String sideBPlayer1, String sideBPlayer2, String sideAScore, String sideBScore, String typeOfFixture, String venueOfFixture, String courtNo, String dateOfFixture, String timeOfFixture, String comments) {
        newItems[0] = sideAPlayer1;
        newItems[1] = sideAPlayer2;
        newItems[2] = sideBPlayer1;
        newItems[3] = sideBPlayer2;
        newItems[4] = sideAScore;
        newItems[5] = sideBScore;
        newItems[6] = typeOfFixture;
        newItems[7] = venueOfFixture;
        newItems[8] = courtNo;
        newItems[9] = dateOfFixture;
        newItems[10] = timeOfFixture;
        newItems[11] = comments;
    }
    
    // update the audit log 
    public void updateAuditLog(String message) {
        // SET TIMESTAMP FOR SQL QUERY
        Date date = new Date();
        long time = date.getTime();
        Timestamp updateTimestamp = new Timestamp(time);
        
        String userWhoChanged = SessionManager.getUsername();
        int userWhoChangedID = SessionManager.getUserID();
        String change = (message + " match");
        
        Connection con = SQLConnection.getConnection();
        Statement st = null;
        
        if (message.equals("Updated")) { // updated MATCH   
            String itemAffected = "";
            String itemChangedTo = "";

            for (int i = 0; i < oldItems.length; i++) {
                // CHECKS IF EACH FIELD WAS CHANGED
                // ADD NEW LOG IF A FIELD WAS CHANGED
                if (!oldItems[i].equals(newItems[i])) {
                    itemAffected = oldItems[i];
                    itemChangedTo = newItems[i];

                    // update change table in mySQL database
                    try {
                        st = con.createStatement();
                        if ((st.executeUpdate("INSERT INTO `change`(`change`, `timeOfChange`, `userWhoChanged`, `itemAffected`, `itemChangedTo`, `user_id`) VALUES ('"+change+"','"+updateTimestamp+"','"+userWhoChanged+"','"+itemAffected+ "','" + itemChangedTo + "','" + userWhoChangedID +"')")) == 1)
                        {
                            System.out.println("AUDIT LOG UPDATED");    
                        } else {
                            System.out.println("Failed to update into audit log.");
                        }           
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    } finally {
                        // call procedure to close the connection  
                        SQLConnection.closeConnection(con, null , null, st);  
                    }
                }
            }
        } else if (message.equals("Inserted")) { // inserted match      
            try {
                st = con.createStatement();
                if ((st.executeUpdate("INSERT INTO `change`(`change`, `timeOfChange`, `userWhoChanged`, `user_id`) VALUES ('"+change+"','"+updateTimestamp+"','"+userWhoChanged+"','" + userWhoChangedID +"')")) == 1)
                {
                    System.out.println("AUDIT LOG UPDATED");    
                } else {
                    System.out.println("Failed to update into audit log.");
                }           
            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                // call procedure to close the connection  
                SQLConnection.closeConnection(con, null , null, st);  
            }           
        } else  { // deleted match
            try {
                st = con.createStatement();
                if ((st.executeUpdate("INSERT INTO `change`(`change`, `timeOfChange`, `userWhoChanged`, `itemAffected`, `user_id`) VALUES ('"+change+"','"+updateTimestamp+"','"+userWhoChanged+"','" + "deleted fixture was ID: "+deletedID+"','" + userWhoChangedID + "')")) == 1)
                {
                    System.out.println("AUDIT LOG UPDATED");    
                } else {
                    System.out.println("Failed to update into audit log.");
                }           
            } catch (SQLException ex) {
                ex.printStackTrace();
            }  finally {
                // call procedure to close the connection  
                SQLConnection.closeConnection(con, null , null, st);  
            }
        }
    }
    
    // execute the SQL query
    public int executeSQLQuery(String query, String message) {
        Connection con = SQLConnection.getConnection();
        Statement st = null;
        int idGenerated = 0;
        try {
            st = con.createStatement();
            // statement with the setting, return generated keys.
            if ((st.executeUpdate(query, Statement.RETURN_GENERATED_KEYS)) == 1)
            {
                msgDlg.setMessage("Data " + message + " successfully");
                msgDlg.setVisible(true);
                // update audit log
                updateAuditLog(message);   
                
                ResultSet rs = st.getGeneratedKeys();
                if (rs.next()) {
                    idGenerated = rs.getInt(1);
                    System.out.println("Generated fixture ID: " + idGenerated);
                }
            } else {
                msgDlg.setMessage("Data not " + message);
                msgDlg.setVisible(true);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            // call procedure to close the connection  
            SQLConnection.closeConnection(con, null , null, st);  
        }
        return idGenerated;     
        
    }
    
    public void quitWindow() {
        // opens main fixtures window
        fixturesWindow fw = new fixturesWindow();
        fw.setVisible(true);
        fw.pack();
        fw.setLocationRelativeTo(null);
        fw.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.dispose();
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
        plFixture = new javax.swing.JPanel();
        playerIcon = new javax.swing.JLabel();
        sideAP1Label = new javax.swing.JLabel();
        sideAP2Label = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        dcCombo = new com.toedter.calendar.JDateChooser();
        helpIcon = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        IDField = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        tournamentIDField = new javax.swing.JTextField();
        sideBP1Label = new javax.swing.JLabel();
        sideBP2Label = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        venueField = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        courtNoField = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        Date date = new Date();
        SpinnerDateModel sm =
        new SpinnerDateModel (date, null, null, Calendar.HOUR_OF_DAY);
        timeSpinner = new javax.swing.JSpinner(sm);
        ccTitleLabel = new javax.swing.JLabel();
        ccLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        commentsField = new javax.swing.JTextArea();
        sideAP1Field = new javax.swing.JTextField();
        sideAP2Field = new javax.swing.JTextField();
        sideBP1Field = new javax.swing.JTextField();
        sideBP2Field = new javax.swing.JTextField();
        typeField = new javax.swing.JTextField();
        btnSelectPlayers = new javax.swing.JButton();
        scoreHelpIcon = new javax.swing.JLabel();
        sideAG1Field = new javax.swing.JComboBox<>();
        sideAG2Field = new javax.swing.JComboBox<>();
        sideAG3Field = new javax.swing.JComboBox<>();
        sideBG1Field = new javax.swing.JComboBox<>();
        sideBG2Field = new javax.swing.JComboBox<>();
        sideBG3Field = new javax.swing.JComboBox<>();
        btnAdd = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1047, 797));
        setResizable(false);
        getContentPane().setLayout(null);

        jPanel.setBackground(new java.awt.Color(0, 51, 102));
        jPanel.setPreferredSize(new java.awt.Dimension(1070, 790));

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

        mpIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/systemIcons/fixtureIcon.png"))); // NOI18N

        topBar.setBackground(new java.awt.Color(255, 51, 0));

        mainMenuLabel.setFont(new java.awt.Font("Adobe Gothic Std", 0, 18)); // NOI18N
        mainMenuLabel.setForeground(new java.awt.Color(255, 255, 255));
        mainMenuLabel.setText("CLUB BADMINTON | CUSTOMISE FIXTURE");

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

        plFixture.setBackground(new java.awt.Color(0, 51, 102));
        plFixture.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "CUSTOMISE FIXTURE", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 13), new java.awt.Color(255, 51, 0))); // NOI18N

        playerIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/systemIcons/fixtureIcon.png"))); // NOI18N

        sideAP1Label.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        sideAP1Label.setForeground(new java.awt.Color(255, 255, 255));
        sideAP1Label.setText("PLAYER 1*:");

        sideAP2Label.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        sideAP2Label.setForeground(new java.awt.Color(255, 255, 255));
        sideAP2Label.setText("PLAYER 2:");

        jLabel4.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("COMMENTS:");

        jLabel7.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("SIDE A SCORE");

        dcCombo.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N

        helpIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/systemIcons/helpIcon.png"))); // NOI18N
        helpIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                helpIconMouseClicked(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("FIXTURE ID:");

        IDField.setEditable(false);
        IDField.setBackground(new java.awt.Color(255, 204, 204));

        jLabel9.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("TOURNAMENT ID:");

        tournamentIDField.setEditable(false);
        tournamentIDField.setBackground(new java.awt.Color(255, 204, 204));

        sideBP1Label.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        sideBP1Label.setForeground(new java.awt.Color(255, 255, 255));
        sideBP1Label.setText("PLAYER 1*:");

        sideBP2Label.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        sideBP2Label.setForeground(new java.awt.Color(255, 255, 255));
        sideBP2Label.setText("PLAYER 2:");

        jLabel13.setFont(new java.awt.Font("Arial Black", 0, 13)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("SIDE B:");

        jLabel14.setFont(new java.awt.Font("Arial Black", 0, 13)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("SIDE A:");

        jLabel15.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText("TYPE OF FIXTURE*:");

        jLabel10.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("GAME 1:");

        jLabel16.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setText("GAME 2:");

        jLabel17.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setText("GAME 3:");

        jLabel18.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setText("GAME 1:");

        jLabel19.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setText("GAME 3:");

        jLabel20.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setText("GAME 2:");

        jLabel21.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setText("SIDE B SCORE");

        jLabel22.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(255, 255, 255));
        jLabel22.setText("FIXTURE DETAILS");

        jLabel5.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("VENUE*:");

        venueField.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N

        jLabel23.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(255, 255, 255));
        jLabel23.setText("COURT NO*:");

        courtNoField.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N

        jLabel24.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(255, 255, 255));
        jLabel24.setText("DATE*:");

        jLabel25.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(255, 255, 255));
        jLabel25.setText("TIME*:");

        JSpinner.DateEditor de = new JSpinner.DateEditor(timeSpinner, "HH:mm");
        timeSpinner.setEditor(de);
        timeSpinner.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N

        ccTitleLabel.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        ccTitleLabel.setForeground(new java.awt.Color(255, 255, 255));
        ccTitleLabel.setText("CHARACTER COUNT:");

        ccLabel.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        ccLabel.setForeground(new java.awt.Color(255, 255, 255));
        ccLabel.setText("0/250");

        commentsField.setColumns(20);
        commentsField.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        commentsField.setLineWrap(true);
        commentsField.setRows(5);
        commentsField.setMaximumSize(new java.awt.Dimension(220, 80));
        commentsField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                commentsFieldKeyTyped(evt);
            }
        });
        jScrollPane1.setViewportView(commentsField);

        sideAP1Field.setEditable(false);
        sideAP1Field.setBackground(new java.awt.Color(255, 204, 204));
        sideAP1Field.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N

        sideAP2Field.setEditable(false);
        sideAP2Field.setBackground(new java.awt.Color(255, 204, 204));
        sideAP2Field.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N

        sideBP1Field.setEditable(false);
        sideBP1Field.setBackground(new java.awt.Color(255, 204, 204));
        sideBP1Field.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N

        sideBP2Field.setEditable(false);
        sideBP2Field.setBackground(new java.awt.Color(255, 204, 204));
        sideBP2Field.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N

        typeField.setEditable(false);
        typeField.setBackground(new java.awt.Color(255, 204, 204));
        typeField.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N

        btnSelectPlayers.setBackground(new java.awt.Color(255, 51, 0));
        btnSelectPlayers.setFont(new java.awt.Font("Adobe Gothic Std", 0, 13)); // NOI18N
        btnSelectPlayers.setForeground(new java.awt.Color(255, 255, 255));
        btnSelectPlayers.setText("SELECT PLAYERS");
        btnSelectPlayers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectPlayersActionPerformed(evt);
            }
        });

        scoreHelpIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/systemIcons/helpIcon.png"))); // NOI18N
        scoreHelpIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                scoreHelpIconMouseClicked(evt);
            }
        });

        sideAG1Field.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        sideAG1Field.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " ", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21" }));

        sideAG2Field.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        sideAG2Field.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " ", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21" }));

        sideAG3Field.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        sideAG3Field.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " ", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21" }));

        sideBG1Field.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        sideBG1Field.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " ", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21" }));

        sideBG2Field.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        sideBG2Field.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " ", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21" }));

        sideBG3Field.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        sideBG3Field.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " ", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21" }));

        javax.swing.GroupLayout plFixtureLayout = new javax.swing.GroupLayout(plFixture);
        plFixture.setLayout(plFixtureLayout);
        plFixtureLayout.setHorizontalGroup(
            plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(plFixtureLayout.createSequentialGroup()
                .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(plFixtureLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(plFixtureLayout.createSequentialGroup()
                                .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel22)
                                    .addGroup(plFixtureLayout.createSequentialGroup()
                                        .addComponent(jLabel23)
                                        .addGap(18, 18, 18)
                                        .addComponent(courtNoField, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, plFixtureLayout.createSequentialGroup()
                                .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(IDField)
                                    .addComponent(tournamentIDField)
                                    .addGroup(plFixtureLayout.createSequentialGroup()
                                        .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel8)
                                            .addComponent(jLabel9)
                                            .addComponent(playerIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(0, 0, Short.MAX_VALUE)))
                                .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(plFixtureLayout.createSequentialGroup()
                                        .addGap(76, 76, 76)
                                        .addComponent(scoreHelpIcon)
                                        .addGap(50, 50, 50))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, plFixtureLayout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel10)
                                        .addGap(6, 6, 6))))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, plFixtureLayout.createSequentialGroup()
                                .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, plFixtureLayout.createSequentialGroup()
                                        .addComponent(jLabel5)
                                        .addGap(41, 41, 41)
                                        .addComponent(venueField))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, plFixtureLayout.createSequentialGroup()
                                        .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel24)
                                            .addComponent(jLabel25))
                                        .addGap(51, 51, 51)
                                        .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(timeSpinner)
                                            .addComponent(dcCombo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                                .addGap(18, 18, 18)
                                .addComponent(jLabel18)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14)
                            .addComponent(jLabel7)
                            .addComponent(jLabel13)
                            .addComponent(jLabel21)
                            .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, plFixtureLayout.createSequentialGroup()
                                    .addComponent(sideAP1Label)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(sideAP1Field))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, plFixtureLayout.createSequentialGroup()
                                    .addComponent(sideAP2Label)
                                    .addGap(18, 18, 18)
                                    .addComponent(sideAP2Field, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(plFixtureLayout.createSequentialGroup()
                                    .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel15)
                                        .addComponent(typeField, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(18, 18, 18)
                                    .addComponent(btnSelectPlayers, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(plFixtureLayout.createSequentialGroup()
                                    .addComponent(sideAG1Field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(24, 24, 24)
                                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(sideAG2Field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel17)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(sideAG3Field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(21, 21, 21)))
                            .addGroup(plFixtureLayout.createSequentialGroup()
                                .addComponent(sideBG1Field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30)
                                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(sideBG2Field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(5, 5, 5)
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(sideBG3Field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, plFixtureLayout.createSequentialGroup()
                                    .addComponent(sideBP1Label)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(sideBP1Field))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, plFixtureLayout.createSequentialGroup()
                                    .addComponent(sideBP2Label)
                                    .addGap(18, 18, 18)
                                    .addComponent(sideBP2Field, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, plFixtureLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(plFixtureLayout.createSequentialGroup()
                                .addComponent(helpIcon)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel4))
                            .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(ccTitleLabel)
                                .addComponent(ccLabel)))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 548, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        plFixtureLayout.setVerticalGroup(
            plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, plFixtureLayout.createSequentialGroup()
                .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(plFixtureLayout.createSequentialGroup()
                        .addComponent(playerIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel8)
                        .addGap(8, 8, 8)
                        .addComponent(IDField, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tournamentIDField, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(jLabel22)))
                    .addGroup(plFixtureLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(typeField, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnSelectPlayers))
                        .addGap(4, 4, 4)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(sideAP1Label)
                            .addComponent(sideAP1Field, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(plFixtureLayout.createSequentialGroup()
                                .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(sideAP2Label)
                                    .addComponent(sideAP2Field, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel7))
                            .addComponent(scoreHelpIcon))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(jLabel16)
                            .addComponent(jLabel17)
                            .addComponent(sideAG1Field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(sideAG2Field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(sideAG3Field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(plFixtureLayout.createSequentialGroup()
                        .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(venueField, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(5, 5, 5)
                        .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel23)
                            .addComponent(courtNoField, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dcCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(plFixtureLayout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(jLabel24)))
                        .addGap(18, 18, 18)
                        .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(timeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel25)))
                    .addGroup(plFixtureLayout.createSequentialGroup()
                        .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(sideBP1Label)
                            .addComponent(sideBP1Field, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(9, 9, 9)
                        .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(sideBP2Field, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(sideBP2Label))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel18)
                            .addComponent(jLabel20)
                            .addComponent(jLabel19)
                            .addComponent(sideBG1Field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(sideBG2Field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(sideBG3Field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(plFixtureLayout.createSequentialGroup()
                        .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(helpIcon)
                            .addGroup(plFixtureLayout.createSequentialGroup()
                                .addGap(9, 9, 9)
                                .addComponent(jLabel4)))
                        .addGap(33, 33, 33)
                        .addComponent(ccTitleLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ccLabel))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnAdd.setBackground(new java.awt.Color(255, 51, 0));
        btnAdd.setFont(new java.awt.Font("Adobe Gothic Std", 0, 13)); // NOI18N
        btnAdd.setForeground(new java.awt.Color(255, 255, 255));
        btnAdd.setText("ADD FIXTURE");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        btnUpdate.setBackground(new java.awt.Color(255, 51, 0));
        btnUpdate.setFont(new java.awt.Font("Adobe Gothic Std", 0, 13)); // NOI18N
        btnUpdate.setForeground(new java.awt.Color(255, 255, 255));
        btnUpdate.setText("SAVE CHANGES");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnDelete.setBackground(new java.awt.Color(255, 51, 0));
        btnDelete.setFont(new java.awt.Font("Adobe Gothic Std", 0, 13)); // NOI18N
        btnDelete.setForeground(new java.awt.Color(255, 255, 255));
        btnDelete.setText("DELETE FIXTURE");
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
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAdd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnDelete, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)
                    .addComponent(btnUpdate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(56, Short.MAX_VALUE))
        );
        jPanelLayout.setVerticalGroup(
            jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLayout.createSequentialGroup()
                .addComponent(topBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelLayout.createSequentialGroup()
                        .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnExit))
                            .addGroup(jPanelLayout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(mpIcon)))
                        .addGap(13, 13, 13)
                        .addComponent(btnAdd)
                        .addGap(18, 18, 18)
                        .addComponent(btnDelete)
                        .addGap(18, 18, 18)
                        .addComponent(btnUpdate))
                    .addGroup(jPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(plFixture, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(99, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel);
        jPanel.setBounds(0, 6, 1070, 790);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        // call procedure to quit this form
        quitWindow();
    }//GEN-LAST:event_btnExitActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        // asks for confirmation from the user of if they would like to do this action
        msgDlg.setMessage("Confirm Add Fixture");
        msgDlg.setVisible(true);
        // gets the status returned (the user's response sets the returnStatus; clicking OK = 1, clicking CANCEL = 0)
        int confirmClick = msgDlg.getReturnStatus();
        if (confirmClick == 1 ) {
            // INSERT QUERY
            boolean validCheck = validationCheck();
            int fixtureCompleted = 0;
            
            // set the fixtureCompleted to TRUE if the status of the match is a RESULT instead of a FIXTURE (a match that hasn't been completed)
            if (matchStatus.equals("result")) {
                fixtureCompleted = 1;
            } else {
                fixtureCompleted = 0;
            }
            
            String typeInput = typeField.getText();
            
            // use of regex expression and trim to only retrieve the number (the ID number) from the string
            String SAP1Input = sideAP1Field.getText().replaceAll("[^0-9]+", " ").trim();
            String SAP2Input = sideAP2Field.getText().replaceAll("[^0-9]+", " ").trim();
            String SBP1Input = sideBP1Field.getText().replaceAll("[^0-9]+", " ").trim();
            String SBP2Input = sideBP2Field.getText().replaceAll("[^0-9]+", " ").trim();
            
            String commentsInput = commentsField.getText();
            
            String venueInput = venueField.getText();
            String courtNoInput = courtNoField.getText();
            int courtNoAsInteger = 0;
            if (courtNoInput != null) {
                courtNoAsInteger = Integer.valueOf(courtNoInput);
            }          

            Date dateInput = dcCombo.getDate();
            DateFormat dayFormat = new SimpleDateFormat("dd-MM-yyyy");
            String dateString = dayFormat.format(dateInput);

            Date timeInput = (Date) timeSpinner.getValue();
            DateFormat timeFormat = new SimpleDateFormat("HH:mm");
            String timeString = timeFormat.format(timeInput);
            
            // validation procedure called before SQL execution to INSERT a new fixture
            if (validCheck) {
                String fixtureType = typeField.getText();
                String query = "";
                // integer variables of the player IDs
                int SAP1ID = 0;
                int SAP2ID = 0;
                int SBP1ID = 0;
                int SBP2ID = 0;
                
                String sideAScore = "";
                String sideBScore = "";   
                
                if (matchStatus.equals("result")) {
                    // must remove any whitespace otherwise, errors may occur
                    sideAScore = sideAG1Field.getSelectedItem().toString().trim() + "," + sideAG2Field.getSelectedItem().toString().trim() + "," + sideAG3Field.getSelectedItem().toString().trim();
                    sideBScore = sideBG1Field.getSelectedItem().toString().trim() + "," + sideBG2Field.getSelectedItem().toString().trim() + "," + sideBG3Field.getSelectedItem().toString().trim();
                }
                
                if (fixtureType.equals("singles")) {
                    SAP1ID = Integer.valueOf(SAP1Input);
                    SBP1ID = Integer.valueOf(SBP1Input);
                    query = "INSERT INTO `fixture`(`sideAPlayer1`, `sideAScore`, `sideBPlayer1`, `sideBScore`, `typeOfFixture`, `venueOfFixture`, `courtNo`, `dateOfFixture`, `timeOfFixture`, `comments`, `fixtureCompleted`) VALUES ('"+SAP1ID+"','"+sideAScore+"','"+SBP1ID+"','"+sideBScore+"','"+typeInput+"','"+venueInput+"','"+courtNoAsInteger+"','"+dateString+"','"+timeString+"','"+commentsInput+"','"+fixtureCompleted+"')";     
                } else {
                    // if it is doubles
                    SAP1ID = Integer.valueOf(SAP1Input);
                    SAP2ID = Integer.valueOf(SAP2Input);
                    SBP1ID = Integer.valueOf(SBP1Input);
                    SBP2ID = Integer.valueOf(SBP2Input);
                    query = "INSERT INTO `fixture`(`sideAPlayer1`, `sideAPlayer2`, `sideAScore`, `sideBPlayer1`, `sideBPlayer2`, `sideBScore`, `typeOfFixture`, `venueOfFixture`, `courtNo`, `dateOfFixture`, `timeOfFixture`, `comments`, `fixtureCompleted`) VALUES ('"+SAP1ID+"','"+SAP2ID+"','"+sideAScore+"','"+SBP1ID+"','"+SBP2ID+"','"+sideBScore+"','"+typeInput+"','"+venueInput+"','"+courtNoAsInteger+"','"+dateString+"','"+timeString+"','"+commentsInput+"','"+fixtureCompleted+"')";
                }
                
                getNewItems(SAP1Input, SAP2Input, SBP1Input, SBP2Input, sideAScore, sideBScore, typeInput, venueInput, courtNoInput, dateString, timeString, commentsInput);
                
                // integer variable which calls the function that executes the SQL query and returns the ID that generated
                int generatedFixtureID = executeSQLQuery(query,"Inserted");                
                // use the generated fixture ID to insert into the fixtureHasPlayers, the player IDs involved in the corresponding fixture ID.
                if (generatedFixtureID != 0) {
                    PreparedStatement pst = null;
                    try {
                        /** use of integer stack to temporarily store the player IDs included in the corresponding fixture ID
                         * so that it can be used as in a for loop to efficiently insert the values.
                        **/
                        Stack <Integer> playerBatch = new Stack <>();
                        
                        // only perform stack operation to push IDs into the stack if the inputs were not NULL
                        if (!SAP1Input.equals("")) {
                            playerBatch.push(SAP1ID);
                        }
                        if (!SAP2Input.equals("")) {
                            playerBatch.push(SAP2ID);
                        }
                        if (!SBP1Input.equals("")) {
                            playerBatch.push(SBP1ID);
                        }
                        if (!SBP2Input.equals("")) {
                            playerBatch.push(SBP2ID);
                        }
                        
                        String fhpQuery = "INSERT INTO `fixtureHasPlayers`(`fixture_id`,`player_id`) VALUES (?,?)";
                        pst = SQLConnection.getConnection().prepareStatement(fhpQuery);
                        
                        // batch processing used for faster execution
                        for (int nextInsert = 0; nextInsert < playerBatch.size(); nextInsert++) {
                            pst.setInt(1, generatedFixtureID);
                            pst.setInt(2, playerBatch.get(nextInsert));
                            pst.addBatch();                           
                        }
                        pst.executeBatch();
                    // SQL exception handling 
                    } catch (SQLException ex) {
                        System.out.println(ex);
                    } finally {
                        // call procedure to close the connection  
                        SQLConnection.closeConnection(SQLConnection.getConnection(), null , pst, null);  
                    }
                }
                choosePlayerList.setReturnStatus(0);
                quitWindow();
            }
        }
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // asks for confirmation from the user of if they would like to do this action
        msgDlg.setMessage("Confirm Update Fixture");
        msgDlg.setVisible(true);
        // gets the status returned (the user's response sets the returnStatus; clicking OK = 1, clicking CANCEL = 0)
        int confirmClick = msgDlg.getReturnStatus();
        if (confirmClick == 1 ) {
            // INSERT QUERY
            boolean validCheck = validationCheck();
            boolean fixtureCompleted = false;
            
            if (matchStatus.equals("result")) {
                fixtureCompleted = true;
            } else {
                fixtureCompleted = false;
            }

            String typeInput = typeField.getText();
            
            String SAP1Input = sideAP1Field.getText().replaceAll("[^0-9]+", " ");
            String SAP2Input = sideAP2Field.getText().replaceAll("[^0-9]+", " ");
            String SBP1Input = sideBP1Field.getText().replaceAll("[^0-9]+", " ");
            String SBP2Input = sideBP2Field.getText().replaceAll("[^0-9]+", " ");
            
            int SAP1ID = Integer.valueOf(SAP1Input);
            int SAP2ID = Integer.valueOf(SAP2Input);
            int SBP1ID = Integer.valueOf(SBP1Input);
            int SBP2ID = Integer.valueOf(SBP2Input);
            
            String commentsInput = commentsField.getText();
            
            String venueInput = venueField.getText();
            String courtNoInput = courtNoField.getText();
            int courtNoAsInteger = Integer.valueOf(courtNoInput);               

            Date dateInput = dcCombo.getDate();
            DateFormat dayFormat = new SimpleDateFormat("dd-MM-yyyy");
            String dateString = dayFormat.format(dateInput);

            Date timeInput = (Date) timeSpinner.getValue();
            DateFormat timeFormat = new SimpleDateFormat("HH:mm");
            String timeString = timeFormat.format(timeInput);
            
            if (validCheck) {              
                String sideAScore = "";
                String sideBScore = "";         
                if (matchStatus.equals("result")) {
                    sideAScore = sideAG1Field.getSelectedItem().toString() + "," + sideAG2Field.getSelectedItem().toString() + "," + sideAG3Field.getSelectedItem().toString();
                    sideBScore = sideBG1Field.getSelectedItem().toString() + "," + sideBG2Field.getSelectedItem().toString() + "," + sideBG3Field.getSelectedItem().toString();
                }
                getNewItems(SAP1Input, SAP2Input, SBP1Input, SBP2Input, sideAScore, sideBScore, typeInput, venueInput, courtNoInput, dateString, timeString, commentsInput);
                String query = "UPDATE `fixture` SET `sideAPlayer1` ='" + SAP1ID + "',`sideAPlayer2`='" + SAP2ID + "', `sideAScore`='" + sideAScore + "', `sideBPlayer1`='" + SBP1ID + "', `sideBPlayer2`='" + SBP2ID + "', `sideBScore`='" + sideBScore + "', `typeOfFixture`='" + typeInput + "', `venueOfFixture`='" + venueInput + "', `courtNo`='" + courtNoAsInteger + "', `dateOfFixture`='" + dateString + "', `timeOfFixture`='" + timeString + "', `comments`='" + commentsInput + "', `fixtureCompleted` ='" + fixtureCompleted +"' WHERE `fixture_id` = " + selectedID;
                executeSQLQuery(query,"Updated");  
                quitWindow();
            }
        }    
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        String deleteQuery = "DELETE FROM `fixture` WHERE fixture_id = " + selectedID;  
        
        // asks for confirmation from the user of if they would like to do this action
        msgDlg.setMessage("Confirm Delete Fixture");
        msgDlg.setVisible(true);
        // gets the status returned (the user's response sets the returnStatus; clicking OK = 1, clicking CANCEL = 0)
        int confirmDelete = msgDlg.getReturnStatus();
        if (confirmDelete == 1 ) {    
            // only allow the user to delete a match if they are an admin
            if (SessionManager.getUserRole().equals("admin")) {
                executeSQLQuery(deleteQuery,"Deleted");  
                quitWindow();
            } else {
                msgDlg.setMessage("Must be admin to delete a fixture");
                msgDlg.setVisible(true);
            }
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void helpIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_helpIconMouseClicked
        // help guide
        msgDlg.setMessage("Comments about the badminton match e.g. 'Excellent performance shown from Player1 to win the match' (maximum 250 characters including spaces) ");
        msgDlg.setVisible(true);
    }//GEN-LAST:event_helpIconMouseClicked

    private void commentsFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_commentsFieldKeyTyped
        // call procedure to re-calculate the comments length everytime the user types a key into the comments field
        recalculateCommentsLength();  
    }//GEN-LAST:event_commentsFieldKeyTyped

    private void btnSelectPlayersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectPlayersActionPerformed
        String typeOfFixture = typeField.getText();
        String sideAPlayer1 = sideAP1Field.getText();
        String sideAPlayer2 = sideAP2Field.getText();
        String sideBPlayer1 = sideBP1Field.getText();
        String sideBPlayer2 = sideBP2Field.getText();
        
        choosePlayerList cpl = new choosePlayerList(fixtureAction, matchStatus, typeOfFixture, sideAPlayer1, sideAPlayer2, sideBPlayer1, sideBPlayer2);
        cpl.setVisible(true);
        cpl.pack();
        cpl.setLocationRelativeTo(null);
        cpl.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.dispose();
    }//GEN-LAST:event_btnSelectPlayersActionPerformed

    private void scoreHelpIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_scoreHelpIconMouseClicked
        // help guide
        msgDlg.setMessage("Place the score for each game of the match if the match has been completed. If the player won the first two games then place a zero on the game three scores");
        msgDlg.setVisible(true);
    }//GEN-LAST:event_scoreHelpIconMouseClicked

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
            java.util.logging.Logger.getLogger(fixtureCustomise.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(fixtureCustomise.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(fixtureCustomise.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(fixtureCustomise.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
                new fixtureCustomise().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField IDField;
    private javax.swing.JLabel LogLabel;
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnDelete;
    private javax.swing.JToggleButton btnExit;
    private javax.swing.JButton btnSelectPlayers;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JLabel ccLabel;
    private javax.swing.JLabel ccTitleLabel;
    private javax.swing.JTextArea commentsField;
    private javax.swing.JTextField courtNoField;
    private com.toedter.calendar.JDateChooser dcCombo;
    private javax.swing.JLabel helpIcon;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel mainMenuLabel;
    private javax.swing.JLabel mpIcon;
    private javax.swing.JPanel plFixture;
    private javax.swing.JLabel playerIcon;
    private javax.swing.JLabel scoreHelpIcon;
    private javax.swing.JComboBox<String> sideAG1Field;
    private javax.swing.JComboBox<String> sideAG2Field;
    private javax.swing.JComboBox<String> sideAG3Field;
    private javax.swing.JTextField sideAP1Field;
    private javax.swing.JLabel sideAP1Label;
    private javax.swing.JTextField sideAP2Field;
    private javax.swing.JLabel sideAP2Label;
    private javax.swing.JComboBox<String> sideBG1Field;
    private javax.swing.JComboBox<String> sideBG2Field;
    private javax.swing.JComboBox<String> sideBG3Field;
    private javax.swing.JTextField sideBP1Field;
    private javax.swing.JLabel sideBP1Label;
    private javax.swing.JTextField sideBP2Field;
    private javax.swing.JLabel sideBP2Label;
    private javax.swing.JSpinner timeSpinner;
    private javax.swing.JPanel topBar;
    private javax.swing.JTextField tournamentIDField;
    private javax.swing.JTextField typeField;
    public javax.swing.JLabel userLoggedOn;
    private javax.swing.JTextField venueField;
    // End of variables declaration//GEN-END:variables
}
