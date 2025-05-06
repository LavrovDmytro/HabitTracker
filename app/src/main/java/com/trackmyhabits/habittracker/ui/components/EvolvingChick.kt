package com.trackmyhabits.habittracker.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun EvolvingChick(chickStageDrawableResId: Int) {
    Image(
        painter = painterResource(id = chickStageDrawableResId),
        contentDescription = "Evolving Chick Stage",
        modifier = Modifier.size(96.dp)
    )
}