package component.student


import QueryError
import js.core.get
import js.core.jso
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import react.FC
import react.Props
import react.dom.html.ReactHTML.details
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.h2
import react.dom.html.ReactHTML.li
import react.dom.html.ReactHTML.ol
import react.dom.html.ReactHTML.summary
import react.router.Params
import react.router.useParams
import react.useContext
import ru.altmanea.webapp.command.commandStudent.ComChangeStud
import ru.altmanea.webapp.config.Config
import ru.altmanea.webapp.data.Lesson
import ru.altmanea.webapp.data.Student
import tanstack.query.core.QueryKey
import tanstack.react.query.useMutation
import tanstack.react.query.useQuery
import tanstack.react.query.useQueryClient
import tools.HTTPResult
import tools.fetch
import tools.fetchText
import userInfoContext
import kotlin.js.json

val pageStudentContainer = FC("PageStudentContainer") { _: Props ->
    val queryClient = useQueryClient()
    val params: Params = useParams()
    val studentId = params["id"]
    val userInfo = useContext(userInfoContext)
    val pageStudentQueryKey = arrayOf("studPage").unsafeCast<QueryKey>()
    val query = useQuery<String, QueryError, String, QueryKey>(
        queryKey = pageStudentQueryKey,
        queryFn = {
            fetchText(
                Config.studentsPath + studentId,
                jso {
                    headers = json("Authorization" to userInfo?.second?.authHeader)
                }
            )
        }
    )

    val updateStudentMutation = useMutation<HTTPResult, Any, Pair<Student, Long>, Any>(
        mutationFn = { student: Pair<Student, Long> ->
            fetch(
                "${Config.studentsPath}$studentId/${ComChangeStud.path}",
                jso {
                    method = "PUT"
                    headers = json(
                        "Content-Type" to "application/json",
                        "Authorization" to userInfo?.second?.authHeader
                    )
                    body = Json.encodeToString(
                        ComChangeStud(
                            studentId.toString(),
                            student.first,
                            student.second
                        )
                    )
                }
            )
        },
        options = jso {
            onSuccess = { _: Any, _: Any, _: Any? ->
                queryClient.invalidateQueries<Any>(pageStudentQueryKey)
            }
        }
    )

    if (query.isLoading) div { +"Loading .." }
    else if (query.isError) div { +"Error!" }
    else {
        val item =
            Json.decodeFromString<Pair<Student, List<Lesson>>>(query.data ?: "")
        CPageStudent {
            student = item.first
            updateStudent = {
                updateStudentMutation.mutateAsync(it, null)
            }
            listLesson = item.second
        }
    }
}


external interface PageStudentProps : Props {
    var student: Student
    var updateStudent: (Pair<Student, Long>) -> Unit
    var listLesson: List<Lesson>
}

val CPageStudent = FC<PageStudentProps>("Page") { props ->
    h1 {
        +props.student.fullname()
    }
    details {
        summary {
            +"Изменить студента"
        }
        CEditStudent {
            oldStudent = props.student
            saveStudent = {
                props.updateStudent(Pair(it, props.student.version))
            }
        }
    }
    h2 {
        +"Список занятий"
    }
    if (props.listLesson.isEmpty()) {
        div {
            +"У этого студента нет занятий"
        }
    } else {
        ol {
            props.listLesson.map {
                li {
                    +it.nameWithTeach()
                }
            }
        }
    }
}
