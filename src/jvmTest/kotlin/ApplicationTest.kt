/*
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.altmanea.webapp.data.Student
import ru.altmanea.webapp.data.json
import ru.altmanea.webapp.main


class ApplicationTest : StringSpec({
    "Students routes" {
        testApplication {
            application {
                main()
            }
            withClue("groupBody") {
                val group = "20b"
                Json.decodeFromString<List<String>>(client.get("groupBody/") {
                        contentType(ContentType.Application.Json) //заголовок (передаем объект в формате Json)
                        setBody(group) //тело запроса
                    }.bodyAsText()).map{it shouldBe "Leonard Hofstadter 20b"}
            }

            withClue("bodyGroup") {
                Json.decodeFromString<List<String>>(client.put("groupBody/") {
                    contentType(ContentType.Application.Json)
                    setBody("20b")
                }.bodyAsText()).first() shouldBe "Leonard Hofstadter"
            }
                */
/*
            val students = studentsRepo.read().filter{it.elem.group == "20a"}
            withClue("two") {
                val group = "20a"
                val getStudent= Json.decodeFromString<List<String>>(client.get("groupBody/") {
                        contentType(ContentType.Application.Json)
                        setBody(group)
                    }.bodyAsText())
                getStudent[0] shouldBe students[0].elem.fullname()
            }
                 *//*

//            val students = withClue("read") {
//                val response = client.get("/students/")
//                response.status shouldBe HttpStatusCode.OK
//                Json.decodeFromString<List<Item<Student>>>(response.bodyAsText()).apply {
//                    size shouldBe 4
//                }
//            }
//            withClue("read id") {
//                val sheldon = students.first { it.elem.firstname == "Sheldon" }
//                val response = client.get("/students/${sheldon.id}")
//                response.status shouldBe HttpStatusCode.OK
//                Json.decodeFromString<Item<Student>>(response.bodyAsText()).apply {
//                    elem.firstname shouldBe "Sheldon"
//                }
//            }
//            val newStudents = withClue("create") {
//                val response = client.post("/students/") {
//                    contentType(ContentType.Application.Json)
//                    setBody(Student("Raj", "Koothrappali", "20m").json)
//                }
//                response.status shouldBe HttpStatusCode.Created
//                Json.decodeFromString<List<Item<Student>>>(
//                    client.get("/students/").bodyAsText()
//                ).apply {
//                    size shouldBe 5
//                }
//            }
//            val emi = withClue("update") {
//                val raj = newStudents.first { it.elem.firstname == "Raj" }
//                client.put("/students/${raj.id}") {
//                    contentType(ContentType.Application.Json)
//                    setBody(Student("Amy", "Fowler", "20m").json)
//                }
//                Json.decodeFromString<Item<Student>>(
//                    client.get("/students/${raj.id}").bodyAsText()
//                ).apply {
//                    elem.firstname shouldBe "Amy"
//                }
//            }
//            withClue("delete"){
//                client.delete("/students/${emi.id}")
//                Json.decodeFromString<List<Item<Student>>>(
//                    client.get("/students/").bodyAsText()
//                ).apply {
//                    size shouldBe 4
//                }
//            }
        }
    }
})


*/
