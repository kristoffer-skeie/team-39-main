package no.uio.ifi.in2000.team39.in2000_project

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import no.uio.ifi.in2000.team39.in2000_project.data.favorite.AppDatabase
import no.uio.ifi.in2000.team39.in2000_project.ui.navigation.SetupNavHost
import no.uio.ifi.in2000.team39.in2000_project.ui.shared.theme.In2000_projectTheme

/**
 * MainActivity is the main entry point of the application.
 * It extends ComponentActivity and sets up the content view using Jetpack Compose.
 * It applies a custom theme and sets up the navigation host.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            In2000_projectTheme {
                AppSurface()
            }
        }
    }

    /**
     * AppSurface is a composable function that sets up the main surface of the app.
     * It initializes the navigation controller and sets up the navigation host.
     */
    @Composable
    private fun AppSurface() {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            val navController = rememberNavController()
            SetupNavHost(navController = navController)
        }
    }
}

/**
 * DatabaseProvider is an object that provides a singleton instance of the AppDatabase using Room.
 * It ensures that only one instance of the database is created throughout the application's lifecycle.
 */
object DatabaseProvider {
    private var instance: AppDatabase? = null

    /**
     * Returns the singleton instance of the AppDatabase.
     * If the instance does not exist, it creates the database instance.
     * Uses synchronized block to ensure thread safety.
     */
    fun getDatabase(context: Context): AppDatabase {
        if (instance == null) {
            synchronized(AppDatabase::class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java, "database-name"
                    ).build()
                }
            }
        }
        return instance!!
    }
}
