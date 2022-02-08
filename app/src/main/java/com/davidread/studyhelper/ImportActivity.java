package com.davidread.studyhelper;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.VolleyError;

import java.util.List;

/**
 * {@link ImportActivity} represents a user interface where subject and question data may be
 * selected to be imported from zyBook's servers.
 */
public class ImportActivity extends AppCompatActivity {

    /**
     * {@link LinearLayout} to be dynamically filled with a {@link CheckBox} for each subject
     * fetched by {@link #mStudyFetcher}.
     */
    private LinearLayout mSubjectLayoutContainer;

    /**
     * {@link StudyFetcher} fetches {@link Subject} objects available to be imported from zyBook's
     * servers.
     */
    private StudyFetcher mStudyFetcher;

    /**
     * {@link ProgressBar} to indicate that a network request is being executed.
     */
    private ProgressBar mLoadingProgressBar;

    /**
     * Callback method invoked when this activity is created. It initializes this activity's
     * member variables and begins fetching subjects using {@link #mStudyFetcher}.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);

        mSubjectLayoutContainer = findViewById(R.id.subject_layout);

        // Show progress bar.
        mLoadingProgressBar = findViewById(R.id.loading_progress_bar);
        mLoadingProgressBar.setVisibility(View.VISIBLE);

        mStudyFetcher = new StudyFetcher(this);
        mStudyFetcher.fetchSubjects(mFetchListener);
    }

    /**
     * {@link StudyFetcher.OnStudyDataReceivedListener} specifies what to do when
     * {@link #mStudyFetcher} either successfully receives {@link Subject} and {@link Question}
     * objects from zyBook's servers or fails.
     */
    private final StudyFetcher.OnStudyDataReceivedListener mFetchListener =
            new StudyFetcher.OnStudyDataReceivedListener() {

                /**
                 * Invoked when {@link Subject} objects are successfully received from
                 * {@link ImportActivity#mStudyFetcher}. It creates a {@link CheckBox} for
                 * each {@link Subject} in {@link ImportActivity#mSubjectLayoutContainer}.
                 *
                 * @param subjectList   {@link List} of {@link Subject} objects received from
                 *                      zyBook's servers.
                 */
                @Override
                public void onSubjectsReceived(List<Subject> subjectList) {

                    // Hide ProgressBar.
                    mLoadingProgressBar.setVisibility(View.GONE);

                    // Create a CheckBox for each subject.
                    for (Subject subject : subjectList) {
                        CheckBox checkBox = new CheckBox(getApplicationContext());
                        checkBox.setTextSize(24);
                        checkBox.setText(subject.getText());
                        checkBox.setTag(subject);
                        mSubjectLayoutContainer.addView(checkBox);
                    }
                }

                /**
                 * Invoked when {@link Question} objects are successfully received from
                 * {@link ImportActivity#mStudyFetcher} for each {@link Subject}. It adds all
                 * {@link Question} objects to the database via a {@link StudyDatabase} instance.
                 *
                 * @param subject       {@link Subject} that the {@link List} of {@link Question}
                 *                      objects are from.
                 * @param questionList  {@link List} of {@link Question} objects received from
                 *                      zyBook's servers.
                 */
                @Override
                public void onQuestionsReceived(Subject subject, List<Question> questionList) {

                    if (!questionList.isEmpty()) {
                        StudyDatabase studyDb = StudyDatabase.getInstance(getApplicationContext());

                        // Add the questions to the database.
                        for (Question question : questionList) {
                            question.setSubjectId(subject.getId());
                            studyDb.questionDao().insertQuestion(question);
                        }

                        Toast.makeText(getApplicationContext(), subject.getText() + " imported successfully",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), subject.getText() + " contained no questions",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                /**
                 * Invoked when an error occurs with {@link ImportActivity#mStudyFetcher}. It
                 * pops an error {@link Toast}.
                 *
                 * @param error {@link VolleyError} invoking this method.
                 */
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "Error loading subjects. Try again later.",
                            Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                    mLoadingProgressBar.setVisibility(View.GONE);
                }
            };

    /**
     * Invoked when the "Import" {@link android.widget.Button} is clicked. It saves the
     * {@link Subject} and {@link Question} objects selected to the database via a
     * {@link StudyDatabase} instance.
     */
    public void importButtonClick(View view) {

        StudyDatabase studyDb = StudyDatabase.getInstance(getApplicationContext());

        // Determine which subjects were selected.
        int numCheckBoxes = mSubjectLayoutContainer.getChildCount();
        for (int i = 0; i < numCheckBoxes; i++) {
            CheckBox checkBox = (CheckBox) mSubjectLayoutContainer.getChildAt(i);
            if (checkBox.isChecked()) {
                Subject subject = (Subject) checkBox.getTag();

                // See if this subject has already been imported.
                if (studyDb.subjectDao().getSubjectByText(subject.getText()) == null) {
                    // Add subject to the database and import.
                    long newId = studyDb.subjectDao().insertSubject(subject);
                    subject.setId(newId);
                    mStudyFetcher.fetchQuestions(subject, mFetchListener);
                } else {
                    Toast.makeText(this, subject.getText() + " is already imported.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}