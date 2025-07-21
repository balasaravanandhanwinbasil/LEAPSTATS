package com.example.leaps20

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

data class TeamMember(
    val name: String,
    val title: String,
    val icon: @Composable () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpView(navController: NavHostController) {
    val darkBlue1 = Color(0xFF123456) // Replace with your Color(.darkBlue1) value

    val team = listOf(
        TeamMember(
            "Tess Lee Jin En",
            "Chief Executive Officer",
            { Icon(Icons.Default.Person, contentDescription = null, tint = darkBlue1, modifier = Modifier.size(32.dp)) }
        ),
        TeamMember(
            "Vijayaganapathy Pavithraa",
            "Chief Operating Officer",
            { Icon(Icons.Default.Group, contentDescription = null, tint = darkBlue1, modifier = Modifier.size(32.dp)) }
        ),
        TeamMember(
            "Xi Yue Gong",
            "Chief Design Officer",
            { Icon(Icons.Default.Brush, contentDescription = null, tint = darkBlue1, modifier = Modifier.size(32.dp)) }
        ),
        TeamMember(
            "Balasaravanan Dhanwin Basil",
            "Chief Technology Officer (Android)",
            { Icon(Icons.Default.Build, contentDescription = null, tint = darkBlue1, modifier = Modifier.size(32.dp)) }
        ),
        TeamMember(
            "Kesler Ang Kang Zhi",
            "Chief Technology Officer (iOS)",
            { Icon(Icons.Default.Build, contentDescription = null, tint = darkBlue1, modifier = Modifier.size(32.dp)) }
        ),
        TeamMember(
            "Mr Ng Jun Wei",
            "Client",
            { Icon(Icons.Default.Person, contentDescription = null, tint = darkBlue1, modifier = Modifier.size(32.dp)) }
        )
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Acknowledgements") }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("About the app/tutorial", fontSize = 18.sp)
            }
            items(team) { member ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(member.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text(member.title, color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    member.icon()
                }
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    "For any problems or feedback, contact codexleaps2.0@gmail.com",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}
