package com.dawnlight.tidyme.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dawnlight.tidyme.data.database.entity.Event
import com.dawnlight.tidyme.data.database.entity.EventType
import com.dawnlight.tidyme.data.database.entity.OccurrenceType
import com.dawnlight.tidyme.data.database.entity.RepeatType
import com.dawnlight.tidyme.ui.theme.TidyMeTheme
import com.dawnlight.tidyme.viewmodel.EventViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun SelfScreen(viewModel: EventViewModel = viewModel()) {
    var isExpanded by remember { mutableStateOf(false) }
    var showSingleDialog by remember { mutableStateOf(false) }
    var showRoutineDialog by remember { mutableStateOf(false) }

    if (showSingleDialog) {
        SingleTaskDialog(
            onDismiss = { showSingleDialog = false },
            viewModel = viewModel
        )
    }

    if (showRoutineDialog) {
        RoutineTaskDialog(
            onDismiss = { showRoutineDialog = false },
            viewModel = viewModel
        )
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Self",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.Center)
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FabOption(text = "Routine", onClick = { showRoutineDialog = true })
                    FabOption(text = "Single", onClick = { showSingleDialog = true })
                }
            }

            val rotation by animateFloatAsState(
                targetValue = if (isExpanded) 45f else 0f,
                label = "FAB rotation"
            )

            FloatingActionButton(
                onClick = { isExpanded = !isExpanded }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    modifier = Modifier.rotate(rotation)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineTaskDialog(onDismiss: () -> Unit, viewModel: EventViewModel) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var frequencyNumber by remember { mutableStateOf("") }
    val frequencyTypes = listOf("Days", "Weeks", "Months")
    var expanded by remember { mutableStateOf(false) }
    var selectedFrequency by remember { mutableStateOf(frequencyTypes[0]) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = "New Routine Task", style = MaterialTheme.typography.titleLarge)
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") }
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    minLines = 2
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = frequencyNumber,
                        onValueChange = { frequencyNumber = it },
                        label = { Text("Repeat every") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            readOnly = true,
                            value = selectedFrequency,
                            onValueChange = { },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            frequencyTypes.forEach {
                                DropdownMenuItem(
                                    text = { Text(it) },
                                    onClick = {
                                        selectedFrequency = it
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank() && frequencyNumber.isNotBlank()) {
                                val frequency = frequencyNumber.toIntOrNull() ?: 1
                                val repeatType = when (selectedFrequency) {
                                    "Days" -> RepeatType.DAYS
                                    "Weeks" -> RepeatType.WEEKS
                                    "Months" -> RepeatType.MONTHS
                                    else -> RepeatType.DAYS
                                }
                                val event = Event(
                                    eventType = EventType.ROUTINE,
                                    title = title,
                                    description = description,
                                    occurrenceType = OccurrenceType.REPEAT,
                                    repeatType = repeatType,
                                    repeatFrequency = frequency,
                                    lastExecutionTimestamp = null
                                )
                                viewModel.insertEvent(event)
                                onDismiss()
                            }
                        },
                        enabled = title.isNotBlank() && frequencyNumber.isNotBlank()
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleTaskDialog(onDismiss: () -> Unit, viewModel: EventViewModel) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val calendar = Calendar.getInstance()
    var selectedDate by remember { mutableStateOf(calendar) }

    val dateState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.timeInMillis
    )
    val timeState = rememberTimePickerState(
        initialHour = selectedDate.get(Calendar.HOUR_OF_DAY),
        initialMinute = selectedDate.get(Calendar.MINUTE)
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        dateState.selectedDateMillis?.let {
                            calendar.timeInMillis = it
                            selectedDate = calendar
                        }
                        showDatePicker = false
                        showTimePicker = true
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = dateState)
        }
    }

    if (showTimePicker) {
        Dialog(onDismissRequest = { showTimePicker = false }) {
            Surface(
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TimePicker(state = timeState)
                    Row(
                        modifier = Modifier.padding(top = 12.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showTimePicker = false }) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(
                            onClick = {
                                calendar.set(Calendar.HOUR_OF_DAY, timeState.hour)
                                calendar.set(Calendar.MINUTE, timeState.minute)
                                selectedDate = calendar
                                showTimePicker = false
                            }
                        ) {
                            Text("OK")
                        }
                    }
                }
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = "New Single Task", style = MaterialTheme.typography.titleLarge)
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") }
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    minLines = 2
                )
                Button(onClick = { showDatePicker = true }) {
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                    Text(text = "Select Date & Time: ${dateFormat.format(selectedDate.time)}")
                }
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                val event = Event(
                                    eventType = EventType.SINGLE,
                                    title = title,
                                    description = description,
                                    occurrenceType = OccurrenceType.ONCE,
                                    repeatType = null,
                                    repeatFrequency = null,
                                    lastExecutionTimestamp = selectedDate.timeInMillis
                                )
                                viewModel.insertEvent(event)
                                onDismiss()
                            }
                        },
                        enabled = title.isNotBlank()
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}


@Composable
private fun FabOption(
    text: String,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
        SmallFloatingActionButton(onClick = onClick) {
            Text(
                text = text.first().toString(),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SelfScreenPreview() {
    TidyMeTheme {
        SelfScreen()
    }
}
