package ru.altmanea.webapp.command.commandLesson

import kotlinx.serialization.Serializable

@Serializable
class ComAddGroup (
    val lessonId: String,
    val groupId: String,
    val version: Long
) {
    companion object {
        const val path="addGroup"
    }
}