package com.jaanonim.you_owe_me_counter

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.jaanonim.you_owe_me_counter.ui.theme.md_theme_light_error
import com.jaanonim.you_owe_me_counter.ui.theme.md_theme_light_secondary
import java.sql.Timestamp
import java.text.SimpleDateFormat


@SuppressLint("DefaultLocale")
@Composable
fun ListElement(
    data: Notification,
    onRemove: (Notification) -> Unit,
    onMove: (Notification) -> Unit
) {
    val openDeleteDialog = remember { mutableStateOf(false) }
    val dismissState = rememberSwipeToDismissBoxState()

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    SwipeToDismissBoxValue.EndToStart -> md_theme_light_error
                    SwipeToDismissBoxValue.StartToEnd -> md_theme_light_secondary
                    else -> Color.Transparent
                }, label = ""
            )
            val alignment = when (dismissState.targetValue) {
                SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                SwipeToDismissBoxValue.Settled -> Alignment.Center
            }
            val icon = when (dismissState.targetValue) {
                SwipeToDismissBoxValue.StartToEnd -> Icons.AutoMirrored.Filled.ArrowForward
                SwipeToDismissBoxValue.EndToStart -> Icons.Filled.Delete
                SwipeToDismissBoxValue.Settled -> Icons.Filled.Delete
            }
            val contentDescription = when (dismissState.targetValue) {
                SwipeToDismissBoxValue.StartToEnd -> "Move"
                SwipeToDismissBoxValue.EndToStart -> "Delete"
                SwipeToDismissBoxValue.Settled -> ""
            }

            Box(
                Modifier
                    .fillMaxSize()
                    .background(color)
            ) {
                Icon(
                    icon,
                    contentDescription,
                    Modifier
                        .align(alignment)
                        .padding(horizontal = 50.dp)
                )
            }
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

    when (dismissState.currentValue) {
        SwipeToDismissBoxValue.EndToStart -> {
            openDeleteDialog.value = true

            LaunchedEffect(Unit) {
                dismissState.snapTo(SwipeToDismissBoxValue.Settled)
            }
        }

        SwipeToDismissBoxValue.StartToEnd -> {
            LaunchedEffect(Unit) {
                dismissState.snapTo(SwipeToDismissBoxValue.Settled)
                onMove(data)
            }

        }

        SwipeToDismissBoxValue.Settled -> {}
    }
}

