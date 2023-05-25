package ru.altmanea.webapp.rest

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.litote.kmongo.eq
import org.litote.kmongo.setValue
import ru.altmanea.webapp.auth.authorization
import ru.altmanea.webapp.auth.roleAdmin
import ru.altmanea.webapp.auth.roleTeacher
import ru.altmanea.webapp.config.Config
import ru.altmanea.webapp.data.Group
import ru.altmanea.webapp.data.Lesson
import ru.altmanea.webapp.data.Student
import ru.altmanea.webapp.repo.groupDb
import ru.altmanea.webapp.repo.lessonDb
import ru.altmanea.webapp.repo.studentDb
import java.util.*


fun Route.groupRoutes() {
    route(Config.groupsPath) {
        authenticate("auth-jwt") {
            authorization(setOf(roleAdmin, roleTeacher)) {
                get {
                    val groups = groupDb.find().toList() as List<Group>
                    if (groups.isEmpty())
                        return@get call.respondText(
                            "Groups not found", status = HttpStatusCode.NotFound
                        )
                    call.respond(groups)
                }
                get("{group}") {
                    val groupNumber = call.parameters["group"]
                        ?: return@get call.respondText(
                            "Missing or malformed id", status = HttpStatusCode.BadRequest
                        )
                    val group = groupDb.find(Group::name eq groupNumber).firstOrNull()
                        ?: return@get call.respondText(
                            "No group with id $groupNumber", status = HttpStatusCode.NotFound
                        )
                    val lessonList = mutableListOf<Lesson>()
                    lessonDb.find().toList().map { lesson ->
                        lesson.groups.map {
                            if (it.name == groupNumber) {
                                lessonList.add(lesson)
                            }
                        }
                    }
                    call.respond(Pair(group, lessonList.toList()))
                }
                get("ByStartName/{startName}") {
                    val startName = call.parameters["startName"] ?: return@get call.respondText(
                        "Missing or malformed startName", status = HttpStatusCode.BadRequest
                    )
                    val groups = groupDb.find().filter { it.name.startsWith(startName) } as List<Group>
                    if (groups.isEmpty())
                        return@get call.respondText(
                            "Groups not found", status = HttpStatusCode.NotFound
                        )
                    call.respond(groups)
                }
            }
            authorization(setOf(roleAdmin)) {
                post {
                    val group = call.receive<Group>()
                    val groupId = Group(
                        group.name, group.students,
                        UUID.randomUUID().toString(),
                        System.currentTimeMillis()
                    )
                    if (groupDb.find(Group::name eq group.name).firstOrNull() != null)
                        return@post call.respondText(
                            "The group already exists", status = HttpStatusCode.BadRequest
                        )
                    groupDb.insertOne(groupId)
                    call.respondText(
                        "Group stored correctly", status = HttpStatusCode.Created
                    )
                }
                delete("/groupDelete/{idG}") {
                    val id = call.parameters["idG"]
                        ?: return@delete call.respondText(
                            "Missing or malformed id", status = HttpStatusCode.BadRequest
                        )
                    val group = groupDb.find(Group::_id eq id).firstOrNull()
                        ?: return@delete call.respondText(
                            "Group not found", status = HttpStatusCode.NotFound
                        )
                    studentDb.updateMany(Student::group eq group.name, setValue(Student::group, ""))
                    groupDb.deleteOne(Group::_id eq id)
                    call.respondText(
                        "Group deleted correctly", status = HttpStatusCode.OK
                    )
                }
            }
        }
    }
}
