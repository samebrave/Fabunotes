package com.samprojects.fabunotes.model

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.samprojects.fabunotes.backup.BackupService
import com.samprojects.fabunotes.colorschemes.AppTheme
import com.samprojects.fabunotes.data.Category
import com.samprojects.fabunotes.data.Note
import com.samprojects.fabunotes.data.NoteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.Locale

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val database = NoteDatabase.getDatabase(application)
    private val noteDao = database.noteDao()
    private val categoryDao = database.categoryDao()

    val allNotes: Flow<List<Note>> = noteDao.getAllNotes()
    val favoriteNotes: Flow<List<Note>> = noteDao.getFavoriteNotes()
    val allCategories: Flow<List<Category>> = categoryDao.getAllCategories()

    var currentNoteTitle by mutableStateOf("")
    var currentNoteContent by mutableStateOf("")
    var currentCategoryName by mutableStateOf("")

    private val sharedPreferences = application.getSharedPreferences("NoteAppPrefs", Context.MODE_PRIVATE)

    var isNoteGridView by mutableStateOf(sharedPreferences.getBoolean("isNoteGridView", false))
        private set

    var isCategoryGridView by mutableStateOf(sharedPreferences.getBoolean("isCategoryGridView", true))
        private set

    var currentTheme by mutableStateOf(AppTheme.System)
        private set

    var currentLanguage by mutableStateOf(Locale.getDefault().language)
        private set

    private val _searchResults = MutableStateFlow<List<Note>>(emptyList())
    val searchResults: StateFlow<List<Note>> = _searchResults

    private val client = OkHttpClient()

    private val groqApiKey = "YOUR_API_KEY"

    private val noteCache = mutableMapOf<Int, Note>()
    private val aiNoteCache = mutableMapOf<String, String>()

    init {
        currentTheme = try {
            AppTheme.valueOf(sharedPreferences.getString("appTheme", AppTheme.System.name) ?: AppTheme.System.name)
        } catch (e: IllegalArgumentException) {
            AppTheme.System
        }
        currentLanguage = sharedPreferences.getString("appLanguage", Locale.getDefault().language) ?: Locale.getDefault().language
        updateLocale(getApplication(), currentLanguage)
    }

    fun toggleNoteViewMode() {
        isNoteGridView = !isNoteGridView
        sharedPreferences.edit().putBoolean("isNoteGridView", isNoteGridView).apply()
    }

    fun toggleCategoryViewMode() {
        isCategoryGridView = !isCategoryGridView
        sharedPreferences.edit().putBoolean("isCategoryGridView", isCategoryGridView).apply()
    }

    fun setTheme(theme: AppTheme) {
        currentTheme = theme
        sharedPreferences.edit().putString("appTheme", theme.name).apply()
    }

    fun setLanguage(languageCode: String) {
        currentLanguage = languageCode
        sharedPreferences.edit().putString("appLanguage", languageCode).apply()
        updateLocale(getApplication(), languageCode)
    }

    private fun updateLocale(context: Context, languageCode: String) {
        val locale = when (languageCode) {
            "en" -> Locale.ENGLISH
            "it" -> Locale.ITALIAN
            "de" -> Locale.GERMAN
            "tr" -> Locale("tr")
            "pt" -> Locale("pt")
            "br" -> Locale("br")
            "es" -> Locale("es")
            "ru" -> Locale("ru")
            else -> Locale.getDefault()
        }
        Locale.setDefault(locale)
        val resources = context.resources
        val configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    fun addNote(categoryId: Int, isPinned: Boolean) {
        val title = if (currentNoteTitle.isBlank()) generateTitleFromContent(currentNoteContent) else currentNoteTitle
        if (title.isNotBlank() || currentNoteContent.isNotBlank()) {
            val newNote = Note(
                title = title,
                content = currentNoteContent,
                categoryId = categoryId,
                isPinned = isPinned
            )
            viewModelScope.launch {
                noteDao.insertNote(newNote)
                currentNoteTitle = ""
                currentNoteContent = ""
            }
        }
    }

    private fun generateTitleFromContent(content: String): String {
        return content.trim().split("\\s+".toRegex()).take(3).joinToString(" ").let {
            if (it.length > 30) it.substring(0, 30) + "..." else it
        }
    }

    fun addCategory() {
        if (currentCategoryName.isNotBlank()) {
            val newCategory = Category(
                name = currentCategoryName,
                color = Color.Gray.toArgb()
            )
            viewModelScope.launch {
                categoryDao.insertCategory(newCategory)
                currentCategoryName = ""
            }
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch {
            categoryDao.insertCategory(category)
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            noteDao.updateNote(note)
            noteCache[note.id] = note
        }
    }

    fun updateNoteTitle(title: String) {
        currentNoteTitle = title
    }

    fun updateNoteContent(content: String) {
        currentNoteContent = content
    }

    fun updateCategoryName(name: String) {
        currentCategoryName = name
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            noteDao.deleteNote(note)
            noteCache.remove(note.id)
        }
    }

    fun togglePinned(note: Note) {
        viewModelScope.launch {
            val updatedNote = note.copy(isPinned = !note.isPinned)
            noteDao.updateNote(updatedNote)
            noteCache[note.id] = updatedNote
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            categoryDao.deleteCategory(category)
        }
    }

    suspend fun getNoteById(id: Int): Note? {
        return noteCache[id] ?: noteDao.getNoteById(id)?.also { noteCache[id] = it }
    }

    fun toggleFavorite(note: Note) {
        viewModelScope.launch {
            val updatedNote = note.copy(isFavorite = !note.isFavorite)
            noteDao.updateNote(updatedNote)
            noteCache[note.id] = updatedNote
        }
    }

    fun getNotesByCategory(categoryId: Int): Flow<List<Note>> {
        return noteDao.getNotesByCategory(categoryId)
    }

    fun searchNotes(query: String) {
        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            _searchResults.value = noteDao.searchNotes("%$query%")
            val endTime = System.currentTimeMillis()
            Log.d("NoteViewModel", "Search took ${endTime - startTime}ms")
        }
    }

    fun backup(uri: Uri) {
        val intent = Intent(getApplication(), BackupService::class.java).apply {
            action = BackupService.ACTION_BACKUP
            putExtra(BackupService.EXTRA_URI, uri)
        }
        getApplication<Application>().startService(intent)
    }

    fun restore(uri: Uri) {
        val intent = Intent(getApplication(), BackupService::class.java).apply {
            action = BackupService.ACTION_RESTORE
            putExtra(BackupService.EXTRA_URI, uri)
        }
        getApplication<Application>().startService(intent)
    }

    suspend fun generateAINote(prompt: String): String {
        return withContext(Dispatchers.IO) {
            aiNoteCache[prompt] ?: run {
                val requestBody = JSONObject().apply {
                    put("model", "deepseek-r1-distill-llama-70b")
                    put("messages", JSONArray().put(JSONObject().apply {
                        put("role", "user")
                        put("content", prompt)
                    }))
                    put("temperature", 0.5)
                    put("max_tokens", 1024)
                }.toString()

                val request = Request.Builder()
                    .url("https://api.groq.com/openai/v1/chat/completions")
                    .addHeader("Authorization", "Bearer $groqApiKey")
                    .addHeader("Content-Type", "application/json")
                    .post(requestBody.toRequestBody("application/json".toMediaType()))
                    .build()

                try {
                    client.newCall(request).execute().use { response ->
                        if (!response.isSuccessful) {
                            val errorBody = response.body?.string() ?: "No error body"
                            throw IOException("Unexpected code $response\nError body: $errorBody")
                        }

                        val responseBody = response.body?.string() ?: throw IOException("Empty response body")
                        val jsonResponse = JSONObject(responseBody)
                        val generatedNote = jsonResponse.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content")
                        aiNoteCache[prompt] = generatedNote
                        generatedNote
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    "Failed to generate AI note: ${e.message}"
                }
            }
        }
    }
}
