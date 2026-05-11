package com.sciencequiz.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_results")
data class QuizResult(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val score: Int,
    val total: Int,
    val category: String,
    val difficulty: Int,
    val timestamp: Long = System.currentTimeMillis()
)
