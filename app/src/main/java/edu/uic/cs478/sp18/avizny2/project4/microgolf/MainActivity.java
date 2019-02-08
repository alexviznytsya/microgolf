/**
 * MainActivity.java
 *
 * Alex Viznytsya
 * CS 478 Software Development for Mobile Platforms
 * Spring 2028, UIC
 *
 * Project 4 - Microgolf
 * 04/16/2018
 */

package edu.uic.cs478.sp18.avizny2.project4.microgolf;

import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public static final int TURN = 1;
    public static final int TURN_QUESTION = 2;
    public static final int TURN_ANSWER = 3;
    public static final int QUIT = 99;

    private ArrayList<Hole> holeList = null;
    private LinearLayout holeContainer = null;
    private Button startButton = null;
    private TextView messageContainer = null;
    private TextView player1Text = null;
    private TextView player2Text = null;


    private Handler mHandler = null;
    private Worker player1Worker = null;
    private Worker2 player2Worker = null;
    private Handler player1ThreadHandler = null;
    private Handler player2ThreadHandler = null;

    private boolean player1Turn = false;
    private boolean player2Turn = false;
    private boolean player1Winner = false;
    private boolean player2Winner = false;

    private Answer answer = null;
    private boolean gameOver = false;
    private String gameMessage = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.answer = new Answer();

        //
        // Create main thread handler:
        //
        this.mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {

                //
                // Check color of player's labels:
                //
                if(player1Turn == false) {
                    MainActivity.this.player1Text.setTextColor(getColor(R.color.player1Hole));
                    MainActivity.this.player2Text.setTextColor(Color.BLACK);
                } else {
                    MainActivity.this.player1Text.setTextColor(Color.BLACK);
                    MainActivity.this.player2Text.setTextColor(getColor(R.color.player2Hole));

                }

                //
                // Process messages from worker threads:
                //
                switch (msg.what){

                    //
                    // Check if question from worker thread is valid and create andwer for
                    // passed hole number:
                    //
                    case MainActivity.TURN_QUESTION:

                        int player = msg.arg2;
                        int holeGroup = MainActivity.this.answer.getHoleGroup(msg.arg1) + 1;
                        String playerName = null;
                        int playerColor = -1;
                        Handler handler = null;
                        int answer = -1;
                        Message message = null;

                        if(player == Answer.PLAYER_1) {
                            handler = MainActivity.this.player1ThreadHandler;
                            playerColor = getColor(R.color.player1Hole);
                            playerName = "Player 1";
                        } else {
                            handler = MainActivity.this.player2ThreadHandler;
                            playerColor = getColor(R.color.player2Hole);
                            playerName = "Player 2";
                        }

                        answer = MainActivity.this.answer.getAnswer(msg.arg2, msg.arg1);
                        message = handler.obtainMessage();
                        message.what = MainActivity.TURN_ANSWER;
                        message.arg1 = answer;

                        //
                        // Answer to worker thread with Jackpot message:
                        //
                        if(answer == Answer.JACKPOT) {
                            message.what = MainActivity.QUIT;
                            MainActivity.this.gameOver = true;
                            if(player == Answer.PLAYER_1) {
                                MainActivity.this.player1Winner = true;
                            } else {
                                MainActivity.this.player2Winner = true;
                            }
                            MainActivity.this.messageContainer.setText("JACKPOT!\n" + playerName + " has won the game!");
                        }
                        //
                        // Answer to worker thread with catastrophy message and stop that worker thread:
                        //
                        else if(answer == Answer.CATASTROPHE){
                            message.what = MainActivity.QUIT;
                            MainActivity.this.gameOver = true;
                            if(player == Answer.PLAYER_1) {
                                MainActivity.this.player2Winner = true;
                            } else {
                                MainActivity.this.player1Winner = true;
                            }
                            MainActivity.this.messageContainer.setText("CATASTROPHY!\n" + playerName + " has lost the game!");
                        } else {
                            MainActivity.this.messageContainer.setText(playerName + " shot:\nGroup: " + holeGroup + ", Hole: " + (msg.arg1 + 1));
                        }

                        //
                        // Remove previous hole and set color to new one:
                        //
                        int prevHole = MainActivity.this.answer.getPrevious(player);
                        if(prevHole >= 0) {
                            MainActivity.this.holeList.get(prevHole).makeDefault();
                        }
                        MainActivity.this.holeList.get(msg.arg1).changeColor(playerColor);
                        handler.sendMessage(message);

                        //
                        // Toggle turns between worker threads using runnable messages:
                        //
                        if(player1Winner == false && player2Winner == false ) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    MainActivity.this.shoot();
                                }
                            });
                        }
                        break;

                    default:
                        super.handleMessage(msg);
                }


                Log.i("Main Activity: ", "Received message: " + msg.toString());
            }
        };

        //
        // Get required UI elements:
        //
        this.holeContainer = (LinearLayout)findViewById(R.id.holesContainer);
        this.messageContainer = (TextView)findViewById(R.id.messageTextView);
        this.player1Text = (TextView)findViewById(R.id.player1TextView);
        this.player2Text = (TextView)findViewById(R.id.player2TextView);
        this.startButton = (Button)findViewById((R.id.startGameButton));

        this.startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.player1ThreadHandler = MainActivity.this.player1Worker.getHandler();
                MainActivity.this.player2ThreadHandler = MainActivity.this.player2Worker.getHandler();
                v.setEnabled(false);
                MainActivity.this.messageContainer.setText("Game has begun!");
                MainActivity.this.shoot();
            }
        });

        //
        // Setup new game params:s
        //
        this.newGame();
    }

    //
    // Create new 50 holes":
    //
    private void createHoles() {
        this.holeContainer.removeAllViews();

        // Add 50 holes to the layout:
        for(int i = 0; i < 50; i++) {
            Hole hole = new Hole(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0,5,0,5);
            hole.setLayoutParams(params);
            this.holeList.add(hole);
            this.holeContainer.addView(hole);
        }
    }

    //
    // Set default values for new game variables:
    //
    private void newGame() {
        if(this.holeList != null) {
            this.holeList.clear();
        }
        this.messageContainer.setText("");
        this.holeList = new ArrayList<Hole>(50);
        this.answer = new Answer();

        this.player1Worker = new Worker("Player 1", this.mHandler);
        new Thread(this.player1Worker).start();
        this.player2Worker = new Worker2("Player 2", this.mHandler);
        new Thread(this.player2Worker).start();

        this.createHoles();
        this.answer.setTargetHole(new Random().nextInt(50));
        this.holeList.get(this.answer.getTargetHole()).changeColor(getColor(R.color.targetHole));
        this.player1Text.setTextColor(Color.BLACK);
        this.player2Text.setTextColor(Color.BLACK);
        this.player1Turn = true;
        this.player2Turn = false;
        this.player1Winner = false;
        this.player2Winner = false;
        this.startButton.setEnabled(true);
    }

    //
    // Toggle between players moves:
    //
    private Handler nextTurn() {
        if(this.player1Turn == true) {
            this.player1Turn = false;
            this.player2Turn = true;
            return player1ThreadHandler;
        } else {
            this.player1Turn = true;
            this.player2Turn = false;
            return player2ThreadHandler;
        }
    }


    //
    // Send message to selected worked thread:
    //
    public void shoot() {
        Handler h = this.nextTurn();
        Message m = h.obtainMessage(TURN);
        h.sendMessage(m);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mmNewGame:
                this.newGame();
                return true;
            case R.id.mmAbout:
                openAboutDialog();
                return true;
            case R.id.mmExit:
                finishAndRemoveTask();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //
    // About dialog popup:
    //
    private void openAboutDialog() {
        final View dialogLayout = getLayoutInflater().inflate(R.layout.dialog_about, null);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(dialogLayout);
        final AlertDialog dialogAbout = dialogBuilder.create();

        // Add "Cancel" button listener
        ((Button)dialogLayout.findViewById(R.id.dialogClose)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAbout.dismiss();
            }
        });

        dialogAbout.show();
    }

}
