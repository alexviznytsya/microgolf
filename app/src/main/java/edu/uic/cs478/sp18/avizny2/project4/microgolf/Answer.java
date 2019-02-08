/**
 * Answer.java
 *
 * Alex Viznytsya
 * CS 478 Software Development for Mobile Platforms
 * Spring 2028, UIC
 *
 * Project 4 - Microgolf
 * 04/16/2018
 */

package edu.uic.cs478.sp18.avizny2.project4.microgolf;

import java.util.ArrayList;

public class Answer {

    static final int JACKPOT = 1;
    static final int NEAR_MISS = 2;
    static final int NEAR_GROUP = 3;
    static final int BIG_MISS = 4;
    static final int CATASTROPHE = 5;

    static final int PLAYER_1 = 1;
    static final int PLAYER_2 = 2;

    private int targetHole = -1;
    private int targetHoleGroup = -1;
    private ArrayList<Integer> player1Holes = null;
    private ArrayList<Integer> player2Holes = null;

    //
    // Default constructor:
    //
    public Answer() {
        this.player1Holes = new ArrayList<Integer>(50);
        this.player2Holes = new ArrayList<Integer>(50);
    }

    //
    // Transform hole number to group number:
    //
    public int getHoleGroup(int hole) {

        return hole / 10;
    }

    //
    // Set target hole:
    //
    public void setTargetHole(int target) {
        this.targetHole = target;
        this.targetHoleGroup = this.getHoleGroup(target);
    }

    //
    // Get target hole:
    //
    public int getTargetHole() {
        return this.targetHole;
    }

    //
    // Create answer for requested worker thread:
    //
    public int getAnswer(int player, int hole) {
        ArrayList<Integer> tOppositePlayerHoles = null;
        ArrayList<Integer> tPlayerHoles = null;

        if(player == PLAYER_1) {
            tPlayerHoles = this.player1Holes;
            tOppositePlayerHoles = this.player2Holes;
        } else {
            tPlayerHoles = this.player2Holes;
            tOppositePlayerHoles = this.player1Holes;
        }

        int tHoleGroup = this.getHoleGroup(hole);
        tPlayerHoles.add(0, hole);

        if(tOppositePlayerHoles.size() > 0 && tOppositePlayerHoles.get(0) == hole) {
            return CATASTROPHE;
        }

        if (hole == this.targetHole) {
            return JACKPOT;
        }

        if (tHoleGroup == this.targetHoleGroup) {
            return NEAR_MISS;
        }

        if(tHoleGroup - 1 == this.targetHoleGroup || tHoleGroup + 1 == this.targetHoleGroup) {
            return NEAR_GROUP;
        }

        return BIG_MISS;
    }

    //
    // Return previous hole using player number:
    //
    public int getPrevious(int player) {
        ArrayList<Integer> pHoleList = null;
        if(player == PLAYER_1) {
            pHoleList = this.player1Holes;
        } else {
            pHoleList = this.player2Holes;
        }
        if(pHoleList.size() > 1) {
            return pHoleList.get(1);
        } else {
            return -1;
        }
    }
}
