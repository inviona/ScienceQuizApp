package com.sciencequiz.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questions")
data class Question(
    @PrimaryKey val id: Int,
    val questionText: String,
    val option1: String,
    val option2: String,
    val option3: String,
    val option4: String,
    val correctAnswer: Int,
    val category: String,
    val funFact: String,
    val difficulty: Int
)
