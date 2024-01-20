package com.jaanonim.you_owe_me_counter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@Composable
fun MainContent(
    innerPadding: PaddingValues
) {
    val context = LocalContext.current
    val notificationList =
        context.notificationRecord.data.collectAsState(initial = null).value?.let { runBlocking { context.notificationRecord.data.first().notificationsList.toList() } }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = innerPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        notificationList.let {
            it?.let { it1 ->
                items(
                    it1.count()
                ) { index ->
                    ListElement(
                        it.get(index)
                    ) {
                        runBlocking {
                            context.notificationRecord.updateData { r ->
                                r.toBuilder().removeNotifications(index).build()
                            }
                        }
                    }
                }
            }
        }

    }
}


