package ru.altmanea.webapp.data
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


@Serializable
class Teacher(
    val lastname: String, //фамилия
    val firstname : String, //имя
    val patronymic: String, //Отчетсво
    val _id: String,
    val version: Long
) {
    fun fullname() =
        "$lastname $firstname $patronymic"

}

val Teacher.json
    get() = Json.encodeToString(this)