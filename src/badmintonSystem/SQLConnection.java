/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
// OBJECTIVE 7: A means to form an organised connection of databases 
package badmintonSystem;

import com.mysql.jdbc.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Neka
 */

public class SQLConnection {
   
    private static Connection con;
    private static String conURL;
    private static String conUsername;
    private static String conPassword;
    
    // LOCAL CONNECTION: ("jdbc:mysql://localhost:3306/badmintonSchema?useSSL=false", "root", "root18");
    // COLLEGE CONNECTION: ("jdbc:mysql://mysql:3306/badmintonSchema?useSSL=false","student_ntoni","woking18");
    
    public static void updateConnectionDetails(String connectionURL, String username, String password) {
        conURL = connectionURL;
        conUsername = username;
        conPassword = password;
    }
    
    public static void closeConnection(java.sql.Connection con, ResultSet rs, PreparedStatement ps, Statement st) {
        System.out.println("-------- closing connection");
        // close the connection to prevent too much connections from opening  
        if (con != null) {  
            try {  
                con.close();  
                System.out.println("[+] connection closed");
            } catch (SQLException ex) {  
                System.out.println(ex);  
            }                
        }   
        // close the statement to prevent too many statements from opening  
        if (st != null) {  
            try {  
                st.close();  
                System.out.println("[+] statement closed");
            } catch (SQLException ex) {  
                System.out.println(ex);  
            }                
        }  
        // close the preparedStatement to prevent too many preparedStatements from opening  
        if (ps != null) {  
            try {  
                ps.close();  
                System.out.println("[+] preparedStatement closed");
            } catch (SQLException ex) {  
                System.out.println(ex);  
            }                
        } 
        // close the resultSet to prevent too much many resultSets from opening  
        if (rs != null) {  
            try {  
                rs.close();  
            } catch (SQLException ex) {  
                System.out.println("[+] resultSet closed");
                System.out.println(ex);  
            }                
        }

    }
    
    public static String getConnectionURL() {
        return conURL;
    }
    
    public static String getConnectionUsername() {
        return conUsername;
    }
    
    public static String getConnectionPassword() {
        return conPassword;
    }
    
    


    public static Connection getConnection() {
        msgDialog msgDlg = new msgDialog(new javax.swing.JFrame(),true);
        try {
            Class.forName("com.mysql.jdbc.Driver");
            try {
                // CONSIDER THE CONNECTION AS NULL IF ANY OF THE PARAMETERS ARE NULL TO AVOID CRASHES
                if (conURL == null || conUsername == null || conPassword == null) {
                    con = null;
                }
                // SET TIMEOUT FOR DRIVER MANAGER GET CONNECTION
                DriverManager.setLoginTimeout(10);
                // GET CONNECTION AND SET PARAMETERS BASED ON THE SET VARIABLES
                con = (Connection) DriverManager.getConnection(conURL, conUsername, conPassword);
                if (con != null) {
                    //System.out.println("Connected successfully");
                } else {
                   msgDlg.setMessage("Failed connection");
                   msgDlg.setVisible(true); 
                }
                return con;
            } catch (SQLException e) {
                // log an exception. for example:
                msgDlg.setMessage("Could not connect! " + e.getMessage());
                msgDlg.setVisible(true);
                // e.printStackTrace();
                return null;
            }
        } catch (ClassNotFoundException e) {
            // log an exception. for example:
            System.out.println("Class not found exception");
            msgDlg.setMessage("Class not found exception!" + e.getMessage());
            msgDlg.setVisible(true);
        } 
        return con;
    }   
}
