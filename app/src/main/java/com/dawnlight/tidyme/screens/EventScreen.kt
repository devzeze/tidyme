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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dawnlight.tidyme.data.database.entity.Event
import com.dawnlight.tidyme.data.database.entity.EventType
import com.dawnlight.tidyme.ui.theme.TidyMeTheme
import com.dawnlight.tidyme.viewmodel.EventViewModel

@Composable
fun EventScreen(viewModel: EventViewModel = viewModel()) {
    val events by viewModel.allEventsOrdered.collectAsState(initial = emptyList())
    var isExpanded by remember { mutableStateOf(false) }
    var showSingleDialog by remember { mutableStateOf(false) }
    var showRoutineDialog by remember { mutableStateOf(false) }
    var selectedEventForDuplication by remember { mutableStateOf<Event?>(null) }

    if (showSingleDialog) {
        SingleTaskDialog(
            onDismiss = {
                showSingleDialog = false
                selectedEventForDuplication = null
            },
            viewModel = viewModel,
            initialTitle = selectedEventForDuplication?.title ?: "",
            initialDescription = selectedEventForDuplication?.description ?: ""
        )
    }

    if (showRoutineDialog) {
        RoutineTaskDialog(
            onDismiss = {
                showRoutineDialog = false
                selectedEventForDuplication = null
            },
            viewModel = viewModel,
            initialTitle = selectedEventForDuplication?.title ?: "",
            initialDescription = selectedEventForDuplication?.description ?: "",
            initialRepeatFrequency = selectedEventForDuplication?.repeatFrequency,
            initialRepeatType = selectedEventForDuplication?.repeatType
        )
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Events",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)
            )
            LazyColumn(
                modifier = Modifier.padding(16.dp)
            ) {
                items(
                    items = events,
                    key = { event -> event.id }
                ) { event ->
                    EventItem(
                        event = event,
                        onSwipeToDismiss = { eventToDelete ->
                            viewModel.deleteEvent(eventToDelete)
                        },
                        onSwipeRight = { eventToMarkUndone ->
                            val updatedEvent = eventToMarkUndone.copy(lastExecutionTimestamp = null)
                            viewModel.updateEvent(updatedEvent)
                        },
                        onLongPress = { longPressedEvent ->
                            selectedEventForDuplication = longPressedEvent
                            when (longPressedEvent.eventType) {
                                EventType.SINGLE -> showSingleDialog = true
                                EventType.ROUTINE -> showRoutineDialog = true
                            }
                        },
                        dismissOnSwipeRight = false
                    )
                }
            }
        }

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

@Composable
fun FabOption(
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
fun EventScreenPreview() {
    TidyMeTheme {
        EventScreen()
    }
}
