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
public class badmintonMatch {
    private static int selectedFixtureID;
    private static int selectedResultID;
    
    private int id;
    private int sideAP1ID;
    private int sideAP2ID;
    private int sideBP1ID;
    private int sideBP2ID;
    private String sideAScore;
    private String sideBScore;
    private int courtNo;
    private String typeOfMatch;
    private String venueOfMatch;
    private String dateOfMatch;
    private String timeOfMatch;
    private String comments;
    private boolean fixtureCompleted;
    private int tournamentID;
    
    public badmintonMatch (int matchID, int sideAPlayer1ID, int sideAPlayer2ID, int sideBPlayer1ID, int sideBPlayer2ID, String sideAScore, String sideBScore, String typeOfMatch, String venueOfMatch, int courtNo, String dateOfMatch, String timeOfMatch, String comments, boolean fixtureCompleted, int tournamentID) {
        this.id = matchID;
        this.sideAP1ID = sideAPlayer1ID;
        this.sideAP2ID = sideAPlayer2ID;
        this.sideBP1ID = sideBPlayer1ID;
        this.sideBP2ID = sideBPlayer2ID;
        this.sideAScore = sideAScore;
        this.sideBScore = sideBScore;
        this.courtNo = courtNo;
        this.typeOfMatch = typeOfMatch;
        this.venueOfMatch = venueOfMatch;
        this.dateOfMatch = dateOfMatch;
        this.timeOfMatch = timeOfMatch;
        this.comments = comments;
        this.fixtureCompleted = fixtureCompleted;
        this.tournamentID = tournamentID;        
    }
    
    public static void selectFixtureID(int IDSelected) 
    {
        selectedFixtureID = IDSelected;
    }
    
    public static int getSelectedFixtureID() 
    {
        return selectedFixtureID;
    }
    
    public static void selectResultID(int IDSelected) 
    {
        selectedResultID = IDSelected;
    }
    
    public static int getSelectedResultID() 
    {
        return selectedResultID;
    }
    
    public int getID() {
        return this.id;
    }

    public int getSideAP1ID() {
        return this.sideAP1ID;
    }
    
    public int getSideAP2ID() {
        return this.sideAP2ID;
    }
    
    public int getSideBP1ID() {
        return this.sideBP1ID;
    }
    
    public int getSideBP2ID() {
        return this.sideBP2ID;
    }
    
    public String getSideAScore() {
        return this.sideAScore;
    }
    
    public String getSideBScore() {
        return this.sideBScore;
    }
    
    public int getCourtNo() {
        return this.courtNo;
    }
    
    public String getTypeOfMatch() {
        return this.typeOfMatch;
    }
    
    public String getVenueOfMatch() {
        return this.venueOfMatch;
    }
    
    public String getDateOfMatch() {
        return this.dateOfMatch;
    }
    
    public String getTimeOfMatch() {
        return this.timeOfMatch;
    }
    
    public String getComments() {
        return this.comments;
    }
    
    public boolean getFixtureCompleted() {
        return this.fixtureCompleted;
    }
    
    public int getTournamentID() {
        return this.tournamentID;
    }
}
