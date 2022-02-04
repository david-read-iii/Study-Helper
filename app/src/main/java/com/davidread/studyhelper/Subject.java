package com.davidread.studyhelper;

/**
 * {@link Subject} is a class used to model a single subject. A subject has a unique id, text, and
 * an update time.
 */
public class Subject {

    /**
     * Unique long id for the subject.
     */
    private long mId;

    /**
     * {@link String} text for the subject.
     */
    private String mText;

    /**
     * Long representing the update time for the subject.
     */
    private long mUpdateTime;

    /**
     * Constructs a new {@link Subject}.
     *
     * @param text {@link String} text for the subject.
     */
    public Subject(String text) {
        mText = text;
        mUpdateTime = System.currentTimeMillis();
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getText() {
        return mText;
    }

    public void setText(String subject) {
        mText = subject;
    }

    public long getUpdateTime() {
        return mUpdateTime;
    }

    public void setUpdateTime(long updateTime) {
        mUpdateTime = updateTime;
    }
}
