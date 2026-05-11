package com.sciencequiz.app

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.sciencequiz.app.viewmodel.QuizViewModel
import com.sciencequiz.app.databinding.ActivityQuizBinding
import java.util.Locale

class QuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuizBinding
    private lateinit var viewModel: QuizViewModel
    private var textToSpeech: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[QuizViewModel::class.java]
        textToSpeech = TextToSpeech(this) { status ->
            if (status != TextToSpeech.ERROR) {
                textToSpeech?.language = Locale.US
            }
        }

        val category = intent.getStringExtra("category") ?: "All"
        val prefs = getSharedPreferences("sciencequest_prefs", MODE_PRIVATE)
        val difficulty = prefs.getInt("difficulty", 0)

        viewModel.startQuiz(category, difficulty)
        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(this) { loading ->
            binding.progressBar.visibility = if (loading) android.view.View.VISIBLE else android.view.View.GONE
            binding.btnOption1.isEnabled = !loading
            binding.btnOption2.isEnabled = !loading
            binding.btnOption3.isEnabled = !loading
            binding.btnOption4.isEnabled = !loading
        }

        viewModel.questions.observe(this) { questions ->
            if (questions.isEmpty()) {
                Toast.makeText(this, "No questions available", Toast.LENGTH_SHORT).show()
                finish()
                return@observe
            }
            updateQuestion()
        }

        viewModel.currentIndex.observe(this) {
            updateQuestion()
        }

        viewModel.isAnswered.observe(this) { answered ->
            if (answered) {
                val correct = viewModel.isCorrect.value
                val question = viewModel.getCurrentQuestion()
                if (correct == true) {
                    showCorrectFeedback()
                } else if (correct == false) {
                    showWrongFeedback(question?.correctAnswer ?: 1)
                }
            } else {
                resetButtonStyles()
            }
        }

        viewModel.isFinished.observe(this) { finished ->
            if (finished) {
                val score = viewModel.score.value ?: 0
                val total = viewModel.getTotalQuestions()
                val intent = Intent(this, ResultActivity::class.java).apply {
                    putExtra("score", score)
                    putExtra("total", total)
                    putExtra("category", intent.getStringExtra("category") ?: "All")
                }
                startActivity(intent)
                finish()
            }
        }
    }

    private fun setupListeners() {
        binding.btnOption1.setOnClickListener { viewModel.selectAnswer(1) }
        binding.btnOption2.setOnClickListener { viewModel.selectAnswer(2) }
        binding.btnOption3.setOnClickListener { viewModel.selectAnswer(3) }
        binding.btnOption4.setOnClickListener { viewModel.selectAnswer(4) }

        binding.btnNext.setOnClickListener {
            viewModel.nextQuestion()
        }

        binding.btnSpeak.setOnClickListener {
            val question = viewModel.getCurrentQuestion() ?: return@setOnClickListener
            val prefs = getSharedPreferences("sciencequest_prefs", MODE_PRIVATE)
            if (prefs.getBoolean("tts_enabled", true)) {
                textToSpeech?.speak(question.questionText, TextToSpeech.QUEUE_FLUSH, null, null)
            } else {
                Toast.makeText(this, "TTS is disabled in settings", Toast.LENGTH_SHORT).show()
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Quit Quiz?")
                .setMessage("Your progress will be lost.")
                .setPositiveButton("Quit") { _, _ -> finish() }
                .setNegativeButton("Continue", null)
                .show()
        }
    }

    private fun updateQuestion() {
        val question = viewModel.getCurrentQuestion() ?: return
        val current = viewModel.getCurrentQuestionNumber()
        val total = viewModel.getTotalQuestions()

        binding.tvProgress.text = "$current / $total"
        binding.progressQuiz.max = total
        binding.progressQuiz.progress = current

        binding.tvCategory.text = question.category
        binding.tvQuestion.text = question.questionText
        binding.btnOption1.text = question.option1
        binding.btnOption2.text = question.option2
        binding.btnOption3.text = question.option3
        binding.btnOption4.text = question.option4

        binding.btnNext.visibility = android.view.View.GONE
        binding.tvFunFact.visibility = android.view.View.GONE
        resetButtonStyles()
    }

    private fun resetButtonStyles() {
        val options = listOf(binding.btnOption1, binding.btnOption2, binding.btnOption3, binding.btnOption4)
        for (btn in options) {
            btn.setBackgroundResource(R.drawable.bg_button_option)
            btn.setTextColor(ContextCompat.getColor(this, R.color.text_primary))
            btn.isEnabled = true
        }
    }

    private fun showCorrectFeedback() {
        val selected = viewModel.selectedAnswer.value ?: return
        val options = listOf(binding.btnOption1, binding.btnOption2, binding.btnOption3, binding.btnOption4)
        val btn = options.getOrNull(selected - 1) ?: return
        btn.setBackgroundResource(R.drawable.bg_button_correct)
        btn.setTextColor(ContextCompat.getColor(this, R.color.snow_white))

        binding.tvFunFact.text = "Fun Fact: ${viewModel.getCurrentQuestion()?.funFact ?: ""}"
        binding.tvFunFact.visibility = android.view.View.VISIBLE
        binding.btnNext.visibility = android.view.View.VISIBLE

        val prefs = getSharedPreferences("sciencequest_prefs", MODE_PRIVATE)
        if (prefs.getBoolean("tts_enabled", true)) {
            textToSpeech?.speak("Correct!", TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    private fun showWrongFeedback(correctAnswer: Int) {
        val selected = viewModel.selectedAnswer.value ?: return
        val options = listOf(binding.btnOption1, binding.btnOption2, binding.btnOption3, binding.btnOption4)
        for (i in options.indices) {
            if (i + 1 == correctAnswer) {
                options[i].setBackgroundResource(R.drawable.bg_button_correct)
                options[i].setTextColor(ContextCompat.getColor(this, R.color.snow_white))
            } else if (i + 1 == selected) {
                options[i].setBackgroundResource(R.drawable.bg_button_wrong)
                options[i].setTextColor(ContextCompat.getColor(this, R.color.snow_white))
            }
            options[i].isEnabled = false
        }

        binding.tvFunFact.text = "Fun Fact: ${viewModel.getCurrentQuestion()?.funFact ?: ""}"
        binding.tvFunFact.visibility = android.view.View.VISIBLE
        binding.btnNext.visibility = android.view.View.VISIBLE

        val prefs = getSharedPreferences("sciencequest_prefs", MODE_PRIVATE)
        if (prefs.getBoolean("tts_enabled", true)) {
            textToSpeech?.speak("Incorrect!", TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    override fun onDestroy() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        super.onDestroy()
    }
}
