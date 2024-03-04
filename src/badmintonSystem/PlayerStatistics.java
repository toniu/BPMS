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
public class PlayerStatistics {
    
    private String movePosition;
    private int ranking;
    private int oldRanking;
    private double rankPoints;
    private int singleMatchWins;
    private int singleMatchLosses;
    private int doubleMatchWins;
    private int doubleMatchLosses;
    private int balance;
    private String form;
    private int singleTournamentWins;
    private int doubleTournamentWins;
    
    public PlayerStatistics(String movePosition, int ranking, int oldRanking, double rankPoints, int singleMatchWins, int singleMatchLosses, int doubleMatchWins, int doubleMatchLosses, int balance, String form, int singleTournamentWins, int doubleTournamentWins) {
        this.movePosition = movePosition;
        this.ranking = ranking;
        this.oldRanking = oldRanking;
        this.rankPoints = rankPoints;
        this.singleMatchWins = singleMatchWins;
        this.singleMatchLosses = singleMatchLosses;
        this.doubleMatchWins = doubleMatchWins;
        this.doubleMatchLosses = doubleMatchLosses;
        this.balance = balance;
        this.form = form;
        this.singleTournamentWins = singleTournamentWins;
        this.doubleTournamentWins = doubleTournamentWins;
    }
    
    public String getMovePosition()
    {
        if (ranking > oldRanking) {
            movePosition = "UP";
        } else if (ranking < oldRanking) {
            movePosition = "DOWN";
        } else {
            // must be the same ranking after update
            movePosition = null;
        }
        return movePosition;
    }
    
    public int getRanking()
    {
        return ranking;
    }
    
    public int getOldRanking()
    {
        return oldRanking;
    }
    
    public double getRankPoints()
    {
        return rankPoints;
    }
    
    public int getSingleMatchWins()
    {
        return singleMatchWins;
    }
    
    public int getSingleMatchLosses()
    {
        return singleMatchLosses;
    }
    
    public int getDoubleMatchWins()
    {
        return doubleMatchWins;
    }
    
    public int getDoubleMatchLosses()
    {
        return doubleMatchLosses;
    }
    
    public int getBalance()
    {
        return balance;
    }
    
    public String getForm()
    {
        return form;
    }
    
    public int getSingleTournamentWins()
    {
        return singleTournamentWins;
    }
    
    public int getDoubleTournamentWins()
    {
        return doubleTournamentWins;
    }

    /*public void setMovePosition(String movePos) {
        this.movePosition = movePos;
    }
    
    public void setRanking(int ranking) {
        this.ranking = ranking;
    }
    
    public void setOldRanking(int oldRanking) {
        this.oldRanking = oldRanking;
    }
    
    public void setRankPoints(double rankPoints) {
        this.rankPoints = rankPoints;
    }
    
    public void setSingleMatchWins(int singleMatchWins) {
        this.singleMatchWins = singleMatchWins;
    }
    
    public void setSingleMatchLosses(int singleMatchLosses) {
        this.singleMatchLosses = singleMatchLosses;
    }
    
    public void setDoubleMatchWins(int doubleMatchWins) {
        this.doubleMatchWins = doubleMatchWins;
    }
    
    public void setDoubleMatchLosses(int doubleMatchLosses) {
        this.doubleMatchLosses = doubleMatchLosses;
    }
    
    public void setBalance(int balance) {
        this.balance = balance;
    }
    
    public void setForm(String form) {
        this.form = form;
    }
    
    public void setSingleTournamentWins(int singleTournamentWins) {
        this.singleTournamentWins = singleTournamentWins;
    }
    
    public void setDoubleTournamentWins(int doubleTournamentWins) {
        this.doubleTournamentWins = doubleTournamentWins;
    }*/
}
