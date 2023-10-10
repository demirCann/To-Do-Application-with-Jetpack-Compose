package com.demircandemir.todoapp.data.models

import androidx.compose.ui.graphics.Color
import com.demircandemir.todoapp.ui.theme.HighPriorityColor
import com.demircandemir.todoapp.ui.theme.LowPriorityColor
import com.demircandemir.todoapp.ui.theme.MediumPriorityColor
import com.demircandemir.todoapp.ui.theme.NonePriorityColor

enum class Priority(val color: Color) {
    High(HighPriorityColor),
    Medium(MediumPriorityColor),
    Low(LowPriorityColor),
    None(NonePriorityColor)
}