package com.sciencequiz.app

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.sciencequiz.app.viewmodel.MainViewModel
import com.sciencequiz.app.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private var textToSpeech: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        textToSpeech = TextToSpeech(this) { status ->
            if (status != TextToSpeech.ERROR) {
                textToSpeech?.language = Locale.US
            }
        }

        setupViews()
        observeViewModel()

        viewModel.loadStats()
    }

    private fun setupViews() {
        binding.btnPhysics.setOnClickListener {
            startQuiz("Physics")
        }
        binding.btnChemistry.setOnClickListener {
            startQuiz("Chemistry")
        }
        binding.btnBiology.setOnClickListener {
            startQuiz("Biology")
        }
        binding.btnRandom.setOnClickListener {
            startQuiz("All")
        }
        binding.cardSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        binding.cardCamera.setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }
        binding.fabShare.setOnClickListener {
            shareProgress()
        }
        binding.cardLearnMore.setOnClickListener {
            openScienceLinks()
        }
    }

    private fun observeViewModel() {
        viewModel.averageScore.observe(this) { avg ->
            binding.tvAverageScore.text = "${avg.toInt()}%"
        }
        viewModel.totalQuizzes.observe(this) { total ->
            binding.tvTotalQuizzes.text = total.toString()
        }
        viewModel.isLoading.observe(this) { loading ->
            binding.progressBar.visibility = if (loading) android.view.View.VISIBLE else android.view.View.GONE
        }
    }

    private fun startQuiz(category: String) {
        val intent = Intent(this, QuizActivity::class.java)
        intent.putExtra("category", category)
        startActivity(intent)
    }

    private fun shareProgress() {
        val avg = viewModel.averageScore.value ?: 0.0
        val total = viewModel.totalQuizzes.value ?: 0
        val text = "I've completed $total quizzes on ScienceQuest with an average score of ${avg.toInt()}%! Can you beat my score?"
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        startActivity(Intent.createChooser(shareIntent, "Share your progress"))
    }

    private fun openScienceLinks() {
        val url = "https://www.sciencekids.co.nz/"
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = android.net.Uri.parse(url)
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "No browser available", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadStats()
    }

    override fun onDestroy() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        super.onDestroy()
    }
}
