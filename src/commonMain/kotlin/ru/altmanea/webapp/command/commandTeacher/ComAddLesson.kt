package ru.altmanea.webapp.command.commandTeacher

import kotlinx.serialization.Serializable

@Serializable
class ComAddLesson (
    val teacherId: String,
    val lessonId: String,
    val version: Long
) {
    companion object {
        const val path="addLesson"
    }
}