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
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListElement(
    data: Notification,
    onRemove: (Notification) -> Unit,
    onMove: (Notification) -> Unit
) {
    val openDeleteDialog = remember { mutableStateOf(false) }
    val dismissState = rememberDismissState()

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    DismissValue.DismissedToEnd -> md_theme_light_secondary
                    DismissValue.DismissedToStart -> md_theme_light_error
                    else -> Color.Transparent
                }, label = ""
            )
            val alignment = when (dismissState.targetValue) {
                DismissValue.DismissedToEnd -> Alignment.CenterStart
                DismissValue.DismissedToStart -> Alignment.CenterEnd
                DismissValue.Default -> Alignment.Center
            }
            val icon = when (dismissState.targetValue) {
                DismissValue.DismissedToEnd -> Icons.AutoMirrored.Filled.ArrowForward
                DismissValue.DismissedToStart -> Icons.Filled.Delete
                DismissValue.Default -> Icons.Filled.Delete
            }
            val contentDescription = when (dismissState.targetValue) {
                DismissValue.DismissedToEnd -> "Delete"
                DismissValue.DismissedToStart -> "Move"
                DismissValue.Default -> ""
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
        DismissValue.DismissedToStart -> {
            openDeleteDialog.value = true

            LaunchedEffect(Unit) {
                dismissState.snapTo(DismissValue.Default)
            }
        }

        DismissValue.DismissedToEnd -> {
            LaunchedEffect(Unit) {
                dismissState.snapTo(DismissValue.Default)
                onMove(data)
            }

        }

        DismissValue.Default -> {}
    }
}

