package ru.altmanea.webapp.command.commandTeacher

import kotlinx.serialization.Serializable
import ru.altmanea.webapp.data.Student
import ru.altmanea.webapp.data.Teacher

@Serializable
class ComChangeTeacher (
    val teacherId: String,
    val teacher: Teacher,
    val version: Long
) {
    companion object {
        const val path="changeTeacher"
    }
}