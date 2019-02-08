/**
 * Strategy.java
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
import java.util.Random;

public class Strategy {

    public static final int RANDOM = 1;
    public static final int CLOSE_GROUP = 2;
    public static final int SAME_GROUP = 3;

    private int maxHoles = 50;
    private int maxGroupHoles = 10;
    private int maxGroups = 10;
    private Random random = null;

    private ArrayList<Integer> playerHoles = null;
    private int previousGroup = -1;

    //
    // Default constructor:
    //
    public Strategy() {
        this.playerHoles = new ArrayList<Integer>(this.maxHoles);
        this.random = new Random();
    }

    //
    // Select one empty hole from previously hole group:
    //
    private int selectFromGroup() {
        int randomGroupHole = this.random.nextInt(this.maxGroupHoles) + (this.previousGroup) * this.maxGroupHoles;
        while(this.playerHoles.contains(randomGroupHole) == true) {
            randomGroupHole = new Random().nextInt(this.maxGroupHoles) + (this.previousGroup) * this.maxGroupHoles;
        }
        this.playerHoles.add(0, randomGroupHole);
        return  randomGroupHole;
    }


    //
    // Select random hole from 50 available:
    //
    public int random() {
            int randomHole = this.random.nextInt(50);
            this.previousGroup = randomHole / this.maxGroupHoles;
            this.playerHoles.add(randomHole);
            return randomHole;
    }

    //
    // Select hole from closest groups:
    //
    public int closeGroup() {
        int randomGroup = this.random.nextInt(2) + 1;
        int hole = -1;
        if(randomGroup == 1) {
            return this.sameGroup();
        }

        if(randomGroup == 2) {
            int adjacentGroup = this.random.nextInt(2);
            if(adjacentGroup == 0) {
                if(this.previousGroup - 1 >= 0) {
                    this.previousGroup -= 1;
                } else {
                    this.previousGroup += 1;
                }
            } else {
                if (this.previousGroup + 1 < 5) {
                    this.previousGroup += 1;
                } else {
                    this.previousGroup -= 1;
                }
            }
        }
        return this.selectFromGroup();
    }

    //
    // Select hole from the same group:
    //
    public int sameGroup() {
        return this.selectFromGroup();
    }

    //
    // Return hole based on passed strategy from worker class:
    //
    public int getStrategy(int s) {
        switch(s) {
            case RANDOM:
                return this.random();
            case CLOSE_GROUP:
                return this.closeGroup();
            case SAME_GROUP:
                return this.sameGroup();
            default:
                return this.random();
        }
    }
}
