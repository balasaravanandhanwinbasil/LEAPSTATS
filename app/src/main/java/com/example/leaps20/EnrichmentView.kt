import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.leaps20.HexagonShape
import java.time.LocalDate
import java.util.*

data class Event(
    var name: String,
    var date: LocalDate,
    var color: Color = Color(0xFFADD8E6), // lightBlue_1 equivalent
    val id: UUID = UUID.randomUUID()
)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnrichmentView() {
    var events by remember { mutableStateOf(listOf<Event>()) }
    var isEnrichmentSheet by remember { mutableStateOf(false) }
    var editingIndex by remember { mutableStateOf<Int?>(null) }

    var newEventName by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedColor by remember { mutableStateOf(Color(0xFFADD8E6)) }

    fun resetForm() {
        newEventName = ""
        selectedDate = LocalDate.now()
        selectedColor = Color(0xFFADD8E6)
        editingIndex = null
        isEnrichmentSheet = false
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Enrichment") })
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = Color.White
            ) {
                Text(
                    "Keep track of events and competitions here",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Black
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(vertical = 16.dp, horizontal = 30.dp),
            verticalArrangement = Arrangement.spacedBy((-70).dp)
        ) {
            itemsIndexed(events) { index, event ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (index % 2 == 0) Arrangement.End else Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    EnrichmentHexagonView(
                        enrichmentName = event.name,
                        enrichmentDate = event.date,
                        color = event.color,
                        modifier = Modifier
                            .size(width = 150.dp, height = 160.dp)
                            .shadow(5.dp, shape = HexagonShape())
                            .then(
                                if (editingIndex == index) Modifier.scale(1.1f)
                                else Modifier
                            )
                            .clickable {
                                editingIndex = index
                                newEventName = event.name
                                selectedDate = event.date
                                selectedColor = event.color
                                isEnrichmentSheet = true
                            }
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (events.size % 2 == 0) Arrangement.End else Arrangement.Start
                ) {
                    AddEventHexagon {
                        resetForm()
                        isEnrichmentSheet = true
                    }
                }
            }
        }

        if (isEnrichmentSheet) {
            EnrichmentEventForm(
                eventName = newEventName,
                onEventNameChange = { newEventName = it },
                eventDate = selectedDate,
                eventColor = selectedColor,
                onEventColorChange = { selectedColor = it },
                onCancel = { resetForm() },
                onAddOrUpdate = {
                    if (newEventName.isBlank()) return@EnrichmentEventForm
                    if (editingIndex != null) {
                        val list = events.toMutableList()
                        list[editingIndex!!] = list[editingIndex!!].copy(
                            name = newEventName,
                            date = selectedDate,
                            color = selectedColor
                        )
                        events = list.sortedBy { it.date }
                    } else {
                        events = (events + Event(newEventName, selectedDate, selectedColor))
                            .sortedBy { it.date }
                    }
                    resetForm()
                },
                onDelete = {
                    editingIndex?.let {
                        val list = events.toMutableList()
                        list.removeAt(it)
                        events = list
                        resetForm()
                    }
                },
                isEditing = editingIndex != null
            )
        }
    }
}

@Composable
fun EnrichmentEventForm(
    eventName: String,
    onEventNameChange: (String) -> Unit,
    eventDate: LocalDate,
    eventColor: Color,
    onEventColorChange: (Color) -> Unit,
    onCancel: () -> Unit,
    onAddOrUpdate: () -> Unit,
    onDelete: () -> Unit,
    isEditing: Boolean
) {
    val showDialog by remember { mutableStateOf(true) }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onCancel,
            title = { Text(if (isEditing) "Edit Event" else "Add Event") },
            text = {
                Column {
                    OutlinedTextField(
                        value = eventName,
                        onValueChange = onEventNameChange,
                        label = { Text("Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Use a simple date picker for demo
                    // You can replace with a proper date picker component
                    Text("Date: ${eventDate.toString()}")

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Color")
                    ColorPicker(eventColor, onEventColorChange)
                }
            },
            confirmButton = {
                Button(onClick = onAddOrUpdate, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                    Text(if (isEditing) "Update" else "Add", color = Color.White)
                }
            },
            dismissButton = {
                Row {
                    if (isEditing) {
                        TextButton(onClick = onDelete) {
                            Text("Delete", color = Color.Red)
                        }
                    }
                    TextButton(onClick = onCancel) {
                        Text("Cancel")
                    }
                }
            }
        )
    }
}

@Composable
fun AddEventHexagon(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(width = 180.dp, height = 190.dp)
            .shadow(5.dp, shape = HexagonShape())
            .background(Color(0xFFF5F5F5), shape = HexagonShape())
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text("+ New", fontSize = MaterialTheme.typography.headlineMedium.fontSize, color = Color.Black)
    }
}

@Composable
fun EnrichmentHexagonView(
    enrichmentName: String,
    enrichmentDate: LocalDate,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.background(color, shape = HexagonShape()),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(enrichmentName, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(enrichmentDate.toString(), color = Color.Black)
        }
    }
}


// Simple Color Picker placeholder (replace with actual implementation)
@Composable
fun ColorPicker(selectedColor: Color, onColorSelected: (Color) -> Unit) {
    // For demo, show some preset colors:
    val colors = listOf(Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Cyan, Color.Magenta, Color.Gray)
    Row {
        colors.forEach { color ->
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .padding(4.dp)
                    .background(color, shape = CircleShape)
                    .clickable { onColorSelected(color) }
                    .border(
                        width = if (color == selectedColor) 3.dp else 1.dp,
                        color = if (color == selectedColor) Color.Black else Color.LightGray,
                        shape = CircleShape
                    )
            )
        }
    }
}
