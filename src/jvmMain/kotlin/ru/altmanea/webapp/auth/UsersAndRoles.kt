package ru.altmanea.webapp.auth

import ru.altmanea.webapp.access.Role
import ru.altmanea.webapp.access.User

val userAdmin = User("admin","admin")
val userTeacher = User("teacher", "teacher")
val userList = listOf(userAdmin, userTeacher)

val roleAdmin = Role("admin")
val roleTeacher = Role("teacher")

val userRoles = mapOf(
    userAdmin to setOf(roleAdmin, roleTeacher),
    userTeacher to setOf(roleTeacher)
)