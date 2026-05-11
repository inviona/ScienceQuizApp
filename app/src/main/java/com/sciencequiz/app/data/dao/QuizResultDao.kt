package com.sciencequiz.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sciencequiz.app.data.entity.QuizResult

@Dao
interface QuizResultDao {

    @Insert
    suspend fun insert(result: QuizResult)

    @Query("SELECT * FROM quiz_results ORDER BY timestamp DESC")
    suspend fun getAllResults(): List<QuizResult>

    @Query("SELECT * FROM quiz_results ORDER BY timestamp DESC LIMIT 10")
    suspend fun getRecentResults(): List<QuizResult>

    @Query("SELECT AVG(score * 1.0 / total) * 100 FROM quiz_results")
    suspend fun getAverageScore(): Double

    @Query("SELECT COUNT(*) FROM quiz_results")
    suspend fun getTotalQuizzes(): Int

    @Query("DELETE FROM quiz_results")
    suspend fun deleteAll()
}
