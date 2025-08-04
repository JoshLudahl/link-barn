package com.softklass.linkbarn.ui.partials

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

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
