package com.jaanonim.you_owe_me_counter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(
    innerPadding: PaddingValues,
    currentTab: Int,
    onTabChange: (Int) -> Unit
) {
    val context = LocalContext.current
    val notificationList =
        context.notificationRecord.data.collectAsState(initial = null).value?.let { runBlocking { context.notificationRecord.data.first().notificationsList.toList() } }

    val titles = listOf("You owe me", "I owe you")

    Column(
        modifier = Modifier
            .padding(innerPadding),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SecondaryTabRow(selectedTabIndex = currentTab) {
            titles.forEachIndexed { index, title ->
                Tab(
                    selected = currentTab == index,
                    onClick = { onTabChange(index) },
                    text = { Text(title) }
                )
            }
        }
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            notificationList?.filter { it.tab == currentTab }?.sortedBy { it.timestamp }?.reversed()
                .let {
                    it?.let { it1 ->
                        items(
                            it1.count()
                        ) { index ->
                            ListElement(
                                it[index],
                                onRemove = { current ->
                                    runBlocking {
                                        context.notificationRecord.updateData { r ->
                                            val builder = r.toBuilder()
                                            for (i in builder.notificationsList.indices.reversed()) {
                                                if (builder.notificationsList[i] == current) {
                                                    builder.removeNotifications(i)
                                                    break
                                                }
                                            }
                                            builder.build()
                                        }
                                    }
                                },
                                onMove = { current ->
                                    runBlocking {
                                        context.notificationRecord.updateData { r ->
                                            val builder = r.toBuilder()
                                            for (i in builder.notificationsList.indices.reversed()) {
                                                if (builder.notificationsList[i] == current) {
                                                    builder.removeNotifications(i)
                                                    builder.addNotifications(
                                                        Notification.newBuilder()
                                                            .setTitle(current.title)
                                                            .setText(current.text)
                                                            .setValue(current.value)
                                                            .setTimestamp(current.timestamp)
                                                            .setTab((currentTab + 1) % titles.size)
                                                            .build()
                                                    )
                                                    break
                                                }
                                            }
                                            builder.build()
                                        }
                                    }
                                }
                            )
                        }
                    }
                }

        }
    }
}


