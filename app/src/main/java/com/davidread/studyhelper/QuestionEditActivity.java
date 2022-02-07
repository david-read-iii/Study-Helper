package com.davidread.studyhelper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

/**
 * {@link QuestionEditActivity} provides a user interface for adding or modifying the attributes of
 * a {@link Question}.
 */
public class QuestionEditActivity extends AppCompatActivity {

    /**
     * {@link String} identifier for a question id.
     */
    public static final String EXTRA_QUESTION_ID = "com.davidread.studyhelper.question_id";

    /**
     * {@link String} identifier for a subject id.
     */
    public static final String EXTRA_SUBJECT_ID = "com.davidread.studyhelper.subject_id";

    /**
     * {@link EditText} for the question text attribute.
     */
    private EditText mQuestionText;

    /**
     * {@link EditText} for the answer attribute.
     */
    private EditText mAnswerText;

    /**
     * {@link StudyDatabase} for getting the attributes of an existing question.
     */
    private StudyDatabase mStudyDb;

    /**
     * Long for the question id passed to this activity.
     */
    private long mQuestionId;

    /**
     * {@link Question} being modified in this activity.
     */
    private Question mQuestion;

    /**
     * Callback method invoked when this activity is initially created. It initializes member
     * variables and updates the user interface given what intent extras are passed to this
     * activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_edit);

        mQuestionText = findViewById(R.id.question_edit_text);
        mAnswerText = findViewById(R.id.answer_edit_text);

        mStudyDb = StudyDatabase.getInstance(getApplicationContext());

        // Get question ID from QuestionActivity.
        Intent intent = getIntent();
        mQuestionId = intent.getLongExtra(EXTRA_QUESTION_ID, -1);

        if (mQuestionId == -1) {
            // Add new question.
            mQuestion = new Question();
            long subjectId = intent.getLongExtra(EXTRA_SUBJECT_ID, 0);
            mQuestion.setSubjectId(subjectId);

            // Load default question text.
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
            String defaultText = sharedPrefs.getString("default_question", "");
            mQuestion.setText(defaultText);
            mQuestionText.setText(defaultText);

            setTitle(R.string.add_question);
        } else {
            // Update existing question.
            mQuestion = mStudyDb.questionDao().getQuestion(mQuestionId);
            mQuestionText.setText(mQuestion.getText());
            mAnswerText.setText(mQuestion.getAnswer());
            setTitle(R.string.update_question);
        }
    }

    /**
     * Invoked when the "Save"
     * {@link com.google.android.material.floatingactionbutton.FloatingActionButton} is clicked.
     * It saves or updates the question in {@link #mStudyDb} and passes the appropriate attributes
     * back to the activity that called this activity.
     */
    public void saveButtonClick(View view) {

        mQuestion.setText(mQuestionText.getText().toString());
        mQuestion.setAnswer(mAnswerText.getText().toString());

        if (mQuestionId == -1) {
            // New question
            long newId = mStudyDb.questionDao().insertQuestion(mQuestion);
            mQuestion.setId(newId);
        } else {
            // Existing question
            mStudyDb.questionDao().updateQuestion(mQuestion);
        }

        // Send back question ID
        Intent intent = new Intent();
        intent.putExtra(EXTRA_QUESTION_ID, mQuestion.getId());
        setResult(RESULT_OK, intent);
        finish();
    }
}