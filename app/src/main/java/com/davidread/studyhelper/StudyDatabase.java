package com.davidread.studyhelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * {@link StudyDatabase} provides dummy {@link Subject} and {@link Question} objects. New objects
 * may be added, but they are not yet preserved in persistent storage.
 */
public class StudyDatabase {

    /**
     * Static reference of {@link StudyDatabase} to follow singleton pattern.
     */
    private static StudyDatabase mStudyDb;

    /**
     * {@link List} of {@link Subject} objects in this dummy database.
     */
    private List<Subject> mSubjects;

    /**
     * {@link HashMap} mapping a subject id to a {@link List} of {@link Question} objects.
     */
    private HashMap<Long, List<Question>> mQuestions;

    /**
     * Enums used to specify how to sort {@link Subject} objects.
     */
    public enum SubjectSortOrder {ALPHABETIC, UPDATE_DESC, UPDATE_ASC}

    /**
     * Returns an instance of {@link StudyDatabase}.
     *
     * @return An instance of {@link StudyDatabase}.
     */
    public static StudyDatabase getInstance() {
        if (mStudyDb == null) {
            mStudyDb = new StudyDatabase();
        }
        return mStudyDb;
    }

    // Prevent instantiating from outside the class

    /**
     * Constructs a new {@link StudyDatabase} with dummy objects put into {@link #mSubjects} and
     * {@link #mQuestions}. Is private to prevent instantiation outside of this class.
     */
    private StudyDatabase() {
        mSubjects = new ArrayList<>();
        mQuestions = new HashMap<>();

        Subject subject = new Subject("Math");
        subject.setId(1);
        addSubject(subject);

        Question question = new Question();
        question.setId(1);
        question.setText("What is 2 + 3?");
        question.setAnswer("2 + 3 = 5");
        question.setSubjectId(1);
        addQuestion(question);

        question = new Question();
        question.setId(2);
        question.setText("What is pi?");
        question.setAnswer("Pi is the ratio of a circle's circumference to its diameter.");
        question.setSubjectId(1);
        addQuestion(question);

        subject = new Subject("History");
        subject.setId(2);
        addSubject(subject);

        question = new Question();
        question.setId(3);
        question.setText("On what date was the U.S. Declaration of Independence adopted?");
        question.setAnswer("July 4, 1776.");
        question.setSubjectId(2);
        addQuestion(question);

        subject = new Subject("Computing");
        subject.setId(3);
        addSubject(subject);
    }

    /**
     * Adds a new {@link Subject} to {@link #mSubjects}.
     *
     * @param subject {@link Subject} to add.
     */
    public void addSubject(Subject subject) {
        mSubjects.add(subject);
        List<Question> questionList = new ArrayList<>();
        mQuestions.put(subject.getId(), questionList);
    }

    /**
     * Returns a {@link Subject} from {@link #mSubjects} given its id.
     *
     * @param subjectId Subject id to look for.
     * @return {@link Subject} associated with the passed id.
     */
    public Subject getSubject(long subjectId) {
        for (Subject subject : mSubjects) {
            if (subject.getId() == subjectId) {
                return subject;
            }
        }
        return null;
    }

    /**
     * Returns {@link #mSubjects}.
     *
     * @param order {@link SubjectSortOrder} specifying how to sort {@link #mSubjects}.
     * @return {@link #mSubjects}.
     */
    public List<Subject> getSubjects(SubjectSortOrder order) {
        return mSubjects;
    }

    /**
     * Adds a new {@link Question} to {@link #mQuestions}.
     *
     * @param question {@link Question} to add.
     */
    public void addQuestion(Question question) {
        List<Question> questionList = mQuestions.get(question.getSubjectId());
        if (questionList != null) {
            questionList.add(question);
        }
    }

    /**
     * Returns a {@link List} of {@link Question} objects given a subject id.
     *
     * @param subjectId Subject id to look for.
     * @return A {@link List} of {@link Question} objects associated with the subject id.
     */
    public List<Question> getQuestions(long subjectId) {
        return mQuestions.get(subjectId);
    }
}
