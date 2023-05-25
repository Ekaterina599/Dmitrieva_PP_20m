package component.student

import QueryError
import csstype.px
import emotion.react.css
import js.core.jso
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import react.FC
import react.Props
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h4
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.label
import react.dom.html.ReactHTML.option
import react.dom.html.ReactHTML.select
import react.useContext
import react.useRef
import ru.altmanea.webapp.config.Config
import ru.altmanea.webapp.data.Group
import ru.altmanea.webapp.data.Student
import tanstack.query.core.QueryKey
import tanstack.react.query.useQuery
import tools.fetchText
import userInfoContext
import web.html.HTMLInputElement
import web.html.HTMLSelectElement
import kotlin.js.json

external interface AddStudentProps : Props {
    var addStudent: (Student) -> Unit
}

val CAddStudent = FC<AddStudentProps>("AddStudent") { props ->
    val firstnameRef = useRef<HTMLInputElement>()
    val surnameRef = useRef<HTMLInputElement>()
    val groupRef = useRef<HTMLSelectElement>()
    val userInfo = useContext(userInfoContext)
    val selectQueryKey = arrayOf("GroupSelectAddToStud").unsafeCast<QueryKey>()
    val query = useQuery<String, QueryError, String, QueryKey>(
        queryKey = selectQueryKey,
        queryFn = {
            fetchText(
                Config.groupsPath,
                jso {
                    headers = json("Authorization" to userInfo?.second?.authHeader)
                }
            )
        }
    )
    val groups: List<Group> =
        try {
            Json.decodeFromString(query.data ?: "")
        } catch (e: Throwable) {
            emptyList()
        }
    h4 { +"Добавить нового студента:" }
    div {
        css{
            marginTop = 15.px
        }
        div {
            label {
                css{
                    marginRight = 33.px
                }
                +"Имя: "
            }
            input { ref = firstnameRef }
        }
        div {
            label { +"Фамилия: " }
            input { ref = surnameRef }
        }
        div{
            label{+"Группа: "}
            select{
                ref = groupRef
                groups.map {
                    option {
                        +it.name
                    }
                }
            }
            button {
                +"Добавить"
                onClick = {
                    firstnameRef.current?.value?.let { firstname ->
                        surnameRef.current?.value?.let { surname ->
                            groupRef.current?.value?.let { group ->
                                val regex = "^[А-ЯЁ][а-яё]*\$".toRegex()
                                if (firstname.matches(regex) && surname.matches(regex)) {
                                    props.addStudent(Student(firstname, surname, group, "", 0))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
