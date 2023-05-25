package ru.altmanea.webapp.command.commandLesson

import kotlinx.serialization.Serializable

@Serializable
class ComDelGroup (
    val lessonId: String,
    val groupId: String,
    val version: Long
) {
    companion object {
        const val path="deleteGroup"
    }
}