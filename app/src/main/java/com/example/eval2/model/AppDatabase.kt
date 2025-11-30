package com.example.eval2.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Service::class, ServiceOrder::class, User::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun serviceDao(): ServiceDao
    abstract fun orderDao(): OrderDao
    abstract fun userDao(): UserDao

    companion object {
        // Hacemos la instancia accesible para poder reemplazarla en los tests
        @Volatile internal var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                val inst = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "eval2.db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = inst
                inst
            }
    }
}
