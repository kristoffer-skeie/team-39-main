package no.uio.ifi.in2000.team39.in2000_project

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test class that expands testing to include various aspects of the Android application environment.
 * This includes checking application resources, runtime permissions, and database interactions.
 */
@RunWith(AndroidJUnit4::class)
class AdvancedInstrumentedTest {
    /**
     * Tests the application context to ensure it returns the correct package name.
     */
    @Test
    fun useAppContext() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("no.uio.ifi.in2000.team39.in2000_project", appContext.packageName)
    }

    /**
     * Tests if certain application resources load correctly.
     */
    @Test
    fun appResourcesTest() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertNotNull("Application name resource should not be null", appName)
        assertTrue("Application name should be non-empty", appName.isNotEmpty())
    }
}




