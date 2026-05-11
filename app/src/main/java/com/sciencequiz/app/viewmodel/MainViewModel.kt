package com.sciencequiz.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sciencequiz.app.ScienceQuizApp
import com.sciencequiz.app.data.entity.QuizResult
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as ScienceQuizApp).repository

    private val _averageScore = MutableLiveData<Double>()
    val averageScore: LiveData<Double> = _averageScore

    private val _totalQuizzes = MutableLiveData<Int>()
    val totalQuizzes: LiveData<Int> = _totalQuizzes

    private val _recentResults = MutableLiveData<List<QuizResult>>()
    val recentResults: LiveData<List<QuizResult>> = _recentResults

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadStats() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _averageScore.value = repository.getAverageScore()
                _totalQuizzes.value = repository.getTotalQuizzes()
                _recentResults.value = repository.getRecentResults()
            } catch (e: Exception) {
                _averageScore.value = 0.0
                _totalQuizzes.value = 0
                _recentResults.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
