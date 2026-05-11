package com.sciencequiz.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sciencequiz.app.data.entity.Question

@Dao
interface QuestionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(questions: List<Question>)

    @Query("SELECT * FROM questions WHERE category = :category ORDER BY RANDOM() LIMIT :limit")
    suspend fun getQuestionsByCategory(category: String, limit: Int): List<Question>

    @Query("SELECT * FROM questions ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomQuestions(limit: Int): List<Question>

    @Query("SELECT * FROM questions WHERE difficulty = :difficulty ORDER BY RANDOM() LIMIT :limit")
    suspend fun getQuestionsByDifficulty(difficulty: Int, limit: Int): List<Question>

    @Query("SELECT COUNT(*) FROM questions")
    suspend fun getCount(): Int
}
