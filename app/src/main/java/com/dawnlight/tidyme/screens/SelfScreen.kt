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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
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
import com.dawnlight.tidyme.data.database.entity.EventCategory
import com.dawnlight.tidyme.data.database.entity.OccurrenceType
import com.dawnlight.tidyme.data.database.entity.RepeatType
import com.dawnlight.tidyme.ui.theme.TidyMeTheme
import com.dawnlight.tidyme.viewmodel.EventViewModel

@Composable
fun SelfScreen(viewModel: EventViewModel = viewModel()) {
    val events by viewModel.getEventsByCategory(EventCategory.SELF).collectAsState(initial = emptyList())
    var isExpanded by remember { mutableStateOf(false) }
    var showSingleDialog by remember { mutableStateOf(false) }
    var showRoutineDialog by remember { mutableStateOf(false) }

    if (showSingleDialog) {
        SingleTaskDialog(
            onDismiss = { showSingleDialog = false },
            viewModel = viewModel,
            category = EventCategory.SELF
        )
    }

    if (showRoutineDialog) {
        RoutineTaskDialog(
            onDismiss = { showRoutineDialog = false },
            viewModel = viewModel,
            category = EventCategory.SELF
        )
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Self",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)
            )
            LazyColumn(
                modifier = Modifier.padding(16.dp)
            ) {
                items(events) { event ->
                    EventItem(event = event)
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
fun EventItem(event: Event) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
fun SelfScreenPreview() {
    TidyMeTheme {
        SelfScreen()
    }
}
