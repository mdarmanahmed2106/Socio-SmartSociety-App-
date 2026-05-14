package com.smartsociety.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(10.dp), // Chips
    medium = RoundedCornerShape(14.dp), // Buttons / Inputs
    large = RoundedCornerShape(20.dp), // Cards
    extraLarge = RoundedCornerShape(24.dp)
)
