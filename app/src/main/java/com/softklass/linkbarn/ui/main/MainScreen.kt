package com.softklass.linkbarn.ui.main

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.softklass.linkbarn.R
import com.softklass.linkbarn.data.model.Link
import kotlinx.coroutines.launch

@Composable
fun MainScreen(viewModel: MainViewModel) {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(padding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(intrinsicSize = IntrinsicSize.Max),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier
                        .weight(2f),
                ) {
                    Text(
                        fontSize = 22.sp,
                        text = stringResource(id = R.string.main_screen_title),
                    )
                    Text(text = "Your saved links.")
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .weight(1f),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Row(
                        modifier = Modifier.fillMaxHeight(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        ModalBottomSheetAddUrl()
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp),
            )


            Row {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    val links by viewModel.links.collectAsState()
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        if (links.isEmpty()) {
                            item {
                                Text(
                                    text = "No links added yet",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    textAlign = TextAlign.Center,
                                    color = Color.Gray,
                                )
                            }
                        } else {
                            items(links) { link ->
                                LinkItem(link = link, viewModel = viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LinkItem(link: Link, viewModel: MainViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val dismissState = rememberSwipeToDismissBoxState(
        positionalThreshold = { distance -> distance * 0.5f },
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                viewModel.deleteLink(link)
                true
            } else {
                false
            }
        },
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp, 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        },
        content = {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                ),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 16.dp),
                    ) {
                        Text(
                            text = link.name ?: "Untitled",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = link.uri.toString(),
                            fontSize = 10.sp,
                            color = Color.Gray,
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = "Share link",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, link.uri.toString())
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, null))
                                },
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Open in browser",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    val intent = Intent(Intent.ACTION_VIEW, link.uri.toString().lowercase().toUri())
                                    context.startActivity(intent)
                                },
                        )
                    }
                }
            }
        },
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalBottomSheetAddUrl(viewModel: MainViewModel = hiltViewModel()) {
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    var url by rememberSaveable { mutableStateOf("") }
    var name by rememberSaveable { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        when (uiState) {
            is AddLinkUiState.Error -> {
                errorMessage = (uiState as AddLinkUiState.Error).message
            }

            is AddLinkUiState.Success -> {
                errorMessage = null
                url = ""
                name = ""
                scope.launch {
                    bottomSheetState.hide()
                    openBottomSheet = false
                }
                viewModel.resetState()
            }

            else -> {
                errorMessage = null
            }
        }
    }

    // App content
    Column(
        horizontalAlignment = Alignment.End,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Button(
            onClick = { openBottomSheet = !openBottomSheet },
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = "Add Link",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 2.dp)
            )
            Text(
                text = "Add Link",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }

    // Sheet content
    if (openBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                openBottomSheet = false
                errorMessage = null
                viewModel.resetState()
            },
            sheetState = bottomSheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Cancel button
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        onClick = {
                            scope.launch {
                                bottomSheetState.hide()
                                openBottomSheet = false
                                errorMessage = null
                                viewModel.resetState()
                            }
                        },
                    ) {
                        Text(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            text = "Cancel",
                        )
                    }
                }

                // Error message
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = Color.Red,
                        modifier = Modifier.padding(vertical = 8.dp),
                    )
                }

                // URL Input Field
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("URL") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(),
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Name Input Field
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(),
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Submit button
                Button(
                    onClick = { viewModel.addLink(name, url) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState !is AddLinkUiState.Loading,
                ) {
                    if (uiState is AddLinkUiState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                        )
                    } else {
                        Text(stringResource(R.string.submit))
                    }
                }
            }
        }
    }
}
