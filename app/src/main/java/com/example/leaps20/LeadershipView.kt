package com.example.leaps20

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeadershipView(
    dataManager: LeadershipData = viewModel(),
    navController: NavHostController
) {
    var showSheet by remember { mutableStateOf(false) }
    var selectedPosition by remember { mutableStateOf("") }
    var selectedYear by remember { mutableStateOf(Calendar.getInstance().get(Calendar.YEAR)) }
    var editingIndex by remember { mutableStateOf<Int?>(null) }

    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val yearRange = (currentYear..currentYear + 10).toList()

    val leadershipPositions by dataManager.leadershipHexes.collectAsState()
    val level = dataManager.currentLevel.collectAsState().value

    LaunchedEffect(Unit) {
        dataManager.loadLeadershipPositions()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFB0E0E6)) // FULL SCREEN BLUE BACKGROUND
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),  // no background here
            containerColor = Color.Transparent  // Make Scaffold background transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                // Title and Level
                SectionHeader(
                    title = "Leadership",
                    subtitle = "Level: $level",
                    icon = Icons.Default.Person,
                    navController = navController
                )

                // Content
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    itemsIndexed(leadershipPositions) { index, position ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = if (index % 2 == 0) Arrangement.Start else Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            LeadershipHexagonView(
                                leadershipPositionName = position.name,
                                leadershipPositionYear = position.year,
                                level = position.level,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        selectedPosition = position.name
                                        selectedYear = position.year
                                        editingIndex = index
                                        showSheet = true
                                    }
                                    .shadow(5.dp, shape = HexagonShape())
                                    .border(2.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), shape = HexagonShape())
                            )
                            Spacer(modifier = Modifier.width(8.dp)) // increased spacing
                            IconButton(
                                onClick = { dataManager.removeLeadershipPosition(index) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Remove position",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }

                    // Add button hex
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (leadershipPositions.size % 2 == 0) Arrangement.Start else Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AddLeadershipHexagon(
                                modifier = Modifier
                                    .weight(1f)
                                    .border(2.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), shape = HexagonShape()),
                                onClick = {
                                    selectedPosition = ""
                                    selectedYear = currentYear
                                    editingIndex = null
                                    showSheet = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showSheet) {
        LeadershipPositionSheet(
            dataManager = dataManager,
            selectedPosition = selectedPosition,
            onPositionChange = { selectedPosition = it },
            selectedYear = selectedYear,
            onYearChange = { selectedYear = it },
            editingIndex = editingIndex,
            onEditingIndexChange = { editingIndex = it },
            showSheet = showSheet,
            onShowSheetChange = { showSheet = it },
            yearRange = yearRange
        )
    }
}



@Composable
fun LeadershipHexagonView(
    leadershipPositionName: String,
    leadershipPositionYear: Int,
    level: String,
    modifier: Modifier = Modifier
) {
    val levelInt = level.toIntOrNull() ?: 1
    val backgroundColor = if (levelInt < 3) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) else MaterialTheme.colorScheme.primary
    val textColor = MaterialTheme.colorScheme.onPrimary

    Box(
        modifier = modifier
            .size(width = 120.dp, height = 140.dp)
            .background(color = backgroundColor, shape = HexagonShape()),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                leadershipPositionName,
                color = textColor,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Year $leadershipPositionYear",
                color = textColor,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
            Text(
                "Level $level",
                color = textColor,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun AddLeadershipHexagon(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .size(width = 120.dp, height = 140.dp)
            .background(color = MaterialTheme.colorScheme.surfaceVariant, shape = HexagonShape())
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "+ Add Position",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
fun LeadershipPositionSheet(
    dataManager: LeadershipData,
    selectedPosition: String,
    onPositionChange: (String) -> Unit,
    selectedYear: Int,
    onYearChange: (Int) -> Unit,
    editingIndex: Int?,
    onEditingIndexChange: (Int?) -> Unit,
    showSheet: Boolean,
    onShowSheetChange: (Boolean) -> Unit,
    yearRange: List<Int>
) {
    val allCategories = LeadershipData.allCategories
    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = { onShowSheetChange(false) },
        title = {
            Text(if (editingIndex != null) "Edit Position" else "Add Position")
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .verticalScroll(scrollState)
            ) {
                allCategories.forEach { category ->
                    Text(
                        category,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    LeadershipData.getPositionsForCategory(category).forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onPositionChange(item) }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                item,
                                color = if (selectedPosition == item) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                            if (selectedPosition == item) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Year", style = MaterialTheme.typography.titleMedium)
                yearRange.forEach { year ->
                    Text(
                        year.toString(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onYearChange(year) }
                            .background(
                                if (year == selectedYear)
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                else
                                    Color.Transparent
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (selectedPosition.isNotEmpty()) {
                        if (editingIndex != null) {
                            dataManager.updateLeadershipPosition(editingIndex, selectedPosition, selectedYear)
                        } else {
                            dataManager.addLeadershipPosition(selectedPosition, selectedYear)
                        }
                        onPositionChange("")
                        onYearChange(Calendar.getInstance().get(Calendar.YEAR))
                        onEditingIndexChange(null)
                        onShowSheetChange(false)
                    }
                },
                enabled = selectedPosition.isNotEmpty()
            ) {
                Text(if (editingIndex != null) "Update" else "Add")
            }
        },
        dismissButton = {
            Row {
                if (editingIndex != null) {
                    TextButton(
                        onClick = {
                            dataManager.removeLeadershipPosition(editingIndex)
                            onPositionChange("")
                            onYearChange(Calendar.getInstance().get(Calendar.YEAR))
                            onEditingIndexChange(null)
                            onShowSheetChange(false)
                        }
                    ) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                TextButton(
                    onClick = {
                        onPositionChange("")
                        onYearChange(Calendar.getInstance().get(Calendar.YEAR))
                        onEditingIndexChange(null)
                        onShowSheetChange(false)
                    }
                ) {
                    Text("Cancel")
                }
            }
        }
    )
}
