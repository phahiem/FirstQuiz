package com.example.newsqlquiz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.newsqlquiz.QuizContract.*;

import java.util.ArrayList;
import java.util.List;

public class QuizDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MyAwesomeQuiz.db";
    private static final int DATABASE_VERSION = 1;

    private SQLiteDatabase db;

    public QuizDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;

        final String SQL_CREATE_QUESTION_TABLE = "CREATE TABLE " +
                QuestionTable.TABLE_NAME + " ( " +
                QuestionTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                QuestionTable.COULMN_QUESTION + " TEXT, " +
                QuestionTable.COLUMN_OPTION1 + " TEXT, " +
                QuestionTable.COLUMN_OPTION2 + " TEXT, " +
                QuestionTable.COLUMN_OPTION3 + " TEXT, " +
                QuestionTable.COLUMN_ANSWER_NR + " INTEGER" +
                ")";

        db.execSQL(SQL_CREATE_QUESTION_TABLE);
        fillQuestionsTable();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + QuestionTable.TABLE_NAME);
        onCreate(db);

    }

    private void fillQuestionsTable(){

        Questions q1 = new Questions("A is Correct","A","B","C",1);
        addQuestion(q1);

        Questions q2 = new Questions("B is Correct","A","B","C",2);
        addQuestion(q2);

        Questions q3 = new Questions("C is Correct","A","B","C",3);
        addQuestion(q3);

        Questions q4 = new Questions("A is Correct again","A","B","C",1);
        addQuestion(q4);

        Questions q5 = new Questions("B is Correct again","A","B","C",2);
        addQuestion(q5);

    }

    private void addQuestion(Questions questions){

        ContentValues cv = new ContentValues();
        cv.put( QuestionTable.COULMN_QUESTION, questions.getQuestion());
        cv.put( QuestionTable.COLUMN_OPTION1, questions.getOption1());
        cv.put( QuestionTable.COLUMN_OPTION2, questions.getOption2());
        cv.put( QuestionTable.COLUMN_OPTION3, questions.getOption3());
        cv.put( QuestionTable.COLUMN_ANSWER_NR, questions.getAnswerNr());

        db.insert(QuestionTable.TABLE_NAME, null, cv);

    }

    public ArrayList<Questions> getAllQuestions(){

        ArrayList<Questions> questionsList = new ArrayList<>();
        db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + QuestionTable.TABLE_NAME,null);

        if (c.moveToFirst()) {

            do {

                Questions questions = new Questions();
                questions.setQuestion(c.getString(c.getColumnIndex(QuestionTable.COULMN_QUESTION)));
                questions.setOption1(c.getString(c.getColumnIndex(QuestionTable.COLUMN_OPTION1)));
                questions.setOption2(c.getString(c.getColumnIndex(QuestionTable.COLUMN_OPTION2)));
                questions.setOption3(c.getString(c.getColumnIndex(QuestionTable.COLUMN_OPTION3)));
                questions.setAnswerNr(c.getInt(c.getColumnIndex(QuestionTable.COLUMN_ANSWER_NR)));
                questionsList.add(questions);

            } while (c.moveToNext());
        }

        c.close();
        return questionsList;

    }
}
