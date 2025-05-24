package com.samprojects.fabunotes.notes

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.samprojects.fabunotes.R
import com.samprojects.fabunotes.model.NoteViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteScreen(viewModel: NoteViewModel, navController: NavController, categoryId: Int) {
    val context = LocalContext.current
    val isSpeechRecognitionAvailable = remember {
        SpeechRecognizer.isRecognitionAvailable(context)
    }
    var showAIInputDialog by remember { mutableStateOf(false) }
    var showSummarizeDialog by remember { mutableStateOf(false) }
    var showSimplifyDialog by remember { mutableStateOf(false) }
    var showTranslateDialog by remember { mutableStateOf(false) }
    var showRecipeDialog by remember { mutableStateOf(false) }
    var showRouteDialog by remember { mutableStateOf(false) }
    var showRecommendationDialog by remember { mutableStateOf(false) }
    var isGeneratingAINote by remember { mutableStateOf(false) }
    var isPinned by remember { mutableStateOf(false) }

    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText: String? =
                result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0)
            spokenText?.let {
                viewModel.updateNoteContent(viewModel.currentNoteContent + (if (viewModel.currentNoteContent.isEmpty()) it else "\n\n$it"))
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.add_note)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.addNote(categoryId, isPinned)
                            navController.popBackStack()
                        }
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Save Note")
                    }
                }
            )
        },
        bottomBar = {
            Row(
                modifier = getBottomBarContentModifier()
                    .fillMaxWidth()
                    .padding(vertical = 8.dp), // Add equal vertical padding
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isSpeechRecognitionAvailable) {
                    IconButton(onClick = {
                        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to add text to your note")
                        }
                        speechRecognizerLauncher.launch(intent)
                    }) {
                        Icon(
                            Icons.Default.Mic,
                            contentDescription = "Voice Input",
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
                AIToolsDropdownMenu(
                    onAIInput = { showAIInputDialog = true },
                    onSummarize = { showSummarizeDialog = true },
                    onSimplify = { showSimplifyDialog = true },
                    onTranslate = { showTranslateDialog = true },
                    onRecipe = { showRecipeDialog = true },
                    onRoute = { showRouteDialog = true },
                    onRecommendation = { showRecommendationDialog = true }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            TextField(
                value = viewModel.currentNoteTitle,
                onValueChange = { viewModel.updateNoteTitle(it) },
                label = { Text(stringResource(id = R.string.title)) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = viewModel.currentNoteContent,
                onValueChange = { viewModel.updateNoteContent(it) },
                label = { Text(stringResource(id = R.string.content)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                textStyle = TextStyle(fontSize = 16.sp),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }
    }



    // AI Input Dialog
    if (showAIInputDialog) {
        AIInputDialog(
            onDismiss = { showAIInputDialog = false },
            onGenerateNote = { prompt ->
                isGeneratingAINote = true
                viewModel.viewModelScope.launch {
                    try {
                        val generatedText = viewModel.generateAINote(prompt)
                        viewModel.updateNoteContent(
                            viewModel.currentNoteContent + (if (viewModel.currentNoteContent.isEmpty()) generatedText else "\n\n$generatedText")
                        )
                    } catch (e: Exception) {
                        Toast.makeText(context, "Failed to generate AI note: ${e.message}", Toast.LENGTH_SHORT).show()
                    } finally {
                        isGeneratingAINote = false
                        showAIInputDialog = false
                    }
                }
            }
        )
    }

    // Summarize Dialog
    if (showSummarizeDialog) {
        SummarizeDialog(
            onDismiss = { showSummarizeDialog = false },
            onSummarize = { text ->
                isGeneratingAINote = true
                viewModel.viewModelScope.launch {
                    try {
                        val summary = viewModel.generateAINote("Can you summarize this text in its original language?\n\n$text")
                        viewModel.updateNoteContent(
                            viewModel.currentNoteContent + (if (viewModel.currentNoteContent.isEmpty()) summary else "\n\n$summary")
                        )
                    } catch (e: Exception) {
                        Toast.makeText(context, "Failed to summarize text: ${e.message}", Toast.LENGTH_SHORT).show()
                    } finally {
                        isGeneratingAINote = false
                        showSummarizeDialog = false
                    }
                }
            }
        )
    }

    // Simplify Dialog
    if (showSimplifyDialog) {
        SimplifyDialog(
            onDismiss = { showSimplifyDialog = false },
            onSimplify = { text ->
                isGeneratingAINote = true
                viewModel.viewModelScope.launch {
                    try {
                        val simplifiedText = viewModel.generateAINote("Can you simplify this text in its original language, making it easier to understand for non-experts?\n\n$text")
                        viewModel.updateNoteContent(
                            viewModel.currentNoteContent + (if (viewModel.currentNoteContent.isEmpty()) simplifiedText else "\n\n$simplifiedText")
                        )
                    } catch (e: Exception) {
                        Toast.makeText(context, "Failed to simplify text: ${e.message}", Toast.LENGTH_SHORT).show()
                    } finally {
                        isGeneratingAINote = false
                        showSimplifyDialog = false
                    }
                }
            }
        )
    }

    // Translate Dialog
    if (showTranslateDialog) {
        TranslateDialog(
            onDismiss = { showTranslateDialog = false },
            onTranslate = { text, targetLanguage ->
                isGeneratingAINote = true
                viewModel.viewModelScope.launch {
                    try {
                        val translatedText = viewModel.generateAINote("Could you please translate this to ${targetLanguage.displayName}?\n\n$text")
                        viewModel.updateNoteContent(
                            viewModel.currentNoteContent + (if (viewModel.currentNoteContent.isEmpty()) translatedText else "\n\n$translatedText")
                        )
                    } catch (e: Exception) {
                        Toast.makeText(context, "Failed to translate text: ${e.message}", Toast.LENGTH_SHORT).show()
                    } finally {
                        isGeneratingAINote = false
                        showTranslateDialog = false
                    }
                }
            }
        )
    }

    // Recipe Dialog
    if (showRecipeDialog) {
        RecipeDialog(
            onDismiss = { showRecipeDialog = false },
            onGenerateRecipe = { ingredients ->
                isGeneratingAINote = true
                viewModel.viewModelScope.launch {
                    try {
                        val recipe = viewModel.generateAINote("Can you provide the best recipe using these ingredients? Please respond in the same language as the ingredients list:\n\n$ingredients")
                        viewModel.updateNoteContent(
                            viewModel.currentNoteContent + (if (viewModel.currentNoteContent.isEmpty()) recipe else "\n\n$recipe")
                        )
                    } catch (e: Exception) {
                        Toast.makeText(context, "Failed to generate recipe: ${e.message}", Toast.LENGTH_SHORT).show()
                    } finally {
                        isGeneratingAINote = false
                        showRecipeDialog = false
                    }
                }
            }
        )
    }

    // Route Dialog
    if (showRouteDialog) {
        RouteDialog(
            onDismiss = { showRouteDialog = false },
            onGenerateRoute = { destination, days ->
                isGeneratingAINote = true
                viewModel.viewModelScope.launch {
                    try {
                        val route = viewModel.generateAINote("Can you get me the best route for $days days in $destination? Please respond in the same language as the destination name.")
                        viewModel.updateNoteContent(
                            viewModel.currentNoteContent + (if (viewModel.currentNoteContent.isEmpty()) route else "\n\n$route")
                        )
                    } catch (e: Exception) {
                        Toast.makeText(context, "Failed to generate route: ${e.message}", Toast.LENGTH_SHORT).show()
                    } finally {
                        isGeneratingAINote = false
                        showRouteDialog = false
                    }
                }
            }
        )
    }

    // Recommendation Dialog
    if (showRecommendationDialog) {
        RecommendationDialog(
            onDismiss = { showRecommendationDialog = false },
            onGenerateRecommendation = { type, genre, details ->
                isGeneratingAINote = true
                viewModel.viewModelScope.launch {
                    try {
                        val prompt = "Can you recommend a $type in the $genre genre? ${if (details.isNotEmpty()) "Additional details: $details" else ""} Please respond in the same language as the request."
                        val recommendation = viewModel.generateAINote(prompt)
                        viewModel.updateNoteContent(
                            viewModel.currentNoteContent + (if (viewModel.currentNoteContent.isEmpty()) recommendation else "\n\n$recommendation")
                        )
                    } catch (e: Exception) {
                        Toast.makeText(context, "Failed to generate recommendation: ${e.message}", Toast.LENGTH_SHORT).show()
                    } finally {
                        isGeneratingAINote = false
                        showRecommendationDialog = false
                    }
                }
            }
        )
    }

    if (isGeneratingAINote) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}