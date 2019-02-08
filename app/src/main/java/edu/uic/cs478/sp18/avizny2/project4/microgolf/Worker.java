/**
 * Worker.java
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
import android.os.Looper;
import android.os.Message;

public class Worker implements Runnable {

    protected String myTag = null;
    protected Handler mHandler = null;
    protected Handler uiHandler = null;
    protected Strategy workerStrategy = null;
    protected int currentStrategy = -1;
    protected int currentShot = -1;

    //
    // Default constructor:
    //
    public Worker(String name, Handler uiHandler) {
        this.myTag = name;
        this.uiHandler = uiHandler;
        this.workerStrategy = new Strategy();
    }

    @Override
    public void run() {
        Looper.prepare();

        //
        // Create worker handler for message queue:
        //
        this.mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {

                    //
                    // Get turn message from UI thread, and based on strategy pick one of the
                    // holes:
                    //
                    case MainActivity.TURN:
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            getLooper().quitSafely();
                        }
                        Worker.this.currentShot = Worker.this.workerStrategy.getStrategy(Worker.this.currentStrategy);
                        Message m = Worker.this.uiHandler.obtainMessage(MainActivity.TURN_QUESTION);
                        m.arg1 = Worker.this.currentShot;
                        if(Worker.this.myTag.contains("Player 1")) {
                            m.arg2 = Answer.PLAYER_1;
                        } else {
                            m.arg2 = Answer.PLAYER_2;
                        }
                        Worker.this.uiHandler.sendMessage(m);
                        break;

                    //
                    // Process turn answer from UI thread:
                    //
                    case MainActivity.TURN_ANSWER:
                        Worker.this.strategy(msg.arg1);
                        break;

                    //
                    // Stop looper and close thread:
                    //
                    case MainActivity.QUIT:
                        getLooper().quitSafely();
                    default:
                        super.handleMessage(msg);
                }
            }
        };

        Looper.loop();
    }

    //
    // Strategy pattern for first worker class:
    //
    protected void strategy(int answer) {
        if(answer == Answer.NEAR_MISS) {
            Worker.this.currentStrategy = Strategy.SAME_GROUP;
        } else if(answer == Answer.NEAR_GROUP) {
            Worker.this.currentStrategy = Strategy.CLOSE_GROUP;
        } else {
            Worker.this.currentStrategy = Strategy.RANDOM;
        }
    }


    //
    // Get handler from this thread:
    //
    public Handler getHandler() {
        return this.mHandler;
    }
}
