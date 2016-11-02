package com.example.julie.scarnes_dice;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ImageView diceImageView;
    private TextView scoreTextView;
    private Button rollButton;
    private Button holdButton;
    private Button resetButton;

    private Animation rotateAnimation;

    private Handler computerHandler = new Handler();
    private Runnable computerTurnTask = new Runnable() {
        @Override
        public void run() {
            if(usersTurn) {
                enableButtons();
                return;
            } else {
                computerLogic();
                computerHandler.postDelayed(this, 500);
            }
        }
    };

    private int usersOverallScore = 0;
    private int usersTurnScore = 0;
    private int computersOverallScore = 0;
    private int computersTurnScore = 0;

    private Random rand = new Random();
    private int currentDie = 0;

    private boolean usersTurn = true;
    private boolean gameOver = false;

    private final int [] drawableImages = {
            R.drawable.dice1,
            R.drawable.dice2,
            R.drawable.dice3,
            R.drawable.dice4,
            R.drawable.dice5,
            R.drawable.dice6
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate);

        diceImageView = (ImageView)findViewById(R.id.dice_image_view);
        scoreTextView = (TextView)findViewById(R.id.score_text_view);

        rollButton = (Button)findViewById(R.id.roll_button);
        rollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                rollLogic();
            }
        });

        holdButton = (Button)findViewById(R.id.hold_button);
        holdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                holdLogic();
            }
        });

        resetButton = (Button)findViewById(R.id.reset_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                resetLogic();
            }
        });
    }

    public void roll(){
        diceImageView.startAnimation(rotateAnimation);
        currentDie = rand.nextInt(6);
        Drawable drawable = getResources().getDrawable(drawableImages[currentDie]);
        diceImageView.setImageDrawable(drawable);
    }

    public void rollLogic(){
        roll();
        if(usersTurn){
            if(currentDie == 0){
                usersTurnScore = 0;
                usersTurn = false;
                disableButtons();
                computerHandler.postDelayed(computerTurnTask, 500);
            } else {
                usersTurnScore += currentDie + 1;
            }
        } else {
            if(currentDie == 0){
                computersTurnScore = 0;
                usersTurn = true;
            } else {
                computersTurnScore += currentDie + 1;
            }
        }
    }

    public void holdLogic() {
        if(usersTurn){
            usersOverallScore += usersTurnScore;
            usersTurnScore = 0;
            if(usersOverallScore > 99 || gameOver) {
                gameOver = true;
                announceWinner();
            } else {
                String s = "Your score: " + usersOverallScore + " Computer's score: " + computersOverallScore;
                scoreTextView.setText(s);
                usersTurn = false;
            }
            disableButtons();
            computerHandler.postDelayed(computerTurnTask, 500);
        } else {
            computersOverallScore += computersTurnScore;
            computersTurnScore = 0;
            if(computersOverallScore > 99 || gameOver){
                gameOver = true;
                announceWinner();
            } else {
                scoreTextView.setText("Your score: " + usersOverallScore + " Computer's score: " + computersOverallScore);
                usersTurn = true;
            }
        }
    }

    public void resetLogic(){
        usersOverallScore = 0;
        usersTurnScore = 0;
        computersTurnScore = 0;
        computersOverallScore = 0;
        currentDie = 0;
        gameOver = false;
        usersTurn = true;
        enableButtons();
        scoreTextView.setText("Your score: 0 Computer's Score: 0");
        diceImageView.setImageDrawable(getResources().getDrawable(drawableImages[0]));
    }

    public void computerLogic(){
        if(computersTurnScore < 20) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    rollLogic();
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    holdLogic();
                }
            });
        }
    }

    public void disableButtons(){
        rollButton.setEnabled(false);
        holdButton.setEnabled(false);
        resetButton.setEnabled(false);
    }
    public void enableButtons(){
        rollButton.setEnabled(true);
        holdButton.setEnabled(true);
        resetButton.setEnabled(true);
    }

    public void announceWinner() {
        rollButton.setEnabled(false);
        holdButton.setEnabled(false);
        if (usersOverallScore > 99) {
            scoreTextView.setText("Congratulations!! You won!!");
        } else {
            scoreTextView.setText("You lost, pal.");
        }
    }
}
