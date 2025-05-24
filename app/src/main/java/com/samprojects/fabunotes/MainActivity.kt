package com.samprojects.fabunotes

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.samprojects.fabunotes.data.NoteDatabase
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize database to ensure it's created
        val db = NoteDatabase.getDatabase(applicationContext)
        Log.d("MainActivity", "Database initialized")

        // Apply saved language
        val sharedPreferences = getSharedPreferences("NoteAppPrefs", Context.MODE_PRIVATE)
        val savedLanguage = sharedPreferences.getString("appLanguage", Locale.getDefault().language)
        savedLanguage?.let { updateLocale(this, it) }

        setContent {
            NoteApp()
        }
    }

    private fun updateLocale(context: Context, languageCode: String) {
        val locale = when (languageCode) {
            "en" -> Locale.ENGLISH
            "it" -> Locale.ITALIAN
            "de" -> Locale.GERMAN
            "tr" -> Locale("tr")
            "pt" -> Locale("pt", "PT")  // European Portuguese
            "pt-BR" -> Locale("pt", "BR")  // Brazilian Portuguese
            "es" -> Locale("es")
            "ru" -> Locale("ru")
            else -> Locale.getDefault()
        }

        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Handle the new intent here if needed
        Log.d("MainActivity", "New intent received")
    }
}


