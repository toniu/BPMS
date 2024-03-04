/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package badmintonSystem;

// required imports
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 *
 * @author Neka
 */
public class mpCustomisePlayer extends javax.swing.JFrame {
    
    // message dialog
    msgDialog msgDlg = new msgDialog(new javax.swing.JFrame(),true);
    // global variables
    int selectedID = badmintonPlayer.getSelectedPlayerID();
    int deletedID = 0;
    String actionChosen = null;
    
    // arrays of items used for comparison if a PLAYER UPDATE is being made
    String[] oldItems = new String[6];
    String[] newItems = new String[6];

    /**
     * Creates new form mpWindow
     */
    
    public mpCustomisePlayer() {
        initComponents();
    }
    
    public mpCustomisePlayer(String actionChosen) {
        initComponents();
        if (actionChosen.equals("UPDATE/DELETE")) {
            // disable Add player if the user is planning to update or delete the player
            btnAdd.setVisible(false);
            btnAdd.setEnabled(false);
            
            // call subroutine to show details of selected player;
            showSelectedPlayerDetails();    
            
            // fields and comboBoxes to enable
            firstNameField.setEditable(false);
            lastNameField.setEditable(false);
            handCombo.setEnabled(true);
            genderCombo.setEnabled(true);
            dcCombo.setEnabled(true);
            statusField.setEditable(true);   
        } else if (actionChosen.equals("VIEW")) {
            // typeOfAction is VIEW so...
            // disable Add / Delete / Update player if the user is planning to view the selected player
            btnAdd.setVisible(false);
            btnAdd.setEnabled(false);   
            btnDelete.setVisible(false);
            btnDelete.setEnabled(false);
            btnUpdate.setVisible(false);
            btnUpdate.setEnabled(false);   

            
            // call subroutine to show details of selected player;
            showSelectedPlayerDetails(); 
            // fields and comboBoxes to disable
            firstNameField.setEditable(false);
            lastNameField.setEditable(false);
            handCombo.setEnabled(false);
            genderCombo.setEnabled(false);
            dcCombo.setEnabled(false);
            statusField.setEditable(false);
            
            // fields or comboBoxes to set as uneditable           
        } else {
            // typeOfAction is INSERT so...
            // disable Delete / Update player if the user is planning to add a new player
            btnDelete.setVisible(false);
            btnDelete.setEnabled(false);
            
            btnUpdate.setVisible(false);
            btnUpdate.setEnabled(false);            
        }
    }
    
     //  validation methods
    
    // function to validate first name
   public boolean validateFirstName(String firstName)
   {
      boolean doesMatch = firstName.matches( "[A-Z][a-zA-Z]*" ); // regex expression
      if (!doesMatch) {
         msgDlg.setMessage("Invalid first name: must have uppercase/lowercase letters only");
         msgDlg.setVisible(true); 
      }
      return doesMatch;
   } 

   // function to validate last name
   public  boolean validateLastName(String lastName)
   {
      boolean doesMatch = lastName.matches( "[a-zA-z]+([ '-][a-zA-Z]+)*" ); // regex expression
      if (!doesMatch) {
         msgDlg.setMessage("Invalid last name: must have uppercase/lowercase letters. Dashes, apostrophes and spaces are allowed too.");
         msgDlg.setVisible(true); 
      }
      return doesMatch;
   }
   
   // function to check if player already exists
    public static boolean checkIfPlayerExists(String firstName, String lastName)
    {
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean checkPlayer = false;
        
        // conditional selectquery about to be executed
        String queryCheck = "SELECT * FROM `player` WHERE `firstName` =? AND `lastName` =?";
        
        try {
            // get the returned connection from function in SQLConnection and prepare the statement with the query
            ps = SQLConnection.getConnection().prepareStatement(queryCheck);
            // set parameters of query as the parameters in this function
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            
            // execute the query
            rs = ps.executeQuery();
            
            // return as true if a record was found
            if(rs.next())
            {
                checkPlayer = true;
            }
        // exception handling of SQL errors
        } catch (SQLException ex) {
            Logger.getLogger(mpCustomisePlayer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            // call procedure to close the connection      
            SQLConnection.closeConnection(SQLConnection.getConnection(), rs, ps, null);  
        }
        return checkPlayer;
    }
    
    // function to check if player already exists
    public static boolean checkIfPlayerNameExistsInUser(String firstName, String lastName)
    {
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean checkPlayerNameInUser = false;
        
        String queryCheck = "SELECT * FROM `user` WHERE `firstName` =? AND `lastName` =?";
        
        try {
            ps = SQLConnection.getConnection().prepareStatement(queryCheck);
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            
            rs = ps.executeQuery();
            
            if(rs.next())
            {
                checkPlayerNameInUser = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(mpCustomisePlayer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            // call procedure to close the connection      
            SQLConnection.closeConnection(SQLConnection.getConnection(), rs, ps, null);  
        }
        return checkPlayerNameInUser;
    }
    
    // procedure to link the user with the player in the SQL table
    public void linkPlayerWithUser(String firstNameInput, String lastNameInput, int generatedID) {
        PreparedStatement psUID = null;
        ResultSet rsUID = null;
         try {
            // the generated player ID
            int playerIDFound = generatedID;

            // query to look for the user ID and role of the user with corresponding player first name and last name

            String queryUID = "SELECT * FROM `user` WHERE `firstName` =? AND `lastName` =?";
            int userIDFound = 0;
            String oldRoleFound = "";
            psUID = SQLConnection.getConnection().prepareStatement(queryUID);

            psUID.setString(1, firstNameInput);  
            psUID.setString(2, lastNameInput);  

            rsUID = psUID.executeQuery();

            while (rsUID.next()) {
                userIDFound = rsUID.getInt("user_id");
                oldRoleFound = rsUID.getString("roleName");
                
            }

            // query to link the two IDs, player ID and user ID as foreign keys found in the userHasPlayer table
            String queryLink = "INSERT INTO `userHasPlayer`(`user_id`, `player_id`) VALUES ('"+userIDFound+"','"+playerIDFound+"')";
            Statement linkSt = null;
            try {
                linkSt = SQLConnection.getConnection().createStatement();
                if ((linkSt.executeUpdate(queryLink)) == 1)
                {
                    msgDlg.setMessage("User linked with created player successfully");
                    msgDlg.setVisible(true);       
                } else {
                    msgDlg.setMessage("Data failed to link");
                    msgDlg.setVisible(true);
                }           
            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                // call procedure to close the connection      
                SQLConnection.closeConnection(SQLConnection.getConnection(), null, null, linkSt);  
            } 

           

            // query to update the linkedWithUser attribute to 'yes'
            String linkedWithUser = "yes";
            String queryPULink = "UPDATE `player` SET `linkedWithUser`='"+linkedWithUser+"' WHERE `player_id` = "+playerIDFound;
            Statement LWUSt;
            try {
                LWUSt = SQLConnection.getConnection().createStatement();
                if ((LWUSt.executeUpdate(queryPULink)) == 1)
                {
                    msgDlg.setMessage("Player's linked with user!");
                    msgDlg.setVisible(true);        
                } else {
                    msgDlg.setMessage("Player's role failed to update");
                    msgDlg.setVisible(true);
                }           
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            // query to update the user's role to player
            String roleSet = "player";
            String querySetRole = "UPDATE `user` SET `roleName`='"+roleSet+"' WHERE `user_id` = "+userIDFound;
            Connection con = SQLConnection.getConnection();
            Statement st;
            try {
                st = con.createStatement();
                if ((st.executeUpdate(querySetRole)) == 1)
                {
                    msgDlg.setMessage("Player's role updated successfully");
                    msgDlg.setVisible(true);        
                } else {
                    msgDlg.setMessage("Player's role failed to update");
                    msgDlg.setVisible(true);
                }           
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            // set timestamp for SQL query
            Date date = new Date();
            long time = date.getTime();
            Timestamp updateTimestamp = new Timestamp(time);

            String userWhoChanged = SessionManager.getUsername();
            String change = ("Updated player's role: "+ firstNameInput + " " + lastNameInput);
            String itemAffected = oldRoleFound;
            String itemChangedTo = roleSet;
            Statement auditSt = null;
            // update change table in mySQL database
            try {
                auditSt = SQLConnection.getConnection().createStatement();
                if ((auditSt.executeUpdate("INSERT INTO `change`(`change`, `timeOfChange`, `userWhoChanged`, `itemAffected`, `itemChangedTo`) VALUES ('"+change+"','"+updateTimestamp+"','"+userWhoChanged+"','"+itemAffected+ "','" + itemChangedTo + "')")) == 1)
                {
                    System.out.println("AUDIT LOG UPDATED");    
                } else {
                    System.out.println("Failed to update into audit log.");
                } 
            // exception handling of SQL errors
            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                // call procedure to close the connection      
                SQLConnection.closeConnection(SQLConnection.getConnection(), null, null, auditSt);  
            }
        // exception handling of SQL errors
        } catch (SQLException ex) {
            Logger.getLogger(mpCustomisePlayer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            // call procedure to close the connection      
            SQLConnection.closeConnection(SQLConnection.getConnection(), rsUID, psUID, null);  
        }
    }
   
    // procedure to set the textfields as the details of the selected player
    public void showSelectedPlayerDetails() {
        // SEARCH FOR FIELDS IN SELECTED PLAYER IN PLAYER TABLE
        String firstName = "";
        String lastName = "";
        String hand = "";
        String status = "";
        int ranking = 0;
        int STW = 0;
        int DTW = 0;
        String gender = "";
        String dateOfBirth = null;
        
        PreparedStatement psA = null;
        ResultSet rsA = null;
        try {
            // conditional join select query
            String queryA = "SELECT * FROM `player`, `playerStatistics`, `playerForm` WHERE player.player_id = ? AND player.player_id = playerStatistics.playerOfStats_id " +
                "AND player.player_id = playerForm.playerOfForm_id AND playerForm.playerOfForm_id = playerStatistics.playerOfStats_id";

            psA = SQLConnection.getConnection().prepareStatement(queryA);

            psA.setInt(1, selectedID);           
            rsA = psA.executeQuery();

            while (rsA.next()) {
                firstName = rsA.getString("firstName");
                lastName = rsA.getString("lastName");
                gender = rsA.getString("gender");
                dateOfBirth = rsA.getString("dateOfBirth");
                hand = rsA.getString("hand");
                status = rsA.getString("status");    
                ranking = rsA.getInt("ranking"); 
                STW = rsA.getInt("singleTournamentWins"); 
                DTW = rsA.getInt("doubleTournamentWins"); 
            }             

            // convert integer variables to string to display on fields
            String IDShown = Integer.toString(selectedID);
            String rankString = Integer.toString(ranking);
            String STWString = Integer.toString(STW);
            String DTWString = Integer.toString(DTW);

            IDField.setText(IDShown);
            firstNameField.setText(firstName);
            lastNameField.setText(lastName);
            handCombo.setSelectedItem(hand);
            genderCombo.setSelectedItem(gender);

            // CONVERT FROM STRING TO JAVA.UTIL.DATE TO SHOW ON DCCOMBO
            if (dateOfBirth != null ) {
                try {
                    Date convertedDate = new SimpleDateFormat("dd-MM-yyyy").parse(dateOfBirth);
                    dcCombo.setDate(convertedDate);
                } catch (ParseException ex) {
                    Logger.getLogger(mpCustomisePlayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            statusField.setText(status);
            rankingField.setText(rankString);
            STWField.setText(STWString);
            DTWField.setText(DTWString);

            getOldItems(firstName,lastName,hand,gender,dateOfBirth,status);


        } catch (SQLException ex) {
            Logger.getLogger(badmintonLogin.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            // call procedure to close the connection      
            SQLConnection.closeConnection(SQLConnection.getConnection(), rsA, psA, null);  
        }
    }
    
    // retrieves the items of the player before an update is made
    public void getOldItems(String oldFirstName, String oldLastName, String oldHand, String oldGender, String oldDOB, String oldStatus) {
        oldItems[0] = oldFirstName;
        oldItems[1] = oldLastName;
        oldItems[2] = oldHand;
        oldItems[3] = oldGender;
        oldItems[4] = oldDOB;
        oldItems[5] = oldStatus;
    }
    
    // retrieves the items of the player after an update is made
    public void getNewItems(String newFirstName, String newLastName, String newHand, String newGender, String newDOB, String newStatus) {
        newItems[0] = newFirstName;
        newItems[1] = newLastName;
        newItems[2] = newHand;
        newItems[3] = newGender;
        newItems[4] = newDOB;
        newItems[5] = newStatus;
    }
    
    // update the audit log 
    public void updateAuditLog(String message, String firstName, String lastName) {
        // SET TIMESTAMP FOR SQL QUERY
        Date date = new Date();
        long time = date.getTime();
        Timestamp updateTimestamp = new Timestamp(time);
        
        // get the data of the user who is currently logged in
        String userWhoChanged = SessionManager.getUsername();
        int userWhoChangedID = SessionManager.getUserID();
        String change = (message + " player: "+ firstName + " " + lastName);
        
        // get returned connection from calling function in SQLConnection
        Connection con = SQLConnection.getConnection();
        Statement st = null;
        
        if (message.equals("Updated")) { // updated PLAYER   
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
                        SQLConnection.closeConnection(con, null, null, st);  
                    }
                }
            }
        } else if (message.equals("Inserted")) { // inserted player      
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
                SQLConnection.closeConnection(con, null, null, st);  
            }     
        } else  { // deleted player
            try {
                st = con.createStatement();
                if ((st.executeUpdate("INSERT INTO `change`(`change`, `timeOfChange`, `userWhoChanged`, `itemAffected`, `user_id`) VALUES ('"+change+"','"+updateTimestamp+"','"+userWhoChanged+"','" + "deleted player was ID: "+deletedID+"','" + userWhoChangedID + "')")) == 1)
                {
                    System.out.println("AUDIT LOG UPDATED");    
                } else {
                    System.out.println("Failed to update into audit log.");
                }           
            } catch (SQLException ex) {
                ex.printStackTrace();
            }  finally {
                // call procedure to close the connection      
                SQLConnection.closeConnection(con, null, null, st);  
            }
        }
    }
    
    // execute the SQL query
    public int executeSQLQuery(String query, String message, String playerFirstName, String playerLastName) {
        // get returned connection from calling function in the class, SQLConnection
        Connection con = SQLConnection.getConnection();
        Statement st = null;
        int idGenerated = 0;
        
        try {
            st = con.createStatement();
            // statement with the setting, return generated keys after the SQL update is done.
            if ((st.executeUpdate(query, Statement.RETURN_GENERATED_KEYS)) == 1)
            {
                msgDlg.setMessage("Data " + message + " successfully");
                msgDlg.setVisible(true);
                // update audit log if first name or last name is not blank
                if (!playerFirstName.trim().equals("") || !playerLastName.trim().equals("")) {
                    updateAuditLog(message, playerFirstName, playerLastName);   
                }
            } else {
                msgDlg.setMessage("Data not " + message);
                msgDlg.setVisible(true);
            }    
        // exception handling of SQL errors
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            // call procedure to close the connection      
            SQLConnection.closeConnection(con, null, null, st);  
        }
        return idGenerated;
    }
    
    public void quitWindow() {
        // closes this form and opens manage players window
        mpWindow mpW = new mpWindow();
        mpW.setVisible(true);
        mpW.pack();
        mpW.setLocationRelativeTo(null);
        mpW.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.dispose();
    }
    
    // FUNCTION TO CHECK IF THE USER ID IS ALREADY LINKED WITH THE PLAYER ID
    public boolean checkLinkedWithUser(String firstNameInput, String lastNameInput) {
        String linkedWithUserResponse = null;
        boolean isPlayerLinkedWithUser = false;
        
        PreparedStatement psUserLink = null;
        ResultSet rsUserLink = null;
        try {

            // QUERY TO CHECK IF THE USERLINK SAYS YES OR NO
            String queryCheckUserLink = "SELECT * FROM `player` WHERE `firstName` =? AND `lastName` =?";
            psUserLink = SQLConnection.getConnection().prepareStatement(queryCheckUserLink);
            
            // set the parameters of the query
            psUserLink.setString(1, firstNameInput);  
            psUserLink.setString(2, lastNameInput);  

            rsUserLink = psUserLink.executeQuery();
            
            // retrieve the attribute of linkedWithUser with ResultSet
            while (rsUserLink.next()) {
                linkedWithUserResponse = rsUserLink.getString("linkedWithUser");
            }
        // exception handling of SQL errors
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            // call procedure to close the connection      
            SQLConnection.closeConnection(SQLConnection.getConnection(), rsUserLink, psUserLink, null);  
        }
        // set to true if the 'linkedWithUser' string was "yes"
        if (linkedWithUserResponse.equals("yes")) {
            isPlayerLinkedWithUser = true;
        } 
        return isPlayerLinkedWithUser;        
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
        plPlayer = new javax.swing.JPanel();
        playerIcon = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lastNameField = new javax.swing.JTextField();
        firstNameField = new javax.swing.JTextField();
        dcCombo = new com.toedter.calendar.JDateChooser();
        statusField = new javax.swing.JTextField();
        rankingField = new javax.swing.JTextField();
        genderCombo = new javax.swing.JComboBox<>();
        handCombo = new javax.swing.JComboBox<>();
        helpIcon = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        IDField = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        DTWField = new javax.swing.JTextField();
        STWField = new javax.swing.JTextField();
        btnAdd = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1070, 530));
        setResizable(false);
        getContentPane().setLayout(null);

        jPanel.setBackground(new java.awt.Color(0, 51, 102));
        jPanel.setPreferredSize(new java.awt.Dimension(1070, 530));

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

        plPlayer.setBackground(new java.awt.Color(0, 51, 102));
        plPlayer.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "CUSTOMISE PLAYER", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 13), new java.awt.Color(255, 51, 0))); // NOI18N

        playerIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/systemIcons/muIcon.png"))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("FIRST NAME:");

        jLabel2.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("LAST NAME:");

        jLabel3.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("HAND:");

        jLabel4.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("STATUS:");

        jLabel5.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("RANKING:");

        jLabel6.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("GENDER:");

        jLabel7.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("DATE OF BIRTH:");

        lastNameField.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N

        firstNameField.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        firstNameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                firstNameFieldActionPerformed(evt);
            }
        });

        dcCombo.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N

        statusField.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N

        rankingField.setEditable(false);
        rankingField.setBackground(new java.awt.Color(255, 204, 204));
        rankingField.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        rankingField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rankingFieldActionPerformed(evt);
            }
        });

        genderCombo.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        genderCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Male", "Female", "Other" }));

        handCombo.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        handCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Left-handed", "Right-handed" }));

        helpIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/systemIcons/helpIcon.png"))); // NOI18N
        helpIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                helpIconMouseClicked(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("PLAYER ID:");

        IDField.setEditable(false);
        IDField.setBackground(new java.awt.Color(255, 204, 204));

        jLabel9.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("SINGLE TOURNAMENT WINS:");

        jLabel10.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("DOUBLE TOURNAMENT WINS:");

        DTWField.setEditable(false);
        DTWField.setBackground(new java.awt.Color(255, 204, 204));

        STWField.setEditable(false);
        STWField.setBackground(new java.awt.Color(255, 204, 204));

        javax.swing.GroupLayout plPlayerLayout = new javax.swing.GroupLayout(plPlayer);
        plPlayer.setLayout(plPlayerLayout);
        plPlayerLayout.setHorizontalGroup(
            plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(plPlayerLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(plPlayerLayout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(plPlayerLayout.createSequentialGroup()
                        .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(plPlayerLayout.createSequentialGroup()
                                .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(plPlayerLayout.createSequentialGroup()
                                        .addComponent(playerIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addComponent(STWField))
                                .addGap(27, 27, 27)
                                .addComponent(helpIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(9, 9, 9))
                            .addGroup(plPlayerLayout.createSequentialGroup()
                                .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel8)
                                    .addComponent(IDField, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel9)
                                    .addComponent(DTWField, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 54, Short.MAX_VALUE)))
                        .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(plPlayerLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel7)
                                .addGap(18, 18, 18)
                                .addComponent(dcCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(plPlayerLayout.createSequentialGroup()
                                .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel6))
                                .addGap(36, 36, 36)
                                .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(firstNameField)
                                    .addComponent(lastNameField)
                                    .addComponent(genderCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(handCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, plPlayerLayout.createSequentialGroup()
                                .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel5))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(statusField, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(rankingField, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(37, 37, 37))))
        );
        plPlayerLayout.setVerticalGroup(
            plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(plPlayerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(firstNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lastNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(handCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(plPlayerLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jLabel6))
                    .addGroup(plPlayerLayout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(genderCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dcCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(plPlayerLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(helpIcon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(statusField, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel4)))))
                .addGap(18, 18, 18)
                .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(rankingField, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(47, 47, 47))
            .addGroup(plPlayerLayout.createSequentialGroup()
                .addComponent(playerIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(IDField, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(STWField, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(DTWField, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

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
        btnUpdate.setText("SAVE CHANGES");
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

        javax.swing.GroupLayout jPanelLayout = new javax.swing.GroupLayout(jPanel);
        jPanel.setLayout(jPanelLayout);
        jPanelLayout.setHorizontalGroup(
            jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(topBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mpIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(plPlayer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnAdd, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                    .addComponent(btnUpdate, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                    .addComponent(btnDelete, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                    .addComponent(btnExit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(50, 50, 50))
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
                                .addComponent(mpIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanelLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnExit)))
                        .addGap(13, 13, 13)
                        .addComponent(btnAdd)
                        .addGap(24, 24, 24)
                        .addComponent(btnDelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnUpdate))
                    .addGroup(jPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(plPlayer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(69, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel);
        jPanel.setBounds(0, 6, 1070, 530);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        // calls procedure to quit window
        quitWindow();
    }//GEN-LAST:event_btnExitActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed

        msgDlg.setMessage("Confirm Add Player");
        msgDlg.setVisible(true);
        int confirmDelete = msgDlg.getReturnStatus();
        if (confirmDelete == 1 ) {
            // INSERT QUERY
            String firstNameInput = firstNameField.getText();
            String lastNameInput = lastNameField.getText();
            String handInput = (String) handCombo.getSelectedItem();
            String genderInput = (String) genderCombo.getSelectedItem();
            Date bDateInput = dcCombo.getDate();
            String birthDate = null;
            String status = statusField.getText();

            if (firstNameInput.equals("")) {
            msgDlg.setMessage("Add a first name");
            msgDlg.setVisible(true);
            }
            else if (lastNameInput.equals("")) {
                msgDlg.setMessage("Add a last name");
                msgDlg.setVisible(true);
            }        
            else if (handInput == null) {
                msgDlg.setMessage("Select player hand");
                msgDlg.setVisible(true);
            }
            else if (genderInput == null) {
                msgDlg.setMessage("Select gender");
                msgDlg.setVisible(true);
            }  
            else if(bDateInput == null) {
                msgDlg.setMessage("Add a date of birth");
                msgDlg.setVisible(true);           
            }
            else {
                
                if (status == null) {
                    status = "active";
                }
                 boolean validatedFirstName = validateFirstName(firstNameInput);
                 boolean validatedLastName = validateLastName(lastNameInput);
                 boolean checkPlayerExists = checkIfPlayerExists(firstNameInput,lastNameInput);
                 
                 SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
                 birthDate = dateformat.format(dcCombo.getDate());
                 
                System.out.println("validated first Name: " + validatedFirstName);
                System.out.println("validated last Name: " + validatedLastName);
                System.out.println("does player exist already: " + checkPlayerExists);
                
                if (validatedFirstName && validatedLastName && !checkPlayerExists) {
                    int generatedID = 0;
                    String queryA = "INSERT INTO `player`(`firstName`, `lastName`, `gender`, `dateOfBirth`, `hand`, `status`) VALUES ('"+firstNameInput+"','"+lastNameInput+"','"+genderInput+"','"+birthDate+"','"+handInput+"','"+status+"')";
                    generatedID = executeSQLQuery(queryA,"Inserted", firstNameInput, lastNameInput); 
                    
                    if (generatedID != 0) {  
                        String statsQuery = "INSERT INTO `playerStatistics`(`ranking`, `oldRanking`, `rankPoints`, `singleMatchWins`, `singleMatchLosses`, `doubleMatchWins`, `doubleMatchLosses`, `singleTournamentWins`, `doubleTournamentWins`) VALUES ('"+ 0 +"','"+ 0 +"','"+ 0 +"','"+ 0 +"','"+ 0 +"','"+ 0 + "','" + 0 + "','" + 0 + "','" + 0 + "')";
                        String formQuery = "INSERT INTO `playerForm`(`form`, `balance`) VALUES ('','" + 0 + "')";
                        
                        executeSQLQuery(statsQuery,"Inserted", "", ""); 
                        executeSQLQuery(formQuery,"Inserted", "", ""); 
                        
                        
                        if (checkIfPlayerNameExistsInUser(firstNameInput,lastNameInput) && (!checkLinkedWithUser(firstNameInput,lastNameInput))) {
                            msgDlg.setMessage("User with the full name: "+ firstNameInput + " " + lastNameInput + ", already exists in this system, would you like to link this user with recently created player?");
                            msgDlg.setVisible(true);    
                            int confirmLink = msgDlg.getReturnStatus();
                            if (confirmLink == 1) {
                                // link the player and user in relationship. Use player_id to link with user_id
                               linkPlayerWithUser(firstNameInput,lastNameInput,generatedID); // subroutine to link recently created player with user
                            }
                        }
                    }
                    quitWindow();
                }
            }
        }
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // confirmation to do this action
        msgDlg.setMessage("Confirm Update Player");
        msgDlg.setVisible(true);
        // get returnStatus of message dialog (clickig OK = 1, clicking cancel = 0)
        int confirmDelete = msgDlg.getReturnStatus();
        if (confirmDelete == 1 ) {
            // get ID of selected badmintonPlayer
            selectedID = badmintonPlayer.getSelectedPlayerID();
            // to get OLD FIRST NAME LAST NAME (IN CASE THE NAME WAS CHANGED)
            String firstNameChanged = oldItems[1];
            String lastNameChanged = oldItems[2];
           
            String firstNameInput = firstNameField.getText();
            String lastNameInput = lastNameField.getText();
            String handInput = (String) handCombo.getSelectedItem();
            String genderInput = (String) genderCombo.getSelectedItem();
            Date bDateInput = dcCombo.getDate();
            String birthDate = null;
            String status = statusField.getText();

            // check if the inputs were null
            if (handInput == null) {
                msgDlg.setMessage("Select player hand");
                msgDlg.setVisible(true);
            }
            else if (genderInput == null) {
                msgDlg.setMessage("Select gender");
                msgDlg.setVisible(true);
            }  
            else if(bDateInput == null) {
                msgDlg.setMessage("Add a date of birth");
                msgDlg.setVisible(true);           
            }
            else {
                
                if (status.trim().equals("")) {
                    status = "active";
                }
                
                boolean checkPlayerExists = checkIfPlayerExists(firstNameInput,lastNameInput);
                
                // simpleDateFormat to convert the date to string
                SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
                birthDate = dateformat.format(dcCombo.getDate());
                System.out.println("does player exist already: " + checkPlayerExists);
                
                // if the player doesn't exist then execute the SQL query
                if (!checkPlayerExists) {
                    String query = "UPDATE `player` SET `firstName`='"+firstNameInput+"',`lastName`='"+lastNameInput+"',`gender`='"+genderInput+"',`dateOfBirth`='"+birthDate+"',`hand`='"+handInput+"',`status`='"+status+"' WHERE `player_id` = "+selectedID;
                    getNewItems(firstNameInput,lastNameInput,handInput,genderInput,birthDate,status);
                    executeSQLQuery(query,"Updated",firstNameChanged,lastNameChanged);
                }
            }  
            // quit the form
            quitWindow();
        }
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        String firstNameInput = firstNameField.getText();
        String lastNameInput = lastNameField.getText();
        
        int IDSelected = badmintonPlayer.getSelectedPlayerID();           
        String deletePlayerQuery = "DELETE FROM `player` WHERE player_id = " + IDSelected;
        deletedID = IDSelected;            
            
        // confirmation to do this action    
        msgDlg.setMessage("Confirm Delete Player");
        msgDlg.setVisible(true);
        // get returnStatus of message dialog (clickig OK = 1, clicking cancel = 0)
        int confirmDelete = msgDlg.getReturnStatus();
        if (confirmDelete == 1 ) {    
            if (checkIfPlayerNameExistsInUser(firstNameInput,lastNameInput) && (checkLinkedWithUser(firstNameInput,lastNameInput))) {
                msgDlg.setMessage("The player you want to delete, " + firstNameInput + " " + lastNameInput + ", was linked with a user in this system. Deleting this player would delete the link between player and user. Would you still like to delete this player?");
                msgDlg.setVisible(true); 
                int confirmDeleteLink = msgDlg.getReturnStatus();
                if (confirmDeleteLink == 1) {
                    // call subroutine to delete player from the player table
                    executeSQLQuery(deletePlayerQuery,"Deleted",firstNameInput,lastNameInput);
                } else {
                    System.out.println("Cancelled Delete Player");
                }
            } else {
                // delete the player
                executeSQLQuery(deletePlayerQuery,"Deleted",firstNameInput,lastNameInput);
            }
            // quit this form
            quitWindow();
        } else {
            System.out.println("Delete Player Cancelled");
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void firstNameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_firstNameFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_firstNameFieldActionPerformed

    private void helpIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_helpIconMouseClicked
        // help guide
        msgDlg.setMessage("The current status of the player e.g. whether the player is injured, active, inactive, ill etc.");
        msgDlg.setVisible(true);
    }//GEN-LAST:event_helpIconMouseClicked

    private void rankingFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rankingFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rankingFieldActionPerformed

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
            java.util.logging.Logger.getLogger(mpCustomisePlayer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(mpCustomisePlayer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(mpCustomisePlayer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(mpCustomisePlayer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new mpCustomisePlayer().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField DTWField;
    private javax.swing.JTextField IDField;
    private javax.swing.JLabel LogLabel;
    private javax.swing.JTextField STWField;
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnDelete;
    private javax.swing.JToggleButton btnExit;
    private javax.swing.JButton btnUpdate;
    private com.toedter.calendar.JDateChooser dcCombo;
    private javax.swing.JTextField firstNameField;
    private javax.swing.JComboBox<String> genderCombo;
    private javax.swing.JComboBox<String> handCombo;
    private javax.swing.JLabel helpIcon;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel;
    private javax.swing.JTextField lastNameField;
    private javax.swing.JLabel mainMenuLabel;
    private javax.swing.JLabel mpIcon;
    private javax.swing.JPanel plPlayer;
    private javax.swing.JLabel playerIcon;
    private javax.swing.JTextField rankingField;
    private javax.swing.JTextField statusField;
    private javax.swing.JPanel topBar;
    public javax.swing.JLabel userLoggedOn;
    // End of variables declaration//GEN-END:variables
}
