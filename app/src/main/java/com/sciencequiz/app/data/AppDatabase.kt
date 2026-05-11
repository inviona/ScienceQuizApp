package com.sciencequiz.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sciencequiz.app.data.dao.QuestionDao
import com.sciencequiz.app.data.dao.QuizResultDao
import com.sciencequiz.app.data.entity.Question
import com.sciencequiz.app.data.entity.QuizResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Question::class, QuizResult::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun questionDao(): QuestionDao
    abstract fun quizResultDao(): QuizResultDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sciencequest_database"
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateQuestions(database.questionDao())
                    }
                }
            }
        }

        suspend fun populateQuestions(questionDao: QuestionDao) {
            if (questionDao.getCount() > 0) return

            val questions = listOf(
                Question(1, "What is the SI unit of force?",
                    "Newton", "Joule", "Watt", "Pascal", 1, "Physics", "Sir Isaac Newton defined the laws of motion and gravity!", 1),
                Question(2, "What is the speed of light in vacuum?",
                    "3 × 10⁶ m/s", "3 × 10⁸ m/s", "3 × 10¹⁰ m/s", "3 × 10⁴ m/s", 2, "Physics", "Light travels at about 300,000 kilometers per second!", 2),
                Question(3, "What type of energy does a moving object have?",
                    "Potential energy", "Chemical energy", "Kinetic energy", "Thermal energy", 3, "Physics", "Kinetic energy depends on mass and velocity: KE = ½mv²", 1),
                Question(4, "Which law states that every action has an equal and opposite reaction?",
                    "Newton's First Law", "Newton's Second Law", "Newton's Third Law", "Law of Gravitation", 3, "Physics", "This is why rockets work — exhaust gases push down, rocket goes up!", 2),
                Question(5, "What is the chemical symbol for water?",
                    "H₂O", "CO₂", "NaCl", "O₂", 1, "Chemistry", "Water covers about 71% of Earth's surface!", 1),
                Question(6, "What is the pH of a neutral substance?",
                    "0", "7", "14", "3", 2, "Chemistry", "Pure water has a pH of exactly 7 at 25°C.", 1),
                Question(7, "Which element has atomic number 6?",
                    "Oxygen", "Nitrogen", "Carbon", "Hydrogen", 3, "Chemistry", "Carbon is the basis of all known life on Earth!", 2),
                Question(8, "What type of chemical bond involves sharing electrons?",
                    "Ionic bond", "Covalent bond", "Metallic bond", "Hydrogen bond", 2, "Chemistry", "Covalent bonds are very strong and form molecules like H₂O.", 3),
                Question(9, "What is the powerhouse of the cell?",
                    "Nucleus", "Ribosome", "Mitochondria", "Golgi body", 3, "Biology", "Mitochondria convert food into ATP — the energy currency of cells!", 1),
                Question(10, "How many bones are in the adult human body?",
                    "106", "206", "306", "150", 2, "Biology", "Babies have about 270 bones that fuse as they grow!", 2),
                Question(11, "Which system is responsible for pumping blood?",
                    "Nervous system", "Respiratory system", "Circulatory system", "Digestive system", 3, "Biology", "The heart pumps about 5 liters of blood per minute!", 1),
                Question(12, "What is the largest organ in the human body?",
                    "Liver", "Brain", "Skin", "Heart", 3, "Biology", "Your skin weighs about 3-4 kilograms and covers 1.5-2 square meters!", 2),
                Question(13, "Which planet is known as the Red Planet?",
                    "Venus", "Mars", "Jupiter", "Saturn", 2, "Physics", "Mars appears red due to iron oxide (rust) on its surface!", 1),
                Question(14, "What force keeps us grounded on Earth?",
                    "Magnetic force", "Gravity", "Friction", "Air pressure", 2, "Physics", "Gravity is the weakest of the four fundamental forces, but it has infinite range!", 1),
                Question(15, "What is the chemical symbol for gold?",
                    "Go", "Gd", "Au", "Ag", 3, "Chemistry", "Au comes from the Latin word 'aurum' meaning shining dawn!", 2),
                Question(16, "What part of a plant conducts photosynthesis?",
                    "Roots", "Stem", "Leaves", "Flowers", 3, "Biology", "Chlorophyll in leaves absorbs sunlight and converts it to energy!", 1),
                Question(17, "What is the atomic number of Oxygen?",
                    "6", "8", "10", "16", 2, "Chemistry", "Oxygen makes up about 21% of Earth's atmosphere.", 2),
                Question(18, "What galaxy is Earth located in?",
                    "Andromeda", "Milky Way", "Sombrero", "Triangulum", 2, "Physics", "The Milky Way contains 100-400 billion stars!", 1),
                Question(19, "What is the smallest unit of matter?",
                    "Molecule", "Cell", "Atom", "Proton", 3, "Chemistry", "Atoms are made of protons, neutrons, and electrons!", 2),
                Question(20, "How many chromosomes do humans have?",
                    "23", "46", "24", "48", 2, "Biology", "You get 23 from each parent for a total of 46!", 3)
            )
            questionDao.insertAll(questions)
        }
    }
}
