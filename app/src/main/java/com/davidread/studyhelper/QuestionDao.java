package com.davidread.studyhelper;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * {@link QuestionDao} defines methods to select, insert, update, and delete {@link Question}
 * entities from the database maintained by {@link StudyDatabase}. At compile time, Room
 * automatically generates implementations of these methods.
 */
@Dao
public interface QuestionDao {

    /**
     * Queries for a {@link Question} matching the passed id.
     *
     * @param id Long id to match.
     * @return A {@link Question}.
     */
    @Query("SELECT * FROM Question WHERE id = :id")
    public Question getQuestion(long id);

    /**
     * Queries for all {@link Question} objects matching a subject id and returns them in a
     * {@link List}.
     *
     * @param subjectId Long subject id to match.
     * @return A {@link List} of {@link Question} objects matching a subject id.
     */
    @Query("SELECT * FROM Question WHERE subject_id = :subjectId ORDER BY id")
    public List<Question> getQuestions(long subjectId);

    /**
     * Inserts a new {@link Question}.
     *
     * @param question A new {@link Question}.
     * @return The long id of the newly inserted {@link Question}.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long insertQuestion(Question question);

    /**
     * Updates all {@link Question} objects matching the id set in the object with the values set
     * in the object.
     *
     * @param question A {@link Question} whose id will be matched and whose values will be set onto
     *                 all matching objects.
     */
    @Update
    public void updateQuestion(Question question);

    /**
     * Deletes all {@link Question} objects matching the id set in the object.
     *
     * @param question A {@link Question} whose id will be matched.
     */
    @Delete
    public void deleteQuestion(Question question);
}