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
public class badmintonUser {
    // declaration of private variables of class
    private static int selectedUserID; // static variable used to select users to edit them in different windows
    
    private int id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String gender;
    private String dateOfBirth;
    private String verified;
    
    // constructor used, where the parameters are assigned to the values of the variables
    public badmintonUser(int ID, String username, String password, String firstName, String lastName, String gender, String dateOfBirth, String verified) {
        this.id = ID;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.verified = verified;
    }
    
    // static procedure to update the current user ID that is being selected on the users table when the user is accessing the badminton 'Manage users' window (muWindow)
    public static void selectUserID(int IDSelected) 
    {
        selectedUserID = IDSelected;
    }
    
    // static function to return the current user ID that is being selected on the users table when the user is accessing the badminton 'Manage users' window (muWindow)
    public static int getSelectedUserID() 
    {
        return selectedUserID;
    }
 
    // getter methods to return the corresponding variables
    public int getID()
    {
        return id;
    }
    
    public String getUsername()
    {
        return username;
    }
    
    public String getPassword()
    {
        return password;
    }
    
    public String getFirstName()
    {
        return firstName;
    }
    
    public String getLastName()
    {
        return lastName;
    }
    
    public String getGender()
    {
        return gender;
    }
    
    public String getDateOfBirth()
    {
        return dateOfBirth;
    }
    
    public String getVerified()
    {
        return verified;       
    }
}