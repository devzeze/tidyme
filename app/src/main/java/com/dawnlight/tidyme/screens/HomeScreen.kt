package com.dawnlight.tidyme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dawnlight.tidyme.data.database.entity.Event
import com.dawnlight.tidyme.data.database.entity.EventType
import com.dawnlight.tidyme.data.database.entity.OccurrenceType
import com.dawnlight.tidyme.data.database.entity.RepeatType
import com.dawnlight.tidyme.ui.theme.TidyMeTheme
import com.dawnlight.tidyme.viewmodel.EventViewModel

@Composable
fun HomeScreen(viewModel: EventViewModel = viewModel()) {
    val events by viewModel.allEventsOrdered.collectAsState(initial = emptyList())
    HomeScreenContent(
        events = events,
        onSwipeToDismiss = { eventId ->
            viewModel.updateLastExecutionTimestamp(eventId, System.currentTimeMillis())
        }
    )
}

@Composable
fun HomeScreenContent(
    events: List<Event>,
    onSwipeToDismiss: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Home",
                fontSize = 32.sp,
                fontWeight = FontWeight.Thin,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)
            )
            LazyColumn(
                modifier = Modifier.padding(16.dp)
            ) {
                items(
                    items = events.filter { event ->
                        if (event.eventType == EventType.SINGLE) {
                            event.lastExecutionTimestamp == null
                        } else {
                            val nextExecutionTime = event.getNextExecutionTime()
                            nextExecutionTime != null && nextExecutionTime <= System.currentTimeMillis()
                        }
                    },
                    key = { event -> event.id }
                ) { event ->
                    EventItem(
                        event = event,
                        onSwipeToDismiss = {
                            onSwipeToDismiss(it.id)
                        },
                        swipeDirection = SwipeDirection.START_TO_END
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    TidyMeTheme {
        HomeScreenContent(
            events = listOf(
                Event(
                    id = "1",
                    title = "Daily Cleaning",
                    description = "Clean the kitchen",
                    eventType = EventType.ROUTINE,
                    occurrenceType = OccurrenceType.REPEAT,
                    repeatType = RepeatType.DAYS,
                    repeatFrequency = 1,
                    lastExecutionTimestamp = null,
                    nextExecutionTimestamp = null
                ),
                Event(
                    id = "2",
                    title = "Monthly Laundry",
                    description = "Wash the curtains",
                    eventType = EventType.ROUTINE,
                    occurrenceType = OccurrenceType.REPEAT,
                    repeatType = RepeatType.MONTHS,
                    repeatFrequency = 1,
                    lastExecutionTimestamp = null,
                    nextExecutionTimestamp = null
                )
            ),
            onSwipeToDismiss = {}
        )
    }
}