package com.jaanonim.you_owe_me_counter

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.jaanonim.you_owe_me_counter.ui.theme.AppTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val openAddDialog = remember { mutableStateOf(false) }
    val openClearDialog = remember { mutableStateOf(false) }
    val total =
        context.notificationRecord.data.collectAsState(initial = null).value?.let { runBlocking { context.notificationRecord.data.first().notificationsList.toList() } }
            .let {
                it?.let { it1 ->
                    it1.map { it.value }.sum()
                }
            }



    AppTheme(content = {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),

            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = {
                        Text(
                            "You owe me counter",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    actions = {
                        IconButton(onClick = {
                            val intent =
                                Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
                            context.startActivity(intent)
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = "Settings",
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior,
                )
            },

            bottomBar = {
                BottomAppBar(
                    actions = {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row() {
                                IconButton(onClick = { openAddDialog.value = true }) {
                                    Icon(
                                        Icons.Filled.Add,
                                        contentDescription = "Add",
                                    )
                                    when {
                                        openAddDialog.value -> {
                                            AddDialog(
                                                onDismissRequest = {
                                                    openAddDialog.value = false
                                                },
                                                onConfirmation = { name, value ->
                                                    openAddDialog.value = false
                                                    runBlocking {
                                                        context.notificationRecord.updateData { r ->
                                                            r.toBuilder().addNotifications(
                                                                Notification.newBuilder()
                                                                    .setTitle(name)
                                                                    .setText("Custom")
                                                                    .setValue(value.toDouble())
                                                                    .setTimestamp(System.currentTimeMillis())
                                                                    .build()
                                                            ).build()
                                                        }
                                                        Log.d("App", "Added notification")
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                                IconButton(onClick = {
                                    openClearDialog.value = true
                                }) {
                                    Icon(
                                        Icons.Filled.Delete,
                                        contentDescription = "Delete all",
                                    )
                                    when {
                                        openClearDialog.value -> {
                                            ClearDialog(
                                                onDismissRequest = {
                                                    openClearDialog.value = false
                                                },
                                                onConfirmation = {
                                                    openClearDialog.value = false
                                                    runBlocking {
                                                        context.notificationRecord.updateData { r ->
                                                            r.toBuilder().clear().build()
                                                        }
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                            Row() {
                                Text(
                                    text = "Total: ", textAlign = TextAlign.Center,
                                    fontSize = TextUnit(4.5f, TextUnitType.Em)
                                )
                                Text(
                                    text = "${String.format("%.2f", total)}PLN",
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = TextUnit(4.5f, TextUnitType.Em)
                                )
                                Spacer(modifier = Modifier.size(10.dp))
                            }
                        }

                    },
                )
            },
        ) { innerPadding ->
            MainContent(innerPadding)
        }
    })
}