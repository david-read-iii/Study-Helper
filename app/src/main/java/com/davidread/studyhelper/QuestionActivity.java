package com.davidread.studyhelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

/**
 * {@link QuestionActivity} provides a user interface for browsing the questions associated with
 * the selected subject. {@link #mAnswerButton} toggles the visibility of the question's answer.
 * Previous and next app bar buttons show the previous and next question in the set. Add, edit, and
 * delete overflow app bar buttons allow modification of the question set.
 */
public class QuestionActivity extends AppCompatActivity {

    /**
     * {@link String} identifier for a subject id passed to this activity.
     */
    public static final String EXTRA_SUBJECT_ID = "com.davidread.studyhelper.subject_id";

    /**
     * {@link StudyDatabase} to get and put persisted {@link Question} objects for whatever subject
     * id is passed to this activity.
     */
    private StudyDatabase mStudyDb;

    /**
     * Long subject id passed to this activity.
     */
    private long mSubjectId;

    /**
     * {@link List} of {@link Question} objects to display in this activity.
     */
    private List<Question> mQuestionList;

    /**
     * {@link Question} most recently deleted using {@link #deleteQuestion()}. Saved so deletes
     * can be undone.
     */
    private Question mDeletedQuestion;

    /**
     * {@link TextView} for the answer label.
     */
    private TextView mAnswerLabel;

    /**
     * {@link TextView} to display the current answer's text.
     */
    private TextView mAnswerText;

    /**
     * {@link Button} for toggling the visibility of the answer.
     */
    private Button mAnswerButton;

    /**
     * {@link TextView} to display the question's text.
     */
    private TextView mQuestionText;

    /**
     * Int representing what question from {@link #mQuestionList} is currently being shown in this
     * activity.
     */
    private int mCurrentQuestionIndex;

    /**
     * {@link ViewGroup} for the layout shown when {@link #mQuestionList} has at least one
     * {@link Question}.
     */
    private ViewGroup mShowQuestionLayout;

    /**
     * {@link ViewGroup} for the layout shown when {@link #mQuestionList} is empty.
     */
    private ViewGroup mNoQuestionLayout;

    /**
     * Callback method invoked when this activity is initially created. It simply initializes
     * member variables and initializes the user interface.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        // SubjectActivity should provide the subject ID of the questions to display.
        Intent intent = getIntent();
        mSubjectId = intent.getLongExtra(EXTRA_SUBJECT_ID, 0);

        // Get all questions for this subject.
        mStudyDb = StudyDatabase.getInstance(getApplicationContext());
        mQuestionList = mStudyDb.questionDao().getQuestions(mSubjectId);

        mQuestionText = findViewById(R.id.question_text_view);
        mAnswerLabel = findViewById(R.id.answer_label_text_view);
        mAnswerText = findViewById(R.id.answer_text_view);
        mAnswerButton = findViewById(R.id.answer_button);
        mShowQuestionLayout = findViewById(R.id.show_question_layout);
        mNoQuestionLayout = findViewById(R.id.no_question_layout);

        // Show first question.
        showQuestion(0);
    }

    /**
     * Callback method invoked when this activity is visible. It updates the user interface given
     * the size of {@link #mQuestionList}.
     */
    @Override
    protected void onStart() {
        super.onStart();

        if (mQuestionList.size() == 0) {
            updateAppBarTitle();
            displayQuestion(false);
        } else {
            displayQuestion(true);
            toggleAnswerVisibility();
        }
    }

    /**
     * Callback method invoked when the action bar is created.
     *
     * @param menu {@link Menu} where the action bar menu should be inflated.
     * @return Whether the action bar should be displayed.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.question_menu, menu);
        return true;
    }

    /**
     * Callback method invoked when an action bar button is selected.
     *
     * @param item {@link MenuItem} that is invoking this method.
     * @return False to allow normal processing to proceed. True to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.previous) {
            showQuestion(mCurrentQuestionIndex - 1);
            return true;
        } else if (item.getItemId() == R.id.next) {
            showQuestion(mCurrentQuestionIndex + 1);
            return true;
        } else if (item.getItemId() == R.id.add) {
            addQuestion();
            return true;
        } else if (item.getItemId() == R.id.edit) {
            editQuestion();
            return true;
        } else if (item.getItemId() == R.id.delete) {
            deleteQuestion();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Callback method invoked when the "Add Question" {@link Button} is clicked. It calls
     * {@link #addQuestion()} for now.
     */
    public void addQuestionButtonClick(View view) {
        addQuestion();
    }

    /**
     * Callback method invoked when the "Show Answer"/"Hide Answer" {@link Button} is clicked. It
     * calls {@link #toggleAnswerVisibility()}.
     */
    public void answerButtonClick(View view) {
        toggleAnswerVisibility();
    }

    /**
     * If display is true, it only displays {@link #mShowQuestionLayout} in this activity.
     * Otherwise, it only displays {@link #mNoQuestionLayout}.
     */
    private void displayQuestion(boolean display) {
        if (display) {
            mShowQuestionLayout.setVisibility(View.VISIBLE);
            mNoQuestionLayout.setVisibility(View.GONE);
        } else {
            mShowQuestionLayout.setVisibility(View.GONE);
            mNoQuestionLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Updates the app bar title to display the subject and number of questions.
     */
    private void updateAppBarTitle() {
        Subject subject = mStudyDb.subjectDao().getSubject(mSubjectId);
        String title = getResources().getString(R.string.question_number,
                subject.getText(), mCurrentQuestionIndex + 1, mQuestionList.size());
        setTitle(title);
    }

    /**
     * Invoked when the "Add" app bar button is clicked. It starts {@link QuestionEditActivity}
     * while passing {@link #mSubjectId} to it.
     */
    private void addQuestion() {
        Intent intent = new Intent(this, QuestionEditActivity.class);
        intent.putExtra(QuestionEditActivity.EXTRA_SUBJECT_ID, mSubjectId);
        mAddQuestionResultLauncher.launch(intent);
    }

    /**
     * {@link ActivityResultLauncher} used to specify what actions to take after
     * {@link QuestionEditActivity} finishes when it is launched using {@link #addQuestion()}. It
     * gets attributes of the newly inserted {@link Question} from {@link #mStudyDb} and inserts
     * it into {@link #mQuestionList}.
     */
    private final ActivityResultLauncher<Intent> mAddQuestionResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            long questionId = data.getLongExtra(QuestionEditActivity.EXTRA_QUESTION_ID, -1);
                            Question newQuestion = mStudyDb.questionDao().getQuestion(questionId);

                            // Add newly created question to the question list and show it.
                            mQuestionList.add(newQuestion);
                            showQuestion(mQuestionList.size() - 1);

                            // Change layout in case this is the first question.
                            displayQuestion(true);

                            Snackbar.make(findViewById(R.id.coordinator_layout), R.string.question_added, BaseTransientBottomBar.LENGTH_SHORT).show();
                        }
                    }
                }
            });

    /**
     * Invoked when the "Edit" app bar button is clicked. It starts {@link QuestionEditActivity}
     * while passing the id of the currently displayed {@link Question} to it.
     */
    private void editQuestion() {
        if (mCurrentQuestionIndex >= 0) {
            Intent intent = new Intent(this, QuestionEditActivity.class);
            long questionId = mQuestionList.get(mCurrentQuestionIndex).getId();
            intent.putExtra(QuestionEditActivity.EXTRA_QUESTION_ID, questionId);
            mEditQuestionResultLauncher.launch(intent);
        }
    }

    /**
     * {@link ActivityResultLauncher} used to specify what actions to take after
     * {@link QuestionEditActivity} finishes when it is launched using {@link #editQuestion()}. It
     * gets attributes of the updated {@link Question} from {@link #mStudyDb} and updates it in
     * {@link #mQuestionList}.
     */
    private final ActivityResultLauncher<Intent> mEditQuestionResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            // Get updated question.
                            long questionId = data.getLongExtra(QuestionEditActivity.EXTRA_QUESTION_ID, -1);
                            Question updatedQuestion = mStudyDb.questionDao().getQuestion(questionId);

                            // Replace current question in question list with updated question.
                            Question currentQuestion = mQuestionList.get(mCurrentQuestionIndex);
                            currentQuestion.setText(updatedQuestion.getText());
                            currentQuestion.setAnswer(updatedQuestion.getAnswer());
                            showQuestion(mCurrentQuestionIndex);

                            Snackbar.make(findViewById(R.id.coordinator_layout), R.string.question_updated, BaseTransientBottomBar.LENGTH_SHORT).show();
                        }
                    }
                }
            });

    /**
     * Invoked when the "Delete" app bar button is clicked. It deletes the currently displayed
     * {@link Question} from {@link #mQuestionList} and {@link #mStudyDb}. However, it stores it
     * at {@link #mDeletedQuestion} in case the user wants to undo this action via a
     * {@link Snackbar} button click.
     */
    private void deleteQuestion() {
        if (mCurrentQuestionIndex >= 0) {
            Question question = mQuestionList.get(mCurrentQuestionIndex);
            mStudyDb.questionDao().deleteQuestion(question);
            mQuestionList.remove(mCurrentQuestionIndex);

            // Save question in case user wants to undo delete
            mDeletedQuestion = question;

            if (mQuestionList.isEmpty()) {
                // No questions left to show
                mCurrentQuestionIndex = -1;
                updateAppBarTitle();
                displayQuestion(false);
            } else {
                showQuestion(mCurrentQuestionIndex);
            }

            // Show delete message with Undo button
            Snackbar snackbar = Snackbar.make(findViewById(R.id.coordinator_layout),
                    R.string.question_deleted, Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.undo, v -> {
                // Add question back with a new auto-increment id
                mDeletedQuestion.setId(0);
                mStudyDb.questionDao().insertQuestion(mDeletedQuestion);

                // Add question back to list of questions, and display it
                mQuestionList.add(mDeletedQuestion);
                showQuestion(mQuestionList.size() - 1);
                displayQuestion(true);
            });
            snackbar.show();
        }
    }

    /**
     * Updates {@link #mQuestionText} and {@link #mAnswerText} with attributes of the
     * {@link Question} in {@link #mQuestionList} at the passed index.
     *
     * @param questionIndex Which {@link Question} should be shown.
     */
    private void showQuestion(int questionIndex) {

        // Show question at the given index
        if (mQuestionList.size() > 0) {
            if (questionIndex < 0) {
                questionIndex = mQuestionList.size() - 1;
            } else if (questionIndex >= mQuestionList.size()) {
                questionIndex = 0;
            }

            mCurrentQuestionIndex = questionIndex;
            updateAppBarTitle();

            Question question = mQuestionList.get(mCurrentQuestionIndex);
            mQuestionText.setText(question.getText());
            mAnswerText.setText(question.getAnswer());
        } else {
            // No questions yet
            mCurrentQuestionIndex = -1;
        }
    }

    /**
     * Toggles the visibility of {@link #mAnswerText} and {@link #mAnswerLabel}.
     */
    private void toggleAnswerVisibility() {
        if (mAnswerText.getVisibility() == View.VISIBLE) {
            mAnswerButton.setText(R.string.show_answer);
            mAnswerText.setVisibility(View.INVISIBLE);
            mAnswerLabel.setVisibility(View.INVISIBLE);
        } else {
            mAnswerButton.setText(R.string.hide_answer);
            mAnswerText.setVisibility(View.VISIBLE);
            mAnswerLabel.setVisibility(View.VISIBLE);
        }
    }
}