package component.teacher

import QueryError
import csstype.px
import emotion.react.css
import invalidateRepoKey
import js.core.jso
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import react.*
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h2
import react.dom.html.ReactHTML.h4
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.option
import react.dom.html.ReactHTML.select
import ru.altmanea.webapp.command.commandTeacher.ComAddLesson
import ru.altmanea.webapp.command.commandTeacher.ComDelLesson
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
import web.html.HTMLInputElement
import web.html.HTMLSelectElement
import kotlin.js.json

external interface LessonSelectTeacherProps : Props {
    var startName: String
    var onPick: (String) -> Unit
    var onNoPick: (String) -> Unit
    var teach: Teacher
}

val CLessonSelectTeacher = FC<LessonSelectTeacherProps>("GroupSelect") { props ->
    val selectQueryKey = arrayOf("SelectLessonTeacher", props.startName).unsafeCast<QueryKey>()
    val userInfo = useContext(userInfoContext)
    val query = useQuery<String, QueryError, String, QueryKey>(
        queryKey = selectQueryKey,
        queryFn = {
            fetchText(
                "${Config.lessonsPath}ByStartName/${props.startName}",
                jso {
                    headers = json("Authorization" to userInfo?.second?.authHeader)
                }
            )
        }
    )
    val selectRef = useRef<HTMLSelectElement>()
    val lessons: List<Lesson> =
        try {
            Json.decodeFromString(query.data ?: "")
        } catch (e: Throwable) {
            emptyList()
        }
    val lessonsT = lessons.filter { it.teacher == "" || it.teacher == props.teach.fullname() }
    select {
        css{
            height = 21.px
        }
        ref = selectRef
        lessonsT.map {
            option {
                +it.fullname()
                value = "${it._id} ${it.version}"
            }
        }
    }
    button {
        +"Добавить"
        onClick = {
            selectRef.current?.value?.let {
                props.onPick(it)
            }
        }
    }
    button {
        +"Удалить"
        onClick = {
            selectRef.current?.value?.let {
                props.onNoPick(it)
            }
        }
    }
}

external interface AddLessonToTeacherProps : Props {
    var teacherAdd: Teacher
}

val CLessonToTeacher = FC<AddLessonToTeacherProps>("AddLessonToTeacher") { props ->
    val queryClient = useQueryClient()
    val invalidateRepoKey = useContext(invalidateRepoKey)
    var input by useState("")
    val inputRef = useRef<HTMLInputElement>()
    val userInfo = useContext(userInfoContext)
    val addStudentMutation = useMutation<HTTPResult, Any, String, Any>(
        mutationFn = { lessonId: String ->
            fetch(
                "${Config.teachersPath}${props.teacherAdd._id}/${ComAddLesson.path}",
                jso {
                    method = "POST"
                    headers = json(
                        "Content-Type" to "application/json",
                        "Authorization" to userInfo?.second?.authHeader
                    )
                    body = Json.encodeToString(
                        ComAddLesson(
                            props.teacherAdd._id,
                            lessonId.substringBefore(" "),
                            lessonId.substringAfter(" ").toLong()
                        )
                    )
                }
            )
        },
        options = jso {
            onSuccess = { _: Any, _: Any, _: Any? ->
                queryClient.invalidateQueries<Any>(invalidateRepoKey)
            }
        }
    )

    val deleteMutation = useMutation<HTTPResult, Any, String, Any>(
        { lessonId: String ->
            fetch(
                "${Config.teachersPath}${props.teacherAdd._id}/${ComDelLesson.path}",
                jso {
                    method = "DELETE"
                    headers = json(
                        "Content-Type" to "application/json",
                        "Authorization" to userInfo?.second?.authHeader
                    )
                    body = Json.encodeToString(
                        ComDelLesson(
                            lessonId.substringBefore(" "),
                            lessonId.substringAfter(" ").toLong()
                        )
                    )
                }
            )
        },
        options = jso {
            onSuccess = { _: Any, _: Any, _: Any? ->
                queryClient.invalidateQueries<Any>(invalidateRepoKey)
            }
        }
    )
    h2 {+"Уроки преподавателя"}
    div {
        +"Добавить урок: "
        input {
            ref = inputRef
            onChange = {
                input = it.target.value
            }
        }
        CLessonSelectTeacher {
            startName = input.capitalize()
            onPick = {
                addStudentMutation.mutateAsync(it, null)
            }
            onNoPick = {
                deleteMutation.mutateAsync(it, null)
            }
            teach = props.teacherAdd
        }
    }
}
