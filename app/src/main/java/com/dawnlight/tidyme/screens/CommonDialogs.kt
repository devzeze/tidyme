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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.dawnlight.tidyme.data.database.entity.Event
import com.dawnlight.tidyme.data.database.entity.EventCategory
import com.dawnlight.tidyme.data.database.entity.EventType
import com.dawnlight.tidyme.data.database.entity.OccurrenceType
import com.dawnlight.tidyme.data.database.entity.RepeatType
import com.dawnlight.tidyme.viewmodel.EventViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventItem(
    event: Event,
    onSwipeToDismiss: ((Event) -> Unit)? = null,
    onLongPress: ((Event) -> Unit)? = null,
    swipeDirection: SwipeDirection = SwipeDirection.END_TO_START
) {
    if (onSwipeToDismiss != null) {
        val dismissState = rememberSwipeToDismissBoxState(
            confirmValueChange = { value ->
                when (swipeDirection) {
                    SwipeDirection.START_TO_END -> {
                        if (value == SwipeToDismissBoxValue.StartToEnd) {
                            onSwipeToDismiss(event)
                            true
                        } else {
                            false
                        }
                    }
                    SwipeDirection.END_TO_START -> {
                        if (value == SwipeToDismissBoxValue.EndToStart) {
                            onSwipeToDismiss(event)
                            true
                        } else {
                            false
                        }
                    }
                }
            }
        )

        SwipeToDismissBox(
            state = dismissState,
            enableDismissFromStartToEnd = swipeDirection == SwipeDirection.START_TO_END,
            enableDismissFromEndToStart = swipeDirection == SwipeDirection.END_TO_START,
            backgroundContent = {
                when (swipeDirection) {
                    SwipeDirection.START_TO_END -> {
                        val color by animateColorAsState(
                            when (dismissState.targetValue) {
                                SwipeToDismissBoxValue.StartToEnd -> Color.Green.copy(alpha = 0.5f)
                                else -> Color.Transparent
                            }, label = "background color"
                        )
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(color)
                                .padding(horizontal = 20.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Icon(
                                Icons.Default.Done,
                                contentDescription = "Mark as done"
                            )
                        }
                    }
                    SwipeDirection.END_TO_START -> {
                        val color by animateColorAsState(
                            when (dismissState.targetValue) {
                                SwipeToDismissBoxValue.EndToStart -> Color.Red.copy(alpha = 0.5f)
                                else -> Color.Transparent
                            }, label = "background color"
                        )
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(color)
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
            )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (event.category) {
                    EventCategory.SELF -> Icons.Filled.Person
                    EventCategory.SPACE -> Icons.Filled.Public
                },
                contentDescription = event.category.name,
                modifier = Modifier.padding(end = 16.dp)
            )
            Column {
                Text(text = "Type: ${event.eventType}", style = MaterialTheme.typography.titleMedium)
                Text(text = "Title: ${event.title}", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Description: ${event.description}", style = MaterialTheme.typography.bodyMedium)
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
                Text(text = "Occurrence: $occurrenceText", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineTaskDialog(
    onDismiss: () -> Unit,
    viewModel: EventViewModel,
    category: EventCategory
) {
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
                                    lastExecutionTimestamp = null,
                                    category = category
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
fun SingleTaskDialog(
    onDismiss: () -> Unit,
    viewModel: EventViewModel,
    category: EventCategory,
    initialTitle: String = "",
    initialDescription: String = ""
) {
    var title by remember { mutableStateOf(initialTitle) }
    var description by remember { mutableStateOf(initialDescription) }
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
                                    lastExecutionTimestamp = null,
                                    category = category
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
