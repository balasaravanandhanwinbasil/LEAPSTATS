package com.codex.leapSTATS.TeacherView

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun CcaDetailView(item: HexagonItem, navController: NavHostController) {
    val studentItems = listOf(
        Student(name = "Kesler Ang Kang Zhi", classes = "S3-05", leaps_level = "5"),
        Student(name = "Tessa Lee", classes = "S3-03", leaps_level = "2")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = item.title,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        DetailContent(item = item, students = studentItems)
    }
}

@Composable
fun DetailContent(item: HexagonItem, students: List<Student>) {
    when (item.title) {
        "Astronomy Club",
        "Guitar Ensemble",
        "ARC @SST",
        "Floorball",
        "Media Club",
        "Athletics (Track)",
        "Robotics @APEX",
        "Badminton",
        "Scouts",
        "Basketball",
        "Show Choir and Dance",
        "English Drama Club",
        "Football",
        "Fencing",
        "Taekwondo" -> {
            Column {
                Text(
                    text = "Details and chart for ${item.title}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                StudentTable(students = students)
            }
        }
        else -> {
            Text(
                text = "Please look for the respective CCA's attendance.",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun StudentTable(students: List<Student>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Header
        StudentTableRow(
            name = "Name",
            classes = "Classes",
            isHeader = true
        )
        Divider()

        // Data rows
        LazyColumn {
            items(students) { student ->
                StudentTableRow(
                    name = student.name,
                    classes = student.classes,
                    isHeader = false
                )
                Divider()
            }
        }
    }
}

@Composable
fun StudentTableRow(
    name: String,
    classes: String,
    isHeader: Boolean
) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = name,
            modifier = Modifier.weight(1f),
            fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = classes,
            modifier = Modifier.weight(1f),
            fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal
        )
    }
}