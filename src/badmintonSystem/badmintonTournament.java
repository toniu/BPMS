/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package badmintonSystem;

// required imports
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;

/**
 *
 * @author Neka
 */
public class badmintonTournament {
    // declaration of private variables of class
    private static List playerList;
    private static int selectedTournamentID; // static variable used to select tournaments to view them in different windows
    
    private int tournamentID;
    private String tournamentName;
    private String tournamentCategory;
    private String tournamentType;
    private int noOfPlayers;
    private String winnerOfTournamentID;
    private boolean tournamentCompleted;
    private String dateStarted;
    private String dateCompleted;
    
    // constructor used, where the parameters are assigned to the values of the variables
    public badmintonTournament (int tournamentId, String tName, String tCategory, String tType, int numOfPlayers, String winnerOfTID, boolean tCompleted, String tDateStarted, String tDateCompleted) {
        this.tournamentID = tournamentId;
        this.tournamentName = tName;
        this.tournamentCategory = tCategory;
        this.tournamentType = tType;
        this.noOfPlayers = numOfPlayers;
        this.winnerOfTournamentID = winnerOfTID;
        this.tournamentCompleted = tCompleted;
        this.dateStarted = tDateStarted;
        this.dateCompleted = tDateCompleted;
    }
    
    // static procedure to update the current tournament ID that is being selected on the tournament table when the user is accessing the badminton 'competitionsWindow' form
    public static void selectTournamentID(int IDSelected) 
    {
        selectedTournamentID = IDSelected;
    }
    
    // static function to return the current tournament ID that is being selected on the tournament table when the user is accessing the badminton 'competitionsWindow' form
    public static int getSelectedTournamentID() 
    {
        return selectedTournamentID;
    }
    
    public int getID() {
        return this.tournamentID;
    }
    
    public String getTournamentName() {
        return this.tournamentName;
    }
    
    public String getTournamentCategory() {
        return this.tournamentCategory;
    }
    
    public String getTournamentType() {
        return this.tournamentType;
    }
    
    public int getNoOfPlayers() {
        return this.noOfPlayers;
    }
    
    public String getWinnerOfTournamentID() {
        return this.winnerOfTournamentID;
    }
    
    public boolean getTournamentCompleted() {
        return this.tournamentCompleted;
    }
    
    public String getDateStarted() {
        return this.dateStarted;
    }
    
    public String getDateCompleted() {
        return this.dateCompleted;
    }
    
    // function which sets the player list in the tournament
    /**
     * The function takes a default List model as the parameter 
     * (which is retrieved from the current elements in the JList for
     * the list of players participating in the tournament) in the form, 
     * chooseTournamentType
     */
    public static List setPlayerList(DefaultListModel tournamentDLM) {
        // instantiate a new ArrayList to be used for the FOR loop
        playerList = new ArrayList<>();
        // clear the playerlist before setting a new one
        playerList.clear();
        /** for loop which adds each DefaultListModel element into the static 
         * playerList of this class by iterating the ArrayList at each index
         */
        for (int i = 0; i < tournamentDLM.size(); i++) {
            playerList.add(tournamentDLM.getElementAt(i));
        }
        
        return playerList;       
    }
    
    /** a getter method to return the current player list of the tournament 
     * being handled
     */
    public static List getPlayerList() {
        return playerList;
    }
    
}
