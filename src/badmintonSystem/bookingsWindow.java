/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
// OBJECTIVE 9: A means of adding/editing bookings 
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author Neka
 */
public class bookingsWindow extends javax.swing.JFrame {
    
    msgDialog msgDlg = new msgDialog(new javax.swing.JFrame(),true);
    // global variables
    String[] oldItems = new String[5];
    String[] newItems = new String[5];
    int idDeleted = 0;
    
    /**
     * Creates new form bookingsWindow
     */
    public bookingsWindow() {
        initComponents();
        DefaultTableModel model = (DefaultTableModel)tblBookings.getModel();
        model.setRowCount(0);
        showBookingsInJTable();
        
    }
    
    
    // function to check if booking already exists
    public static boolean checkIfBookingAlreadyExists(int courtNo, String dateOfBooking, String timeOfBooking, int durationOfBooking) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean checkBooking = false;
        
        // query to search for any bookings where court No and dateSession is the same as their respective parameters
        String queryCheck = "SELECT * FROM `booking` WHERE `courtNo` =? AND `dateOfSession` =? AND `timeOfSession` =?";
        
        try {
            // prepare statement with query
            ps = SQLConnection.getConnection().prepareStatement(queryCheck);
            // set the parameters of query as the parameters of this function
            ps.setInt(1, courtNo);
            ps.setString(2, dateOfBooking);
            ps.setString(3, timeOfBooking);
            
            rs = ps.executeQuery();
            
            // if results are found then the exact booking already exists, thus return true
            if(rs.next())
            {
                checkBooking = true;
            }
        // exception handling of SQL errors
        } catch (SQLException ex) {
            Logger.getLogger(bookingsWindow.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            // call procedure to close connection
            SQLConnection.closeConnection(SQLConnection.getConnection(), rs, ps, null);
        }
        // return the final value
        return checkBooking;
    }
    
    // function to check if the user ID of the booking being updated matches the user logged into the system
    public static boolean checkIfUserValid(int bookingIDSearch) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean checkBooking = false;
        int otherUserOfBookingID = 0;
        
        // query to search for bookings where the ID is the function's parameter 
        String queryCheck = "SELECT * FROM `booking` WHERE `booking_id` =?";
        
        try {
            // get connection from SQLConnection class function and prepare statement with query
            ps = SQLConnection.getConnection().prepareStatement(queryCheck);
            // set parameter to the function's parameter 
            ps.setInt(1, bookingIDSearch);
            
            // execute the query
            rs = ps.executeQuery();
            
            // if results are found then get the ID of the other user who booked
            if(rs.next())
            {
                otherUserOfBookingID = rs.getInt("userOfBooking_id");
            }
        // exception handling of SQL errors
        } catch (SQLException ex) {
            Logger.getLogger(bookingsWindow.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            // call procedure to close connection
            SQLConnection.closeConnection(SQLConnection.getConnection(), rs, ps, null);
        }
        
        // if the user logged in ID is the same as the other user's ID then return as true
        if (SessionManager.getUserID() == otherUserOfBookingID) {
            checkBooking = true;
        }
        
        return checkBooking;
    }
    
    // function to return the full name of the user with the given user ID
    public String retrieveNameOfUser(int searchID) {
        String firstNameFound = "";
        String lastNameFound = "";
        String fullName = "";
        if (searchID != 0) {
            // the returned connection from getting the connection from the method in SQLConnection
            Connection connection = SQLConnection.getConnection();
            
            // query to search for first name and last name of user by linking the two SQL tables of 'booking' and 'user' 
            String query = "SELECT * FROM `user`, `booking` WHERE user.user_id = booking.userOfBooking_id AND user.user_id = " + searchID;
            PreparedStatement pst = null;
            ResultSet rs = null;

            try {
                // get connection from SQLConnection class function and prepare statement with query
                pst = connection.prepareStatement(query);
                 // execute the query
                rs = pst.executeQuery(query);
                
                // while results are being searched, retrieve the first name and last name
                while (rs.next()) {
                    firstNameFound = rs.getString("user.firstName");
                    lastNameFound = rs.getString("user.lastName");
                    // combine the two strings found
                    fullName = firstNameFound + " " + lastNameFound;
                }
            }
            // exception handling of SQL errors
            catch (SQLException e) {
                e.printStackTrace();
            } finally {
            // call procedure to close connection
            SQLConnection.closeConnection(connection, rs, pst, null);
            }
        }
        return fullName;
    }
    
    
    public ArrayList<badmintonBooking> getBookingsList()
    {
        ArrayList<badmintonBooking> bookingsList = new ArrayList<badmintonBooking>();
        Connection connection = SQLConnection.getConnection();
        
        String query = "SELECT * FROM `booking` ";
        Statement st = null;
        ResultSet rs = null;
        
        try {
            st = connection.createStatement();
            rs = st.executeQuery(query);
            
            badmintonBooking booking;
            
            while (rs.next()) {
                booking = new badmintonBooking(rs.getInt("booking_id"),rs.getInt("courtNo"),rs.getInt("durationOfSession"),rs.getString("dateOfSession"),rs.getString("timeOfSession"),rs.getString("status"),rs.getInt("userOfBooking_id"));
                bookingsList.add(booking);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // call procedure to close connection
            SQLConnection.closeConnection(connection, rs, null, st);
        }
        return bookingsList;
    }
    
    // get list of bookings from mySQL database
    public void showBookingsInJTable()
    {
        // get the arrayList from the method called
        ArrayList<badmintonBooking> list = getBookingsList();
        DefaultTableModel model = (DefaultTableModel)tblBookings.getModel();
        // array of the amount of rows in the table
        Object[] row = new Object[7];
        // for every item in the arrayList, add a new row with the contents of the item
        for (int i = 0; i < list.size(); i++) {
            row[0] = list.get(i).getID();
            row[1] = list.get(i).getCourtNo();
            row[2] = list.get(i).getDuration();
            row[3] = list.get(i).getDate();
            row[4] = list.get(i).getTime();
            row[5] = list.get(i).getStatus();
            row[6] = retrieveNameOfUser(list.get(i).getUserWhoBookedID());
            
            model.addRow(row);
        }
    }
    // procedure to get an array of old items of the booking before the user made changes to it
    public void getOldItems(String oldCourtNo, String oldDuration, String oldDate, String oldTime, String oldStatus) {
        oldItems[0] = oldCourtNo;
        oldItems[1] = oldDuration;
        oldItems[2] = oldDate;
        oldItems[3] = oldTime;
        oldItems[4] = oldStatus;
    }
    
    // procedure to get an array of new items of the booking after the user made changes to it
    public void getNewItems(String newCourtNo, String newDuration, String newDate, String newTime, String newStatus) {
        newItems[0] = newCourtNo;
        newItems[1] = newDuration;
        newItems[2] = newDate;
        newItems[3] = newTime;
        newItems[4] = newStatus;
    }
    
    // update the audit log 
    public void updateAuditLog(String message) {
        // SET TIMESTAMP FOR SQL QUERY
        Date date = new Date();
        long time = date.getTime();
        Timestamp updateTimestamp = new Timestamp(time);
        
        String userWhoChanged = SessionManager.getUsername();
        String change = (message + " booking");
        
        if (message.equals("Updated")) {
            String itemAffected = "";
            String itemChangedTo = "";

            for (int i = 0; i < oldItems.length; i++) {
                // CHECKS IF EACH FIELD WAS CHANGED
                // ADD NEW LOG IF A FIELD WAS CHANGED
                if (!oldItems[i].equals(newItems[i])) {
                    itemAffected = oldItems[i];
                    itemChangedTo = newItems[i];
                    
                    Connection con = SQLConnection.getConnection();
                    Statement st = null;
                    // update change table in mySQL database
                    try {
                        st = con.createStatement();
                        if ((st.executeUpdate("INSERT INTO `change`(`change`, `timeOfChange`, `userWhoChanged`, `itemAffected`, `itemChangedTo`, `user_id`) VALUES ('"+change+"','"+updateTimestamp+"','"+userWhoChanged+"','"+itemAffected + "','" + itemChangedTo + "','" + SessionManager.getUserID() + "')")) == 1)
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
        } else if (message.equals("Inserted")) {           
            Connection con = SQLConnection.getConnection();
            Statement st = null;
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
        } else  { // deleted BOOKING
            Connection con = SQLConnection.getConnection();
            Statement st = null;
            try {
                st = con.createStatement();
                if ((st.executeUpdate("INSERT INTO `change`(`change`, `timeOfChange`, `userWhoChanged`, `itemAffected`, `user_id`) VALUES ('"+change+"','"+updateTimestamp+"','"+userWhoChanged + "','" + "deleted booking was ID: " + idDeleted + "','" + SessionManager.getUserID() + "')")) == 1)
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
            // create statement 
            st = con.createStatement();
            if ((st.executeUpdate(query)) == 1)
            {
                msgDlg.setMessage("Data " + message + " successfully");
                msgDlg.setVisible(true);
                // update audit log
                updateAuditLog(message);
                // refresh tblBookings data
               DefaultTableModel model = (DefaultTableModel) tblBookings.getModel();
               model.setRowCount(0);
               showBookingsInJTable();
               
            } else {
                msgDlg.setMessage("Data not " + message);
                msgDlg.setVisible(true);
            }     
        // exception handling of SQL errors
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            // call procedure to close connection
            SQLConnection.closeConnection(con, null, null, st);
        }     
    }
    
    
    // validation on inputs before executing SQL queries
    public boolean validateAdd(String courtNo, String duration, String date, String time) {
        boolean courtNoValid = false;
        boolean durationValid = false;
        boolean dateValid = false;
        boolean timeValid = false;
        
        boolean isValid = false;
        
        int courtNoInput = 0;
        
        try {
            // attempt to convert string to integer 
           courtNoInput  = Integer.parseInt(courtNo);
           if (courtNoInput > 1) {
               courtNoValid = true;
           } else {
               msgDlg.setMessage("INVALID | Court No input must be positive integers only from 1 onwards");
                msgDlg.setVisible(true);
                courtNoValid = false;            
           }           
           // exception handling to validate integers
        } catch (NumberFormatException ex) {
            // if invalid then catch the format exception 
            msgDlg.setMessage("INVALID | Court No input must be positive integers only from 1 onwards");
            msgDlg.setVisible(true);
            courtNoValid = false;
        }       
        
        String digitRegex = "\\d+";
        
        if (duration.matches(digitRegex) && duration.length() > 0) {
            // duration cannot start with 0 (this is invalid)
            if (duration.startsWith("0")) {
                durationValid = false;
                msgDlg.setMessage("INVALID | Duration request must start with 1-9");
                msgDlg.setVisible(true);
            } else {
                // duration must be between 30 - 120 minutes
                int durationInteger = Integer.valueOf(duration);
                if (durationInteger > 120 || durationInteger < 30) {
                   durationValid = false;
                   msgDlg.setMessage("INVALID | Duration of booking has to range from 30 to 120 minutes");
                   msgDlg.setVisible(true);
                } else {
                   durationValid = true;
                }
            }
        }
        else {
            durationValid = false;
            msgDlg.setMessage("INVALID | Duration request must be positive numbers only");
            msgDlg.setVisible(true);  
        }
        
        // if the date is blank then it is invalid
        if (date.trim().equals("")) {
            dateValid = false;
            msgDlg.setMessage("INVALID | Date request is blank");
            msgDlg.setVisible(true);
        } else {
            dateValid = true;
        }   
        
        if (time.trim().equals("")) {
            timeValid = false;
            msgDlg.setMessage("INVALID | Time request is blank");
            msgDlg.setVisible(true);
        } else {
            // opening and closing times of badminton sessions in the local gym
            String earliestTime = "06:30";
            String latestTime = "20:00";
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

            try {
                Date parsedEarliestTime = sdf.parse(earliestTime);
                Date parsedlLatestTime = sdf.parse(latestTime);
                Date parsedTime = sdf.parse(time);
                // INVALID time if the booking time is before the opening times
                if (parsedTime.before(parsedEarliestTime)) {
                    timeValid = false;
                    msgDlg.setMessage("INVALID | Booking request is earlier than opening times (06:30)");
                    msgDlg.setVisible(true);
                // INVALID time if the booking time is after the closing times
                } else if (parsedTime.after(parsedlLatestTime)) {
                    timeValid = false;
                    msgDlg.setMessage("INVALID | Booking request is later than closing times (20:00)");
                    msgDlg.setVisible(true);
                // VALID time if the booking time is within the opening and closing times
                } else {
                    timeValid = true;
                    System.out.println("VALID | Booking is within the opening and closing times");
                }   
                // exception handling of parsin erross
            } catch (ParseException ex) {
                Logger.getLogger(bookingsWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }             
        
        // all these strings must be valid for the function to return as valid
        if (courtNoValid && durationValid && dateValid && timeValid) {
            isValid = true;
        } else {
            isValid = false;
        }
        return isValid;
    }
    
    //
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
        bookingsIcon = new javax.swing.JLabel();
        topBar = new javax.swing.JPanel();
        mainMenuLabel = new javax.swing.JLabel();
        LogLabel = new javax.swing.JLabel();
        userLoggedOn = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblBookings = new javax.swing.JTable();
        btnUpdate = new javax.swing.JButton();
        btnAdd = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        dcDate = new com.toedter.calendar.JDateChooser();
        Date date = new Date();
        SpinnerDateModel sm =
        new SpinnerDateModel (date, null, null, Calendar.HOUR_OF_DAY);
        timeSpinner = new javax.swing.JSpinner(sm);
        durationField = new javax.swing.JTextField();
        courtNoField = new javax.swing.JTextField();
        IDField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        btnRefresh = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        statusField = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        bookedByField = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1070, 530));
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

        bookingsIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/systemIcons/bookingIcon.png"))); // NOI18N

        topBar.setBackground(new java.awt.Color(255, 51, 0));

        mainMenuLabel.setFont(new java.awt.Font("Adobe Gothic Std", 0, 18)); // NOI18N
        mainMenuLabel.setForeground(new java.awt.Color(255, 255, 255));
        mainMenuLabel.setText("CLUB BADMINTON | BOOKINGS");

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

        tblBookings.setBackground(new java.awt.Color(0, 51, 102));
        tblBookings.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tblBookings.setForeground(new java.awt.Color(255, 255, 255));
        tblBookings.setModel(new javax.swing.table.DefaultTableModel(
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
                "ID", "COURT NO.", "DURATION", "DATE ", "TIME", "STATUS", "BOOKED BY"
            }
        ));
        tblBookings.setGridColor(new java.awt.Color(255, 0, 0));
        tblBookings.setSelectionBackground(new java.awt.Color(0, 51, 153));
        tblBookings.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblBookingsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblBookings);

        btnUpdate.setBackground(new java.awt.Color(255, 51, 0));
        btnUpdate.setFont(new java.awt.Font("Adobe Gothic Std", 0, 13)); // NOI18N
        btnUpdate.setForeground(new java.awt.Color(255, 255, 255));
        btnUpdate.setText("UPDATE");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnAdd.setBackground(new java.awt.Color(255, 51, 0));
        btnAdd.setFont(new java.awt.Font("Adobe Gothic Std", 0, 13)); // NOI18N
        btnAdd.setForeground(new java.awt.Color(255, 255, 255));
        btnAdd.setText("ADD");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        btnDelete.setBackground(new java.awt.Color(255, 51, 0));
        btnDelete.setFont(new java.awt.Font("Adobe Gothic Std", 0, 13)); // NOI18N
        btnDelete.setForeground(new java.awt.Color(255, 255, 255));
        btnDelete.setText("DELETE");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("BOOKING DETAILS");

        jLabel2.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("COURT NO:");

        jLabel3.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("DURATION (minutes):");

        jLabel4.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("DATE:");

        jLabel5.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("TIME:");

        dcDate.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N

        JSpinner.DateEditor de = new JSpinner.DateEditor(timeSpinner, "HH:mm");
        timeSpinner.setEditor(de);
        timeSpinner.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N

        durationField.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N

        courtNoField.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        courtNoField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                courtNoFieldActionPerformed(evt);
            }
        });

        IDField.setEditable(false);
        IDField.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N

        jLabel6.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("ID:");

        btnRefresh.setBackground(new java.awt.Color(255, 51, 0));
        btnRefresh.setFont(new java.awt.Font("Adobe Gothic Std", 0, 13)); // NOI18N
        btnRefresh.setForeground(new java.awt.Color(255, 255, 255));
        btnRefresh.setText("REFRESH");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("STATUS:");

        statusField.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N

        jLabel8.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("BOOKED BY:");

        bookedByField.setEditable(false);
        bookedByField.setBackground(new java.awt.Color(255, 204, 204));
        bookedByField.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N

        jLabel9.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("OPENING TIMES:  6:30 AM - 8:00 PM");

        javax.swing.GroupLayout jPanelLayout = new javax.swing.GroupLayout(jPanel);
        jPanel.setLayout(jPanelLayout);
        jPanelLayout.setHorizontalGroup(
            jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(topBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanelLayout.createSequentialGroup()
                        .addComponent(bookingsIcon)
                        .addGap(814, 814, 814)
                        .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelLayout.createSequentialGroup()
                        .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelLayout.createSequentialGroup()
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(bookedByField, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 599, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9))
                        .addGap(14, 14, 14)))
                .addGap(0, 28, Short.MAX_VALUE))
            .addGroup(jPanelLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelLayout.createSequentialGroup()
                        .addGap(142, 142, 142)
                        .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(66, 66, 66)
                        .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(61, 61, 61)
                        .addComponent(btnRefresh)
                        .addGap(60, 60, 60)
                        .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanelLayout.createSequentialGroup()
                            .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanelLayout.createSequentialGroup()
                                    .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(49, 49, 49)))
                            .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanelLayout.createSequentialGroup()
                                    .addGap(6, 6, 6)
                                    .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(durationField, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(courtNoField, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(dcDate, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(jPanelLayout.createSequentialGroup()
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(statusField, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(timeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelLayout.createSequentialGroup()
                            .addComponent(jLabel6)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelLayout.createSequentialGroup()
                                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(54, 54, 54))
                                .addComponent(IDField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(315, Short.MAX_VALUE))
        );
        jPanelLayout.setVerticalGroup(
            jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLayout.createSequentialGroup()
                .addComponent(topBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelLayout.createSequentialGroup()
                        .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnAdd)
                            .addComponent(btnUpdate)
                            .addComponent(btnDelete)
                            .addComponent(btnRefresh))
                        .addGap(31, 31, 31)
                        .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelLayout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(IDField, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel6))
                                .addGap(18, 18, 18)
                                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel2)
                                    .addComponent(courtNoField, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanelLayout.createSequentialGroup()
                                        .addGap(28, 28, 28)
                                        .addComponent(jLabel3))
                                    .addGroup(jPanelLayout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(durationField, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanelLayout.createSequentialGroup()
                                        .addGap(34, 34, 34)
                                        .addComponent(jLabel4))
                                    .addGroup(jPanelLayout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addComponent(dcDate, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelLayout.createSequentialGroup()
                        .addComponent(bookingsIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(233, 233, 233)))
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(timeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5))
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(18, 18, 18)
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(statusField, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(bookedByField, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        getContentPane().add(jPanel);
        jPanel.setBounds(0, 6, 1070, 530);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        // quit bookings window and return back to main menu
        badmintonMenu bMenu = new badmintonMenu();
        bMenu.setVisible(true);
        bMenu.pack();
        bMenu.setLocationRelativeTo(null);
        bMenu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.dispose();
    }//GEN-LAST:event_btnExitActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // assigment of all of the inputs from the text fields
        int IDInput = Integer.valueOf(IDField.getText());
        String courtNoInput = courtNoField.getText();
        String durationString = durationField.getText();
        int durationInput = Integer.valueOf(durationString);
        Date dateInput = dcDate.getDate();
        DateFormat dayFormat = new SimpleDateFormat("dd-MM-yyyy");
        String dateString = dayFormat.format(dateInput);
        
        Date timeInput = (Date) timeSpinner.getValue();
        DateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String timeString = timeFormat.format(timeInput);
        
        String statusInput = statusField.getText();
        
        boolean sameUser = checkIfUserValid(IDInput);
        
        // message dialog for user to confirm update booking
        msgDlg.setMessage("Confirm Update Booking ?");
        msgDlg.setVisible(true);
        // get the return status the user has set (OK = 1, CANCEL = 0)
        int confirmDelete = msgDlg.getReturnStatus();
        if (confirmDelete == 1 ) {
            // must be the same user who booked or an admin to update bookings
            if ((sameUser) || SessionManager.getUserRole().equals("admin")) {

                // original booking values (for UPDATE comparison)
                String oldCourtNo = null;
                String oldDuration = null;
                String oldDate = null;
                String oldTime = null;
                String oldStatus = null;
                
                PreparedStatement ps = null;
                ResultSet rs = null;
                try {
                    String query = "SELECT * FROM booking WHERE booking_id = ?";
                    ps = SQLConnection.getConnection().prepareStatement(query);

                    ps.setInt(1, IDInput);           
                    rs = ps.executeQuery();

                    while (rs.next()) {
                        oldCourtNo = rs.getString("courtNo");
                        oldDuration = Integer.toString(rs.getInt("durationOfSession"));
                        oldDate = rs.getString("dateOfSession");
                        oldTime = rs.getString("timeOfSession");
                        oldStatus = rs.getString("status");

                    }         
                } catch (SQLException ex) {
                    ex.printStackTrace();
                } finally {
                    // procedure to close connection
                    SQLConnection.closeConnection(SQLConnection.getConnection(), rs, ps, null);
                }

                // procedure that updates the array of old items
                getOldItems(oldCourtNo,oldDuration,oldDate,oldTime,oldStatus);
                // procedure that updates the array of new items after booking changes have been made
                getNewItems(courtNoInput,durationString,dateString,timeString,statusInput);
                
                // query to update booking
                String query = "UPDATE `booking` SET `courtNo`='"+courtNoInput+"',`durationOfSession`='"+durationInput+"',`dateOfSession`='"+dateString+"',`timeOfSession`='"+timeString+"' WHERE `booking_id` = "+IDInput;
                
                // call procedure to update with given query
                executeSQLQuery(query,"Updated");
            } else {
                msgDlg.setMessage("You can only update your own bookings");
                msgDlg.setVisible(true);
            }
        } else {
            System.out.println("Update Booking Cancelled");
        }
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        String courtNoInput = courtNoField.getText();
        String durationString = durationField.getText();
        Date dateInput = dcDate.getDate();
        DateFormat dayFormat = new SimpleDateFormat("dd-MM-yyyy");
        
        Date timeInput = (Date) timeSpinner.getValue();
        DateFormat timeFormat = new SimpleDateFormat("HH:mm");
        
        String dateString = "";
        String timeString = "";
        
        if (dateInput != null) {
           dateString = dayFormat.format(dateInput); 
        }
        if (timeInput != null) {
           timeString = timeFormat.format(timeInput); 
        }
        
        msgDlg.setMessage("Confirm Add Booking ?");
        msgDlg.setVisible(true);
        int confirmDelete = msgDlg.getReturnStatus();
        if (confirmDelete == 1 ) {
            boolean validateInsertUpdate = validateAdd(courtNoInput,durationString,dateString,timeString);
            if (validateInsertUpdate) {
                int durationInput = Integer.valueOf(durationString);
                int courtNoAsInteger = Integer.valueOf(courtNoInput);
                boolean bookingAlreadyExists = checkIfBookingAlreadyExists(courtNoAsInteger, dateString, timeString, durationInput);
                if (!bookingAlreadyExists) {
                    String query = "INSERT INTO `booking`(`courtNo`, `durationOfSession`, `dateOfSession`, `timeOfSession`, `userOfBooking_id`) VALUES ('"+courtNoInput+"','"+durationInput+"','"+dateString+"','"+timeString+"','"+ SessionManager.getUserID() + "')";
                    executeSQLQuery(query,"Inserted");          
                } else {
                   msgDlg.setMessage("Booking for the same date and time already occupied!");
                   msgDlg.setVisible(true);
                }
            }           
        } else {
            System.out.println("Add Booking Cancelled");
        }
    }//GEN-LAST:event_btnAddActionPerformed

    private void courtNoFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_courtNoFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_courtNoFieldActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        TableModel model = tblBookings.getModel();   
        int IDSelected = 0;
        
        if (IDField.getText() != null) {
            int IDInput = Integer.valueOf(IDField.getText());
            boolean sameUser = checkIfUserValid(IDInput);
            
            if ((sameUser) || SessionManager.getUserRole().equals("admin"))  {

                msgDlg.setMessage("Confirm Delete Booking ?");
                msgDlg.setVisible(true);
                int confirmDelete = msgDlg.getReturnStatus();
                if (confirmDelete == 1 ) {
                    // if table is empty
                    if (model.getValueAt(1,0).toString() == null) {
                        msgDlg.setMessage("No selected booking to delete");
                        msgDlg.setVisible(true);
                        System.out.println("empty table, no booking to delete");
                    } else {
                        IDSelected = Integer.valueOf(IDField.getText());
                        if (IDSelected == 0) {
                            msgDlg.setMessage("No selected booking to delete");
                            msgDlg.setVisible(true);
                        } else {
                            String query = "DELETE FROM `booking` WHERE booking_id = " + IDSelected;
                            idDeleted = IDSelected;            
                            executeSQLQuery(query,"Deleted");
                        }
                    }
                } else {
                    System.out.println("Delete Booking Cancelled");
                }
            } else {
                msgDlg.setMessage("Cannot delete a booking that was not yours");
            }
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void tblBookingsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblBookingsMouseClicked
        // display selected row in JTextFields
        int i = tblBookings.getSelectedRow();
        TableModel model = tblBookings.getModel();
        
        if (model.getValueAt(1,0).toString() == null) {
            System.out.println("empty table");
        }
        IDField.setText(model.getValueAt(i,0).toString());
        courtNoField.setText(model.getValueAt(i,1).toString());
        durationField.setText(model.getValueAt(i,2).toString());
        if (model.getValueAt(i,5) != null) {            
            statusField.setText(model.getValueAt(i,5).toString());
        }
        bookedByField.setText(model.getValueAt(i,6).toString());
        
        // CONVERT FROM STRING TO JAVA.UTIL.DATE TO SHOW ON DC DATE
        try {
            DefaultTableModel DTM = (DefaultTableModel)tblBookings.getModel();
            Date date = new SimpleDateFormat("dd-MM-yyyy").parse((String)DTM.getValueAt(i, 3));
            dcDate.setDate(date);
        // exception handling of parsing errors
        } catch (ParseException ex) {
            Logger.getLogger(bookingsWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // CONVERT FROM STRING TO JAVA.UTIL.DATE TO SHOW ON TIME SPINNER
        String timeString = model.getValueAt(i,4).toString();
        DateFormat tsSDF = new SimpleDateFormat("hh:mm");
        try {
            Date convertedTime = tsSDF.parse(timeString);
            Object timeValue = convertedTime;
            timeSpinner.setValue(timeValue);   
        // exception handling of parsing errors
        } catch (ParseException ex) {
            Logger.getLogger(bookingsWindow.class.getName()).log(Level.SEVERE, null, ex);
        }             
        
    }//GEN-LAST:event_tblBookingsMouseClicked

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        // convert the table's model to DTM
        DefaultTableModel model = (DefaultTableModel)tblBookings.getModel();
        // set row count to 0 and call procedure to show bookings in the JTable
        model.setRowCount(0);
        showBookingsInJTable();
    }//GEN-LAST:event_btnRefreshActionPerformed

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
            java.util.logging.Logger.getLogger(bookingsWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(bookingsWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(bookingsWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(bookingsWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new bookingsWindow().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField IDField;
    private javax.swing.JLabel LogLabel;
    private javax.swing.JTextField bookedByField;
    private javax.swing.JLabel bookingsIcon;
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnDelete;
    private javax.swing.JToggleButton btnExit;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JTextField courtNoField;
    private com.toedter.calendar.JDateChooser dcDate;
    private javax.swing.JTextField durationField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel mainMenuLabel;
    private javax.swing.JTextField statusField;
    private javax.swing.JTable tblBookings;
    private javax.swing.JSpinner timeSpinner;
    private javax.swing.JPanel topBar;
    public javax.swing.JLabel userLoggedOn;
    // End of variables declaration//GEN-END:variables
}
