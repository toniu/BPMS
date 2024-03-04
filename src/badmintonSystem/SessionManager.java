/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package badmintonSystem;
/**
 *
 * @author Neka
 */

// required imports
import java.util.Date;
import java.sql.Timestamp;

public class SessionManager {
    
    // declaration of private variables
    private static int userID;
    private static String username;
    private static boolean userLoggedOn;
    private static String status;
    private static String userRole;
    private static Timestamp startTime;
    private static Timestamp endTime;
    
    // use of constructor (a method to set the variables of an object being instantiated)
    public SessionManager(int userID, String username, boolean userloggedOn, String status, String userRole, Timestamp startTime, Timestamp endTime) {
        this.userID = userID;
        this.username = username;
        this.userLoggedOn = userloggedOn;
        this.status = status;
        this.userRole = userRole;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    
    // setter function to start a new session as a new user is logged in
    public static String startNewSession(int userIDrequest, String usernameRequest, String userRoleRequest)
    {
        if (userLoggedOn == false) {
            // USER IS NOW LOGGED ON
            userLoggedOn = true;
            
            // retrieve the user's details (their user ID, username and user role)
            userID = userIDrequest;
            username = usernameRequest;
            userRole = userRoleRequest;
            
            // create a timestamp of the time at which this new session stared
            startTime = getTimestamp();
            userLoggedOn = true;
            // return a new status and print to the console log
            status = "New session, USER ID: " + userID + " | USERNAME LOGGED ON: " + username + " | USER'S ROLE: " + userRole + " | START TIME: " + startTime;
            System.out.println(status);
        } else {
            Timestamp timeChecked = getTimestamp();
            System.out.println("Session already occuring, USER ID: " + userID + " | CURRENT USER: " + username + " | TIME CHECKED: " + timeChecked);
        }
        return status;
    }
    
    // setter function to get the currently running session
    public static String getCurrentSession()
    {
        // create timestamp of the time as which this status was checked
        Timestamp timeChecked = getTimestamp();
        if (userLoggedOn == true) {
            // return status and print to the console log
            status = "Current session, USER ID: " + userID + " | CURRENT USER: " + username + " | USER'S ROLE: " + userRole + " | TIME CHECKED: " + timeChecked;
            System.out.println(status);
        } else {
            // no session running, return new status of no session occuring
            status = "No session | TIME CHECKED: " + timeChecked;
        }
        return status;
    }
    
    // setter function to end a running session
    public static String endCurrentSession()
    {
        if (userLoggedOn == true) {
            // USER IS NOW LOGGED OFF
            userLoggedOn = false;
            
            // return status and print to the console log
            endTime = getTimestamp();
            // no session to end, return new status of no session occuring
            status = "Session ended, USER ID: " + userID + " | USERNAME LOGGED OFF: " + username + " | END TIME: " + endTime;
            System.out.println(status);
            
            // SET SESSION DETAILS OF USER TO NULL (since no user using session currently);
            userID = 0;
            username = null;          
            
        } else {
            // no user logged in, thus no session to end
            System.out.println("PROGRAM QUIT | No session to end");
        }
        return status;
    }
    
    // getter functions to return the corresponding variables
    public static int getUserID()
    {
        return userID;
    }
    
    public static String getUserRole()
    {
        return userRole;
    }
    
    public static String getUsername()
    {
        return username;
    }
    
    // return the timestamp of the time at which this function was called
    public static Timestamp getTimestamp()
    {
        // new instance of date (of the time at which this was executed)
        Date date = new Date();
        long time = date.getTime();
        // new instance of timestamp
        Timestamp newTimestamp = new Timestamp(time);
        return newTimestamp;
    }
    
}
