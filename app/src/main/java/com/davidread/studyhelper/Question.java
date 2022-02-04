package com.davidread.studyhelper;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

/**
 * {@link Question} is a class used to model a single question. A question has a unique id, text, an
 * answer, and a subject id.
 */
@Entity(foreignKeys = @ForeignKey(entity = Subject.class, parentColumns = "id",
        childColumns = "subject_id", onDelete = CASCADE))
public class Question {

    /**
     * Unique long id for the question.
     */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long mId;

    /**
     * {@link String} text for the question.
     */
    @ColumnInfo(name = "text")
    private String mText;

    /**
     * {@link String} answer to the question.
     */
    @ColumnInfo(name = "answer")
    private String mAnswer;

    /**
     * Long id corresponding to an existing subject.
     */
    @ColumnInfo(name = "subject_id")
    private long mSubjectId;

    /**
     * Constructs a new {@link Question}.
     *
     * @param mText      {@link String} text for the question.
     * @param mAnswer    {@link String} answer to the question.
     * @param mSubjectId Long id corresponding to an existing subject.
     */
    public Question(String mText, String mAnswer, long mSubjectId) {
        this.mText = mText;
        this.mAnswer = mAnswer;
        this.mSubjectId = mSubjectId;
    }

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
