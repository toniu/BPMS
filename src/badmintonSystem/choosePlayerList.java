/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package badmintonSystem;

// required imports
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JComboBox;
import javax.swing.JFrame;

/**
 *
 * @author Neka
 */
public class choosePlayerList extends javax.swing.JFrame {
    private static String typeOfFixtureChosen = null;
    private static String sideAP1PlayerChosen = null;
    private static String sideAP2PlayerChosen = null;
    private static String sideBP1PlayerChosen = null;
    private static String sideBP2PlayerChosen = null;
    private static int returnStatus;
    
    // message dialog
    msgDialog msgDlg = new msgDialog(new javax.swing.JFrame(),true);
    
    String actionMade = "";
    String match = "";

    /**
     * Creates new form mpWindow
     */
    
    public choosePlayerList() {
        initComponents();
    }
    
    public choosePlayerList(String actionChosen, String matchStatus, String typeOfFixtureSelected, String sideAP1Selected, String sideAP2Selected, String sideBP1Selected, String sideBP2Selected) {
        initComponents();
        actionMade = actionChosen;
        match = matchStatus;
        selectedFixtureLabel.setText("[" + typeOfFixtureSelected + " match]" + " " + sideAP1Selected + ", " + sideAP2Selected + "VS. " + sideBP1Selected + ", " + sideBP2Selected);
        // call method to bind comboBox with the SQL database
        bindComboBoxWithDB(sideAP1Combo);
        bindComboBoxWithDB(sideAP2Combo);
        bindComboBoxWithDB(sideBP1Combo);
        bindComboBoxWithDB(sideBP2Combo);
    }
    
    // procedure to display error message
    public void displayErrorMessage(String errorMessage) {
       msgDlg.setMessage(errorMessage);
       msgDlg.setVisible(true);
   }

   // procedure which binds comboBox with the SQL database
   public void bindComboBoxWithDB(JComboBox JC) {
       JC.addItem("");
       // while loop which adds an element of the player and their ID to the ComboBox
       Statement st = null;
       ResultSet rs = null;
       try {
            st = SQLConnection.getConnection().createStatement();
            String selectQuery = "SELECT * FROM player";
            rs = st.executeQuery(selectQuery);
            while (rs.next()) {
                JC.addItem("[" + rs.getString(1) + "] " + rs.getString(2) + " " + rs.getString(3));
            }
       } catch (SQLException ex) {
           msgDlg.setMessage("Unable to receive players list: " + ex);
       } finally {
           // call procedure to close connection  
           SQLConnection.closeConnection(SQLConnection.getConnection(), rs, null, st); 
       }
   }
   
   // validation methods to avoid console log errors or the program from crashing 
   public boolean validateInputs(String typeOfFixtureInput, String sideAP1PlayerInput, String sideAP2PlayerInput, String sideBP1PlayerInput, String sideBP2PlayerInput) {
       String typeFixtureTest = typeOfFixtureInput;
       String sideAP1Test = sideAP1PlayerInput;
       String sideAP2Test = sideAP2PlayerInput;
       String sideBP1Test = sideBP1PlayerInput;
       String sideBP2Test = sideBP2PlayerInput;
       
       boolean isValid = false;
       
       // check if typeFixture is not null
       if (typeFixtureTest.equals("singles")) {
           if (!(sideAP1Test.equals("")) && !(sideBP1Test.equals("")) && !(sideAP1Test.equals(sideBP1Test))) {
               isValid = true;
           } else {
               isValid = false;
               displayErrorMessage(" | details are BLANK or DUPLICATE player entry");
           }
       } else if (typeFixtureTest.equals("doubles")) {
           // check that the comboboxes for the players are not blank
           isValid = true;
           String[] playerFields = new String[] {sideAP1Test, sideAP2Test, sideBP1Test, sideBP2Test};
           if (sideAP1Test.equals("") || sideAP2Test.equals("") || sideBP1Test.equals("") || sideBP2Test.equals("")) {
               isValid = false;
               displayErrorMessage(" | details are BLANK");
           } else {
               isValid = true;
               // checks if all fields are different to each other
               for (int i = 0; i < playerFields.length; i++) {
                   for (int j = 0; j < playerFields.length; j++) {
                       if (i != j) {
                            if (playerFields[i].equals(playerFields[j])) {
                                isValid = false;
                                displayErrorMessage(" | duplicate entry of the same player for one fixture");
                            }
                       }
                   }
               }
           }
       } else {
           isValid = false;
       }
       return isValid;
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
        mpIcon = new javax.swing.JLabel();
        topBar = new javax.swing.JPanel();
        mainMenuLabel = new javax.swing.JLabel();
        LogLabel = new javax.swing.JLabel();
        userLoggedOn = new javax.swing.JLabel();
        plFixture = new javax.swing.JPanel();
        sideAP1Label = new javax.swing.JLabel();
        sideAP2Label = new javax.swing.JLabel();
        helpIcon = new javax.swing.JLabel();
        sideAP1Combo = new javax.swing.JComboBox<>();
        sideAP2Combo = new javax.swing.JComboBox<>();
        sideBP2Combo = new javax.swing.JComboBox<>();
        sideBP2Label = new javax.swing.JLabel();
        sideBP1Label = new javax.swing.JLabel();
        sideBP1Combo = new javax.swing.JComboBox<>();
        typeLabel = new javax.swing.JLabel();
        typeCombo = new javax.swing.JComboBox<>();
        sideAP1Label3 = new javax.swing.JLabel();
        sideAP1Label4 = new javax.swing.JLabel();
        sideAP1Label5 = new javax.swing.JLabel();
        selectedFixtureLabel = new javax.swing.JLabel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1069, 458));
        setResizable(false);
        getContentPane().setLayout(null);

        jPanel.setBackground(new java.awt.Color(0, 51, 102));
        jPanel.setPreferredSize(new java.awt.Dimension(1069, 427));

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

        sideAP1Label.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        sideAP1Label.setForeground(new java.awt.Color(255, 255, 255));
        sideAP1Label.setText("PLAYER 1*:");

        sideAP2Label.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        sideAP2Label.setForeground(new java.awt.Color(255, 255, 255));
        sideAP2Label.setText("PLAYER 2:");

        helpIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/systemIcons/helpIcon.png"))); // NOI18N
        helpIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                helpIconMouseClicked(evt);
            }
        });

        sideAP1Combo.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N

        sideAP2Combo.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N

        sideBP2Combo.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N

        sideBP2Label.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        sideBP2Label.setForeground(new java.awt.Color(255, 255, 255));
        sideBP2Label.setText("PLAYER 2:");

        sideBP1Label.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        sideBP1Label.setForeground(new java.awt.Color(255, 255, 255));
        sideBP1Label.setText("PLAYER 1*:");

        sideBP1Combo.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N

        typeLabel.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        typeLabel.setForeground(new java.awt.Color(255, 255, 255));
        typeLabel.setText("TYPE OF MATCH:");

        typeCombo.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        typeCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "please select", "singles", "doubles" }));
        typeCombo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                typeComboItemStateChanged(evt);
            }
        });

        sideAP1Label3.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        sideAP1Label3.setForeground(new java.awt.Color(255, 255, 255));
        sideAP1Label3.setText("SIDE A:");

        sideAP1Label4.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        sideAP1Label4.setForeground(new java.awt.Color(255, 255, 255));
        sideAP1Label4.setText("SIDE B:");

        sideAP1Label5.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        sideAP1Label5.setForeground(new java.awt.Color(255, 255, 255));
        sideAP1Label5.setText("CURRENT SELECTED FIXTURE:");

        selectedFixtureLabel.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        selectedFixtureLabel.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout plFixtureLayout = new javax.swing.GroupLayout(plFixture);
        plFixture.setLayout(plFixtureLayout);
        plFixtureLayout.setHorizontalGroup(
            plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(plFixtureLayout.createSequentialGroup()
                .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(plFixtureLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(helpIcon)
                        .addGap(39, 39, 39)
                        .addComponent(sideAP1Label5)
                        .addGap(18, 18, 18)
                        .addComponent(selectedFixtureLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 485, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(plFixtureLayout.createSequentialGroup()
                        .addGap(93, 93, 93)
                        .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(sideAP1Label4)
                                .addGroup(plFixtureLayout.createSequentialGroup()
                                    .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(sideAP2Label)
                                            .addComponent(sideAP1Label))
                                        .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(sideBP2Label)
                                            .addComponent(sideBP1Label)))
                                    .addGap(18, 18, 18)
                                    .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(sideAP2Combo, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(sideBP1Combo, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(sideBP2Combo, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(sideAP1Combo, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(sideAP1Label3)
                                .addGroup(plFixtureLayout.createSequentialGroup()
                                    .addComponent(typeLabel)
                                    .addGap(18, 18, 18)
                                    .addComponent(typeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        plFixtureLayout.setVerticalGroup(
            plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, plFixtureLayout.createSequentialGroup()
                .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(plFixtureLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(helpIcon)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, plFixtureLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(sideAP1Label5)
                            .addComponent(selectedFixtureLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)))
                .addComponent(sideAP1Label3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(typeLabel)
                    .addComponent(typeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sideAP1Label)
                    .addComponent(sideAP1Combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sideAP2Combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sideAP2Label))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(sideAP1Label4)
                .addGap(7, 7, 7)
                .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sideBP1Label)
                    .addComponent(sideBP1Combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sideBP2Combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sideBP2Label))
                .addGap(161, 161, 161))
        );

        okButton.setBackground(new java.awt.Color(255, 51, 0));
        okButton.setFont(new java.awt.Font("Adobe Gothic Std", 0, 13)); // NOI18N
        okButton.setForeground(new java.awt.Color(255, 255, 255));
        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setBackground(new java.awt.Color(255, 51, 0));
        cancelButton.setFont(new java.awt.Font("Adobe Gothic Std", 0, 13)); // NOI18N
        cancelButton.setForeground(new java.awt.Color(255, 255, 255));
        cancelButton.setText("CANCEL");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
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
                .addGap(18, 18, 18)
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(okButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cancelButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(12, Short.MAX_VALUE))
        );
        jPanelLayout.setVerticalGroup(
            jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLayout.createSequentialGroup()
                .addComponent(topBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(mpIcon))
                    .addGroup(jPanelLayout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(okButton)
                        .addGap(18, 18, 18)
                        .addComponent(cancelButton))
                    .addGroup(jPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(plFixture, javax.swing.GroupLayout.PREFERRED_SIZE, 378, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        getRootPane().setDefaultButton(okButton);

        getContentPane().add(jPanel);
        jPanel.setBounds(0, 6, 1069, 450);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void helpIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_helpIconMouseClicked
        // help guide of what the comments mean
        msgDlg.setMessage("Comments about the badminton match e.g. 'Excellent performance shown from Player1 to win the match' (maximum 280 characters including spaces) ");
        msgDlg.setVisible(true);
    }//GEN-LAST:event_helpIconMouseClicked

    private void typeComboItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_typeComboItemStateChanged
        // dont bother showing the player 2 comboBoxes if the type of match chosen is singles, as those comboBoxes in this case would be unneeded
        if (typeCombo.getSelectedItem() == "singles") {
            sideAP2Combo.setSelectedIndex(0);
            sideBP2Combo.setSelectedIndex(0);
            
            sideAP2Combo.setEnabled(false);
            sideBP2Combo.setEnabled(false);
            
            sideAP2Label.setVisible(false);
            sideBP2Label.setVisible(false);
        } else if (typeCombo.getSelectedItem() == "doubles"){
            
            sideAP2Combo.setEnabled(true);
            sideBP2Combo.setEnabled(true);
            
            sideAP2Label.setVisible(true);
            sideBP2Label.setVisible(true);
        }
    }//GEN-LAST:event_typeComboItemStateChanged

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        String typeOfFixtureInput = typeCombo.getSelectedItem().toString();
        String sideAP1PlayerInput = sideAP1Combo.getSelectedItem().toString();
        String sideAP2PlayerInput = sideAP2Combo.getSelectedItem().toString();
        String sideBP1PlayerInput = sideBP1Combo.getSelectedItem().toString();
        String sideBP2PlayerInput = sideBP2Combo.getSelectedItem().toString();
        boolean inputsAreValid = validateInputs(typeOfFixtureInput, sideAP1PlayerInput, sideAP2PlayerInput, sideBP1PlayerInput, sideBP2PlayerInput);
        System.out.println("valid input?: " + inputsAreValid);
        // set returnStatus to 1 and the parameters as the selected items in the comboboxes
        if (inputsAreValid) {
            returnStatus = 1;
            doClose(typeOfFixtureInput, sideAP1PlayerInput, sideAP2PlayerInput, sideBP1PlayerInput, sideBP2PlayerInput);
        } else {
            System.out.println("Invalid input");
        }
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        // set returnStatus to 0 and the parameters as null
        returnStatus = 0;
        doClose(null, null, null, null, null);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void doClose(String typeOfFixtureInput, String sideAP1PlayerInput, String sideAP2PlayerInput, String sideBP1PlayerInput, String sideBP2PlayerInput) {
        // closes the form and returns back to the fixtureCustomise form with this method's parameters temporarily stored as the current inputs for the customised badminton match
        typeOfFixtureChosen = typeOfFixtureInput;
        sideAP1PlayerChosen = sideAP1PlayerInput;
        sideAP2PlayerChosen = sideAP2PlayerInput;
        sideBP1PlayerChosen = sideBP1PlayerInput;
        sideBP2PlayerChosen = sideBP2PlayerInput;
       

        fixtureCustomise fc = new fixtureCustomise(actionMade, match);
        fc.setVisible(true);
        fc.pack();
        fc.setLocationRelativeTo(null);
        fc.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        setVisible(false);
        this.dispose();
    }
    
    // getter methods 
    public static String getSelectedType() {
        return typeOfFixtureChosen;
    }
    
    public static String getSelectedAP1() {
        return sideAP1PlayerChosen;
    }
    
    public static String getSelectedAP2() {
        return sideAP2PlayerChosen;
    }
    
    public static String getSelectedBP1() {
        return sideBP1PlayerChosen;
    }
    
    public static String getSelectedBP2() {
        return sideBP2PlayerChosen;
    }
    
    public static int getReturnStatus() {
        return returnStatus;
    }
    
    // setter methods
    public static int setReturnStatus(int newReturnStatus) {
        returnStatus = newReturnStatus;
        return returnStatus;
    }
    
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
            java.util.logging.Logger.getLogger(choosePlayerList.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(choosePlayerList.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(choosePlayerList.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(choosePlayerList.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
                new choosePlayerList().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel LogLabel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel helpIcon;
    private javax.swing.JPanel jPanel;
    private javax.swing.JLabel mainMenuLabel;
    private javax.swing.JLabel mpIcon;
    private javax.swing.JButton okButton;
    private javax.swing.JPanel plFixture;
    private javax.swing.JLabel selectedFixtureLabel;
    private javax.swing.JComboBox<String> sideAP1Combo;
    private javax.swing.JLabel sideAP1Label;
    private javax.swing.JLabel sideAP1Label3;
    private javax.swing.JLabel sideAP1Label4;
    private javax.swing.JLabel sideAP1Label5;
    private javax.swing.JComboBox<String> sideAP2Combo;
    private javax.swing.JLabel sideAP2Label;
    private javax.swing.JComboBox<String> sideBP1Combo;
    private javax.swing.JLabel sideBP1Label;
    private javax.swing.JComboBox<String> sideBP2Combo;
    private javax.swing.JLabel sideBP2Label;
    private javax.swing.JPanel topBar;
    private javax.swing.JComboBox<String> typeCombo;
    private javax.swing.JLabel typeLabel;
    public javax.swing.JLabel userLoggedOn;
    // End of variables declaration//GEN-END:variables
}
