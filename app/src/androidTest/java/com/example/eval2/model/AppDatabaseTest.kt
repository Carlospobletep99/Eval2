package com.example.eval2.model

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {

    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // Usamos una base de datos en memoria para los tests
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun `database_isCreatedAndDaosAreAccessible`() {
        // Verificamos que la base de datos se ha creado y que podemos acceder a los DAOs
        assertNotNull(db)
        assertNotNull(db.orderDao())
        assertNotNull(db.serviceDao())
        assertNotNull(db.userDao())
    }
}