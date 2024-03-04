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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 *
 * @author Neka
 */
public class mpPlayerStats extends javax.swing.JFrame {
    
    msgDialog msgDlg = new msgDialog(new javax.swing.JFrame(),true);
    // global variables
    int selectedID = badmintonPlayer.getSelectedPlayerID();
    int deletedID = 0;
    String actionChosen = null;
    
    // arrays of items used for comparison if a PLAYER UPDATE is being made
    String[] oldItems = new String[6];
    String[] newItems = new String[6];

    /**
     * Creates new form mpPlayerStats
     */
    public mpPlayerStats() {
        initComponents();
    }
    
    public mpPlayerStats(String actionChosen) {
        initComponents();
        if (actionChosen.equals("UPDATE")) {
            
            // call subroutine to show details of selected player;
            showSelectedPlayerStats();
            
            // fields and comboBoxes to enable
       
        } else {
            // typeOfAction is VIEW so...
            // disable Update player if the user is viewing a player
            
            btnUpdate.setVisible(false);
            btnUpdate.setEnabled(false);  
            
            // disable the + / - buttons
            btnSMWAdd.setVisible(false);
            btnSMWAdd.setEnabled(false);
            
            btnSMWSubtract.setVisible(false);
            btnSMWSubtract.setEnabled(false);
            
            btnDMWAdd.setVisible(false);
            btnDMWAdd.setEnabled(false);
            
            btnDMWSubtract.setVisible(false);
            btnDMWSubtract.setEnabled(false);
            
            btnSMLAdd.setVisible(false);
            btnSMLAdd.setEnabled(false);
            
            btnSMLSubtract.setVisible(false);
            btnSMLSubtract.setEnabled(false);
            
            btnDMLAdd.setVisible(false);
            btnDMLAdd.setEnabled(false);
            
            btnDMLSubtract.setVisible(false);
            btnDMLSubtract.setEnabled(false);
            
            btnSTWAdd.setVisible(false);
            btnSTWAdd.setEnabled(false);
            
            btnSTWSubtract.setVisible(false);
            btnSTWSubtract.setEnabled(false);
            
            btnDTWAdd.setVisible(false);
            btnDTWAdd.setEnabled(false);
            
            btnDTWSubtract.setVisible(false);
            btnDTWSubtract.setEnabled(false);
            
            showSelectedPlayerStats();
        }
    }
    
    // procedure to set the text fields of the form to the retrieved record from the SQL database
    public void showSelectedPlayerStats() {
        // SEARCH FOR FIELDS IN SELECTED PLAYER IN PLAYER TABLE
        String firstName = "";
        String lastName = "";
        int ranking = 0;
        int SMW = 0;
        int SML = 0;
        int DMW = 0;
        int DML = 0;
        
        int STW = 0;
        int DTW = 0;
        int balance = 0;
        String form = "";
        double RP = 0;
        
        PreparedStatement psA = null;
        ResultSet rsA = null;
        try {
            String queryA = "SELECT * FROM `player`, `playerStatistics`, `playerForm` WHERE player.player_id = playerStatistics.playerOfStats_id " +
                "AND player.player_id = playerForm.playerOfForm_id AND playerForm.playerOfForm_id = playerStatistics.playerOfStats_id AND player.player_id = ?";

            psA = SQLConnection.getConnection().prepareStatement(queryA);

            psA.setInt(1, selectedID);           
            rsA = psA.executeQuery();

            while (rsA.next()) {
                firstName = rsA.getString("firstName");
                lastName = rsA.getString("lastName");
                ranking = rsA.getInt("ranking"); 
                SMW = rsA.getInt("singleMatchWins"); 
                SML = rsA.getInt("singleMatchLosses"); 
                DMW = rsA.getInt("doubleMatchWins"); 
                DML = rsA.getInt("doubleMatchLosses"); 
                STW = rsA.getInt("singleTournamentWins"); 
                DTW = rsA.getInt("doubleTournamentWins"); 
            }
            
            int SMP = SMW + SML;
            int DMP = DMW + DML;

            // convert integer/double variables to string to display on fields
            String IDShown = Integer.toString(selectedID);
            String rankString = Integer.toString(ranking);
            
            String SMWString = Integer.toString(SMW);
            String SMLString = Integer.toString(SML);
            String SMPString = Integer.toString(SMP);
            
            String DMWString = Integer.toString(DMW);
            String DMLString = Integer.toString(DML);
            String DMPString = Integer.toString(DMP);
            
            String balanceString = Integer.toString(balance);
            
            String STWString = Integer.toString(STW);
            String DTWString = Integer.toString(DTW);
            
            // set text on textfields
            firstNameField.setText(firstName);
            lastNameField.setText(lastName);
            IDField.setText(IDShown);
            rankField.setText(rankString);
            
            SMWField.setText(SMWString);
            SMLField.setText(SMLString);
            SMPField.setText(SMPString);
            
            DMWField.setText(DMWString);
            DMLField.setText(DMLString);
            DMPField.setText(DMPString);
            
            balanceField.setText(balanceString);
            
            STWField.setText(STWString);
            DTWField.setText(DTWString);

            //getOldItems();
            
        // exception handling of SQL errors    
        } catch (SQLException ex) {
            Logger.getLogger(mpPlayerStats.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            // procedure to close connection
            SQLConnection.closeConnection(SQLConnection.getConnection(), rsA, psA, null);
        }
        
    } 
    
    // execute the SQL query
    public void executeSQLQuery(String query, String message) {
        // get returned connection from the function called in the class, SQLConnection
        Connection con = SQLConnection.getConnection();
        Statement st = null;
        try {
            st = con.createStatement();
            if ((st.executeUpdate(query)) == 1)
            {
                if (message.equals("statistics Updated")) {
                    msgDlg.setMessage("Player " + message + " successfully");
                    msgDlg.setVisible(true);
                }             
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
    
    // procedure to recalculate the balance ( = the no. of matches won - the no. of matches lost)
    public void recalculateBalance() {
        int SMW = Integer.valueOf(SMWField.getText());
        int SML = Integer.valueOf(SMLField.getText());
        int DMW = Integer.valueOf(DMWField.getText());
        int DML = Integer.valueOf(DMLField.getText());
        
        int balance = (SMW+DMW)-(SML+DML);
        balanceField.setText(Integer.toString(balance));
    }
    
    // procedure to recalculate the number of single matches played ( = singles match wins + singles match losses)
    public void recalculateSingleMatchesPlayed() {
        int SMW = Integer.valueOf(SMWField.getText());
        int SML = Integer.valueOf(SMLField.getText());
        
        int SMP = SMW + SML;
        SMPField.setText(Integer.toString(SMP));
    }
    
    // procedure to recalculate the number of double matches played ( = doubles match wins + doubles match losses)
    public void recalculateDoubleMatchesPlayed() {
        int DMW = Integer.valueOf(DMWField.getText());
        int DML = Integer.valueOf(DMLField.getText());
        
        int DMP = DMW + DML;
        DMPField.setText(Integer.toString(DMP));
    }
    
    // procedure to recalculate the rank points 
    public void recalculateRankPoints() {
        // single match wins/losses and double match wins/losses
        int SMW = Integer.valueOf(SMWField.getText());
        int SML = Integer.valueOf(SMLField.getText());
        int DMW = Integer.valueOf(DMWField.getText());
        int DML = Integer.valueOf(DMLField.getText());
        
        // tournament wins for (singles, doubles)
        int STW = Integer.valueOf(STWField.getText());
        int DTW = Integer.valueOf(DTWField.getText());
        
        // total matches played
        int totalMatchesPlayed = (SMW + SML + DMW + DML);
        // balance
        int balance = (SMW+DMW)-(SML+DML);
        int formWins = 0;
        String formString = formField.getText();
        // for loop to count the number of wins (W) in the form string e.g. WWLLW would mean 3 wins in the last five matches played
        if (formString != null) {
            char indexChar = 0;
            for (int i = 0; i < formString.length(); i++) {
                indexChar = formString.charAt(i);
                if (indexChar == 'W') {
                    formWins = formWins + 1;
                }
            }
        }
        // formula used to calculate the rank points of the player
        double finalRankPoints = ((SMW*1.5)+(DMW*0.5))/totalMatchesPlayed + (balance+formWins/1.5) + (STW*4) + (DTW*2.5);
        double roundedRP = Math.floor(finalRankPoints * 100) / 100; 
        // if the player has negative points then set their rank points to zero (as the code must prevent negative rank points)
        if (roundedRP < 0) {
            roundedRP = 0;
        }
        // convert from double to string in order to put on textfield
        String RPString = Double.toString(roundedRP);
        rankPointsField.setText(RPString);
    }
    
    // function to update the form
    public String updateForm(String formString, String result) {
        String newFormString = formString;
        // check length of form string 
        if (formString.length() < 5 || formString == null) {
            // if the result was "WIN" (adding a new match win result) then add a "W" to the string
            if (result.equals("WIN")) {
                newFormString = "W" + newFormString;
            } else if (result.equals("LOSS")) { // result was "LOSS" (adding a new match loss result) then add an "L" to the string
                newFormString = "L" + newFormString;
            } else { // result was MINUS (taking away the match)
                if (formString.length() >= 2) {
                    newFormString = newFormString.substring(1);  
                }
            }
        } else {
            // if the user took away a result and the string is not null or its length is not less than 5 then substring the string at index 1.
            if (result.equals("MINUS")) {
                newFormString = newFormString.substring(1);
            } else {
                // if the user added a result of win or loss and the string is not null or its length is not less than 5 then shift the string (remove the last character) and then add the "W" or "L"
                newFormString = formString.charAt(formString.length() - 1) + formString.substring(0, formString.length() - 1);
                newFormString = newFormString.substring(1);
                if (result.equals("WIN")) {
                    // adds a "W" to the string
                    newFormString = "W" + newFormString;
                } else if (result.equals("LOSS")) { // result was "LOSS"
                    // adds an "L" to the string
                    newFormString = "L" + newFormString;
                }
            }
        }
        // set the textfield as the new string that was manipulated with the above code
        formField.setText(newFormString);
        // returns the string
        return newFormString;
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
        lastNameField = new javax.swing.JTextField();
        firstNameField = new javax.swing.JTextField();
        SMWField = new javax.swing.JTextField();
        formField = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        IDField = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        SMLField = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        SMPField = new javax.swing.JTextField();
        DMWField = new javax.swing.JTextField();
        btnSMWAdd = new javax.swing.JButton();
        btnSMLSubtract = new javax.swing.JButton();
        btnSMLAdd = new javax.swing.JButton();
        btnSMWSubtract = new javax.swing.JButton();
        btnDMWAdd = new javax.swing.JButton();
        DMLField = new javax.swing.JTextField();
        btnDMWSubtract = new javax.swing.JButton();
        btnDMLAdd = new javax.swing.JButton();
        btnDMLSubtract = new javax.swing.JButton();
        DMPField = new javax.swing.JTextField();
        balanceField = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        rankField = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        STWField = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        DTWField = new javax.swing.JTextField();
        btnSTWAdd = new javax.swing.JButton();
        btnSTWSubtract = new javax.swing.JButton();
        btnDTWAdd = new javax.swing.JButton();
        btnDTWSubtract = new javax.swing.JButton();
        helpIcon = new javax.swing.JLabel();
        btnUpdate = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        rankPointsField = new javax.swing.JTextField();

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
                .addContainerGap(39, Short.MAX_VALUE))
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
        plPlayer.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "EDIT PLAYER STATISTICS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 13), new java.awt.Color(255, 51, 0))); // NOI18N

        playerIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/systemIcons/muIcon.png"))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("FIRST NAME:");

        jLabel2.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("LAST NAME:");

        jLabel3.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("SINGLE MATCH WINS:");

        jLabel4.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("BALANCE:");

        jLabel5.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("FORM:");

        jLabel6.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("SINGLE MATCH LOSSES:");

        lastNameField.setEditable(false);
        lastNameField.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N

        firstNameField.setEditable(false);
        firstNameField.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        firstNameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                firstNameFieldActionPerformed(evt);
            }
        });

        SMWField.setEditable(false);
        SMWField.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        SMWField.setText("0");

        formField.setEditable(false);
        formField.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        formField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                formFieldActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("PLAYER ID:");

        IDField.setEditable(false);
        IDField.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        IDField.setText("0");

        jLabel9.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("SINGLE MATCHES PLAYED:");

        jLabel10.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("DOUBLE MATCH WINS:");

        SMLField.setEditable(false);
        SMLField.setText("0");

        jLabel11.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("DOUBLE MATCH LOSSES:");

        jLabel12.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("DOUBLE MATCHES PLAYED:");

        SMPField.setEditable(false);
        SMPField.setText("0");

        DMWField.setEditable(false);
        DMWField.setText("0");
        DMWField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DMWFieldActionPerformed(evt);
            }
        });

        btnSMWAdd.setBackground(new java.awt.Color(0, 102, 51));
        btnSMWAdd.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        btnSMWAdd.setForeground(new java.awt.Color(255, 255, 255));
        btnSMWAdd.setText("+");
        btnSMWAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSMWAddActionPerformed(evt);
            }
        });

        btnSMLSubtract.setBackground(new java.awt.Color(153, 0, 0));
        btnSMLSubtract.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        btnSMLSubtract.setForeground(new java.awt.Color(255, 255, 255));
        btnSMLSubtract.setText("-");
        btnSMLSubtract.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSMLSubtractActionPerformed(evt);
            }
        });

        btnSMLAdd.setBackground(new java.awt.Color(0, 102, 51));
        btnSMLAdd.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        btnSMLAdd.setForeground(new java.awt.Color(255, 255, 255));
        btnSMLAdd.setText("+");
        btnSMLAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSMLAddActionPerformed(evt);
            }
        });

        btnSMWSubtract.setBackground(new java.awt.Color(153, 0, 0));
        btnSMWSubtract.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        btnSMWSubtract.setForeground(new java.awt.Color(255, 255, 255));
        btnSMWSubtract.setText("-");
        btnSMWSubtract.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSMWSubtractActionPerformed(evt);
            }
        });

        btnDMWAdd.setBackground(new java.awt.Color(0, 102, 51));
        btnDMWAdd.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        btnDMWAdd.setForeground(new java.awt.Color(255, 255, 255));
        btnDMWAdd.setText("+");
        btnDMWAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDMWAddActionPerformed(evt);
            }
        });

        DMLField.setEditable(false);
        DMLField.setText("0");
        DMLField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DMLFieldActionPerformed(evt);
            }
        });

        btnDMWSubtract.setBackground(new java.awt.Color(153, 0, 0));
        btnDMWSubtract.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        btnDMWSubtract.setForeground(new java.awt.Color(255, 255, 255));
        btnDMWSubtract.setText("-");
        btnDMWSubtract.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDMWSubtractActionPerformed(evt);
            }
        });

        btnDMLAdd.setBackground(new java.awt.Color(0, 102, 51));
        btnDMLAdd.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        btnDMLAdd.setForeground(new java.awt.Color(255, 255, 255));
        btnDMLAdd.setText("+");
        btnDMLAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDMLAddActionPerformed(evt);
            }
        });

        btnDMLSubtract.setBackground(new java.awt.Color(153, 0, 0));
        btnDMLSubtract.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        btnDMLSubtract.setForeground(new java.awt.Color(255, 255, 255));
        btnDMLSubtract.setText("-");
        btnDMLSubtract.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDMLSubtractActionPerformed(evt);
            }
        });

        DMPField.setEditable(false);
        DMPField.setText("0");

        balanceField.setEditable(false);
        balanceField.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        balanceField.setText("0");
        balanceField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                balanceFieldActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("CURRENT RANKING:");

        rankField.setEditable(false);
        rankField.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        rankField.setText("0");

        jLabel13.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("SINGLE TOURNAMENT WINS:");

        STWField.setEditable(false);
        STWField.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        STWField.setText("0");

        jLabel14.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("DOUBLE TOURNAMENT WINS:");

        DTWField.setEditable(false);
        DTWField.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        DTWField.setText("0");
        DTWField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DTWFieldActionPerformed(evt);
            }
        });

        btnSTWAdd.setBackground(new java.awt.Color(0, 102, 51));
        btnSTWAdd.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        btnSTWAdd.setForeground(new java.awt.Color(255, 255, 255));
        btnSTWAdd.setText("+");
        btnSTWAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSTWAddActionPerformed(evt);
            }
        });

        btnSTWSubtract.setBackground(new java.awt.Color(153, 0, 0));
        btnSTWSubtract.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        btnSTWSubtract.setForeground(new java.awt.Color(255, 255, 255));
        btnSTWSubtract.setText("-");
        btnSTWSubtract.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSTWSubtractActionPerformed(evt);
            }
        });

        btnDTWAdd.setBackground(new java.awt.Color(0, 102, 51));
        btnDTWAdd.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        btnDTWAdd.setForeground(new java.awt.Color(255, 255, 255));
        btnDTWAdd.setText("+");
        btnDTWAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDTWAddActionPerformed(evt);
            }
        });

        btnDTWSubtract.setBackground(new java.awt.Color(153, 0, 0));
        btnDTWSubtract.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        btnDTWSubtract.setForeground(new java.awt.Color(255, 255, 255));
        btnDTWSubtract.setText("-");
        btnDTWSubtract.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDTWSubtractActionPerformed(evt);
            }
        });

        helpIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/systemIcons/helpIcon.png"))); // NOI18N
        helpIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                helpIconMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout plPlayerLayout = new javax.swing.GroupLayout(plPlayer);
        plPlayer.setLayout(plPlayerLayout);
        plPlayerLayout.setHorizontalGroup(
            plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(plPlayerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel13)
                    .addComponent(jLabel14)
                    .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(plPlayerLayout.createSequentialGroup()
                            .addComponent(STWField, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(btnSTWAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(btnSTWSubtract, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(plPlayerLayout.createSequentialGroup()
                            .addComponent(DTWField, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(btnDTWAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(btnDTWSubtract, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)))
                    .addComponent(jLabel8)
                    .addGroup(plPlayerLayout.createSequentialGroup()
                        .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(IDField, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(playerIcon, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addComponent(helpIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(rankField, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24)
                .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(plPlayerLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(balanceField, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 43, Short.MAX_VALUE)
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(formField, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26))
                    .addGroup(plPlayerLayout.createSequentialGroup()
                        .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(plPlayerLayout.createSequentialGroup()
                                .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, plPlayerLayout.createSequentialGroup()
                                        .addComponent(jLabel12)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(DMPField, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addGroup(plPlayerLayout.createSequentialGroup()
                                                .addComponent(jLabel3)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                                                .addComponent(SMWField, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(plPlayerLayout.createSequentialGroup()
                                                .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                    .addGroup(plPlayerLayout.createSequentialGroup()
                                                        .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                            .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                        .addGap(12, 12, 12))
                                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, plPlayerLayout.createSequentialGroup()
                                                        .addComponent(jLabel6)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                                .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                    .addComponent(SMPField, javax.swing.GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE)
                                                    .addComponent(SMLField)
                                                    .addComponent(DMWField)
                                                    .addComponent(DMLField))))
                                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(40, 40, 40)
                                .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(btnSMWAdd, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnDMLAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                                    .addComponent(btnSMLAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                                    .addComponent(btnDMWAdd, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                                .addGap(31, 31, 31)
                                .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(btnSMLSubtract, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 50, Short.MAX_VALUE)
                                    .addComponent(btnSMWSubtract, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                                    .addComponent(btnDMLSubtract, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                                    .addComponent(btnDMWSubtract, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)))
                            .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, plPlayerLayout.createSequentialGroup()
                                    .addComponent(jLabel2)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lastNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, plPlayerLayout.createSequentialGroup()
                                    .addComponent(jLabel1)
                                    .addGap(18, 18, 18)
                                    .addComponent(firstNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        plPlayerLayout.setVerticalGroup(
            plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(plPlayerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(plPlayerLayout.createSequentialGroup()
                        .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(firstNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(lastNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(SMWField, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnSMWAdd)
                            .addComponent(btnSMWSubtract))
                        .addGap(7, 7, 7)
                        .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(SMLField, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnSMLSubtract)
                            .addComponent(btnSMLAdd))
                        .addGap(8, 8, 8)
                        .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(SMPField, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(15, 15, 15)
                        .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(DMWField, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnDMWAdd)
                                .addComponent(btnDMWSubtract)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11)
                            .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(DMLField, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnDMLAdd)
                                .addComponent(btnDMLSubtract)))
                        .addGap(13, 13, 13)
                        .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(DMPField, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12))
                        .addGap(18, 18, 18)
                        .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(formField, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5)
                            .addComponent(jLabel4)
                            .addComponent(balanceField, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(74, 74, 74))
                    .addGroup(plPlayerLayout.createSequentialGroup()
                        .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(playerIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(helpIcon))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(IDField, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rankField, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(STWField, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnSTWAdd)
                            .addComponent(btnSTWSubtract))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(DTWField, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnDTWAdd)
                            .addComponent(btnDTWSubtract))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
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

        jLabel15.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText("RANK POINTS:");

        rankPointsField.setEditable(false);
        rankPointsField.setBackground(new java.awt.Color(255, 204, 204));
        rankPointsField.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N

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
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(btnUpdate, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                        .addComponent(btnExit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel15)
                    .addComponent(rankPointsField, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                        .addGap(101, 101, 101)
                        .addComponent(btnUpdate)
                        .addGap(45, 45, 45)
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(rankPointsField, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(plPlayer, javax.swing.GroupLayout.PREFERRED_SIZE, 438, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(31, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel);
        jPanel.setBounds(0, 6, 1060, 530);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        // returns back to the rankings table and closes this form
        mpRankings mpR = new mpRankings();
        mpR.setVisible(true);
        mpR.pack();
        mpR.setLocationRelativeTo(null);
        mpR.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.dispose();
    }//GEN-LAST:event_btnExitActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
       // convert the integer text fields from string to integer 
        int SMW = Integer.valueOf(SMWField.getText());
        int SML = Integer.valueOf(SMLField.getText());

        int DMW = Integer.valueOf(SMWField.getText());
        int DML = Integer.valueOf(SMLField.getText());
        
        int STW = Integer.valueOf(STWField.getText());
        int DTW = Integer.valueOf(DTWField.getText());
        
        int balance = Integer.valueOf(balanceField.getText());    
        String form = formField.getText();
        
        // message dialog to get confirmation from the user of if they would like to do this action
        msgDlg.setMessage("Confirm save changes?");
        msgDlg.setVisible(true);
        // getter function to return the status set from the user (OK = 1, CANCEL = 0)
        int confirmSaveChanges = msgDlg.getReturnStatus();
        // if the user clicked "OK"...
        if (confirmSaveChanges == 1) {
            // validation to check that the rankPoints are valid
            if (rankPointsField.getText().equals("") || rankPointsField.getText().equals("NaN")) {
                msgDlg.setMessage("Invalid input for rank points (rank points is not a valid number)");
                msgDlg.setVisible(true);
            } else {
                // parse the rank points into double and do an SQL update on the player's form and statstics
                double rankPoints = Double.parseDouble(rankPointsField.getText());
            
                String formQuery = "UPDATE `playerForm` SET `form`='" + form + "',`balance`='" + balance + "' WHERE `playerOfForm_id` = " + selectedID;
                String statsQuery = "UPDATE `playerStatistics` SET `rankPoints`='" + rankPoints + "', `singleMatchWins`='" + SMW + "', `singleMatchLosses`='" + SML + "', `doubleMatchWins`='" + DMW + "', `doubleMatchLosses`='" + DML + "', `singleTournamentWins`='" + STW + "',`doubleTournamentWins`='" + DTW + "' WHERE `playerOfStats_id` = "+selectedID;

                executeSQLQuery(formQuery,"form Updated");
                executeSQLQuery(statsQuery,"statistics Updated");
            }         
        }
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void firstNameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_firstNameFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_firstNameFieldActionPerformed

    private void helpIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_helpIconMouseClicked
        // message dialog to guide the user in what to do
        msgDlg.setMessage(" Use the + / - buttons to update the number of wins and losses for the player");
        msgDlg.setVisible(true);
    }//GEN-LAST:event_helpIconMouseClicked

    private void formFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_formFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_formFieldActionPerformed

    private void DMWFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DMWFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_DMWFieldActionPerformed

    private void DMLFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DMLFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_DMLFieldActionPerformed

    private void btnDMWAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDMWAddActionPerformed
        int doubleMatchWins = Integer.valueOf(DMWField.getText());
        // increment the value by 1
        doubleMatchWins = doubleMatchWins + 1;
        String newDMWValue = Integer.toString(doubleMatchWins);
        DMWField.setText(newDMWValue);
        
        // RE-CALCULATE doubleMatchesPlayed       
        recalculateDoubleMatchesPlayed();
        // RE-CALCULATE BALANCE
        recalculateBalance();
        // UPDATE FORM
        String formString = formField.getText();
        updateForm(formString, "WIN");
        // UPDATE RANK POINTS
        recalculateRankPoints();
    }//GEN-LAST:event_btnDMWAddActionPerformed

    private void btnDMLAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDMLAddActionPerformed
        int doubleMatchLosses = Integer.valueOf(DMLField.getText());
        // increment the value by 1
        doubleMatchLosses = doubleMatchLosses + 1;
        String newDMLValue = Integer.toString(doubleMatchLosses);
        DMLField.setText(newDMLValue);
        
        // RE-CALCULATE doubleMatchesPlayed       
        recalculateDoubleMatchesPlayed();
        // RE-CALCULATE BALANCE
        recalculateBalance();
        // UPDATE FORM
        String formString = formField.getText();
        updateForm(formString, "LOSS");
        // UPDATE RANK POINTS
        recalculateRankPoints();
    }//GEN-LAST:event_btnDMLAddActionPerformed

    private void btnSMWAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSMWAddActionPerformed
        int singleMatchWins = Integer.valueOf(SMWField.getText());
        // increment the value by 1
        singleMatchWins = singleMatchWins + 1;
        String newSMWValue = Integer.toString(singleMatchWins);
        SMWField.setText(newSMWValue);
        
        // RE-CALCULATE singleMatchesPlayed       
        recalculateSingleMatchesPlayed();
        // RE-CALCULATE BALANCE
        recalculateBalance();
        // UPDATE FORM
        String formString = formField.getText();
        updateForm(formString, "WIN");
        // UPDATE RANK POINTS
        recalculateRankPoints();
    }//GEN-LAST:event_btnSMWAddActionPerformed

    private void btnSMWSubtractActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSMWSubtractActionPerformed
        int singleMatchWins = Integer.valueOf(SMWField.getText());
        if (singleMatchWins >= 0) {
            singleMatchWins = singleMatchWins - 1;
            // prevents the number from being negative
            if (singleMatchWins < 0) {
                singleMatchWins = 0;
            }
            String newSMWValue = Integer.toString(singleMatchWins);
            SMWField.setText(newSMWValue);

            // RE-CALCULATE singleMatchesPlayed       
            recalculateSingleMatchesPlayed();
            // RE-CALCULATE BALANCE
            recalculateBalance();
            // UPDATE RANK POINTS
            recalculateRankPoints();
            // UPDATE FORM
            String formString = formField.getText();
            updateForm(formString, "MINUS");
        }
    }//GEN-LAST:event_btnSMWSubtractActionPerformed

    private void balanceFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_balanceFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_balanceFieldActionPerformed

    private void btnSMLAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSMLAddActionPerformed
        int singleMatchLosses = Integer.valueOf(SMLField.getText());
        // increment the value by 1
        singleMatchLosses = singleMatchLosses + 1;
        String newSMLValue = Integer.toString(singleMatchLosses);
        SMLField.setText(newSMLValue);
        
        // RE-CALCULATE singleMatchesPlayed       
        recalculateSingleMatchesPlayed();
        // RE-CALCULATE BALANCE
        recalculateBalance();
        // UPDATE FORM
        String formString = formField.getText();
        updateForm(formString, "LOSS");
        // UPDATE RANK POINTS
        recalculateRankPoints();
    }//GEN-LAST:event_btnSMLAddActionPerformed

    private void btnSMLSubtractActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSMLSubtractActionPerformed
        int singleMatchLosses = Integer.valueOf(SMLField.getText());
        if (singleMatchLosses >= 0) {
            singleMatchLosses = singleMatchLosses - 1;
            // prevents the number from being negative
            if (singleMatchLosses < 0) {
                singleMatchLosses = 0;
            }
            String newSMLValue = Integer.toString(singleMatchLosses);
            SMLField.setText(newSMLValue);

            // RE-CALCULATE singleMatchesPlayed       
            recalculateSingleMatchesPlayed();
            // RE-CALCULATE BALANCE
            recalculateBalance();
            // UPDATE RANK POINTS
            recalculateRankPoints();
            // UPDATE FORM
            String formString = formField.getText();
            updateForm(formString, "MINUS");
        }
    }//GEN-LAST:event_btnSMLSubtractActionPerformed

    private void btnDMWSubtractActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDMWSubtractActionPerformed
        int doubleMatchWins = Integer.valueOf(DMWField.getText());
        if (doubleMatchWins >= 0) {
            doubleMatchWins = doubleMatchWins - 1;
            // prevents the number from being negative
            if (doubleMatchWins < 0) {
                doubleMatchWins = 0;
            }
            String newDMWValue = Integer.toString(doubleMatchWins);
            DMWField.setText(newDMWValue);

            // RE-CALCULATE doubleMatchesPlayed       
            recalculateDoubleMatchesPlayed();
            // RE-CALCULATE BALANCE
            recalculateBalance();
            // UPDATE RANK POINTS
            recalculateRankPoints();
            // UPDATE FORM
            String formString = formField.getText();
            updateForm(formString, "MINUS");
        }
    }//GEN-LAST:event_btnDMWSubtractActionPerformed

    private void btnDMLSubtractActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDMLSubtractActionPerformed
        int doubleMatchLosses = Integer.valueOf(DMLField.getText());
        if (doubleMatchLosses >= 0) {
            doubleMatchLosses = doubleMatchLosses - 1;
            // prevents the number from being negative
            if (doubleMatchLosses < 0) {
                doubleMatchLosses = 0;
            }
            String newDMLValue = Integer.toString(doubleMatchLosses);
            DMLField.setText(newDMLValue);

            // RE-CALCULATE doubleMatchesPlayed       
            recalculateDoubleMatchesPlayed();
            // RE-CALCULATE BALANCE
            recalculateBalance();
            // UPDATE RANK POINTS
            recalculateRankPoints();
            // UPDATE FORM
            String formString = formField.getText();
            updateForm(formString, "MINUS");
        }
    }//GEN-LAST:event_btnDMLSubtractActionPerformed

    private void DTWFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DTWFieldActionPerformed
   
    }//GEN-LAST:event_DTWFieldActionPerformed

    private void btnSTWAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSTWAddActionPerformed
        int singleTournamentWins = Integer.valueOf(STWField.getText());
        // increment the value by 1
        singleTournamentWins = singleTournamentWins + 1;
        String newSTWValue = Integer.toString(singleTournamentWins);
        STWField.setText(newSTWValue);
        
        // UPDATE RANK POINTS
        recalculateRankPoints();
    }//GEN-LAST:event_btnSTWAddActionPerformed

    private void btnSTWSubtractActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSTWSubtractActionPerformed
        int singleTournamentWins = Integer.valueOf(STWField.getText());
        if (singleTournamentWins >= 0) {
            singleTournamentWins = singleTournamentWins - 1;
            // prevents the number from being negative
            if (singleTournamentWins < 0) {
                singleTournamentWins = 0;
            }
            String newSTWValue = Integer.toString(singleTournamentWins);
            STWField.setText(newSTWValue);

            // UPDATE RANK POINTS
            recalculateRankPoints();
        }        
        
    }//GEN-LAST:event_btnSTWSubtractActionPerformed

    private void btnDTWAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDTWAddActionPerformed
        int doubleTournamentWins = Integer.valueOf(DTWField.getText());
        // increment the value by 1
        doubleTournamentWins = doubleTournamentWins + 1;
        String newDTWValue = Integer.toString(doubleTournamentWins);
        DTWField.setText(newDTWValue);
        
        // UPDATE RANK POINTS
        recalculateRankPoints();
    }//GEN-LAST:event_btnDTWAddActionPerformed

    private void btnDTWSubtractActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDTWSubtractActionPerformed
        int doubleTournamentWins = Integer.valueOf(DTWField.getText());
        if (doubleTournamentWins >= 0) {
            doubleTournamentWins = doubleTournamentWins - 1;
            // prevents the number from being negative
            if (doubleTournamentWins < 0) {
                doubleTournamentWins = 0;
            }
            String newDTWValue = Integer.toString(doubleTournamentWins);
            DTWField.setText(newDTWValue);

            // UPDATE RANK POINTS
            recalculateRankPoints();
        }
    }//GEN-LAST:event_btnDTWSubtractActionPerformed

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
            java.util.logging.Logger.getLogger(mpPlayerStats.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(mpPlayerStats.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(mpPlayerStats.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(mpPlayerStats.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new mpPlayerStats().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField DMLField;
    private javax.swing.JTextField DMPField;
    private javax.swing.JTextField DMWField;
    private javax.swing.JTextField DTWField;
    private javax.swing.JTextField IDField;
    private javax.swing.JLabel LogLabel;
    private javax.swing.JTextField SMLField;
    private javax.swing.JTextField SMPField;
    private javax.swing.JTextField SMWField;
    private javax.swing.JTextField STWField;
    private javax.swing.JTextField balanceField;
    private javax.swing.JButton btnDMLAdd;
    private javax.swing.JButton btnDMLSubtract;
    private javax.swing.JButton btnDMWAdd;
    private javax.swing.JButton btnDMWSubtract;
    private javax.swing.JButton btnDTWAdd;
    private javax.swing.JButton btnDTWSubtract;
    private javax.swing.JToggleButton btnExit;
    private javax.swing.JButton btnSMLAdd;
    private javax.swing.JButton btnSMLSubtract;
    private javax.swing.JButton btnSMWAdd;
    private javax.swing.JButton btnSMWSubtract;
    private javax.swing.JButton btnSTWAdd;
    private javax.swing.JButton btnSTWSubtract;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JTextField firstNameField;
    private javax.swing.JTextField formField;
    private javax.swing.JLabel helpIcon;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
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
    private javax.swing.JTextField rankField;
    private javax.swing.JTextField rankPointsField;
    private javax.swing.JPanel topBar;
    public javax.swing.JLabel userLoggedOn;
    // End of variables declaration//GEN-END:variables
}
