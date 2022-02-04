package com.davidread.studyhelper;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * {@link StudyDatabase} defines this app's database configuration and serves as the main access
 * point for manipulating persisted {@link Subject} and {@link Question} objects.
 */
@Database(entities = {Question.class, Subject.class}, version = 1)
public abstract class StudyDatabase extends RoomDatabase {

    /**
     * {@link String} file name of the database file.
     */
    private static final String DATABASE_NAME = "study.db";

    /**
     * Static reference of {@link StudyDatabase} to follow singleton pattern.
     */
    private static StudyDatabase mStudyDatabase;

    /**
     * Returns an instance of {@link StudyDatabase}.
     *
     * @return An instance of {@link StudyDatabase}.
     */
    public static StudyDatabase getInstance(Context context) {
        if (mStudyDatabase == null) {
            mStudyDatabase = Room.databaseBuilder(context, StudyDatabase.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .build();
            mStudyDatabase.addStarterData();
        }
        return mStudyDatabase;
    }

    /**
     * {@link QuestionDao} instance for manipulating persisted {@link Question} objects.
     *
     * @return A {@link QuestionDao} instance.
     */
    public abstract QuestionDao questionDao();

    /**
     * {@link SubjectDao} instance for manipulating persisted {@link Subject} objects.
     *
     * @return A {@link QuestionDao} instance.
     */
    public abstract SubjectDao subjectDao();

    /**
     * Checks if the database is empty. If so, it initializes it with some dummy {@link Subject} and
     * {@link Question} objects.
     */
    private void addStarterData() {

        if (subjectDao().getSubjects().size() == 0) {

            // Execute code on a background thread.
            runInTransaction(() -> {

                Subject subject = new Subject("Math");
                long subjectId = subjectDao().insertSubject(subject);

                Question question = new Question("What is 2 + 3?",
                        "2 + 3 = 5",
                        subjectId
                );
                questionDao().insertQuestion(question);

                question = new Question("What is pi?",
                        "Pi is the ratio of a circle's circumference to its diameter.",
                        subjectId
                );
                questionDao().insertQuestion(question);

                subject = new Subject("History");
                subjectId = subjectDao().insertSubject(subject);

                question = new Question("On what date was the U.S. Declaration of Independence adopted?",
                        "July 4, 1776.",
                        subjectId
                );
                questionDao().insertQuestion(question);

                subject = new Subject("Computing");
                subjectId = subjectDao().insertSubject(subject);
            });
        }
    }
}
