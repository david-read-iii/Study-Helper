package com.davidread.studyhelper;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * {@link SubjectDao} defines methods to select, insert, update, and delete {@link Subject} entities
 * from the database maintained by {@link StudyDatabase}. At compile time, Room automatically
 * generates implementations of these methods.
 */
@Dao
public interface SubjectDao {

    /**
     * Queries for a {@link Subject} matching the passed id.
     *
     * @param id Long id to match.
     * @return A {@link Subject}.
     */
    @Query("SELECT * FROM Subject WHERE id = :id")
    public Subject getSubject(long id);

    /**
     * Queries for a {@link Subject} matching the passed text.
     *
     * @param subjectText Long id to match.
     * @return A {@link Subject}.
     */
    @Query("SELECT * FROM Subject WHERE text = :subjectText")
    public Subject getSubjectByText(String subjectText);

    /**
     * Queries for all {@link Subject} objects and returns them in a {@link List}.
     *
     * @return A {@link List} of {@link Subject} objects.
     */
    @Query("SELECT * FROM Subject ORDER BY text COLLATE NOCASE")
    public List<Subject> getSubjects();

    /**
     * Queries for all {@link Subject} objects and returns them in a {@link List} sorted such that
     * newest objects are returned first.
     *
     * @return A {@link List} of {@link Subject} objects.
     */
    @Query("SELECT * FROM Subject ORDER BY updated DESC")
    public List<Subject> getSubjectsNewerFirst();

    /**
     * Queries for all {@link Subject} objects and returns them in a {@link List} sorted such that
     * older objects are returned first.
     *
     * @return A {@link List} of {@link Subject} objects.
     */
    @Query("SELECT * FROM Subject ORDER BY updated ASC")
    public List<Subject> getSubjectsOlderFirst();

    /**
     * Inserts a new {@link Subject}.
     *
     * @param subject A new {@link Subject}.
     * @return The long id of the newly inserted {@link Subject}.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long insertSubject(Subject subject);

    /**
     * Updates all {@link Subject} objects matching the id set in the object with the values set
     * in the object.
     *
     * @param subject A {@link Subject} whose id will be matched and whose values will be set onto
     *                all matching objects.
     */
    @Update
    public void updateSubject(Subject subject);

    /**
     * Deletes all {@link Subject} objects matching the id set in the object.
     *
     * @param subject A {@link Subject} whose id will be matched.
     */
    @Delete
    public void deleteSubject(Subject subject);
}