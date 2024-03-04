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
import javax.swing.DefaultListModel;
import javax.swing.JFrame;


/**
 *
 * @author Neka
 */
public class chooseTournamentType extends javax.swing.JFrame {
    // instantiations of new DLMS
    DefaultListModel playerDLM = new DefaultListModel();
    DefaultListModel tournamentDLM = new DefaultListModel();
    
    // declaration of private variables
    private static int returnStatus;
    private static String tournamentType;
    private static String tournamentCategory;
    
    // message dialog
    msgDialog msgDlg = new msgDialog(new javax.swing.JFrame(),true);


    /**
     * Creates new form mpWindow
     */
    
    public chooseTournamentType() {
        initComponents();
        bindListWithDB();
    }
    
    // set the tournament name text as the parameter 'tournamentName', if this form is instantiated with parameters instead of none.
    public chooseTournamentType(String tournamentName, String typeOfTournamentInput, String categoryOfTournamentInput) {
        initComponents();
        
        selectedFixtureLabel.setText(tournamentName);
        bindListWithDB();
        
    }
    
    // procedure to display error message dialogs
   public void displayErrorMessage(String errorMessage) {
       msgDlg.setMessage(errorMessage);
       msgDlg.setVisible(true);
   }
   
   // procedure to bind the JList with the SQL database
   public void bindListWithDB() {
       Statement st = null;
       ResultSet rs = null;
       try {
           // while results are being searched, add an element into the JList with each including the player name and their player ID
            st = SQLConnection.getConnection().createStatement();
            String selectQuery = "SELECT * FROM player";
            rs = st.executeQuery(selectQuery);
            
            while (rs.next()) {
                playerDLM.addElement("[" + rs.getString(1) + "] " + rs.getString(2) + " " + rs.getString(3));
            }
            playerJList.setModel(playerDLM);
        // exception handling of the SQL errors
       } catch (SQLException ex) {
           msgDlg.setMessage("Unable to receive players list: " + ex);
       } finally {
           // call procedure to close connection    
           SQLConnection.closeConnection(SQLConnection.getConnection(), rs, null, st);   
       }
            
       // call procedure to update tournament size
       updateTournamentSize();
   }
   
   // procedure to add a new element (a player) into the DLM
   // LIST OPERATION: adds a player into the tournament list and removes them from the player list of unpicked players
   private void addPlayerToTournament(String name) {
       if (name != null) {
           tournamentPlayerJList.setModel(tournamentDLM);
           playerJList.setModel(playerDLM);

           tournamentDLM.addElement(name);
           playerDLM.removeElement(name);
       }
   }
   
   // LIST OPERATION: procedure to clear all elements in tournament list and include all players into player list of unpicked players
   private void clearTournament() {
       for (int i = 0; i < tournamentDLM.size(); i++) {
            playerDLM.addElement(tournamentDLM.getElementAt(i));
        }
        tournamentDLM.clear();
        tournamentPlayerJList.setModel(tournamentDLM);
   }
   
   // procedure to update the tournament size (size of the tournament player JList)
   private void updateTournamentSize() {
       // retrieves the size of the list of players included in the tournament
       int size = tournamentPlayerJList.getModel().getSize();
       // set the text as size of tournament
       sizeField.setText(String.valueOf(size));
   }
   
   // a method to check if the number of players in the tournament is suitable for knockout brackets tournament
   private boolean validBracketsNumber(int testNumber) {
       while (testNumber > 1) {
           testNumber = testNumber / 2;
       }
       if ((testNumber*2) == 1) {
           return true;
       } else {
           return false;
       }
   }
   
   // function to check if a number is even or not by checking if the remainder is 0 when divided by 2.
   private boolean isEven (int testNumber) {
       if ((testNumber % 2) == 0) {
           return true;
       } else {
           return false;
       }
   }
   
   // validation methods
   public boolean validateInputs() {
       boolean isValid = false;
       int tournamentSize = Integer.valueOf(sizeField.getText());
       // checks if inputs are blank
       if (typeCombo.getSelectedItem() == "please select" || categoryCombo.getSelectedItem() == "please select") {
           isValid = false;
           displayErrorMessage("Inputs are blank");
       } else {
           if (typeCombo.getSelectedItem() == "round-robin") {
               if (categoryCombo.getSelectedItem() == "singles") {
                   // validation to check that the tournament size is within the acceptable range of 4-32 competitiors
                    if (tournamentSize < 4 || tournamentSize > 32) {
                        isValid = false;
                        displayErrorMessage("Limit amount of players to create singles round-robin tournament is 4-32 players");
                    } else {
                        // validation to check that the number of players is even
                        boolean even = isEven(tournamentSize);
                        if (even) {
                            isValid = true;
                        } else {
                            isValid = false;
                            displayErrorMessage("Tournament size to create singles round-robin tournament must be even");
                        }
                    }
               } else {
                   // validation to check that the tournament size is within the acceptable range of 8-64 competitiors
                   if (tournamentSize < 8 || tournamentSize > 64) {
                        isValid = false;
                        displayErrorMessage("Limit amount of players to create doubles round-robin tournament is 4-32 teams (8-64 players)");
                    } else {
                       // validation to check that the number of players is even
                        boolean even = isEven(tournamentSize);
                        if (even) {
                            isValid = true;
                        } else {
                            isValid = false;
                            displayErrorMessage("Tournament size to create doubles round-robin tournament must be even");
                        }
                    }
               }
           } else {
               boolean acceptableNumber = validBracketsNumber(tournamentSize);
               if (acceptableNumber) {
                    isValid = true;
                    if (categoryCombo.getSelectedItem() == "singles") {
                        // validation to check that the tournament size is within the acceptable range of 4-32 competitiors
                        if (tournamentSize < 4 || tournamentSize > 32) {
                            isValid = false;
                            displayErrorMessage("Limit amount of players to create singles knockouts brackets tournament is 4-32 players");
                        } else {
                            // validation to check that the number of players is even
                            boolean even = isEven(tournamentSize);
                            if (even) {
                                isValid = true;
                            } else {
                                isValid = false;
                                displayErrorMessage("Tournament size to create singles knockouts tournament must be even");
                            }
                        }
                    } else {
                        // validation to check that the tournament size is within the acceptable range of 8-64 competitiors
                        if (tournamentSize < 8 || tournamentSize > 64) {
                             isValid = false;
                             displayErrorMessage("Limit amount of players to create doubles knockouts brackets tournament is 4-32 teams (8-64 players)");
                        } else {
                            // validation to check that the number of players is even
                             boolean even = isEven(tournamentSize);
                            if (even) {
                                isValid = true;
                            } else {
                                isValid = false;
                                displayErrorMessage("Tournament size to create doubles knockouts tournament must be even");
                            }
                        }
                    }
               } else {
                    isValid = false;
                    displayErrorMessage("Invalid number of players for knockouts brackets tournament: must be 4, 8, 16, 32... competing sides etc.");
               }
           }
   
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
        typeLabel = new javax.swing.JLabel();
        typeCombo = new javax.swing.JComboBox<>();
        sideAP1Label5 = new javax.swing.JLabel();
        selectedFixtureLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tournamentPlayerJList = new javax.swing.JList<>();
        categoryCombo = new javax.swing.JComboBox<>();
        sideAP2Label1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        playerJList = new javax.swing.JList<>();
        btnAddPlayer = new javax.swing.JButton();
        btnRemovePlayer = new javax.swing.JButton();
        btnClearAll = new javax.swing.JButton();
        typeLabel1 = new javax.swing.JLabel();
        sizeField = new javax.swing.JTextField();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1071, 718));
        setResizable(false);
        setSize(new java.awt.Dimension(1071, 718));
        getContentPane().setLayout(null);

        jPanel.setBackground(new java.awt.Color(0, 51, 102));

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

        plFixture.setBackground(new java.awt.Color(0, 51, 102));
        plFixture.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "CUSTOMISE TOURNAMENT", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 13), new java.awt.Color(255, 51, 0))); // NOI18N

        sideAP1Label.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        sideAP1Label.setForeground(new java.awt.Color(255, 255, 255));
        sideAP1Label.setText("TOURNAMENT CATEGORY:");

        sideAP2Label.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        sideAP2Label.setForeground(new java.awt.Color(255, 255, 255));
        sideAP2Label.setText("TOURNAMENT SIZE:");

        helpIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/systemIcons/helpIcon.png"))); // NOI18N
        helpIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                helpIconMouseClicked(evt);
            }
        });

        typeLabel.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        typeLabel.setForeground(new java.awt.Color(255, 255, 255));
        typeLabel.setText("TOURNAMENT TYPE:");

        typeCombo.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        typeCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "please select", "round-robin" }));
        typeCombo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                typeComboItemStateChanged(evt);
            }
        });

        sideAP1Label5.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        sideAP1Label5.setForeground(new java.awt.Color(255, 255, 255));
        sideAP1Label5.setText("CURRENT SELECTED TOURNAMENT:");

        selectedFixtureLabel.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        selectedFixtureLabel.setForeground(new java.awt.Color(255, 255, 255));

        tournamentPlayerJList.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jScrollPane1.setViewportView(tournamentPlayerJList);

        categoryCombo.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        categoryCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "please select", "singles", "doubles" }));
        categoryCombo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                categoryComboItemStateChanged(evt);
            }
        });

        sideAP2Label1.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        sideAP2Label1.setForeground(new java.awt.Color(255, 255, 255));
        sideAP2Label1.setText("PLAYER LIST:");

        playerJList.setBackground(new java.awt.Color(0, 51, 102));
        playerJList.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        playerJList.setForeground(new java.awt.Color(255, 255, 255));
        jScrollPane2.setViewportView(playerJList);

        btnAddPlayer.setBackground(new java.awt.Color(0, 102, 51));
        btnAddPlayer.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        btnAddPlayer.setForeground(new java.awt.Color(255, 255, 255));
        btnAddPlayer.setText("ADD SELECTED PLAYER");
        btnAddPlayer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddPlayerActionPerformed(evt);
            }
        });

        btnRemovePlayer.setBackground(new java.awt.Color(204, 0, 0));
        btnRemovePlayer.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        btnRemovePlayer.setForeground(new java.awt.Color(255, 255, 255));
        btnRemovePlayer.setText("REMOVE PLAYER FROM TOURNAMENT");
        btnRemovePlayer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemovePlayerActionPerformed(evt);
            }
        });

        btnClearAll.setBackground(new java.awt.Color(153, 0, 0));
        btnClearAll.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        btnClearAll.setForeground(new java.awt.Color(255, 255, 255));
        btnClearAll.setText("CLEAR ALL PLAYERS FROM TOURNAMENT");
        btnClearAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearAllActionPerformed(evt);
            }
        });

        typeLabel1.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        typeLabel1.setForeground(new java.awt.Color(255, 255, 255));
        typeLabel1.setText("TOURNAMENT SIZE:");

        sizeField.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N

        javax.swing.GroupLayout plFixtureLayout = new javax.swing.GroupLayout(plFixture);
        plFixture.setLayout(plFixtureLayout);
        plFixtureLayout.setHorizontalGroup(
            plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(plFixtureLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(helpIcon)
                .addGap(28, 28, 28)
                .addComponent(sideAP1Label5)
                .addGap(18, 18, 18)
                .addComponent(selectedFixtureLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 420, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(27, Short.MAX_VALUE))
            .addGroup(plFixtureLayout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(plFixtureLayout.createSequentialGroup()
                        .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(typeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(sideAP1Label, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(typeCombo, 0, 400, Short.MAX_VALUE)
                            .addComponent(categoryCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(91, 91, 91))
                    .addGroup(plFixtureLayout.createSequentialGroup()
                        .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(btnClearAll, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(btnRemovePlayer, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(typeLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, plFixtureLayout.createSequentialGroup()
                                    .addGap(48, 48, 48)
                                    .addComponent(sideAP2Label1, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(220, 220, 220))
                                .addGroup(plFixtureLayout.createSequentialGroup()
                                    .addGap(34, 34, 34)
                                    .addComponent(sideAP2Label, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(sizeField, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addContainerGap()))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, plFixtureLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(btnAddPlayer, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(87, 87, 87))))))
        );
        plFixtureLayout.setVerticalGroup(
            plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, plFixtureLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(sideAP1Label5)
                        .addComponent(helpIcon))
                    .addGroup(plFixtureLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(selectedFixtureLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(20, 20, 20)
                .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(typeLabel)
                    .addComponent(typeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(categoryCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sideAP1Label))
                .addGap(31, 31, 31)
                .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sideAP2Label1)
                    .addComponent(typeLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
                    .addComponent(jScrollPane2))
                .addGap(18, 18, 18)
                .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAddPlayer)
                    .addComponent(btnRemovePlayer))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(plFixtureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnClearAll)
                    .addComponent(sideAP2Label)
                    .addComponent(sizeField, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
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
                .addGap(37, 37, 37)
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cancelButton, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
                    .addComponent(okButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(13, Short.MAX_VALUE))
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
                        .addComponent(plFixture, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        getRootPane().setDefaultButton(okButton);

        getContentPane().add(jPanel);
        jPanel.setBounds(0, 6, 1070, 710);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void helpIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_helpIconMouseClicked
        // help message dialog to guide the user on what to do
        msgDlg.setMessage("Set the tournament type and category. To add players for a doubles tournament, the players must be arranged so that the next two players in the list form one team");
        msgDlg.setVisible(true);
    }//GEN-LAST:event_helpIconMouseClicked

    private void typeComboItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_typeComboItemStateChanged

    }//GEN-LAST:event_typeComboItemStateChanged
    
    // event for OK button
    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed

        String typeOfTournamentInput = typeCombo.getSelectedItem().toString();
        String categoryOfTournamentInput = categoryCombo.getSelectedItem().toString();

        boolean inputsAreValid = validateInputs();
        System.out.println("valid input?: " + inputsAreValid);
        if (inputsAreValid) {
            // call procedure to set player list as the chosen player list for tournament 
            badmintonTournament.setPlayerList(tournamentDLM);
            returnStatus = 1;
            doClose(typeOfTournamentInput,categoryOfTournamentInput);
        } else {
            System.out.println("invalid input");
        }
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        // set returnStatus as zero
        returnStatus = 0;
        doClose(null, null);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void categoryComboItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_categoryComboItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_categoryComboItemStateChanged

    private void btnAddPlayerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddPlayerActionPerformed
        // add a player into the tournament
        String selectedPlayer = playerJList.getSelectedValue();
        addPlayerToTournament(selectedPlayer);
        updateTournamentSize();       
    }//GEN-LAST:event_btnAddPlayerActionPerformed
    
    // event Remove player (removes player from the tournament)
    private void btnRemovePlayerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemovePlayerActionPerformed
        // get string value of the player to transfer from tournament JList to player JList 
        String selectedPlayer = tournamentPlayerJList.getSelectedValue();
        // get the index of the list
        int index = tournamentPlayerJList.getSelectedIndex();
        // LIST OPERATION: remove selected element from tournament list and add it back to the player list of unpicked players
        if (tournamentPlayerJList.isSelectedIndex(index)) {
            tournamentDLM.removeElementAt(index);
            playerDLM.addElement(selectedPlayer); 
            // update tournament size
            updateTournamentSize();
        }
    }//GEN-LAST:event_btnRemovePlayerActionPerformed
    
    // event to clear all players (removes all players from tournament and returns them back to the player list of unpicked players)
    private void btnClearAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearAllActionPerformed
        clearTournament();    
        // update tournament size
        updateTournamentSize();
    }//GEN-LAST:event_btnClearAllActionPerformed
    
    // closes the form and returns back to the to competitionCustomise form with this method's parameters temporarily stored as the current inputs for the customised badminton tournament
    private void doClose(String typeOfTournamentInput, String categoryOfTournamentInput) {
        tournamentType = typeOfTournamentInput;
        tournamentCategory = categoryOfTournamentInput;        
        
        competitionCustomise cc = new competitionCustomise("INSERT",typeOfTournamentInput, categoryOfTournamentInput);
        cc.setVisible(true);
        cc.pack();
        cc.setLocationRelativeTo(null);
        cc.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        setVisible(false);
        this.dispose();
    }
    
    // getter / setter methods
    public static int getReturnStatus() {
        return returnStatus;
    }
    
    public static int setReturnStatus(int newReturnStatus) {
        returnStatus = newReturnStatus;
        return returnStatus;
    }
    
    public static String getSelectedType() {
        return tournamentType;
    }
    
    public static String getSelectedCategory() {
        return tournamentCategory;
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
            java.util.logging.Logger.getLogger(chooseTournamentType.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(chooseTournamentType.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(chooseTournamentType.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(chooseTournamentType.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
                new chooseTournamentType().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel LogLabel;
    private javax.swing.JButton btnAddPlayer;
    private javax.swing.JButton btnClearAll;
    private javax.swing.JButton btnRemovePlayer;
    private javax.swing.JButton cancelButton;
    private javax.swing.JComboBox<String> categoryCombo;
    private javax.swing.JLabel helpIcon;
    private javax.swing.JPanel jPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel mainMenuLabel;
    private javax.swing.JLabel mpIcon;
    private javax.swing.JButton okButton;
    private javax.swing.JPanel plFixture;
    private javax.swing.JList<String> playerJList;
    private javax.swing.JLabel selectedFixtureLabel;
    private javax.swing.JLabel sideAP1Label;
    private javax.swing.JLabel sideAP1Label5;
    private javax.swing.JLabel sideAP2Label;
    private javax.swing.JLabel sideAP2Label1;
    private javax.swing.JTextField sizeField;
    private javax.swing.JPanel topBar;
    private javax.swing.JList<String> tournamentPlayerJList;
    private javax.swing.JComboBox<String> typeCombo;
    private javax.swing.JLabel typeLabel;
    private javax.swing.JLabel typeLabel1;
    public javax.swing.JLabel userLoggedOn;
    // End of variables declaration//GEN-END:variables
}
