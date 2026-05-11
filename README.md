# ScienceQuest

A feature-rich Android science quiz application built with Kotlin, Material Design 3, and modern Android architecture components.

## Features

### Quiz System
- **Multiple Categories**: Physics, Chemistry, Biology, or Random (mixed) questions
- **Difficulty Levels**: All Levels, Easy, Medium, Hard — configurable in Settings
- **20 Built-in Questions**: Curated science questions with fun facts for each answer
- **Randomized Questions**: Questions are shuffled each quiz session
- **10 Questions Per Quiz**: Consistent quiz length for fair scoring
- **Instant Feedback**: Correct/wrong visual feedback on option buttons after each answer
- **Fun Facts**: An educational fun fact is shown after each question is answered
- **Score Tracking**: Score is incremented in real-time for correct answers
- **Progress Indicator**: Linear progress bar and text showing current question number

### Results & Scoring
- **Detailed Results Screen**: Shows score (e.g., 8/10), percentage, category
- **Star Rating**: 0–3 stars based on performance (≥90% = 3 stars, ≥70% = 2 stars, ≥50% = 1 star)
- **Encouragement Messages**: Dynamic messages based on score percentage
- **Play Again**: Restart the same quiz category directly from results
- **Persistent Stats**: Average score and total quizzes count displayed on the home screen
- **SQLite Persistence**: All quiz results saved locally via Room database

### Text-to-Speech (TTS)
- **Question Reading**: Tap the speaker icon to hear the current question read aloud
- **Result Announcement**: Score and message are spoken aloud on the results screen
- **Correct/Incorrect Audio**: Vocal feedback after answering (toggleable)
- **Settings Toggle**: Enable/disable TTS in the Settings screen
- **Persistence**: TTS preference is saved across app sessions

### Camera Integration
- **CameraX-powered**: Uses Android CameraX for reliable camera control
- **In-app Photo Capture**: Take a profile photo within the app
- **Confirm/Retake Flow**: Preview captured photo with confirm or retake options
- **Runtime Permission Handling**: Requests camera permission with proper fallback
- **Persistent Photo**: Saved photo path stored in SharedPreferences

### Settings
- **Text-to-Speech Toggle**: Enable or disable all TTS features
- **Difficulty Selection**: Choose quiz difficulty (All/Easy/Medium/Hard)
- **Reset Progress**: Delete all quiz results with confirmation dialog

### Share & Social
- **Share Progress**: Share stats via any installed app (WhatsApp, email, etc.)
- **Share Results**: Share individual quiz results from the results screen
- **External Learning**: "Learn More" button opens science education websites

### UI/UX
- **Material Design 3**: Modern UI with Material components (Cards, Chips, Progress Indicators)
- **Category-specific Colors**: Physics (indigo), Chemistry (teal), Biology (green), Random (orange)
- **Smooth Navigation**: Back navigation with confirmation dialog to prevent accidental quiz exits
- **Loading States**: Circular progress indicator during data loading
- **Scrollable Layouts**: All screens use NestedScrollView for content overflow

## Architecture

### Tech Stack
- **Language**: Kotlin
- **Minimum SDK**: 26 (Android 8.0)
- **Target SDK**: 34 (Android 14)
- **Architecture**: MVVM (Model-View-ViewModel)
- **Database**: Room (SQLite)
- **UI**: View Binding, Material Design 3, ConstraintLayout
- **Camera**: CameraX
- **Async**: Kotlin Coroutines + LiveData

### Dependencies
| Library             | Purpose                      |
|---------------------|------------------------------|
| AppCompat           | Backward-compatible UI       |
| Material            | Material Design 3 components |
| ConstraintLayout    | Complex layouts              |
| Room                | Local database (SQLite)      |
| Lifecycle (ViewModel/LiveData) | MVVM architecture  |
| CameraX             | Camera integration           |
| Coroutines          | Async operations             |
| RecyclerView        | List display                 |

### Project Structure
```
com.sciencequiz.app/
├── ScienceQuizApp.kt              # Application class
├── MainActivity.kt                # Home screen
├── QuizActivity.kt                # Quiz screen
├── ResultActivity.kt              # Results screen
├── SettingsActivity.kt            # Settings screen
├── CameraActivity.kt              # Profile photo camera
├── viewmodel/
│   ├── MainViewModel.kt           # Home screen logic
│   ├── QuizViewModel.kt           # Quiz logic
│   └── SettingsViewModel.kt       # Settings logic
└── data/
    ├── AppDatabase.kt             # Room database + seed data
    ├── entity/
    │   ├── Question.kt            # Question entity
    │   └── QuizResult.kt          # Quiz result entity
    ├── dao/
    │   ├── QuestionDao.kt         # Question queries
    │   └── QuizResultDao.kt       # Result queries
    └── repository/
        └── QuizRepository.kt      # Data access layer
```

## Requirements Checklist

| Requirement                          | Implemented          | Location                                                               |
|--------------------------------------|----------------------|------------------------------------------------------------------------|
| Kotlin language                      | ✅                   | All `.kt` files                                                        |
| MVVM architecture                    | ✅                   | ViewModels + Activities as Views                                       |
| View Binding                          | ✅                   | `build.gradle.kts:36`, used in all Activities                          |
| Room database                        | ✅                   | `AppDatabase.kt`, `Question.kt`, `QuizResult.kt`                       |
| Multiple activities                  | ✅                   | Main, Quiz, Result, Settings, Camera (5 activities)                    |
| Quiz with categories                 | ✅                   | Physics, Chemistry, Biology, Random                                    |
| Difficulty levels                    | ✅                   | All/Easy/Medium/Hard in Settings                                       |
| Score tracking                       | ✅                   | `QuizViewModel.selectAnswer()`, `ResultActivity`                       |
| Persistent statistics                | ✅                   | `QuizResultDao` with AVG and COUNT queries                             |
| Progress indicator                   | ✅                   | `LinearProgressIndicator` in `activity_quiz.xml`                       |
| Question shuffling                   | ✅                   | `ORDER BY RANDOM()` in `QuestionDao`                                   |
| Instant answer feedback              | ✅                   | Correct = green, Wrong = red highlight                                 |
| Fun facts after answer               | ✅                   | `tvFunFact` in `activity_quiz.xml`                                     |
| Text-to-Speech                       | ✅                   | `TextToSpeech` in Quiz, Result, and Main activities                    |
| TTS settings toggle                  | ✅                   | `SettingsActivity` + `SettingsViewModel`                               |
| Camera integration                   | ✅                   | `CameraActivity` with CameraX                                          |
| Camera permission handling           | ✅                   | `ActivityResultContracts.RequestPermission`                            |
| Share functionality                  | ✅                   | `Intent.ACTION_SEND` in Main and Result activities                     |
| External learning links              | ✅                   | Opens science education URLs in browser                                |
| Confirmation dialogs                 | ✅                   | Quiz quit dialog, Reset progress dialog                                |
| Material Design 3                    | ✅                   | Material components throughout                                         |
| Kotlin Coroutines                    | ✅                   | All database operations via `viewModelScope.launch`                    |
| LiveData observers                   | ✅                   | All ViewModel-to-Activity communication                                |
| Seed data on first launch            | ✅                   | `AppDatabase.populateQuestions()` with 20 questions                    |
| Responsive/scrollable layouts        | ✅                   | `NestedScrollView` on all screens                                      |
| SharedPreferences for settings       | ✅                   | TTS toggle, difficulty level, profile photo path                       |

## How to Build & Run

1. **Clone or open** the project in Android Studio
2. **Sync Gradle** — all dependencies will be downloaded automatically
3. **Run** on an emulator (API 26+) or physical device
4. The database is **auto-populated** with 20 questions on first launch

### Build Variants
- **Debug**: Standard debug build with `adb` install support
- **Release**: ProGuard-minified release build (`./gradlew assembleRelease`)

## License

This project was developed as a portfolio piece demonstrating Android development with Kotlin, modern architecture components, and Material Design principles.
