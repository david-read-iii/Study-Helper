package com.davidread.studyhelper;

/**
 * {@link Question} is a class used to model a single question. A question has a unique id, text, an
 * answer, and a subject id.
 */
public class Question {

    /**
     * Unique long id for the question.
     */
    private long mId;

    /**
     * {@link String} text for the question.
     */
    private String mText;

    /**
     * {@link String} answer to the question.
     */
    private String mAnswer;

    /**
     * Long id corresponding to an existing subject.
     */
    private long mSubjectId;

    public void setId(long id) {
        mId = id;
    }

    public long getId() {
        return mId;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public String getAnswer() {
        return mAnswer;
    }

    public void setAnswer(String answer) {
        mAnswer = answer;
    }

    public long getSubjectId() {
        return mSubjectId;
    }

    public void setSubjectId(long subjectId) {
        mSubjectId = subjectId;
    }
}
