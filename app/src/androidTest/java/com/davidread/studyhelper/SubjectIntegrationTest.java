package com.davidread.studyhelper;

import static android.app.Activity.RESULT_OK;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.allOf;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * {@link SubjectIntegrationTest} provides integration tests that tests some features in
 * {@link SubjectActivity}, {@link QuestionActivity}, and {@link QuestionEditActivity}.
 */
@RunWith(AndroidJUnit4.class)
public class SubjectIntegrationTest {

    /**
     * {@link Context} for using {@link Intent}, {@link StudyDatabase}, and opening the action bar
     * overflow menu in activities.
     */
    private Context mAppContext;

    /**
     * {@link Subject} used for testing.
     */
    private Subject mTestSubject;

    /**
     * Invoked before each test method. It initializes {@link #mAppContext}, adds
     * {@link #mTestSubject} to {@link StudyDatabase}, and sets up preferences such that newly
     * created subjects appear first.
     */
    @Before
    public void createTestSubject() {
        mAppContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        StudyDatabase studyDb = StudyDatabase.getInstance(mAppContext);

        mTestSubject = new Subject("TEST SUBJECT");
        long newId = studyDb.subjectDao().insertSubject(mTestSubject);
        mTestSubject.setId(newId);

        // Set preferences so newly created subject appears first.
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mAppContext);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString("subject_order", "new_first");
        editor.apply();
    }

    /**
     * Invoked after each test method. It simply removes {@link #mTestSubject} from
     * {@link StudyDatabase}.
     */
    @After
    public void deleteTestSubject() {
        StudyDatabase studyDb = StudyDatabase.getInstance(mAppContext);
        studyDb.subjectDao().deleteSubject(mTestSubject);
    }

    /**
     * Integration test that verifies that clicking {@link #mTestSubject} in {@link SubjectActivity}
     * starts the appropriate {@link QuestionActivity}.
     */
    @Test
    public void testClickSubject() {

        // Initialize Espresso-Intents.
        Intents.init();

        // Start SubjectActivity.
        ActivityScenario<SubjectActivity> activityScenario =
                ActivityScenario.launch(SubjectActivity.class);

        // Click on first subject in RecyclerView.
        onView(withId(R.id.subject_recycler_view)).perform(actionOnItemAtPosition(0, click()));

        // Verify QuestionActivity started with test subject.
        intended(allOf(
                hasComponent(QuestionActivity.class.getName()),
                hasExtra(QuestionActivity.EXTRA_SUBJECT_ID, mTestSubject.getId())
        ));

        // Clean up.
        activityScenario.close();

        // Must be called at end of each test case.
        Intents.release();
    }

    /**
     * Integration test that verifies that a question can be added properly in
     * {@link QuestionEditActivity}.
     */
    @Test
    public void testAddQuestion() {

        // Initialize Espresso-Intents.
        Intents.init();

        // Create a test question.
        String questionText = "TEST QUESTION";
        Question question = new Question();
        question.setSubjectId(mTestSubject.getId());
        question.setText(questionText);
        question.setAnswer("TEST ANSWER");

        // Add question to database.
        StudyDatabase studyDb = StudyDatabase.getInstance(mAppContext);
        long newId = studyDb.questionDao().insertQuestion(question);
        question.setId(newId);

        // Create an ActivityResult stub that returns the question ID of the question added.
        Intent data = new Intent();
        data.putExtra(QuestionEditActivity.EXTRA_QUESTION_ID, question.getId());
        Instrumentation.ActivityResult activityResult = new Instrumentation.ActivityResult(RESULT_OK, data);

        // Verify QuestionEditActivity tried to start and respond with stub result.
        intending(hasComponent(QuestionEditActivity.class.getName())).respondWith(activityResult);

        // Start QuestionActivity with the test subject.
        Intent intent = new Intent(mAppContext, QuestionActivity.class);
        intent.putExtra(QuestionActivity.EXTRA_SUBJECT_ID, mTestSubject.getId());
        ActivityScenario<QuestionActivity> activityScenario = ActivityScenario.launch(intent);

        // Open overflow menu.
        openActionBarOverflowOrOptionsMenu(mAppContext);

        // Click on the Add menu item.
        onView(withText("Add")).perform(click());

        // Verify the added question is displayed.
        onView(withId(R.id.question_text_view)).check(matches(withText(questionText)));

        // Clean up.
        activityScenario.close();

        // Must be called at end of each test case.
        Intents.release();
    }
}