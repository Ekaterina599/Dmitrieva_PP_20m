package ru.altmanea.webapp.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
class Group(
    val name: String,
    val students: List<Student>,
    val _id: String,
    val version: Long
)

val Group.json
    get() = Json.encodeToString(this)