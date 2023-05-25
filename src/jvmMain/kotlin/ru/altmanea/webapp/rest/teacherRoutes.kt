package ru.altmanea.webapp.rest

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.litote.kmongo.and
import org.litote.kmongo.eq
import org.litote.kmongo.setValue
import ru.altmanea.webapp.auth.authorization
import ru.altmanea.webapp.auth.roleAdmin
import ru.altmanea.webapp.auth.roleTeacher
import ru.altmanea.webapp.command.commandTeacher.ComAddLesson
import ru.altmanea.webapp.command.commandTeacher.ComChangeTeacher
import ru.altmanea.webapp.command.commandTeacher.ComDelLesson
import ru.altmanea.webapp.config.Config
import ru.altmanea.webapp.data.Lesson
import ru.altmanea.webapp.data.Teacher
import ru.altmanea.webapp.repo.*
import java.util.*

fun Route.teacherRoutes() {
    route(Config.teachersPath) {
        authenticate("auth-jwt") {
            authorization(setOf(roleAdmin, roleTeacher)) {
                get {
                    val students = teacherDb.find().toList() as List<Teacher>
                    if (students.isEmpty())
                        return@get call.respondText("No students found", status = HttpStatusCode.NotFound)
                    call.respond(students)
                }
                get("ByStartName/{startName}") {
                    val startName = call.parameters["startName"] ?: return@get call.respondText(
                        "Missing or malformed startName", status = HttpStatusCode.BadRequest
                    )
                    val teachers = teacherDb.find().filter {
                        it.fullname().startsWith(startName)
                    } as List<Teacher>
                    if (teachers.isEmpty()) return@get call.respondText(
                        "No teacher found",
                        status = HttpStatusCode.NotFound
                    )
                    call.respond(teachers)
                }
                get("{id}") {
                    val id =
                        call.parameters["id"] ?: return@get call.respondText(
                            "Missing or malformed id", status = HttpStatusCode.BadRequest
                        )
                    val teacher = teacherDb.find(Teacher::_id eq id).firstOrNull()
                        ?: return@get call.respondText(
                            "No teacher with id $id", status = HttpStatusCode.NotFound
                        )
                    val lessonList = lessonDb.find(Lesson::teacher eq teacher.fullname()).toList() as List<Lesson>
                    call.respond(Pair(teacher, lessonList))
                }
            }
            authorization(setOf(roleAdmin)) {
                post {
                    val teacher = call.receive<Teacher>()
                    val teacherId = Teacher(
                        teacher.firstname, teacher.lastname, teacher.patronymic,
                        UUID.randomUUID().toString(),
                        System.currentTimeMillis()
                    )
                    if (teacherDb.find(
                            and(
                                Teacher::firstname eq teacher.firstname, Teacher::lastname eq teacher.lastname,
                                Teacher::patronymic eq teacher.patronymic
                            )
                        ).firstOrNull() != null
                    )
                        return@post call.respondText(
                            "The teacher already exists", status = HttpStatusCode.BadRequest
                        )
                    teacherDb.insertOne(teacherId)
                    call.respondText(
                        "Teacher stored correctly", status = HttpStatusCode.Created
                    )
                }
                delete("/teacherDelete/{idT}") {
                    val id = call.parameters["idT"] ?: return@delete call.respondText(
                        "Missing or malformed lesson id", status = HttpStatusCode.BadRequest
                    )
                    teacherDb.deleteOne(Teacher::_id eq id)
                    call.respondText(
                        "Teacher deleted correctly", status = HttpStatusCode.OK
                    )
                }
                put("{id}/${ComChangeTeacher.path}") {
                    val command = Json.decodeFromString(ComChangeTeacher.serializer(), call.receive())
                    val teacher = teacherDb.find(Teacher::_id eq command.teacherId).firstOrNull() as Teacher
                    if (command.version != teacher.version)
                        call.respondText(
                            "Student had updated on server", status = HttpStatusCode.BadRequest
                        )
                    val newVersionTeacher = Teacher(
                        command.teacher.lastname, command.teacher.firstname,
                        command.teacher.patronymic, command.teacher._id,
                        System.currentTimeMillis()
                    )
                    teacherDb.replaceOne(Teacher::_id eq command.teacherId, newVersionTeacher)
                    call.respondText(
                        "Student updates correctly", status = HttpStatusCode.Created
                    )
                }
                post("{id}/${ComAddLesson.path}") {
                    val command = Json.decodeFromString(ComAddLesson.serializer(), call.receive())
                    val lesson =
                        lessonDb.find(Lesson::_id eq command.lessonId).firstOrNull()
                            ?: return@post call.respondText(
                                "NotFound lesson", status = HttpStatusCode.NotFound
                            )
                    val teacher =
                        teacherDb.find(Teacher::_id eq command.teacherId).firstOrNull()
                            ?: return@post call.respondText(
                                "NotFound teachers", status = HttpStatusCode.NotFound
                            )
                    if (command.version != lesson.version)
                        return@post call.respondText(
                            "Lesson had updated on server", status = HttpStatusCode.BadRequest
                        )
                    lessonDb.updateOne(
                        Lesson::_id eq command.lessonId,
                        setValue(Lesson::teacher, teacher.fullname())
                    )
                    lessonDb.updateOne(
                        Lesson::_id eq command.lessonId,
                        setValue(Lesson::version, System.currentTimeMillis())
                    )
                    call.respondText(
                        "Lesson stored correctly", status = HttpStatusCode.Created
                    )
                }
                delete("{id}/${ComDelLesson.path}") {
                    val command = Json.decodeFromString(ComDelLesson.serializer(), call.receive())
                    val lesson =
                        lessonDb.find(Lesson::_id eq command.lessonId).firstOrNull()
                            ?: return@delete call.respondText(
                                "NotFound lesson", status = HttpStatusCode.NotFound
                            )
                    if (command.version != lesson.version)
                        return@delete call.respondText(
                            "Lesson had updated on server", status = HttpStatusCode.BadRequest
                        )
                    lessonDb.updateOne(
                        Lesson::_id eq command.lessonId,
                        setValue(Lesson::teacher, "")
                    )
                    lessonDb.updateOne(
                        Lesson::_id eq command.lessonId,
                        setValue(Lesson::version, System.currentTimeMillis())
                    )
                    call.respondText(
                        "delete lesson ok", status = HttpStatusCode.Created
                    )
                }
            }
        }
    }
}

