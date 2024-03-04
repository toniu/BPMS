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
public class badmintonPlayer extends badmintonUser {
    private static int selectedPlayerID; // static variable used to select players to edit them in different windows
    
    // declaration of private variables of class
    private int id;
    private String hand;
    private String status;
    private String linkedWithUser;
    
    private PlayerStatistics statistics;
    
    public badmintonPlayer(int ID, String firstName, String lastName, String gender, String dateOfBirth, String hand, String status, int ranking, int oldRanking, double rankPoints, int singleMatchWins, int singleMatchLosses, int doubleMatchWins, int doubleMatchLosses, String form, int balance, int singleTournamentWins, int doubleTournamentWins, String linkedWithUser) {
        // a badminton user can be a badminton player as well, thus this class inherits the properties that the class, badmintonUser has
        super(0, "", "", firstName, lastName, gender, dateOfBirth, "");
        
        this.id = ID;
        this.hand = hand;
        this.status = status;
        
        // use of aggregation - each player has player statistics
        this.statistics = new PlayerStatistics("", ranking, oldRanking, rankPoints, singleMatchWins, singleMatchLosses, doubleMatchWins, doubleMatchLosses, balance, form, singleTournamentWins, doubleTournamentWins);
    }

    // returns the class instance, PlayerStatistics which is within the class, badmintonPlayer
    public PlayerStatistics getStatistics() {
        return statistics;
    }
    // static procedure to update the current player ID that is being selected on the player table when the user is accessing the badminton 'Manage players' window (mpWindow)
    public static void selectPlayerID(int IDSelected) 
    {
        selectedPlayerID = IDSelected;
    }
    
    // static function to return the current player ID that is being selected on the player table when the user is accessing the badminton 'Manage players' window (mpWindow)
    public static int getSelectedPlayerID() 
    {
        return selectedPlayerID;
    }
    
    // getter methods to return the corresponding variables
    
    /** the badminton user ID is different to the badminton player ID therefore
     * the getter method, getID() is overridden.
     * 
     */
    public int getID()
    {
        return id;
    }
    
    
    public String getHand()
    {
        return hand;
    }
    
    public String getStatus()
    {
        return status;
    }
    
    public String getLinkedWithUser()
    {
        return linkedWithUser;
    }
}
