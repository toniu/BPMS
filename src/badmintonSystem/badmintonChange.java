/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package badmintonSystem;

import java.util.Date;

/**
 *
 * @author Neka
 */
public class badmintonChange {
    private int id;
    private String change;
    private String timeOfChange;
    private String userWhoChanged;
    private String itemAffected;
    private String itemChangedTo;
    
    public badmintonChange(int ID, String change, String timeOfChange, String userWhoChanged, String itemAffected, String itemChangedTo) {
        this.id = ID;
        this.change = change;
        this.timeOfChange = timeOfChange;
        this.userWhoChanged = userWhoChanged;
        this.itemAffected= itemAffected;
        this.itemChangedTo = itemChangedTo;
    }
    
    public int getID()
    {
        return id;
    }
    
    public String getChange()
    {
        return change;
    }
    
    public String getTimeOfChange()
    {
        return timeOfChange;
    }
    
    public String getUserWhoChanged()
    {
        return userWhoChanged;
    }
    
    public String getItemAffected()
    {
        return itemAffected;
    }
    
    public String getItemChangedTo()
    {
        return itemChangedTo;
    }
    
}
