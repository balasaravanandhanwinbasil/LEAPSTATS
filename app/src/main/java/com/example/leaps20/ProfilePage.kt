package com.example.leaps20

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileView(
    userManager: UserManager = UserManager.shared,
    userData: UserData = viewModel(factory = UserDataFactory(LocalContext.current)),
    navController: NavHostController
) {
    val name by userData.name.collectAsState()
    val year by userData.year.collectAsState()
    val house by userData.house.collectAsState()
    val cca by userData.cca.collectAsState()
    val profileImageData by userData.profileImageData.collectAsState()

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showEditSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("My Profile",
                        fontWeight = FontWeight.Bold,
                        color = Colours.text
                    ) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Colours.text
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Colours.background
                )
            )
        },
        containerColor = Colours.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .background(Colours.background)
        ) {
            // --- Profile Header ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Colours.background),
                contentAlignment = Alignment.Center
            ) {
                Box {
                    ProfileImageView(
                        imageData = profileImageData,
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape)
                    )
                    IconButton(
                        onClick = { showEditSheet = true },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(36.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Colours.text
                )
                Text(
                    text = "Class of $year",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Gray
                )
            }

            Spacer(Modifier.height(32.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text("House:",
                        color = Colours.text,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(4.dp))
                    ProfileHexagonView(
                        title = house,
                        color = backColorHouse(house),
                        modifier = Modifier.size(140.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text("CCA:",
                        color = Colours.text,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(4.dp))
                    ProfileHexagonView(
                        title = cca,
                        color = backColorCCA(cca),
                        modifier = Modifier.size(140.dp)
                    )
                }
            }

            Spacer(Modifier.height(100.dp))

            // --- Logout Button ---
            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Logout",
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            // --- Logout Dialog ---
            if (showLogoutDialog) {
                AlertDialog(
                    onDismissRequest = { showLogoutDialog = false },
                    title = { Text("Are you sure you want to logout?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                userManager.logout()
                                showLogoutDialog = false
                            }
                        ) { Text("Logout", color = Color.Red) }
                    },
                    dismissButton = {
                        TextButton(onClick = { showLogoutDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            // --- Edit Sheet ---
            if (showEditSheet) {
                EditView(
                    userData = userData,
                    onDismiss = { showEditSheet = false },
                    userManager = userManager
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}


@Composable
fun ProfileImageView(imageData: ByteArray?, modifier: Modifier = Modifier) {
    if (imageData == null) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Select Image",
            modifier = modifier,
            tint = Color.Gray
        )
    } else {
        val bitmap = remember(imageData) {
            BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
        }
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Profile Image",
            modifier = modifier
        )
    }
}

@Composable
fun EditView(
    userData: UserData,
    onDismiss: () -> Unit,
    userManager: UserManager
) {
    val houseOptions = listOf("Red", "Blue", "Green", "Yellow", "Black")
    val ccaOptions = listOf(
        "ARC", "Astronomy", "Media", "Robotics", "SYFC", "Drama",
        "Guitar", "Show choir", "Dance", "Athletics", "Badminton",
        "Basketball", "Fencing", "Floorball", "Football", "Taekwondo", "Scouts"
    )

    var name by remember { mutableStateOf(userData.name.value) }
    var year by remember { mutableStateOf(userData.year.value.toString()) }
    var house by remember { mutableStateOf(userData.house.value) }
    var cca by remember { mutableStateOf(userData.cca.value) }
    var yearError by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Edit Profile", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                // Graduation year as TextField with restrictions
                OutlinedTextField(
                    value = year,
                    onValueChange = { input ->
                        // Allow only digits, max length 4
                        if (input.length <= 4 && input.all { it.isDigit() }) {
                            year = input

                            // Validate year range if input not empty
                            yearError = if (input.isNotEmpty()) {
                                val yearInt = input.toInt()
                                if (yearInt in 2000..2100) null else "Year must be between 2000 and 2100"
                            } else null
                        }
                    },
                    label = { Text("Graduation Year") },
                    isError = yearError != null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                    textStyle = LocalTextStyle.current.copy(fontSize = 18.sp)
                )
                if (yearError != null) {
                    Text(
                        text = yearError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.Start)
                    )
                }

                Spacer(Modifier.height(12.dp))

                DropdownSelector(
                    label = "House",
                    options = houseOptions,
                    selected = house,
                    onSelectedChange = { house = it },
                    backgroundColor = backColorHouse(house),
                    contentColor = Color.White,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                DropdownSelector(
                    label = "CCA",
                    options = ccaOptions,
                    selected = cca,
                    onSelectedChange = { cca = it },
                    backgroundColor = backColorCCA(cca),
                    contentColor = Color.White,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            // Only save if no errors
                            if (yearError == null) {
                                userData.name.value = name
                                userData.year.value = year.toIntOrNull() ?: userData.year.value
                                userData.house.value = house
                                userData.cca.value = cca

                                userData.saveField("name", name)
                                userData.saveField("year", userData.year.value)
                                userData.saveField("house", house)
                                userData.saveField("cca", cca)
                                userData.saveLocal()
                                onDismiss()
                            }
                        },
                        enabled = yearError == null
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}



@Composable
fun ProfileHexagonView(
    title: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.size(140.dp),
        shape = HexagonShape(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = title,
                color = Color.White,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(8.dp),
                maxLines = 2,
                fontSize = 22.sp
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownSelector(
    label: String,
    options: List<String>,
    selected: String,
    onSelectedChange: (String) -> Unit,
    backgroundColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        TextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label, color = Color.Black) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .background(backgroundColor), // keep dropdown background for clarity
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.Black,
                focusedTrailingIconColor = contentColor,
                unfocusedTrailingIconColor = contentColor,
                cursorColor = contentColor
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, color = Color.Black) },
                    onClick = {
                        onSelectedChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

fun backColorHouse(house: String): Color = when (house) {
    "Red" -> Color(0xFFE53935)
    "Blue" -> Color(0xFF1E88E5)
    "Green" -> Color(0xFF43A047)
    "Yellow" -> Color(0xFFFDD835)
    "Black" -> Color(0xFF212121)
    else -> Color.LightGray
}

fun backColorCCA(cca: String): Color = when (cca) {
    "ARC" -> Color(0xFF7B1FA2)
    "Astronomy" -> Color(0xFF0D47A1)
    "Media" -> Color(0xFF616161)
    "Robotics" -> Color(0xFF0097A7)
    "SYFC" -> Color(0xFF00695C)
    "Drama" -> Color(0xFFD81B60)
    "Guitar" -> Color(0xFF5D4037)
    "Show choir" -> Color(0xFF3949AB)
    "Dance" -> Color(0xFFAD1457)
    "Athletics" -> Color(0xFFF4511E)
    "Badminton" -> Color(0xFF00897B)
    "Basketball" -> Color(0xFFFF7043)
    "Fencing" -> Color(0xFF455A64)
    "Floorball" -> Color(0xFF546E7A)
    "Football" -> Color(0xFF1B5E20)
    "Taekwondo" -> Color(0xFFEF5350)
    "Scouts" -> Color(0xFF3E2723)
    else -> Color(0xFFE53935) // default red
}
