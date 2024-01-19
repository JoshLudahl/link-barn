package com.softklass.linkbarn.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.softklass.linkbarn.navigation.Screen

@Composable
fun MainScreen(navController: NavController) {
    Greeting("Goats")
    Button(text = "Click Me", navController = navController)
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hey $name!",
        modifier = modifier
    )
}

@Composable
fun Button(
    text: String,
    navController: NavController
) {
    Box(Modifier.fillMaxSize()) {
        TextButton(onClick = { navController.navigate(Screen.Settings.route) }) {
            Text(text = text)
        }
    }
}
