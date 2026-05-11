package com.sciencequiz.app

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sciencequiz.app.databinding.ActivityResultBinding
import java.util.Locale

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private var textToSpeech: TextToSpeech? = null
    private var score = 0
    private var total = 0
    private var category = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        score = intent.getIntExtra("score", 0)
        total = intent.getIntExtra("total", 0)
        category = intent.getStringExtra("category") ?: "All"

        textToSpeech = TextToSpeech(this) { status ->
            if (status != TextToSpeech.ERROR) {
                textToSpeech?.language = Locale.US
            }
        }

        setupViews()
    }

    private fun setupViews() {
        val percentage = if (total > 0) (score * 100 / total) else 0

        binding.tvScore.text = "$score / $total"
        binding.tvPercentage.text = "${percentage}%"
        binding.tvCategory.text = "Category: $category"

        val message = when {
            percentage >= 90 -> "Excellent! You're a Science Genius!"
            percentage >= 70 -> "Great job! Keep learning!"
            percentage >= 50 -> "Good effort! Review the fun facts."
            else -> "Don't give up! Try again!"
        }
        binding.tvMessage.text = message

        val stars = when {
            percentage >= 90 -> 3
            percentage >= 70 -> 2
            percentage >= 50 -> 1
            else -> 0
        }
        updateStars(stars)

        val prefs = getSharedPreferences("sciencequest_prefs", MODE_PRIVATE)
        if (prefs.getBoolean("tts_enabled", true)) {
            textToSpeech?.speak("You scored $score out of $total. $message", TextToSpeech.QUEUE_FLUSH, null, null)
        }

        binding.btnShare.setOnClickListener {
            shareResult()
        }

        binding.btnLearnMore.setOnClickListener {
            learnMore()
        }

        binding.btnPlayAgain.setOnClickListener {
            val intent = Intent(this, QuizActivity::class.java).apply {
                putExtra("category", category)
            }
            startActivity(intent)
            finish()
        }

        binding.btnHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            startActivity(intent)
            finish()
        }
    }

    private fun updateStars(count: Int) {
        binding.star1.setImageResource(if (count >= 1) R.drawable.ic_star_filled else R.drawable.ic_star_empty)
        binding.star2.setImageResource(if (count >= 2) R.drawable.ic_star_filled else R.drawable.ic_star_empty)
        binding.star3.setImageResource(if (count >= 3) R.drawable.ic_star_filled else R.drawable.ic_star_empty)
    }

    private fun shareResult() {
        val text = "I scored $score/$total on ScienceQuest ($category)! ${if (score == total) "Perfect score! 🎉" else "Try to beat me!"}"
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        startActivity(Intent.createChooser(shareIntent, "Share your result"))
    }

    private fun learnMore() {
        val url = "https://www.sciencekids.co.nz/experiments.html"
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = android.net.Uri.parse(url)
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "No browser available", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        super.onDestroy()
    }
}
