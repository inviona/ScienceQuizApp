package com.sciencequiz.app.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sciencequiz.app.ScienceQuizApp
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("sciencequest_prefs", Context.MODE_PRIVATE)
    private val repository = (application as ScienceQuizApp).repository

    val ttsEnabled = MutableLiveData(prefs.getBoolean("tts_enabled", true))
    val selectedDifficulty = MutableLiveData(prefs.getInt("difficulty", 0))

    private val _resetComplete = MutableLiveData(false)
    val resetComplete: LiveData<Boolean> = _resetComplete

    fun setTtsEnabled(enabled: Boolean) {
        ttsEnabled.value = enabled
        prefs.edit().putBoolean("tts_enabled", enabled).apply()
    }

    fun setDifficulty(difficulty: Int) {
        selectedDifficulty.value = difficulty
        prefs.edit().putInt("difficulty", difficulty).apply()
    }

    fun resetProgress() {
        viewModelScope.launch {
            try {
                repository.deleteAllResults()
                _resetComplete.value = true
            } catch (_: Exception) { }
        }
    }
}
