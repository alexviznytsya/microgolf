/**
 * Worker2.java
 *
 * Alex Viznytsya
 * CS 478 Software Development for Mobile Platforms
 * Spring 2028, UIC
 *
 * Project 4 - Microgolf
 * 04/16/2018
 */

package edu.uic.cs478.sp18.avizny2.project4.microgolf;

import android.os.Handler;

public class Worker2 extends Worker {

    //
    // Default constructor:
    //
    public Worker2(String name, Handler uiHandler) {
        super(name, uiHandler);
    }

    //
    // Override strategy selection for second worker class.
    // It will make him less smarter than first worker class.
    //
    @Override
    protected void strategy(int answer) {
        if(answer == Answer.NEAR_MISS) {
            Worker2.this.currentStrategy = Strategy.CLOSE_GROUP;
        } else if(answer == Answer.NEAR_GROUP) {
            Worker2.this.currentStrategy = Strategy.CLOSE_GROUP;
        } else {
            Worker2.this.currentStrategy = Strategy.RANDOM;
        }
    }

}
