package com.sciencequiz.app

import android.app.Application
import com.sciencequiz.app.data.AppDatabase
import com.sciencequiz.app.data.repository.QuizRepository

class ScienceQuizApp : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy {
        QuizRepository(database.questionDao(), database.quizResultDao())
    }
}
