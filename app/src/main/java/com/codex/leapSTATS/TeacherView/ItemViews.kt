package com.codex.leapSTATS.TeacherView

import java.util.UUID

data class Student(
    val name: String,
    val classes: String,
    val leaps_level: String,
    val id: String = UUID.randomUUID().toString()
)

data class HexagonItem(
    val title: String,
    val x: Float,
    val y: Float,
    val isCenter: Boolean,
    val section: Section,
    val fontSize: Float,
    val id: String = UUID.randomUUID().toString()
) {
    enum class Section {
        LEFT, MIDDLE, RIGHT
    }
}
