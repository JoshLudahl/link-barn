package com.softklass.linkbarn.ui.partials

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Archive
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun <T> SwipeToDismissContainer(
    item: T,
    onSwipeLeftToRight: (T) -> Unit,
    onSwipeRightToLeft: (T) -> Unit,
    animationDuration: Long = 500L,
    leftIcon: ImageVector = Icons.Rounded.Edit,
    rightIcon: ImageVector = Icons.Rounded.Delete,
    content: @Composable (T) -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        initialValue = SwipeToDismissBoxValue.Settled,
        positionalThreshold = { distance -> distance * .25f },
    )

    var isSwipeLeftToRight by remember { mutableStateOf(false) }
    var isSwipeRightToLeft by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = isSwipeRightToLeft) {
        if (isSwipeRightToLeft) {
            delay(animationDuration)
            onSwipeRightToLeft(item)
        }
    }

    LaunchedEffect(key1 = isSwipeLeftToRight) {
        if (isSwipeLeftToRight) {
            onSwipeLeftToRight(item)
            dismissState.reset()
            isSwipeLeftToRight = false
        }
    }

    AnimatedVisibility(
        visible = !isSwipeRightToLeft,
        exit = shrinkVertically(
            animationSpec = tween(durationMillis = animationDuration.toInt()),
            shrinkTowards = Alignment.Top,
        ) + fadeOut(),
    ) {
        SwipeToDismissBox(
            state = dismissState,
            enableDismissFromStartToEnd = true,
            enableDismissFromEndToStart = true,
            onDismiss = {
                when (it) {
                    SwipeToDismissBoxValue.StartToEnd -> {
                        isSwipeLeftToRight = true
                    }

                    SwipeToDismissBoxValue.EndToStart -> {
                        isSwipeRightToLeft = true
                    }

                    else -> {}
                }
            },
            backgroundContent = {
                DismissBackground(
                    leftIcon = leftIcon,
                    rightIcon = rightIcon,
                )
            },
        ) {
            content(item)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DismissBackground(
    leftIcon: ImageVector = Icons.Rounded.Archive,
    rightIcon: ImageVector = Icons.Rounded.Delete,
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent, shape = RoundedCornerShape(16.dp))
            .padding(16.dp, 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,

    ) {
        Icon(
            imageVector = leftIcon,
            contentDescription = "Archive",
            tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier)
        Icon(
            // make sure add archive.xml resource to drawable folder
            imageVector = rightIcon,
            contentDescription = "Delete",
            tint = MaterialTheme.colorScheme.error,
        )
    }
}
