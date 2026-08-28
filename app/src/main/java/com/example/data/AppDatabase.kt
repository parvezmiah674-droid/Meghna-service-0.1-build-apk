package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Technician::class, ChatMessage::class, PaymentRecord::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun serviceDao(): ServiceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "meghna_service_database"
                )
                .addCallback(DatabaseCallback(scope))
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.serviceDao())
                    }
                }
            }

            suspend fun populateInitialData(dao: ServiceDao) {
                val initialTechs = listOf(
                    Technician(
                        name = "পারভেজ ইলেকট্রিক এন্ড স্যানিটারি সার্ভিস",
                        category = "ইলেকট্রিক ও স্যানিটারি",
                        phone = "01700000000",
                        address = "মেঘনা, কুমিল্লা",
                        rating = 4.9,
                        completedJobs = 34
                    ),
                    Technician(
                        name = "রফিকুল ইসলাম",
                        category = "সিসি ক্যামেরা সেটআপ",
                        phone = "01800000000",
                        address = "মেঘনা, কুমিল্লা",
                        rating = 4.8,
                        completedJobs = 21
                    ),
                    Technician(
                        name = "আরিফ হোসেন",
                        category = "সোলার প্যানেল ও আইপিএস",
                        phone = "01900000000",
                        address = "মেঘনা, কুমিল্লা",
                        rating = 4.7,
                        completedJobs = 19
                    ),
                    Technician(
                        name = "মো: শাহিন আলম",
                        category = "এসি ও ফ্রিজ মেরামত",
                        phone = "01711223344",
                        address = "মানিকারচর বাজার, মেঘনা, কুমিল্লা",
                        rating = 4.9,
                        completedJobs = 28
                    ),
                    Technician(
                        name = "কামাল হোসেন",
                        category = "হোম পেইন্টিং ও ডেকোরেশন",
                        phone = "01822334455",
                        address = "মেঘনা ঘাট, কুমিল্লা",
                        rating = 4.6,
                        completedJobs = 15
                    )
                )
                dao.insertAllTechnicians(initialTechs)
            }
        }
    }
}
