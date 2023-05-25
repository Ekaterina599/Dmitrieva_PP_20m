package ru.altmanea.webapp.repo

import mongoDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.getCollection
import org.litote.kmongo.setValue
import ru.altmanea.webapp.data.Group
import ru.altmanea.webapp.data.Lesson
import ru.altmanea.webapp.data.Student
import ru.altmanea.webapp.data.Teacher
import java.util.*

val studentDb = mongoDatabase.getCollection<Student>().apply { drop() }
val groupDb = mongoDatabase.getCollection<Group>().apply { drop() }
val teacherDb = mongoDatabase.getCollection<Teacher>().apply { drop() }
val lessonDb = mongoDatabase.getCollection<Lesson>().apply { drop() }

fun createTestData() {
    listOf(
        Student("Алексеев", "Андрей", "20м", UUID.randomUUID().toString(), System.currentTimeMillis()),
        Student("Андреев", "Иван", "20м", UUID.randomUUID().toString(), System.currentTimeMillis()),
        Student("Курочкина", "Соня", "20м", UUID.randomUUID().toString(), System.currentTimeMillis()),
        Student("Федотова", "Татьяна", "20з", UUID.randomUUID().toString(), System.currentTimeMillis() ),
        Student("Горохова", "Евгения", "20з", UUID.randomUUID().toString(), System.currentTimeMillis()),
        Student("Бобр", "Петр", "20з", UUID.randomUUID().toString(), System.currentTimeMillis()),
        Student("Павлинов", "Артем", "20и", UUID.randomUUID().toString(), System.currentTimeMillis()),
        Student("Фиалкова", "Ксения", "20и", UUID.randomUUID().toString(), System.currentTimeMillis()),
        Student("Горшков", "Егор", "20и", UUID.randomUUID().toString(), System.currentTimeMillis() ),
        Student("Желтых", "Денис", "20и", UUID.randomUUID().toString(), System.currentTimeMillis()),
        Student("Зубов", "Андрей", "20и", UUID.randomUUID().toString(), System.currentTimeMillis())
    ).apply {
        map {
            studentDb.insertOne(it)
        }
    }
    listOf(
        Group("20м", emptyList(),UUID.randomUUID().toString(), System.currentTimeMillis()),
        Group("20з", emptyList(),UUID.randomUUID().toString(), System.currentTimeMillis()),
        Group("20и", emptyList(),UUID.randomUUID().toString(), System.currentTimeMillis())
    ).apply {
        map {
            groupDb.insertOne(it)
            val stud = studentDb.find().filter(Student::group eq it.name).toList() as List<Student>
            groupDb.updateOne(Group::_id eq it._id, setValue(Group::students, stud))
        }
    }
    listOf(
        Teacher("Малютин", "Андрей", "Геннадьевич",  UUID.randomUUID().toString(), System.currentTimeMillis()),
        Teacher("Окишев", "Андрей", "Сергеевич",  UUID.randomUUID().toString(), System.currentTimeMillis() ),
        Teacher("Альтман", "Евгений","Анатольевич",UUID.randomUUID().toString(), System.currentTimeMillis()),
        Teacher("Тихонова", "Наталья", "Алексеевна", UUID.randomUUID().toString(), System.currentTimeMillis())
    ).apply {
        map {
            teacherDb.insertOne(it)
        }
    }
}

