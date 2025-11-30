package com.example.eval2.model

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.jupiter.api.Test

class AppDatabaseTest {

    @Test
    fun `getDatabase construye la base de datos`() {
        // Mockeamos todo lo necesario del framework de Android y Room
        mockkStatic(Room::class)
        val mockContext = mockk<Context>(relaxed = true)
        val mockBuilder = mockk<RoomDatabase.Builder<AppDatabase>>(relaxed = true)
        val mockDb = mockk<AppDatabase>(relaxed = true)

        // Definimos el comportamiento de los mocks
        every { Room.databaseBuilder(any(), any<Class<AppDatabase>>(), any()) } returns mockBuilder
        every { mockBuilder.fallbackToDestructiveMigration() } returns mockBuilder
        every { mockBuilder.build() } returns mockDb

        // Actuación: Llamamos al método que queremos probar
        val dbInstance = AppDatabase.getDatabase(mockContext)

        // Afirmación: Verificamos que se llamó a los métodos correctos para construir la BD
        verify { Room.databaseBuilder(any(), AppDatabase::class.java, "eval2.db") }
        verify { mockBuilder.fallbackToDestructiveMigration() }
        verify { mockBuilder.build() }
    }
}