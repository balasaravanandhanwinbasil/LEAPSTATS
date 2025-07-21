package com.example.leaps20

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
                title = { Text("My Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showEditSheet = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))

            ProfileImageView(
                imageData = profileImageData,
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Class of $year",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(24.dp))

            ProfileHexagonView(
                title = house,
                color = backColorHouse(house)
            )

            Spacer(Modifier.height(24.dp))

            ProfileHexagonView(
                title = cca,
                color = backColorCCA(cca)
            )

            Spacer(Modifier.weight(1f))

            Button(
                onClick = { showLogoutDialog = true },
                // Removed containerColor override to avoid background override
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Logout", color = Color.Red, fontWeight = FontWeight.Bold)
            }

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
                        ) {
                            Text("Logout", color = Color.Red)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showLogoutDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }

        if (showEditSheet) {
            EditView(
                userData = userData,
                onDismiss = { showEditSheet = false },
                userManager = userManager
            )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditView(
    userData: UserData,
    onDismiss: () -> Unit,
    userManager: UserManager
) {
    val yearRange = (2025..2222).map { it.toString() }
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

                DropdownSelector(
                    label = "Graduation Year",
                    options = yearRange,
                    selected = year,
                    onSelectedChange = { year = it },
                    backgroundColor = Color.LightGray, // keep for dropdown clarity
                    contentColor = Color.Black,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                DropdownSelector(
                    label = "House",
                    options = houseOptions,
                    selected = house,
                    onSelectedChange = { house = it },
                    backgroundColor = backColorHouse(house), // keep for meaningful color
                    contentColor = Color.White,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                DropdownSelector(
                    label = "CCA",
                    options = ccaOptions,
                    selected = cca,
                    onSelectedChange = { cca = it },
                    backgroundColor = backColorCCA(cca), // keep for meaningful color
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
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}


@Composable
fun ProfileHexagonView(title: String, color: Color) {
    Box(
        modifier = Modifier
            .size(169.dp)
            .clip(HexagonShape())
            .background(color), // keep for meaningful visual
        contentAlignment = Alignment.Center
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
