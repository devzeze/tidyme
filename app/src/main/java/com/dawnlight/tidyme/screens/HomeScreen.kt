package com.dawnlight.tidyme.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dawnlight.tidyme.data.database.entity.EventType
import com.dawnlight.tidyme.ui.theme.TidyMeTheme
import com.dawnlight.tidyme.viewmodel.EventViewModel

@Composable
fun HomeScreen(viewModel: EventViewModel = viewModel()) {
    val events by viewModel.allEventsOrdered.collectAsState(initial = emptyList())

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Home",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)
            )
            LazyColumn(
                modifier = Modifier.padding(16.dp)
            ) {
                items(events.filter { event ->
                    if (event.eventType == EventType.SINGLE) {
                        event.lastExecutionTimestamp == null
                    } else {
                        val nextExecutionTime = event.getNextExecutionTime()
                        nextExecutionTime != null && nextExecutionTime <= System.currentTimeMillis()
                    }
                }) { event ->
                    EventItem(
                        event = event,
                        onSwipeToDismiss = {
                            viewModel.updateLastExecutionTimestamp(it.id, System.currentTimeMillis())
                        }
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
        HomeScreen()
    }
}