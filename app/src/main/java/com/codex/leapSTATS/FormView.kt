package com.codex.leapSTATS

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormView(
    userData: UserData,
    navController: NavHostController
) {
    var selectedComponent by remember { mutableStateOf("") }
    var selectedTeacher by remember { mutableStateOf("") }
    var errorIdentified by remember { mutableStateOf("") }
    var errorDescription by remember { mutableStateOf("") }

    var showFirstConfirmation by remember { mutableStateOf(false) }
    var showSecondConfirmation by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val components = listOf("Leadership", "Enrichment", "Achievement", "Participation")
    val teachers = listOf(
        "Mr Tan Hoe Teck",
        "Mr Thomas Wan"
    )

    val userName by userData.name.collectAsState()
    val userCCA by userData.cca.collectAsState()
    val userEmail = UserManager.shared.currentUserEmail ?: "No email"

    val generatedMessage = """
        LEAPS Error
        
        Student Details:
        Name: $userName
        CCA: $userCCA
        Email: $userEmail
        
        Error Details:
        Component: 
        $selectedComponent
        Teacher IC: 
        $selectedTeacher
        -
        Error Identified: 
        $errorIdentified
        Description:
        $errorDescription
    """.trimIndent()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Report Errors",
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp,
                            color = Colours.text
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            FrostedBottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                "Found an error in your LEAPS record? Report the error here.",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                "LEAPS records are given at the start and end of every year.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )


            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Which Component? *",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            DropdownWithHeader(
                label = "Select Component",
                options = components,
                selectedOption = selectedComponent,
                onOptionSelected = { selectedComponent = it }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "Teacher IC *",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            SearchableDropdown(
                label = "Select Teacher",
                options = teachers,
                selectedOption = selectedTeacher,
                onOptionSelected = { selectedTeacher = it }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Error Identified TextField
            Text(
                "What is the error identified? *",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = errorIdentified,
                onValueChange = { errorIdentified = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Brief summary of the error") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Error Description TextField
            Text(
                "Please describe the error in details *",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = errorDescription,
                onValueChange = { errorDescription = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                placeholder = { Text("Provide detailed information about the error...") },
                maxLines = 8,
                colors = OutlinedTextFieldDefaults.colors()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Submit Button
            Button(
                onClick = {
                    if (selectedComponent.isNotEmpty() &&
                        selectedTeacher.isNotEmpty() &&
                        errorIdentified.isNotEmpty() &&
                        errorDescription.isNotEmpty()) {
                        showFirstConfirmation = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedComponent.isNotEmpty() &&
                        selectedTeacher.isNotEmpty() &&
                        errorIdentified.isNotEmpty() &&
                        errorDescription.isNotEmpty()
            ) {
                Text("Submit Report", modifier = Modifier.padding(8.dp))
            }
        }
    }

    // First Confirmation Dialog
    if (showFirstConfirmation) {
        AlertDialog(
            onDismissRequest = { showFirstConfirmation = false },
            title = { Text("Confirm Submission") },
            text = {
                Column {
                    Text("Please review your error report:")
                    Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            generatedMessage,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier
                                .padding(6.dp)
                        )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showFirstConfirmation = false
                    showSecondConfirmation = true
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFirstConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Second Confirmation Dialog
    if (showSecondConfirmation) {
        AlertDialog(
            onDismissRequest = { showSecondConfirmation = false },
            title = { Text("Final Confirmation") },
            text = {
                Text("Are you absolutely sure you want to submit this error report? This action cannot be undone.")
            },
            confirmButton = {
                TextButton(onClick = {
                    showSecondConfirmation = false
                    showSuccessDialog = true
                }) {
                    Text("Yes, Submit", color = MaterialTheme.colorScheme.primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSecondConfirmation = false }) {
                    Text(
                        "Go Back",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )
    }

    // Success Dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                // Reset form
                selectedComponent = ""
                selectedTeacher = ""
                errorIdentified = ""
                errorDescription = ""
            },
            title = { Text("✓ Report Submitted") },
            text = {
                Text("Your error report has been successfully submitted.")
            },
            confirmButton = {
                TextButton(onClick = {
                    showSuccessDialog = false
                    selectedComponent = ""
                    selectedTeacher = ""
                    errorIdentified = ""
                    errorDescription = ""
                    navController.popBackStack()
                }) {
                    Text("Done")
                }
            }
        )
    }
}

@Composable
fun SearchableDropdown(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredOptions = if (searchQuery.isEmpty()) {
        options
    } else {
        options.filter { it.contains(searchQuery, ignoreCase = true) }
    }

    Column {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown",
                    modifier = Modifier.clickable { expanded = true }
                )
            },
            enabled = false,
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                searchQuery = ""
            },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            // Search field inside dropdown
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                placeholder = { Text("Search teachers...") },
                singleLine = true
            )

            Divider()

            if (filteredOptions.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("No teachers found") },
                    onClick = {}
                )
            } else {
                filteredOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                            searchQuery = ""
                        }
                    )
                }
            }
        }
    }
}