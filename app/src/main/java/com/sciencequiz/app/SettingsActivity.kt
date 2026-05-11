package com.sciencequiz.app

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.sciencequiz.app.viewmodel.SettingsViewModel
import com.sciencequiz.app.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[SettingsViewModel::class.java]

        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.switchTts.isChecked = viewModel.ttsEnabled.value ?: true
        binding.switchTts.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setTtsEnabled(isChecked)
        }

        binding.radioGroup.check(
            when (viewModel.selectedDifficulty.value ?: 0) {
                1 -> R.id.radio_easy
                2 -> R.id.radio_medium
                3 -> R.id.radio_hard
                else -> R.id.radio_all
            }
        )

        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val difficulty = when (checkedId) {
                R.id.radio_easy -> 1
                R.id.radio_medium -> 2
                R.id.radio_hard -> 3
                else -> 0
            }
            viewModel.setDifficulty(difficulty)
        }

        binding.btnReset.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Reset Progress")
                .setMessage("This will delete all your quiz results. This cannot be undone.")
                .setPositiveButton("Reset") { _, _ ->
                    viewModel.resetProgress()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun observeViewModel() {
        viewModel.resetComplete.observe(this) { complete ->
            if (complete) {
                Toast.makeText(this, "Progress reset successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
