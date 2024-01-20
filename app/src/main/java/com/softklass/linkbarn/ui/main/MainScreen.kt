package com.softklass.linkbarn.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ElevatedSuggestionChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.softklass.linkbarn.R
import com.softklass.linkbarn.ui.theme.Dark
import com.softklass.linkbarn.ui.theme.DarkOrange
import com.softklass.linkbarn.ui.theme.Light
import com.softklass.linkbarn.ui.theme.LightBrown
import com.softklass.linkbarn.ui.theme.LightOrange
import kotlinx.coroutines.launch

@Composable
fun MainScreen(navController: NavController) {

    Column(modifier = Modifier.fillMaxWidth()) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(intrinsicSize = IntrinsicSize.Max),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(2f)
                    .background(color = DarkOrange)
            ) {
                Text(
                    fontSize = 22.sp,
                    text = stringResource(id = R.string.main_screen_title)
                )
                Text(text = "Header Sub title")
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .weight(1f)
                    .background(color = Dark),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxHeight(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ModalBottomSheetSample()
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
        }
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(16.dp))
        Row {
            Column(
                modifier = Modifier
                    .background(color = LightOrange)
                    .fillMaxWidth()
            ) {
                Text(text = "Category Section")
            }
        }

        Row {
            Column(
                modifier = Modifier
                    .background(color = Light)
                    .fillMaxWidth()
            ) {
                Text(text = "List Section")
            }
        }
    }

}

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalBottomSheetSample() {
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )

    // App content
    Column(
        horizontalAlignment = Alignment.End,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        ElevatedSuggestionChip(
            icon = {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Add Link"
                )
            },
            onClick = { openBottomSheet = !openBottomSheet },
            label = { Text("Add Link") },
            colors = SuggestionChipDefaults.elevatedSuggestionChipColors(containerColor = LightBrown)
        )
    }

    // Sheet content
    if (openBottomSheet) {
        val windowInsets = WindowInsets(0)

        ModalBottomSheet(
            onDismissRequest = { openBottomSheet = false },
            sheetState = bottomSheetState,
            windowInsets = windowInsets
        ) {

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {

                TextButton(
                    // Note: If you provide logic outside of onDismissRequest to remove the sheet,
                    // you must additionally handle intended state cleanup, if any.
                    onClick = {
                        scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                            if (!bottomSheetState.isVisible) {
                                openBottomSheet = false
                            }
                        }
                    }
                ) {
                    Text(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkOrange,
                        text = "Cancel"
                    )
                }
            }

            var text by remember { mutableStateOf("") }
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.padding(horizontal = 16.dp),
                label = { Text("Text field") }
            )

            LazyColumn {
                items(25) {
                    ListItem(
                        headlineContent = { Text("Item $it") },
                        leadingContent = {
                            Icon(
                                Icons.Default.Favorite,
                                contentDescription = "Localized description"
                            )
                        }
                    )
                }
            }
        }
    }
}
