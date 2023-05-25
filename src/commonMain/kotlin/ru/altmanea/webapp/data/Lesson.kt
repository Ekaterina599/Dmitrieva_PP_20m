package ru.altmanea.webapp.data
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


@Serializable
data class Lesson(
    val name: String,
    val type: String, //Тип урока: лекция, лаб, практика
    val hours: Int = 0,
    val teacher: String,
    val groups: List<Group>,
    val _id: String,
    val version: Long
){
    fun fullname() =
        "$name $type $hours"
    fun nameWithTeach() = "$name $type $teacher"
    fun addGroup(group: Group)=
        Lesson(
            name,
            type,
            hours,
            teacher,
            groups + group,
            _id,
            version
        )
}
val Lesson.json
    get() = Json.encodeToString(this)