package com.sciencequiz.app.data.repository

import com.sciencequiz.app.data.dao.QuestionDao
import com.sciencequiz.app.data.dao.QuizResultDao
import com.sciencequiz.app.data.entity.Question
import com.sciencequiz.app.data.entity.QuizResult

class QuizRepository(
    private val questionDao: QuestionDao,
    private val quizResultDao: QuizResultDao
) {
    suspend fun getQuestions(category: String, difficulty: Int, limit: Int = 10): List<Question> {
        return when {
            category == "All" && difficulty == 0 -> questionDao.getRandomQuestions(limit)
            category == "All" -> questionDao.getQuestionsByDifficulty(difficulty, limit)
            difficulty == 0 -> questionDao.getQuestionsByCategory(category, limit)
            else -> questionDao.getQuestionsByCategory(category, limit)
                .filter { it.difficulty == difficulty }
                .take(limit)
                .ifEmpty { questionDao.getQuestionsByCategory(category, limit) }
        }
    }

    suspend fun saveResult(result: QuizResult) {
        quizResultDao.insert(result)
    }

    suspend fun getAverageScore(): Double {
        val avg = quizResultDao.getAverageScore()
        return if (avg.isNaN()) 0.0 else avg
    }

    suspend fun getTotalQuizzes(): Int {
        return quizResultDao.getTotalQuizzes()
    }

    suspend fun getRecentResults(): List<QuizResult> {
        return quizResultDao.getRecentResults()
    }

    suspend fun deleteAllResults() {
        quizResultDao.deleteAll()
    }
}
