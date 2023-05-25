package ru.altmanea.webapp.rest

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.altmanea.webapp.config.Config
import ru.altmanea.webapp.data.Lesson
import ru.altmanea.webapp.repo.lessonDb
import kotlinx.serialization.json.Json
import org.litote.kmongo.*
import ru.altmanea.webapp.auth.authorization
import ru.altmanea.webapp.auth.roleAdmin
import ru.altmanea.webapp.auth.roleTeacher
import ru.altmanea.webapp.command.commandLesson.ComAddGroup
import ru.altmanea.webapp.command.commandLesson.ComAddTeacher
import ru.altmanea.webapp.command.commandLesson.ComDelGroup
import ru.altmanea.webapp.command.commandLesson.ComDelTeacher
import ru.altmanea.webapp.data.Group
import ru.altmanea.webapp.repo.groupDb

import java.util.*

fun Route.lessonRoutes() {
    route(Config.lessonsPath) {
        authenticate("auth-jwt") {
            authorization(setOf(roleAdmin, roleTeacher)) {
                get {
                    val lessons = lessonDb.find().toList() as List<Lesson>
                    call.respond(lessons)
                }
                get("{id}") {
                    val id = call.parameters["id"]
                        ?: return@get call.respondText(
                            "Missing or malformed id", status = HttpStatusCode.BadRequest
                        )
                    val lesson = lessonDb.find(Lesson::_id eq id).firstOrNull()
                        ?: return@get call.respondText(
                            "No lesson with id $id", status = HttpStatusCode.NotFound
                        )
                    call.respond(lesson)
                }
                get("ByStartName/{startName}") {
                    val startName = call.parameters["startName"] ?: return@get call.respondText(
                        "Missing or malformed startName", status = HttpStatusCode.BadRequest
                    )
                    val lessons = lessonDb.find().filter {
                        it.name.startsWith(startName)
                    } as List<Lesson>
                    if (lessons.isEmpty()) return@get call.respondText(
                        "No lessons found",
                        status = HttpStatusCode.NotFound
                    )
                    call.respond(lessons)
                }
            }
            authorization(setOf(roleAdmin)) {
                post {
                    val lesson = call.receive<Lesson>()
                    val lessonId = Lesson(
                        lesson.name,
                        lesson.type,
                        lesson.hours,
                        lesson.teacher,
                        lesson.groups,
                        UUID.randomUUID().toString(),
                        System.currentTimeMillis()
                    )
                    if (lessonDb.find(Lesson::name eq lesson.name)
                            .firstOrNull() != null
                    ) return@post call.respondText(
                        "The lesson already exists", status = HttpStatusCode.BadRequest
                    )
                    lessonDb.insertOne(lessonId)
                    call.respondText(
                        "Lesson stored correctly", status = HttpStatusCode.Created
                    )
                }
                delete("/lessonDelete/{idL}") {
                    val id = call.parameters["idL"] ?: return@delete call.respondText(
                        "Missing or malformed lesson id", status = HttpStatusCode.BadRequest
                    )
                    lessonDb.deleteOne(Lesson::_id eq id)
                    call.respondText(
                        "Lesson deleted correctly", status = HttpStatusCode.OK
                    )
                }

                post("{id}/${ComAddTeacher.path}") {
                    val command = Json.decodeFromString(ComAddTeacher.serializer(), call.receive())
                    val lesson =
                        lessonDb.find(Lesson::_id eq command.lessonId).firstOrNull()
                            ?: return@post call.respondText(
                                "NF les", status = HttpStatusCode.NotFound
                            )
                    if (command.version != lesson.version)
                        return@post call.respondText(
                            "Student had updated on server", status = HttpStatusCode.BadRequest
                        )
                    if (lesson.teacher == "") {
                        lessonDb.updateOne(
                            Lesson::_id eq command.lessonId, setValue(Lesson::teacher, command.teacher)
                        )
                        lessonDb.updateOne(
                            Lesson::_id eq command.lessonId, setValue(Lesson::version, System.currentTimeMillis())
                        )
                        call.respondText(
                            "Lesson stored correctly", status = HttpStatusCode.Created
                        )
                    } else {
                        return@post call.respondText(
                            "lesson is alredy...", status = HttpStatusCode.BadRequest
                        )
                    }
                }

                delete("{id}/${ComDelTeacher.path}") {
                    val command = Json.decodeFromString(ComDelTeacher.serializer(), call.receive())
                    val lesson =
                        lessonDb.find(Lesson::_id eq command.lessonId).firstOrNull()
                            ?: return@delete call.respondText(
                                "NF les", status = HttpStatusCode.NotFound
                            )
                    if (command.version != lesson.version)
                        return@delete call.respondText(
                            "Student had updated on server", status = HttpStatusCode.BadRequest
                        )
                    lessonDb.updateOne(
                        Lesson::_id eq command.lessonId, setValue(Lesson::teacher, "")
                    )
                    lessonDb.updateOne(
                        Lesson::_id eq command.lessonId, setValue(Lesson::version, System.currentTimeMillis())
                    )
                    call.respondText(
                        "Lesson stored correctly", status = HttpStatusCode.Created
                    )
                }
                post("{id}/${ComAddGroup.path}") {
                    val command = Json.decodeFromString(ComAddGroup.serializer(), call.receive())
                    val lesson =
                        lessonDb.find(Lesson::_id eq command.lessonId).firstOrNull()
                            ?: return@post call.respondText(
                                "NotFound lesson", status = HttpStatusCode.NotFound
                            )
                    if (command.version != lesson.version)
                        return@post call.respondText(
                            "Student had updated on server", status = HttpStatusCode.BadRequest
                        )
                    val group =
                        groupDb.find(Group::name eq command.groupId).firstOrNull() ?: return@post call.respondText(
                            "NotFound group", status = HttpStatusCode.NotFound
                        )
                    val newGroup = lesson.addGroup(group)
                    lessonDb.replaceOne(Lesson::_id eq command.lessonId, newGroup)
                    lessonDb.updateOne(
                        Lesson::_id eq command.lessonId,
                        setValue(Lesson::version, System.currentTimeMillis())
                    )
                    call.respondText(
                        "Lesson stored correctly", status = HttpStatusCode.Created
                    )
                }
                delete("{id}/${ComDelGroup.path}") {
                    val command = Json.decodeFromString(ComDelGroup.serializer(), call.receive())
                    val lesson =
                        lessonDb.find(Lesson::_id eq command.lessonId).firstOrNull()
                            ?: return@delete call.respondText(
                                "NF les", status = HttpStatusCode.NotFound
                            )
                    if (command.version != lesson.version)
                        return@delete call.respondText(
                            "Student had updated on server", status = HttpStatusCode.BadRequest
                        )
                    val newGroup = lesson.groups.filter { it.name != command.groupId }
                    lessonDb.updateOne(
                        Lesson::_id eq command.lessonId,
                        setValue(Lesson::groups, newGroup)
                    )
                    lessonDb.updateOne(
                        Lesson::_id eq command.lessonId,
                        setValue(Lesson::version, System.currentTimeMillis())
                    )
                    call.respondText(
                        "Lesson stored correctly", status = HttpStatusCode.Created
                    )
                }
            }
        }
    }
}

