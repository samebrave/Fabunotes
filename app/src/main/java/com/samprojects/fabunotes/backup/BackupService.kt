package com.samprojects.fabunotes.backup

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.samprojects.fabunotes.data.Category
import com.samprojects.fabunotes.data.CategoryDao
import com.samprojects.fabunotes.data.Note
import com.samprojects.fabunotes.data.NoteDao
import com.samprojects.fabunotes.data.NoteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStreamWriter
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class BackupService : LifecycleService() {
    companion object {
        const val ACTION_BACKUP = "com.example.noteai.ACTION_BACKUP"
        const val ACTION_RESTORE = "com.example.noteai.ACTION_RESTORE"
        const val EXTRA_URI = "com.example.noteai.EXTRA_URI"
        private const val TAG = "BackupService"
    }

    private lateinit var noteDao: NoteDao
    private lateinit var categoryDao: CategoryDao

    override fun onCreate() {
        super.onCreate()
        val database = NoteDatabase.getDatabase(applicationContext)
        noteDao = database.noteDao()
        categoryDao = database.categoryDao()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        when (intent?.action) {
            ACTION_BACKUP -> {
                val uri = intent.getParcelableExtra<Uri>(EXTRA_URI)
                if (uri != null) {
                    lifecycleScope.launch {
                        backup(uri)
                    }
                } else {
                    Log.e(TAG, "Backup URI is null")
                }
            }
            ACTION_RESTORE -> {
                val uri = intent.getParcelableExtra<Uri>(EXTRA_URI)
                if (uri != null) {
                    lifecycleScope.launch {
                        restore(uri)
                    }
                } else {
                    Log.e(TAG, "Restore URI is null")
                }
            }
        }

        return START_NOT_STICKY
    }

    private suspend fun backup(uri: Uri) = withContext(Dispatchers.IO) {
        try {
            val notes = noteDao.getAllNotes().first()
            val categories = categoryDao.getAllCategories().first()

            val backupData = BackupData(notes, categories)
            val json = Gson().toJson(backupData)

            contentResolver.openOutputStream(uri)?.use { outputStream ->
                ZipOutputStream(outputStream).use { zipOut ->
                    zipOut.putNextEntry(ZipEntry("backup.json"))
                    OutputStreamWriter(zipOut).use { writer ->
                        writer.write(json)
                    }
                }
            }

            Log.d(TAG, "Backup completed successfully. Notes: ${notes.size}, Categories: ${categories.size}")
        } catch (e: Exception) {
            Log.e(TAG, "Backup failed", e)
        }
    }

    private suspend fun restore(uri: Uri) = withContext(Dispatchers.IO) {
        try {
            var json = ""
            contentResolver.openInputStream(uri)?.use { inputStream ->
                ZipInputStream(inputStream).use { zipIn ->
                    var entry = zipIn.nextEntry
                    while (entry != null) {
                        if (entry.name == "backup.json") {
                            json = zipIn.bufferedReader().use { it.readText() }
                            break
                        }
                        entry = zipIn.nextEntry
                    }
                }
            }

            if (json.isNotEmpty()) {
                val backupData = Gson().fromJson(json, BackupData::class.java)
                noteDao.deleteAllNotes()
                categoryDao.deleteAllCategories()
                backupData.notes.forEach { noteDao.insertNote(it) }
                backupData.categories.forEach { categoryDao.insertCategory(it) }

                Log.d(TAG, "Restore completed successfully. Notes: ${backupData.notes.size}, Categories: ${backupData.categories.size}")

                // Notify the app to reload data
                val intent = Intent("com.example.noteai.DATABASE_UPDATED")
                sendBroadcast(intent)
            } else {
                Log.e(TAG, "No backup data found in the zip file")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Restore failed", e)
        }
    }

    data class BackupData(val notes: List<Note>, val categories: List<Category>)
}