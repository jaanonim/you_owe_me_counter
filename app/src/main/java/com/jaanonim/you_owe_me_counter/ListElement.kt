package com.jaanonim.you_owe_me_counter

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import com.jaanonim.you_owe_me_counter.ui.theme.md_theme_light_error
import kotlinx.coroutines.delay
import java.sql.Timestamp
import java.text.SimpleDateFormat


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListElement(
    data: Notification,
    onRemove: (Notification) -> Unit
) {
    val openDeleteDialog = remember { mutableStateOf(false) }
    var show by remember { mutableStateOf(true) }
    val dismissState = rememberDismissState(
        confirmValueChange = {
            if (it == DismissValue.DismissedToStart || it == DismissValue.DismissedToEnd) {
                show = false
                true
            } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    DismissValue.DismissedToEnd -> md_theme_light_error
                    DismissValue.DismissedToStart -> md_theme_light_error
                    else -> Color.Transparent
                }, label = ""
            )

            Box(
                Modifier
                    .fillMaxSize()
                    .background(color)
            )
        }
    ) {
        Column {
            ListItem(
                headlineContent = { Text(data.title) },
                supportingContent = { Text(data.text) },
                overlineContent = {
                    Text(
                        text = SimpleDateFormat.getDateTimeInstance()
                            .format(Timestamp(data.timestamp))
                    )
                },
                trailingContent = {
                    Text(
                        "${String.format("%.2f", data.value.toFloat())}PLN",
                        lineHeight = TextUnit(2f, TextUnitType.Em),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        fontSize = TextUnit(4.5f, TextUnitType.Em)
                    )
                }
            )
        }
        when {
            openDeleteDialog.value -> {
                DeleteDialog(
                    onDismissRequest = {
                        openDeleteDialog.value = false
                    },
                    onConfirmation = {
                        openDeleteDialog.value = false
                        onRemove(data)
                    }
                )
            }
        }
    }

    if (dismissState.currentValue != DismissValue.Default) {
        LaunchedEffect(Unit) {
            delay(800)
            dismissState.reset()
        }
    }

    LaunchedEffect(show) {
        if (!show) {
            delay(800)
            show = true
            openDeleteDialog.value = true
        }
    }
}

