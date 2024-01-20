package com.jaanonim.you_owe_me_counter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog


@Composable
fun AddDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: (name: String, value: Number) -> Unit,
) {
    var name by remember { mutableStateOf("") };
    var value by remember { mutableStateOf("") };

    Dialog(
        onDismissRequest = {
            onDismissRequest()
        }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(1f)
                .height(375.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = "Add new record", style = MaterialTheme.typography.titleMedium)
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .padding(16.dp),
                )
                TextField(
                    value = value,
                    onValueChange = { value = it },
                    label = { Text("Amount") },
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .padding(16.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    trailingIcon = {
                        Text(text = "PLN")
                    }
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    TextButton(
                        onClick = {
                            onDismissRequest()
                        }
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            onConfirmation(
                                name,
                                value.replace(',', '.').toDouble()
                            )
                        }
                    ) {
                        Text("Add")
                    }
                }

            }
        }
    }
}