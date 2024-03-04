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
public class badmintonBooking {
    private int id;
    private int courtNo;
    private int duration;
    private String date;
    private String time;
    private String status;
    private int userWhoBookedID;
    
    public badmintonBooking(int ID, int courtNo, int durationOfSession, String dateOfSession, String timeOfSession, String status, int userWhoBookedId) {
        this.id = ID;
        this.courtNo = courtNo;
        this.duration = durationOfSession;
        this.date = dateOfSession;
        this.time = timeOfSession;
        this.status = status;
        this.userWhoBookedID = userWhoBookedId;
    }
    
    public int getID()
    {
        return id;
    }
    
    public int getCourtNo()
    {
        return courtNo;
    }
    
    public int getDuration()
    {
        return duration;
    }
    
    public String getDate()
    {
        return date;
    }
    
    public String getTime()
    {
        return time;
    }
    
    public String getStatus()
    {
        return status;
    }
    
    public int getUserWhoBookedID()
    {
        return userWhoBookedID;
    }
    
}
