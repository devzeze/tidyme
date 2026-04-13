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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventItem(
    event: Event,
    onSwipeToDismiss: ((Event) -> Unit)? = null,
    onSwipeRight: ((Event) -> Unit)? = null,
    onLongPress: ((Event) -> Unit)? = null,
    swipeDirection: SwipeDirection = SwipeDirection.END_TO_START,
    dismissOnSwipeRight: Boolean = true
) {
    val hasSwipeAction = onSwipeToDismiss != null || onSwipeRight != null

    if (hasSwipeAction) {
        val dismissState = rememberSwipeToDismissBoxState(
            confirmValueChange = { value ->
                when (value) {
                    SwipeToDismissBoxValue.StartToEnd -> {
                        if (onSwipeRight != null) {
                            onSwipeRight(event)
                            dismissOnSwipeRight
                        } else if (swipeDirection == SwipeDirection.START_TO_END && onSwipeToDismiss != null) {
                            onSwipeToDismiss(event)
                            true
                        } else {
                            false
                        }
                    }
                    SwipeToDismissBoxValue.EndToStart -> {
                        if (swipeDirection == SwipeDirection.END_TO_START && onSwipeToDismiss != null) {
                            onSwipeToDismiss(event)
                            true
                        } else {
                            false
                        }
                    }
                    else -> false
                }
            }
        )

        val enableStartToEnd = onSwipeRight != null || (swipeDirection == SwipeDirection.START_TO_END && onSwipeToDismiss != null)
        val enableEndToStart = swipeDirection == SwipeDirection.END_TO_START && onSwipeToDismiss != null

        SwipeToDismissBox(
            state = dismissState,
            enableDismissFromStartToEnd = enableStartToEnd,
            enableDismissFromEndToStart = enableEndToStart,
            backgroundContent = {
                val startColor by animateColorAsState(
                    when (dismissState.targetValue) {
                        SwipeToDismissBoxValue.StartToEnd -> Color.Green.copy(alpha = 0.5f)
                        else -> Color.Transparent
                    }, label = "start background color"
                )
                val endColor by animateColorAsState(
                    when (dismissState.targetValue) {
                        SwipeToDismissBoxValue.EndToStart -> Color.Red.copy(alpha = 0.5f)
                        else -> Color.Transparent
                    }, label = "end background color"
                )

                Box(Modifier.fillMaxSize()) {
                    if (enableStartToEnd) {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(startColor)
                                .padding(horizontal = 20.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Icon(
                                Icons.Default.Done,
                                contentDescription = if (onSwipeRight != null) "Mark as undone" else "Mark as done"
                            )
                        }
                    }
                    if (enableEndToStart) {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(endColor)
                                .padding(horizontal = 20.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete"
                            )
                        }
                    }
                }
            }
        ) {
            EventCardContent(event = event, onLongPress = onLongPress)
        }
    } else {
        EventCardContent(event = event, onLongPress = onLongPress)
    }
}

enum class SwipeDirection {
    START_TO_END,  // Swipe right
    END_TO_START   // Swipe left
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun EventCardContent(
    event: Event,
    onLongPress: ((Event) -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .combinedClickable(
                onClick = {},
                onLongClick = { onLongPress?.invoke(event) }
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = event.title, style = MaterialTheme.typography.bodyLarge)
                Text(text = event.description, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                horizontalAlignment = Alignment.End
            ) {
                val occurrenceText = when (event.occurrenceType) {
                    OccurrenceType.ONCE -> "Once"
                    OccurrenceType.REPEAT -> {
                        val repeatTypeText = when (event.repeatType) {
                            RepeatType.DAYS -> "Days"
                            RepeatType.WEEKS -> "Weeks"
                            RepeatType.MONTHS -> "Months"
                            null -> ""
                        }
                        "Repeat every ${event.repeatFrequency} $repeatTypeText"
                    }
                }
                Text(text = occurrenceText, style = MaterialTheme.typography.bodySmall)
                event.nextExecutionTimestamp?.let {
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    Text(
                        text = dateFormat.format(it),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

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
fun SingleTaskDialog(
    onDismiss: () -> Unit,
    viewModel: EventViewModel,
    initialTitle: String = "",
    initialDescription: String = ""
) {
    Dialog(onDismissRequest = onDismiss) {
        SingleTaskDialogContent(
            onDismiss = onDismiss,
            onSave = { viewModel.insertEvent(it) },
            initialTitle = initialTitle,
            initialDescription = initialDescription
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleTaskDialogContent(
    onDismiss: () -> Unit,
    onSave: (Event) -> Unit,
    initialTitle: String = "",
    initialDescription: String = ""
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
            Text(text = "New Single Task", style = MaterialTheme.typography.titleLarge)
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
                Text(text = "Select Date & Time: ${dateFormat.format(selectedTimestamp)}")
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
                                lastExecutionTimestamp = null,
                                nextExecutionTimestamp = selectedTimestamp
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
fun EventItemPreview() {
    TidyMeTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            EventItem(
                event = Event(
                    id = "1",
                    title = "Daily Cleaning",
                    description = "Clean the kitchen",
                    eventType = EventType.ROUTINE,
                    occurrenceType = OccurrenceType.REPEAT,
                    repeatType = RepeatType.DAYS,
                    repeatFrequency = 1,
                    lastExecutionTimestamp = null,
                    nextExecutionTimestamp = System.currentTimeMillis()
                ),
                onSwipeToDismiss = {}
            )
            EventItem(
                event = Event(
                    id = "2",
                    title = "Doctor Appointment",
                    description = "Visit the dentist",
                    eventType = EventType.SINGLE,
                    occurrenceType = OccurrenceType.ONCE,
                    repeatType = null,
                    repeatFrequency = null,
                    lastExecutionTimestamp = null,
                    nextExecutionTimestamp = System.currentTimeMillis()
                ),
                onSwipeToDismiss = {}
            )
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
fun SingleTaskDialogPreview() {
    TidyMeTheme {
        SingleTaskDialogContent(
            onDismiss = {},
            onSave = {},
            initialTitle = "Doctor Appointment",
            initialDescription = "Visit the dentist"
        )
    }
}
