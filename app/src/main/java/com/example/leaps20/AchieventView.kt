package com.example.leaps20

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController


@Composable
fun AchievementsView(
    navController: NavHostController,
    achievementsData: AchievementsData = viewModel()
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AchievementsAwardsView(
            navController = navController,
            achievementsData = achievementsData,
            onBack = {}
        )
    }
}

@Composable
fun AchievementsAwardsView(
    navController: NavHostController,
    achievementsData: AchievementsData = viewModel(),
    onBack: () -> Unit,
) {
    val achievements = achievementsData.hexes
    val level by achievementsData.currentHighestLevel.collectAsState(initial = 0)

    var showDialog by remember { mutableStateOf(false) }
    var editingIndex by remember { mutableStateOf<Int?>(null) }

    var awardName by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("Competition") }
    var selectedRepresent by remember { mutableStateOf("Intra-school") }
    var selectedYears by remember { mutableStateOf("1 year") }
    var selectedAwards by remember { mutableStateOf("Participation (No award)") }

    var expandedType by remember { mutableStateOf(false) }
    var expandedRepresentation by remember { mutableStateOf(false) }
    var expandedYear by remember { mutableStateOf(false) }
    var expandedAward by remember { mutableStateOf(false) }

    fun resetForm() {
        editingIndex = null
        awardName = ""
        selectedType = "Competition"
        selectedRepresent = "Intra-school"
        selectedYears = "1 year"
        selectedAwards = "Participation (No award)"
        expandedType = false
        expandedRepresentation = false
        expandedYear = false
        expandedAward = false
        showDialog = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SectionHeader(
                title = "Achievements",
                subtitle = "Level: $level",
                icon = Icons.Default.EmojiEvents,
                navController = navController,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopCenter
            ) {
                // Left Column
                Column(
                    verticalArrangement = Arrangement.spacedBy((-25).dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.offset(x = (-60).dp, y = 10.dp)
                ) {
                    achievements.filterIndexed { index, _ -> index % 2 == 0 }
                        .forEachIndexed { colIndex, achievement ->
                            AchievementHexagon(
                                name = achievement.name,
                                level = achievement.level,
                                color = getColorForLevel(achievement.level),
                                textColor = getTextColorForLevel(achievement.level),
                                modifier = Modifier
                                    .size(150.dp, 160.dp)
                                    .clickable {
                                        awardName = achievement.name
                                        selectedType = "Competition"
                                        selectedRepresent = achievement.representation
                                        selectedYears = achievement.year
                                        selectedAwards = achievement.award
                                        editingIndex = colIndex * 2
                                        showDialog = true
                                    }
                                    .border(
                                        2.dp,
                                        MaterialTheme.colorScheme.outline,
                                        shape = HexagonShape()
                                    )
                            )
                        }
                }

                // Right Column
                Column(
                    verticalArrangement = Arrangement.spacedBy((-25).dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.offset(x = (60).dp, y = 80.dp)
                ) {
                    achievements.filterIndexed { index, _ -> index % 2 == 1 }
                        .forEachIndexed { colIndex, achievement ->
                            AchievementHexagon(
                                name = achievement.name,
                                level = achievement.level,
                                color = getColorForLevel(achievement.level),
                                textColor = getTextColorForLevel(achievement.level),
                                modifier = Modifier
                                    .size(150.dp, 160.dp)
                                    .clickable {
                                        awardName = achievement.name
                                        selectedType = "Competition"
                                        selectedRepresent = achievement.representation
                                        selectedYears = achievement.year
                                        selectedAwards = achievement.award
                                        editingIndex = colIndex * 2 + 1
                                        showDialog = true
                                    }
                                    .border(
                                        2.dp,
                                        MaterialTheme.colorScheme.outline,
                                        shape = HexagonShape()
                                    )
                            )
                        }
                }
            }

            Spacer(modifier = Modifier.height(55.dp))

            AddAchievementHexagon(
                modifier = Modifier
                    .size(150.dp, 160.dp)
                    .clickable {
                        resetForm()
                        showDialog = true
                    }
                    .border(
                        2.dp,
                        MaterialTheme.colorScheme.outline,
                        shape = HexagonShape()
                    ),
                onClick = {
                    resetForm()
                    showDialog = true
                })

        }

        if (showDialog) {
            AchievementFormDialog(
                awardName = awardName,
                onAwardNameChange = { awardName = it },
                selectedType = selectedType,
                onSelectedTypeChange = { selectedType = it },
                selectedRepresent = selectedRepresent,
                onSelectedRepresentChange = { selectedRepresent = it },
                selectedYears = selectedYears,
                onSelectedYearsChange = { selectedYears = it },
                selectedAwards = selectedAwards,
                onSelectedAwardsChange = { selectedAwards = it },
                onSave = {
                    if (awardName.isNotBlank()) {
                        showDialog = false
                        if (editingIndex != null) {
                            achievementsData.updateAchievement(
                                editingIndex!!,
                                awardName,
                                selectedAwards,
                                selectedRepresent,
                                selectedYears
                            ) { success ->
                                if (success) resetForm()
                            }
                        } else {
                            achievementsData.addAchievement(
                                awardName,
                                selectedAwards,
                                selectedRepresent,
                                selectedYears
                            ) { success ->
                                if (success) resetForm()
                            }
                        }
                    }
                },
                onDelete = {
                    if (editingIndex != null) {
                        achievementsData.removeAchievement(editingIndex!!)
                        resetForm()
                    } else {
                        resetForm()
                    }
                },
                onCancel = { resetForm() },
                expandedType = expandedType,
                onExpandedTypeChange = { expandedType = it },
                expandedRepresentation = expandedRepresentation,
                onExpandedRepresentationChange = { expandedRepresentation = it },
                expandedYear = expandedYear,
                onExpandedYearChange = { expandedYear = it },
                expandedAward = expandedAward,
                onExpandedAwardChange = { expandedAward = it }
            )
        }
    }
}

@Composable
fun AchievementFormDialog(
    awardName: String,
    onAwardNameChange: (String) -> Unit,
    selectedType: String,
    onSelectedTypeChange: (String) -> Unit,
    selectedRepresent: String,
    onSelectedRepresentChange: (String) -> Unit,
    selectedYears: String,
    onSelectedYearsChange: (String) -> Unit,
    selectedAwards: String,
    onSelectedAwardsChange: (String) -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit,
    onCancel: () -> Unit,
    expandedType: Boolean,
    onExpandedTypeChange: (Boolean) -> Unit,
    expandedRepresentation: Boolean,
    onExpandedRepresentationChange: (Boolean) -> Unit,
    expandedYear: Boolean,
    onExpandedYearChange: (Boolean) -> Unit,
    expandedAward: Boolean,
    onExpandedAwardChange: (Boolean) -> Unit
) {
    val typeOptions = listOf("Competition", "Event", "SYF")
    val representationOptions = listOf(
        "Intra-school",
        "School/External Organisation",
        "National (SG/MOE/UG HQ)"
    )
    val yearOptions = listOf("1 year", "2 years", "3 or more years")
    val awardOptions = listOf(
        "Participation (No award)",
        "Top 4 team placing",
        "Top 8 individual placing",
        "Gold/Silver/Bronze/Merit certificate"
    )

    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text(if (awardName.isEmpty()) "Add Achievement" else awardName) },
        text = {
            Column {
                OutlinedTextField(
                    value = awardName,
                    onValueChange = onAwardNameChange,
                    label = { Text("Name*") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = awardName.isBlank()
                )
                if (awardName.isBlank()) {
                    Text(
                        text = "Name cannot be empty",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                DropdownWithHeader(
                    label = "Type",
                    options = typeOptions,
                    selectedOption = selectedType,
                    onOptionSelected = {
                        onSelectedTypeChange(it)
                        onExpandedTypeChange(false)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))

                DropdownWithHeader(
                    label = "Representation",
                    options = representationOptions,
                    selectedOption = selectedRepresent,
                    onOptionSelected = {
                        onSelectedRepresentChange(it)
                        onExpandedRepresentationChange(false)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))

                DropdownWithHeader(
                    label = "Years in Competition",
                    options = yearOptions,
                    selectedOption = selectedYears,
                    onOptionSelected = {
                        onSelectedYearsChange(it)
                        onExpandedYearChange(false)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))

                DropdownWithHeader(
                    label = "Award",
                    options = awardOptions,
                    selectedOption = selectedAwards,
                    onOptionSelected = {
                        onSelectedAwardsChange(it)
                        onExpandedAwardChange(false)
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onSave,
                enabled = awardName.isNotBlank()
            ) {
                Text(if (awardName.isEmpty()) "Add" else "Update")
            }
        },
        dismissButton = {
            Row {
                if (awardName.isNotEmpty()) {
                    TextButton(onClick = onDelete) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                }
                TextButton(onClick = onCancel) {
                    Text("Close", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownWithHeader(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedOption,
                onValueChange = {},
                readOnly = true,
                label = { Text(label) },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}


fun getColorForLevel(level: String): Color = when (level) {
    "1", "2" -> Color(0xFFADD8E6) // Light Blue
    "3", "4", "5" -> Color(0xFF1E90FF) // Darker Blue
    else -> Color(0xFFADD8E6)
}

fun getTextColorForLevel(level: String): Color = when (level) {
    "3", "4", "5" -> Color.White
    else -> Color.Black
}

@Composable
fun AchievementHexagon(
    name: String,
    level: String,
    color: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    HexagonShapeBox(
        color = color,
        modifier = modifier.size(150.dp, 160.dp)
            .border(
                2.dp,
                MaterialTheme.colorScheme.outline,
                shape = HexagonShape()
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = name,
                color = textColor,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 3,
                softWrap = true
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Level $level",
                color = textColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun AddAchievementHexagon(modifier: Modifier = Modifier, onClick: () -> Unit) {
    HexagonShapeBox(
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
            .size(150.dp, 160.dp)
            .clickable(onClick = onClick)
            .border(2.dp, MaterialTheme.colorScheme.outline, shape = HexagonShape())
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                "+ New",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = MaterialTheme.typography.headlineMedium.fontSize
            )
        }
    }
}

@Composable
fun HexagonShapeBox(
    color: Color,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .background(color = color, shape = HexagonShape()),
        contentAlignment = Alignment.Center,
        content = content
    )
}
