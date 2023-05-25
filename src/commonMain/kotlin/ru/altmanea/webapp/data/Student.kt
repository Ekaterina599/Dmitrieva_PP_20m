package ru.altmanea.webapp.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


@Serializable
class Student(
    val firstname: String,
    val surname: String,
    var group: String,
    val _id: String,
    val version: Long
){
    fun fullname() =
        "$firstname $surname $group"
    fun shortname() =
        "$firstname $surname"
}


val Student.json
    get() = Json.encodeToString(this)

