package ru.altmanea.webapp.command.commandLesson

import kotlinx.serialization.Serializable

@Serializable
class ComDelTeacher(
    val lessonId: String,
    val version: Long
) {
    companion object {
        const val path="deleteTeacher"
    }
}