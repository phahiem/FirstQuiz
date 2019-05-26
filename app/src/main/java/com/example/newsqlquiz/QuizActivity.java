package com.example.newsqlquiz;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class QuizActivity extends AppCompatActivity {

    public static final String EXTRA_SCORE = "extraScore";
    private long backPressedTime;
    private static final long COUNTDOWN_IN_MILIS = 30000;

    private static final String KEY_SCORE = "keyScore";
    private static final String KEY_QUESTION_COUNT = "keyQuestionCount";
    private static final String KEY_MILLIS_LEFT = "keyMillisLeft";
    private static final String KEY_ANSWERED = "keyAnswered";
    private static final String KEY_QuestionList = "keyQuestionList";

    private TextView textViewQuestion;
    private TextView textViewScore;
    private TextView textViewQuestionCounter;
    private TextView textViewCountDown;
    private RadioGroup rbGroup;
    private RadioButton rb1;
    private RadioButton rb2;
    private RadioButton rb3;
    private Button buttonConfirmNext;

    private ColorStateList textColorDefaultRb;
    private ColorStateList textColorDefaultcd;

    private CountDownTimer countDownTimer;
    private long timeLeftInMilis;


    private ArrayList<Questions> questionsList;
    private int questionCounter;
    private int questionCountTotal;
    private Questions currentQuestion;

    private int score;
    private boolean answered;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        textViewQuestion = findViewById(R.id.text_view_question);
        textViewScore = findViewById(R.id.text_view_score);
        textViewQuestionCounter = findViewById(R.id.text_view_question_count);
        textViewCountDown = findViewById(R.id.text_view_countdown);
        rbGroup = findViewById(R.id.radio_group);
        rb1 = findViewById(R.id.radio_button1);
        rb2 = findViewById(R.id.radio_button2);
        rb3 = findViewById(R.id.radio_button3);
        buttonConfirmNext = findViewById(R.id.button_confirm_next);

        textColorDefaultRb = rb1.getTextColors();
        textColorDefaultcd = textViewCountDown.getTextColors();

        if ( savedInstanceState == null ) {
            QuizDbHelper dbHelper = new QuizDbHelper(this);
            questionsList = dbHelper.getAllQuestions();

            questionCountTotal = questionsList.size();
            Collections.shuffle(questionsList);

            showNextQuestion();
        } else {

            questionsList = savedInstanceState.getParcelableArrayList(KEY_QuestionList);
            questionCountTotal = questionsList.size();
            questionCounter = savedInstanceState.getInt(KEY_QUESTION_COUNT);
            currentQuestion = questionsList.get(questionCounter - 1);
            score = savedInstanceState.getInt(KEY_SCORE);
            timeLeftInMilis = savedInstanceState.getLong(KEY_MILLIS_LEFT);
            answered = savedInstanceState.getBoolean(KEY_ANSWERED);

            if (!answered){
                startCountDown();
            } else{
                updateCountDownText();
                showSolution();
            }


        }
        buttonConfirmNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!answered){

                    if( rb1.isChecked() || rb2.isChecked() || rb3.isChecked() ){

                        checkAnswer();
                    } else {
                        Toast.makeText(QuizActivity.this,"Please Select an Answer.",Toast.LENGTH_SHORT).show();
                    }
                } else {

                    showNextQuestion();
                }
            }
        });

    }

    private void showNextQuestion(){

        rb1.setTextColor(textColorDefaultRb);
        rb2.setTextColor(textColorDefaultRb);
        rb3.setTextColor(textColorDefaultRb);
        rbGroup.clearCheck();

        if ( questionCounter < questionCountTotal ){

            currentQuestion = questionsList.get(questionCounter);
            textViewQuestion.setText(currentQuestion.getQuestion());
            rb1.setText(currentQuestion.getOption1());
            rb2.setText(currentQuestion.getOption2());
            rb3.setText(currentQuestion.getOption3());

            questionCounter++;
            textViewQuestionCounter.setText("Question: " + questionCounter + "/" + questionCountTotal);
            answered = false;
            buttonConfirmNext.setText("Confirm");
            timeLeftInMilis = COUNTDOWN_IN_MILIS;
            startCountDown();



        } else {

            finishQuiz();
        }
    }

    private void startCountDown(){

        countDownTimer = new CountDownTimer(timeLeftInMilis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMilis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {

                timeLeftInMilis = 0;
                checkAnswer();

            }
        }.start();
    }

    private void updateCountDownText(){

        int minutes = (int) (timeLeftInMilis / 1000) / 60;
        int seconds = (int) (timeLeftInMilis / 1000) % 60;

        String timeformatted = String.format(Locale.getDefault(), "%02d:%02d", minutes,seconds);
        textViewCountDown.setText(timeformatted);

        if(timeLeftInMilis < 10000){

            textViewCountDown.setTextColor(Color.RED);
        }else {

            textViewCountDown.setTextColor(textColorDefaultcd);
        }
    }

    private void checkAnswer(){

        answered = true;
        countDownTimer.cancel();

        RadioButton rbSelected = findViewById(rbGroup.getCheckedRadioButtonId());
        int answerNr = rbGroup.indexOfChild(rbSelected) + 1;

        if( answerNr == currentQuestion.getAnswerNr()){

            score++;
            textViewScore.setText("Score: " + score);

        }

        showSolution();
    }

    private void showSolution(){

        rb1.setTextColor(Color.RED);
        rb2.setTextColor(Color.RED);
        rb3.setTextColor(Color.RED);

        switch ( currentQuestion.getAnswerNr() ){

            case 1:
                rb1.setTextColor(Color.GREEN);
                textViewQuestion.setText("Answer 1 is correct!");
                break;

            case 2:
                rb2.setTextColor(Color.GREEN);
                textViewQuestion.setText("Answer 2 is correct!");
                break;

            case 3:
                rb3.setTextColor(Color.GREEN);
                textViewQuestion.setText("Answer 3 is correct!");
                break;
        }

        if( questionCounter < questionCountTotal ){

            buttonConfirmNext.setText("Next");
        } else {

            buttonConfirmNext.setText("Finish!");
        }
    }

    private void finishQuiz(){

        Intent ResultIntent = new Intent();
        ResultIntent.putExtra(EXTRA_SCORE, score);
        setResult(RESULT_OK, ResultIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if ( backPressedTime + 2000 > System.currentTimeMillis()){
            finishQuiz();
        } else {

            Toast.makeText(QuizActivity.this, "Press back again to finish", Toast.LENGTH_SHORT).show();
        }

        backPressedTime = System.currentTimeMillis();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SCORE, score);
        outState.putInt(KEY_QUESTION_COUNT, questionCounter);
        outState.putLong(KEY_MILLIS_LEFT, timeLeftInMilis);
        outState.putBoolean(KEY_ANSWERED, answered);
        outState.putParcelableArrayList(KEY_QuestionList, questionsList);
    }
}
