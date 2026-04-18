package com.dawnlight.tidyme.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.dawnlight.tidyme.data.database.entity.Event
import com.dawnlight.tidyme.data.database.entity.EventType
import com.dawnlight.tidyme.data.database.entity.OccurrenceType
import com.dawnlight.tidyme.data.database.entity.RepeatType
import com.dawnlight.tidyme.ui.theme.TidyMeTheme
import com.dawnlight.tidyme.viewmodel.EventViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import kotlin.String

@Composable
fun RoutineTaskDialog(
    onDismiss: () -> Unit,
    viewModel: EventViewModel,
    initialTitle: String = "",
    initialDescription: String = "",
    initialRepeatFrequency: Int?,
    initialRepeatType: RepeatType?
) {
    Dialog(onDismissRequest = onDismiss) {
        RoutineTaskDialogContent(
            onDismiss = onDismiss,
            onSave = { viewModel.insertEvent(it) },
            initialTitle = initialTitle,
            initialDescription = initialDescription,
            initialRepeatFrequency = initialRepeatFrequency,
            initialRepeatType = initialRepeatType
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineTaskDialogContent(
    onDismiss: () -> Unit,
    onSave: (Event) -> Unit,
    initialTitle: String = "",
    initialDescription: String = "",
    initialRepeatFrequency: Int? = null,
    initialRepeatType: RepeatType? = null
) {
    var title by remember { mutableStateOf(initialTitle) }
    var description by remember { mutableStateOf(initialDescription) }
    var frequencyNumber by remember { mutableStateOf(initialRepeatFrequency?.toString() ?: "") }
    val frequencyTypes = listOf("Days", "Weeks", "Months")
    var expanded by remember { mutableStateOf(false) }
    var selectedFrequency by remember { mutableStateOf(initialRepeatType?.toString()?.lowercase()?.replaceFirstChar { it.uppercase() } ?: frequencyTypes[0]) }

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
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
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
                                lastExecutionTimestamp = null,
                                nextExecutionTimestamp = null
                            )
                            onSave(event)
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

@Composable
fun SporadicTaskDialog(
    onDismiss: () -> Unit,
    viewModel: EventViewModel,
    initialTitle: String = "",
    initialDescription: String = "",
    dialogTitle: String = "New Single Task",
    eventType: EventType = EventType.SINGLE
) {
    Dialog(onDismissRequest = onDismiss) {
        SporadicTaskDialogContent(
            onDismiss = onDismiss,
            onSave = { viewModel.insertEvent(it) },
            initialTitle = initialTitle,
            initialDescription = initialDescription,
            dialogTitle = dialogTitle,
            eventType = eventType
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SporadicTaskDialogContent(
    onDismiss: () -> Unit,
    onSave: (Event) -> Unit,
    initialTitle: String,
    initialDescription: String,
    dialogTitle: String ,
    eventType: EventType
) {
    var title by remember { mutableStateOf(initialTitle) }
    var description by remember { mutableStateOf(initialDescription) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    
    var selectedTimestamp by remember { mutableLongStateOf(System.currentTimeMillis()) }

    if (showDatePicker) {
        val dateState = rememberDatePickerState(
            initialSelectedDateMillis = selectedTimestamp
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        dateState.selectedDateMillis?.let { pickedDateMillis ->
                            val currentCal = Calendar.getInstance().apply { timeInMillis = selectedTimestamp }
                            val pickedCal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { 
                                timeInMillis = pickedDateMillis 
                            }
                            
                            currentCal.set(Calendar.YEAR, pickedCal.get(Calendar.YEAR))
                            currentCal.set(Calendar.MONTH, pickedCal.get(Calendar.MONTH))
                            currentCal.set(Calendar.DAY_OF_MONTH, pickedCal.get(Calendar.DAY_OF_MONTH))
                            
                            selectedTimestamp = currentCal.timeInMillis
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
        val calForTime = Calendar.getInstance().apply { timeInMillis = selectedTimestamp }
        val timeState = rememberTimePickerState(
            initialHour = calForTime.get(Calendar.HOUR_OF_DAY),
            initialMinute = calForTime.get(Calendar.MINUTE)
        )
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
                                val finalCal = Calendar.getInstance().apply { timeInMillis = selectedTimestamp }
                                finalCal.set(Calendar.HOUR_OF_DAY, timeState.hour)
                                finalCal.set(Calendar.MINUTE, timeState.minute)
                                selectedTimestamp = finalCal.timeInMillis
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

    Surface(
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = dialogTitle, style = MaterialTheme.typography.titleLarge)
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                Text(text = "Select Date & Time:\n${dateFormat.format(selectedTimestamp)}")
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
                                eventType = eventType,
                                title = title,
                                description = description,
                                occurrenceType = OccurrenceType.ONCE,
                                repeatType = null,
                                repeatFrequency = null,
                                lastExecutionTimestamp = if(eventType == EventType.SINGLE)  null else selectedTimestamp,
                                nextExecutionTimestamp = if(eventType == EventType.SINGLE)  selectedTimestamp else null
                            )
                            onSave(event)
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

@Preview(showBackground = true)
@Composable
fun RoutineTaskDialogPreview() {
    TidyMeTheme {
        RoutineTaskDialogContent(
            onDismiss = {},
            onSave = {},
            initialTitle = "Daily Cleaning",
            initialDescription = "Clean the kitchen",
            initialRepeatFrequency = 1,
            initialRepeatType = RepeatType.DAYS
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SporadicTaskDialogPreview() {
    TidyMeTheme {
        SporadicTaskDialogContent(
            onDismiss = {},
            onSave = {},
            initialTitle = "Doctor Appointment",
            initialDescription = "Visit the dentist",
            dialogTitle = "New Single Task",
            eventType = EventType.SINGLE
        )
    }
}
