package ru.altmanea.webapp.command.commandLesson

import kotlinx.serialization.Serializable
import ru.altmanea.webapp.data.Student

@Serializable
class ComAddTeacher (
    val lessonId: String,
    val teacher: String,
    val version: Long
) {
    companion object {
        const val path="addTeacher"
    }
}