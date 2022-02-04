package com.davidread.studyhelper;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * {@link Subject} is a class used to model a single subject. A subject has a unique id, text, and
 * an update time.
 */
@Entity
public class Subject {

    /**
     * Unique long id for the subject.
     */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long mId;

    /**
     * {@link String} text for the subject.
     */
    @NonNull
    @ColumnInfo(name = "text")
    private String mText;

    /**
     * Long representing the update time for the subject.
     */
    @ColumnInfo(name = "updated")
    private long mUpdateTime;

    /**
     * Constructs a new {@link Subject}.
     *
     * @param text {@link String} text for the subject.
     */
    public Subject(@NonNull String text) {
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

    public void setText(@NonNull String text) {
        mText = text;
    }

    public long getUpdateTime() {
        return mUpdateTime;
    }

    public void setUpdateTime(long updateTime) {
        mUpdateTime = updateTime;
    }
}
