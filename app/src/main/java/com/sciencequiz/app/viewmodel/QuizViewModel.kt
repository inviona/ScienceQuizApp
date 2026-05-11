package com.sciencequiz.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sciencequiz.app.ScienceQuizApp
import com.sciencequiz.app.data.entity.Question
import com.sciencequiz.app.data.entity.QuizResult
import kotlinx.coroutines.launch

class QuizViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as ScienceQuizApp).repository

    private val _questions = MutableLiveData<List<Question>>()
    val questions: LiveData<List<Question>> = _questions

    private val _currentIndex = MutableLiveData(0)
    val currentIndex: LiveData<Int> = _currentIndex

    private val _selectedAnswer = MutableLiveData<Int?>(null)
    val selectedAnswer: LiveData<Int?> = _selectedAnswer

    private val _isAnswered = MutableLiveData(false)
    val isAnswered: LiveData<Boolean> = _isAnswered

    private val _score = MutableLiveData(0)
    val score: LiveData<Int> = _score

    private val _isCorrect = MutableLiveData<Boolean?>(null)
    val isCorrect: LiveData<Boolean?> = _isCorrect

    private val _isFinished = MutableLiveData(false)
    val isFinished: LiveData<Boolean> = _isFinished

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _category = MutableLiveData("All")
    private val _difficulty = MutableLiveData(0)

    fun startQuiz(category: String, difficulty: Int) {
        _category.value = category
        _difficulty.value = difficulty
        _currentIndex.value = 0
        _score.value = 0
        _isFinished.value = false
        _isAnswered.value = false
        _selectedAnswer.value = null
        _isCorrect.value = null
        loadQuestions(category, difficulty)
    }

    private fun loadQuestions(category: String, difficulty: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _questions.value = repository.getQuestions(category, difficulty)
            } catch (e: Exception) {
                _questions.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectAnswer(answerIndex: Int) {
        if (_isAnswered.value == true) return
        _selectedAnswer.value = answerIndex
        val question = _questions.value?.getOrNull(_currentIndex.value ?: 0) ?: return
        val correct = answerIndex == question.correctAnswer
        _isCorrect.value = correct
        _isAnswered.value = true
        if (correct) {
            _score.value = (_score.value ?: 0) + 1
        }
    }

    fun nextQuestion() {
        val current = _currentIndex.value ?: 0
        val total = _questions.value?.size ?: 0
        if (current + 1 >= total) {
            finishQuiz()
        } else {
            _currentIndex.value = current + 1
            _isAnswered.value = false
            _selectedAnswer.value = null
            _isCorrect.value = null
        }
    }

    private fun finishQuiz() {
        _isFinished.value = true
        viewModelScope.launch {
            try {
                val result = QuizResult(
                    score = _score.value ?: 0,
                    total = _questions.value?.size ?: 0,
                    category = _category.value ?: "All",
                    difficulty = _difficulty.value ?: 0
                )
                repository.saveResult(result)
            } catch (_: Exception) { }
        }
    }

    fun getCurrentQuestion(): Question? {
        val idx = _currentIndex.value ?: return null
        return _questions.value?.getOrNull(idx)
    }

    fun getCurrentQuestionNumber(): Int {
        return (_currentIndex.value ?: 0) + 1
    }

    fun getTotalQuestions(): Int {
        return _questions.value?.size ?: 0
    }
}
