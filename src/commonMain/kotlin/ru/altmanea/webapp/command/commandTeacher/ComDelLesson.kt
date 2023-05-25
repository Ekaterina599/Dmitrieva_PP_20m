package ru.altmanea.webapp.command.commandTeacher

import kotlinx.serialization.Serializable

@Serializable
class ComDelLesson (
    val lessonId: String,
    val version: Long
) {
    companion object {
        const val path="deleteLesson"
    }
}