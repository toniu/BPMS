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
public class muCustomiseUser extends javax.swing.JFrame {
    
    // message dialog
    msgDialog msgDlg = new msgDialog(new javax.swing.JFrame(),true);
    // global variables
    int selectedID = badmintonUser.getSelectedUserID();
    int deletedID = 0;
    String actionChosen = null;
    
    // arrays of items used for comparison if a PLAYER UPDATE is being made
    String[] oldItems = new String[6];
    String[] newItems = new String[6];

    /**
     * Creates new form mpWindow
     */
    
    public muCustomiseUser() {
        initComponents();
    }
    
    public muCustomiseUser(String actionChosen) {
        initComponents();
        if (actionChosen.equals("UPDATE/DELETE")) {
            // disable Add player if the user is planning to update or delete the player           
            // call subroutine to show details of selected player;
            showSelectedUserDetails();  
            
            if (!SessionManager.getUserRole().equals("admin")) {
                btnDelete.setVisible(false);
                btnDelete.setEnabled(false);
            }
            // fields and comboBoxes to enable
            firstNameField.setEditable(false);
            lastNameField.setEditable(false);
            genderCombo.setEnabled(true);
            dcCombo.setEnabled(true);
        } else {
            // typeOfAction is VIEW so...
            // disable Add / Delete / Update player if the user is planning to view the selected player 
            btnDelete.setVisible(false);
            btnDelete.setEnabled(false);
            btnUpdate.setVisible(false);
            btnUpdate.setEnabled(false);   

            // call subroutine to show details of selected player;
            showSelectedUserDetails(); 
            // fields and comboBoxes to disable
            firstNameField.setEditable(false);
            lastNameField.setEditable(false);
            passwordField.setVisible(false);
            passwordField.setEnabled(false);
            confirmPasswordField.setVisible(false);
            confirmPasswordField.setEnabled(false);            
            passLabel.setVisible(false);
            passLabel.setEnabled(false);
            cPassLabel.setVisible(false);
            cPassLabel.setEnabled(false);
            showPassCB.setVisible(false);
            showPassCB.setEnabled(false);
            genderCombo.setEnabled(false);
            dcCombo.setEnabled(false);
                    
        }
    }
  
   // procedure to set the textfields of the user that was selected
    public void showSelectedUserDetails() {
        // SEARCH FOR FIELDS IN SELECTED PLAYER IN PLAYER TABLE
        String username = "";
        String password = "";
        String firstName = "";
        String lastName = "";
        String gender = "";
        String dateOfBirth = null;
        
        PreparedStatement psA = null;
        ResultSet rsA = null;
        try {
            
            // conditional select query
            String queryA = "SELECT * FROM user WHERE user_id = ?";
            
            // get returned connection from calling the function in the class, SQLConnection, and prepare statement with the given query
            psA = SQLConnection.getConnection().prepareStatement(queryA);

            // set parameters of query
            psA.setInt(1, selectedID);           
            rsA = psA.executeQuery();

            while (rsA.next()) {
                username = rsA.getString("username");
                password = rsA.getString("password");
                firstName = rsA.getString("firstName");
                lastName = rsA.getString("lastName");
                gender = rsA.getString("gender");
                dateOfBirth = rsA.getString("dateOfBirth");

            }             

            // convert integer variables to string to display on fields
            String IDShown = Integer.toString(selectedID);

            IDField.setText(IDShown);
            firstNameField.setText(firstName);
            lastNameField.setText(lastName);
            usernameField.setText(username);
            
            genderCombo.setSelectedItem(gender);
            if (passwordField.isEnabled() ) {
                passwordField.setText(password);  
            }

            // CONVERT FROM STRING TO JAVA.UTIL.DATE TO SHOW ON DCCOMBO
            if (dateOfBirth != null) {
                try {
                    Date convertedDate = new SimpleDateFormat("dd-MM-yyyy").parse(dateOfBirth);
                    dcCombo.setDate(convertedDate);
                // exception handling of parsing errors
                } catch (ParseException ex) {
                    Logger.getLogger(muCustomiseUser.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        // exception handling of SQL errors
        } catch (SQLException ex) {
            Logger.getLogger(badmintonLogin.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            // procedure to close connection
            SQLConnection.closeConnection(SQLConnection.getConnection(), rsA, psA, null);
        }
    }
    
    
    // update the audit log 
    public void updateAuditLog(String message, String firstName, String lastName) {
        // SET TIMESTAMP FOR SQL QUERY
        Date date = new Date();
        long time = date.getTime();
        Timestamp updateTimestamp = new Timestamp(time);
        
        // get the data of the user that is currently logged in
        String userWhoChanged = SessionManager.getUsername();
        int userWhoChangedID = SessionManager.getUserID();
        String change = (message + " player: "+ firstName + " " + lastName);
        
        if (message.equals("Deleted"))  { // deleted user
            // get returned connection from calling the function in the class, SQLConnection
            Connection con = SQLConnection.getConnection();
            Statement st = null;
            try {
                st = con.createStatement();
                if ((st.executeUpdate("INSERT INTO `change`(`change`, `timeOfChange`, `userWhoChanged`, `itemAffected`, `user_id`) VALUES ('"+change+"','"+updateTimestamp+"','"+userWhoChanged+"','" + "deleted player was ID: "+deletedID+"','" + userWhoChangedID + "')")) == 1)
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
    
    // execute the SQL query with the given query
    public void executeSQLQuery(String query, String message, String playerFirstName, String playerLastName) {
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
                updateAuditLog(message, playerFirstName, playerLastName);           
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
    
     // validate function to validate password
    public boolean validatePass(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            msgDlg.setMessage("Both password fields do not match. Re-type password");
            msgDlg.setVisible(true);
            return false;
        }
        else {
            // INVALID - if the password length is less than 6 characters or more than 29 characters
            if (password.length() > 6 && password.length() < 30) {
                // if the password suits the criteria, return true
                if (checkPass(password)) {
                    return true;
                }
                else {
                    msgDlg.setMessage("Password must have at least 1 captial letter, one number, no spaces and one lowercase letter");
                    msgDlg.setVisible(true);
                    return false;
                }
            }
            else {
                msgDlg.setMessage("Password length must be between 7-29 characters");
                msgDlg.setVisible(true);
                return false;
            }          
        }
    }
    
    // function to check that password follows specific criteria
    // [+] at least one captial letter
    // [+] at least one number
    // [+] no spaces
    // [+] at least one lowercase letter
    // [+] length must be between 7-29 characters
    private boolean checkPass(String password) {
        boolean hasNum = false; boolean hasCapitals = false; boolean hasLower = false; boolean hasSpaces = false; char currentCharacter;
        for (int i = 0; i < password.length(); i++) {
            currentCharacter = password.charAt(i);
            // VALID if password has at least one number
            if (Character.isDigit(currentCharacter)) {
                hasNum = true;               
            }
            // VALID if password has at least uppercase character
            else if (Character.isUpperCase(currentCharacter)) {
                hasCapitals = true;
            }
            // VALID if password has at least lowercase character
            else if (Character.isLowerCase(currentCharacter)) {
                hasLower = true;
            }
            // VALID if password has no whitespace
            else if (Character.isWhitespace(currentCharacter)) {
                hasSpaces = true;
            }
            if (hasNum && hasCapitals && hasLower && (hasSpaces == false)) {               
                return true;
            }        
        }
        return false;
    }

    
    // FUNCTION TO CHECK IF THE USER ID IS ALREADY LINKED WITH THE PLAYER ID
    public boolean checkLinkedWithPlayer(String firstNameInput, String lastNameInput) {
        String linkedWithUserResponse = null;
        boolean isPlayerLinkedWithUser = false;
        
        PreparedStatement psUserLink = null;
        ResultSet rsUserLink = null;
        try {


            // QUERY TO CHECK IF THE USERLINK SAYS YES OR NO
            String queryCheckUserLink = "SELECT * FROM `player` WHERE `firstName` =? AND `lastName` =?";
            psUserLink = SQLConnection.getConnection().prepareStatement(queryCheckUserLink);

            psUserLink.setString(1, firstNameInput);  
            psUserLink.setString(2, lastNameInput);  

            rsUserLink = psUserLink.executeQuery();

            while (rsUserLink.next()) {
                linkedWithUserResponse = rsUserLink.getString("linkedWithUser");
            }
        // exception handling of SQL errors
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            // procedure to close connection
            SQLConnection.closeConnection(SQLConnection.getConnection(), rsUserLink, psUserLink, null);
        }
        // return true if the linkedWithUser is currently set as "yes"
        if (linkedWithUserResponse.equals("yes")) {
            isPlayerLinkedWithUser = true;
        } 
        return isPlayerLinkedWithUser;      
    }
    
    // procedure to close this form and open manage users 
    public void quitWindow() {
        // opens manage users window and closes this form
        muWindow muW = new muWindow();
        muW.setVisible(true);
        muW.pack();
        muW.setLocationRelativeTo(null);
        muW.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
        plPlayer = new javax.swing.JPanel();
        playerIcon = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lastNameField = new javax.swing.JTextField();
        firstNameField = new javax.swing.JTextField();
        dcCombo = new com.toedter.calendar.JDateChooser();
        genderCombo = new javax.swing.JComboBox<>();
        helpIcon = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        IDField = new javax.swing.JTextField();
        usernameField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        passLabel = new javax.swing.JLabel();
        showPassCB = new javax.swing.JCheckBox();
        passwordField = new javax.swing.JPasswordField();
        confirmPasswordField = new javax.swing.JPasswordField();
        cPassLabel = new javax.swing.JLabel();
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
        plPlayer.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "USER", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 13), new java.awt.Color(255, 51, 0))); // NOI18N

        playerIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/systemIcons/muIcon.png"))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("FIRST NAME:");

        jLabel2.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("LAST NAME:");

        jLabel6.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("GENDER:");

        jLabel7.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("DATE OF BIRTH:");

        lastNameField.setEditable(false);
        lastNameField.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N

        firstNameField.setEditable(false);
        firstNameField.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        firstNameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                firstNameFieldActionPerformed(evt);
            }
        });

        dcCombo.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N

        genderCombo.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        genderCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Male", "Female", "Other" }));

        helpIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/systemIcons/helpIcon.png"))); // NOI18N
        helpIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                helpIconMouseClicked(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("USER ID:");

        IDField.setEditable(false);
        IDField.setBackground(new java.awt.Color(255, 204, 204));

        usernameField.setEditable(false);
        usernameField.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N

        jLabel3.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("USERNAME:");

        passLabel.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        passLabel.setForeground(new java.awt.Color(255, 255, 255));
        passLabel.setText("PASSWORD:");

        showPassCB.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        showPassCB.setForeground(new java.awt.Color(255, 255, 255));
        showPassCB.setText("SHOW PASSWORD");
        showPassCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showPassCBActionPerformed(evt);
            }
        });

        passwordField.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N

        confirmPasswordField.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N

        cPassLabel.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        cPassLabel.setForeground(new java.awt.Color(255, 255, 255));
        cPassLabel.setText("CONFIRM PASSWORD:");

        javax.swing.GroupLayout plPlayerLayout = new javax.swing.GroupLayout(plPlayer);
        plPlayer.setLayout(plPlayerLayout);
        plPlayerLayout.setHorizontalGroup(
            plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, plPlayerLayout.createSequentialGroup()
                .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(plPlayerLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(plPlayerLayout.createSequentialGroup()
                                .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel8)
                                    .addComponent(IDField, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 76, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, plPlayerLayout.createSequentialGroup()
                                .addComponent(playerIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(helpIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(42, 42, 42)))
                        .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(plPlayerLayout.createSequentialGroup()
                                .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel1))
                                .addGap(36, 36, 36)
                                .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(firstNameField, javax.swing.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE)
                                    .addComponent(lastNameField)))
                            .addGroup(plPlayerLayout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addGap(18, 18, 18)
                                .addComponent(dcCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(plPlayerLayout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addGap(63, 63, 63)
                                .addComponent(genderCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel3)
                            .addComponent(passLabel)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, plPlayerLayout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(showPassCB, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cPassLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(confirmPasswordField)
                            .addComponent(usernameField, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
                            .addComponent(passwordField, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE))))
                .addGap(35, 35, 35))
        );
        plPlayerLayout.setVerticalGroup(
            plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(plPlayerLayout.createSequentialGroup()
                .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(plPlayerLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(firstNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)))
                    .addComponent(helpIcon))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lastNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(20, 20, 20)
                .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(usernameField, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(plPlayerLayout.createSequentialGroup()
                        .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(confirmPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cPassLabel)
                            .addComponent(showPassCB))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                        .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(genderCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addGap(18, 18, 18)
                        .addComponent(dcCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(26, 26, 26))
            .addGroup(plPlayerLayout.createSequentialGroup()
                .addComponent(playerIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(IDField, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(passLabel))
                .addGap(0, 0, Short.MAX_VALUE))
        );

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
                        .addGap(72, 72, 72)
                        .addComponent(btnDelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnUpdate))
                    .addGroup(jPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(plPlayer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(81, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel);
        jPanel.setBounds(0, 6, 1070, 530);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        // quits this form
        quitWindow();
    }//GEN-LAST:event_btnExitActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // confirmation to do this action
        msgDlg.setMessage("Confirm Update User");
        msgDlg.setVisible(true);
        // get returnStatus of message dialog (clickig OK = 1, clicking cancel = 0)
        int confirmDelete = msgDlg.getReturnStatus();
        if (confirmDelete == 1 ) {
            // get ID of selected user
            selectedID = badmintonUser.getSelectedUserID();
           
            String firstNameInput = firstNameField.getText();
            String lastNameInput = lastNameField.getText();
            String passwordInput = String.valueOf(passwordField.getPassword());
            String confirmPassInput = String.valueOf(confirmPasswordField.getPassword());
            
            boolean passwordValid = validatePass(passwordInput,confirmPassInput);
            
            String genderInput = (String) genderCombo.getSelectedItem();
            Date bDateInput = dcCombo.getDate();
            String birthDate = null;
            
            if (genderInput == null) {
                // if the comboBox for gender is blank
                msgDlg.setMessage("Select gender");
                msgDlg.setVisible(true);
            }  
            else if(bDateInput == null) {
                // reject if the input for date of birth is blank
                msgDlg.setMessage("Add a date of birth");
                msgDlg.setVisible(true);           
            } else if (!passwordValid) {
                // reject if the input for password is invalid
                System.out.println("Invalid password input");
            } else {
                // data is valid therefore update the user's details
                SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
                birthDate = dateformat.format(dcCombo.getDate());
                
                String query = "UPDATE `user` SET `password`='"+ confirmPassInput +"',`firstName`='"+firstNameInput+"',`lastName`='"+lastNameInput+"',`gender`='"+genderInput+"',`dateOfBirth`='"+birthDate+"' WHERE `user_id` = "+selectedID;
                executeSQLQuery(query,"Updated",firstNameInput,lastNameInput);     
                quitWindow();
            }
        }
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        String firstNameInput = firstNameField.getText();
        String lastNameInput = lastNameField.getText();
        
        int IDSelected = badmintonUser.getSelectedUserID();           
        String deletePlayerQuery = "DELETE FROM `user` WHERE user_id = " + IDSelected;
        deletedID = IDSelected;            
            
        // confirmation to do this action   
        msgDlg.setMessage("Confirm Delete User");
        msgDlg.setVisible(true);
        // get returnStatus of message dialog (clickig OK = 1, clicking cancel = 0)
        int confirmDelete = msgDlg.getReturnStatus();
        if (confirmDelete == 1 ) {    
            if (checkLinkedWithPlayer(firstNameInput,lastNameInput)) {
                msgDlg.setMessage("The user you want to delete, " + firstNameInput + " " + lastNameInput + ", was linked with a player profile in this system. Deleting this user would delete the link between user and player. Would you still like to delete this user?");
                msgDlg.setVisible(true); 
                int confirmDeleteLink = msgDlg.getReturnStatus();
                if (confirmDeleteLink == 1) {                
                    // QUERY TO DELETE PLAYER FROM PLAYER TABLE
                    executeSQLQuery(deletePlayerQuery,"Deleted",firstNameInput,lastNameInput);
                } else {
                    System.out.println("Cancelled Delete Player");
                }
            } else {
                // call subroutine to delete player with the given query
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

    private void showPassCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showPassCBActionPerformed
        // show the password if the tick is selected
        if (showPassCB.isSelected()) {
            passwordField.setEchoChar((char)0);
            confirmPasswordField.setEchoChar((char)0);
        }
        else {
            // if not selected then hide the password as asteriks
            passwordField.setEchoChar('*');
            confirmPasswordField.setEchoChar('*');
        }
    }//GEN-LAST:event_showPassCBActionPerformed

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
            java.util.logging.Logger.getLogger(muCustomiseUser.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(muCustomiseUser.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(muCustomiseUser.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(muCustomiseUser.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
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
                new muCustomiseUser().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField IDField;
    private javax.swing.JLabel LogLabel;
    private javax.swing.JButton btnDelete;
    private javax.swing.JToggleButton btnExit;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JLabel cPassLabel;
    private javax.swing.JPasswordField confirmPasswordField;
    private com.toedter.calendar.JDateChooser dcCombo;
    private javax.swing.JTextField firstNameField;
    private javax.swing.JComboBox<String> genderCombo;
    private javax.swing.JLabel helpIcon;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel;
    private javax.swing.JTextField lastNameField;
    private javax.swing.JLabel mainMenuLabel;
    private javax.swing.JLabel mpIcon;
    private javax.swing.JLabel passLabel;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JPanel plPlayer;
    private javax.swing.JLabel playerIcon;
    private javax.swing.JCheckBox showPassCB;
    private javax.swing.JPanel topBar;
    public javax.swing.JLabel userLoggedOn;
    private javax.swing.JTextField usernameField;
    // End of variables declaration//GEN-END:variables
}
