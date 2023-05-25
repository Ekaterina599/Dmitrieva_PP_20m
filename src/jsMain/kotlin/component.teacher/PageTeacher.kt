package component.teacher

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
import ru.altmanea.webapp.command.commandTeacher.ComChangeTeacher
import ru.altmanea.webapp.config.Config
import ru.altmanea.webapp.data.Lesson
import ru.altmanea.webapp.data.Teacher
import tanstack.query.core.QueryKey
import tanstack.react.query.useMutation
import tanstack.react.query.useQuery
import tanstack.react.query.useQueryClient
import tools.HTTPResult
import tools.fetch
import tools.fetchText
import userInfoContext
import kotlin.js.json

val pageTeacherContainer = FC("PageTeacherContainer") { _: Props ->
    val queryClient = useQueryClient()
    val params: Params = useParams()
    val teacherId = params["id"]
    val userInfo = useContext(userInfoContext)
    val pageTeachQueryKey = arrayOf("teachPage").unsafeCast<QueryKey>()
    val query = useQuery<String, QueryError, String, QueryKey>(
        queryKey = pageTeachQueryKey,
        queryFn = {
            fetchText(
                Config.teachersPath + teacherId,
                jso {
                    headers = json("Authorization" to userInfo?.second?.authHeader)
                }
            )
        }
    )

    val updateTeacherMutation = useMutation<HTTPResult, Any, Pair<Teacher, Long>, Any>(
        mutationFn = { teacher: Pair<Teacher, Long> ->
            fetch(
                "${Config.teachersPath}$teacherId/${ComChangeTeacher.path}",
                jso {
                    method = "PUT"
                    headers = json(
                        "Content-Type" to "application/json",
                        "Authorization" to userInfo?.second?.authHeader
                        )
                    body = Json.encodeToString(
                        ComChangeTeacher(
                            teacherId.toString(),
                            teacher.first,
                            teacher.second
                        )
                    )
                }
            )
        },
        options = jso {
            onSuccess = { _: Any, _: Any, _: Any? ->
                queryClient.invalidateQueries<Any>(pageTeachQueryKey)
            }
        }
    )

    if (query.isLoading) div { +"Loading .." }
    else if (query.isError) div { +"Error!" }
    else {
        val item =
            Json.decodeFromString<Pair<Teacher, List<Lesson>>>(query.data ?: "")
        CPageTeacher {
            teacher = item.first
            updateTeacher = {
                updateTeacherMutation.mutateAsync(it, null)
            }
            listLesson = item.second
        }
    }
}


external interface PageTeacherProps : Props {
    var teacher: Teacher
    var updateTeacher: (Pair<Teacher, Long>) -> Unit
    var listLesson: List<Lesson>
}

val CPageTeacher= FC<PageTeacherProps>("PageTeacher") { props ->
    h1{
        +props.teacher.fullname()
    }
    details {
        summary{
            +"Изменить преподавателя"
        }
        CEditTeacher {
            oldTeacher = props.teacher
            saveTeacher = {
                props.updateTeacher(it)
            }
        }
    }
    CLessonToTeacher{
        teacherAdd = props.teacher
    }
    if(props.listLesson.isEmpty()){
        div{
            +"У этого преподавателя нет занятий"
        }
    }
    else {
        ol {
            props.listLesson.map {
                li {
                    +it.fullname()
                }
            }
        }
    }
}
