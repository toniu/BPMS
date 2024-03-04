/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package badmintonSystem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;

/**
 *
 * @author Neka
 */
public class test {
    
    public static void runMain() {
        int tournamentID = 1;
        int amountOfPlayers = 32; // can be 4, 8, 16, or 32
        int numOfMatches = (amountOfPlayers / 2) - 1;
        int getRounds = 0;
        int divideMatches = numOfMatches + 1;
        while (divideMatches != 1) {
            divideMatches = divideMatches / 2;
            getRounds++;
        }
        System.out.println("Tournament ID: " + tournamentID);
        System.out.println("Amount of players: " + amountOfPlayers);
        System.out.println("Number of rounds: " + getRounds);
       
    }
    
    public static void runTest() {
        
        Stack <Integer> fixtureIDStack = new Stack <Integer>();
        Stack <Integer> playerinFixtureIDStack = new Stack <Integer>();
        
        fixtureIDStack.push(1);
        fixtureIDStack.push(2);
        fixtureIDStack.push(3);
        fixtureIDStack.push(4);
        
        playerinFixtureIDStack.push(1);
        playerinFixtureIDStack.push(2);
        playerinFixtureIDStack.push(3);
        playerinFixtureIDStack.push(4);
        
        playerinFixtureIDStack.push(5);
        playerinFixtureIDStack.push(6);
        playerinFixtureIDStack.push(7);
        playerinFixtureIDStack.push(8);
        
        playerinFixtureIDStack.push(9);
        playerinFixtureIDStack.push(10);
        playerinFixtureIDStack.push(11);
        playerinFixtureIDStack.push(12);
        
        playerinFixtureIDStack.push(13);
        playerinFixtureIDStack.push(14);
        playerinFixtureIDStack.push(15);
        playerinFixtureIDStack.push(16);
        
        Stack <Integer> numStack = new Stack <Integer>();
        int nextMatch = 0;
        for (int i = 0; i < playerinFixtureIDStack.size(); i++) {
             if (i % 4 == 0) {
                numStack.push(i);
            } else {
                numStack.push(numStack.get(i - 1));
            }

            // the calculation repeated to get the 1, 1, 2, 2, 3, 3 etc. sequence
            nextMatch = numStack.get(i) / 4 + 1;
            
            System.out.println(" Next record:  FIXTURE ID: " + fixtureIDStack.get(nextMatch - 1) + " | PLAYER ID: " + playerinFixtureIDStack.get(i));
            nextMatch++;
        }
        
        /*DefaultListModel DLM = new DefaultListModel();
        DefaultListModel DLM2 = new DefaultListModel();
        DLM.addElement("[1] BobA");
        DLM.addElement("[2] BobB");
        DLM.addElement("[3] CamA");
        DLM.addElement("[4] CamB");
        DLM.addElement("[5] ManA");
        DLM.addElement("[6] ManB");
        DLM.addElement("[7] DonA");
        DLM.addElement("[8] DonB");
        DLM.addElement("[9] JonA");
        DLM.addElement("[10] JonB");
        DLM.addElement("[11] EliA");
        DLM.addElement("[12] EliB");     
        
        badmintonTournament.setPlayerList(DLM);
        
        Stack <Integer> numStack = new Stack<Integer>();
        
        List listPlayers = badmintonTournament.getPlayerList(); // retrieve tournament player list
        int tournamentSize = listPlayers.size(); // size of tournament
        String playerIDString;
        int playerID;

        // loop to bind data/parameters to the prepared statement for each player in the list
        for (int nextRow = 0; nextRow < tournamentSize; nextRow++) {
                if (nextRow % 2 == 0) {
                    numStack.push(nextRow);
                } else {
                    numStack.push(numStack.get(nextRow - 1));
                }
                

                // use of regex expression and trim to only retrieve the number (the ID number) from the string
                playerIDString = listPlayers.get(nextRow).toString().replaceAll("[^0-9]+", " ").trim();
                playerID = Integer.valueOf(playerIDString);
                System.out.println(listPlayers.get(nextRow) + ", ID: " + playerIDString + ", TEAM ID: " + (numStack.get(nextRow) / 2 + 1));

        }*/
        

        
   
    }
    public static void main(String args[]) {
        runTest();
    }  
}
