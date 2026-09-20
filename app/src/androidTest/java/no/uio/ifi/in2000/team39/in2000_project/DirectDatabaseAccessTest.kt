package no.uio.ifi.in2000.team39.in2000_project

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test to check direct database access and queries.
 */
@RunWith(AndroidJUnit4::class)
class DirectDatabaseAccessTest {
    /**
     * Test if the application's database is directly accessible and can be queried without errors.
     */
    @Test
    fun databaseAccessTest() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // Create or open a database directly using SQLiteDatabase
        val db = context.openOrCreateDatabase("test_database.db", Context.MODE_PRIVATE, null)

        // Attempt to create a simple table if not exists
        db.execSQL("CREATE TABLE IF NOT EXISTS test_table (id INTEGER PRIMARY KEY, name TEXT)")

        // Assert database is open
        assertTrue("Database should be open", db.isOpen)

        // Attempt to insert a sample record
        db.execSQL("INSERT INTO test_table (name) VALUES ('IN2000')")

        // Query the database
        val cursor = db.rawQuery("SELECT * FROM test_table", null)
        assertNotNull("Cursor should not be null", cursor)
        assertTrue("Cursor should have at least one entry", cursor.moveToFirst())
        assertEquals(
            "Name should match the inserted value",
            "IN2000",
            cursor.getString(cursor.getColumnIndex("name"))
        )

        // Close cursor and database
        cursor.close()
        db.close()
    }
}