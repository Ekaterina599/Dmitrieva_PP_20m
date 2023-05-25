package component.lesson

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
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.option
import react.dom.html.ReactHTML.select
import ru.altmanea.webapp.command.commandLesson.ComAddTeacher
import ru.altmanea.webapp.command.commandLesson.ComDelTeacher
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

external interface TeacherSelectProps : Props {
    var startName: String
    var onPick: (String) -> Unit
    var onNoPick: (String) -> Unit
}

val CTeacherSelect = FC<TeacherSelectProps>("TeacherSelect") { props ->
    val selectQueryKey = arrayOf("TeacherSelectAdd", props.startName).unsafeCast<QueryKey>()
    val userInfo = useContext(userInfoContext)
    val query = useQuery<String, QueryError, String, QueryKey>(
        queryKey = selectQueryKey,
        queryFn = {
            fetchText(
                "${Config.teachersPath}ByStartName/${props.startName}",
                jso {
                    headers = json("Authorization" to userInfo?.second?.authHeader)
                }
            )
        }
    )
    val selectRef = useRef<HTMLSelectElement>()
    val teacher: List<Teacher> =
        try {
            Json.decodeFromString(query.data ?: "")
        } catch (e: Throwable) {
            emptyList()
        }
    select {
        css{
            height = 21.px
        }
        ref = selectRef
        teacher.map {
            option {
                +it.fullname()
                value = it.fullname()
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

external interface AddTeacherProps : Props {
    var lessonAddTeacher: Lesson
}

val CAddTeacherToLesson = FC<AddTeacherProps>("AddTeacher") { props ->
    val queryClient = useQueryClient()
    val invalidateRepoKey = useContext(invalidateRepoKey)
    var input by useState("")
    val userInfo = useContext(userInfoContext)
    val inputRef = useRef<HTMLInputElement>()

    val addTeacherMutation = useMutation<HTTPResult, Any, String, Any>(
        mutationFn = { teacher: String ->
            fetch(
                "${Config.lessonsPath}${props.lessonAddTeacher._id}/${ComAddTeacher.path}",
                jso {
                    method = "POST"
                    headers = json(
                        "Content-Type" to "application/json",
                        "Authorization" to userInfo?.second?.authHeader
                    )
                    body = Json.encodeToString(
                        ComAddTeacher(
                            props.lessonAddTeacher._id,
                            teacher,
                            props.lessonAddTeacher.version
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
        {
            fetch(
                "${Config.lessonsPath}${props.lessonAddTeacher._id}/${ComDelTeacher.path}",
                jso {
                    method = "DELETE"
                    headers = json(
                        "Content-Type" to "application/json",
                        "Authorization" to userInfo?.second?.authHeader
                    )
                    body = Json.encodeToString(
                        ComDelTeacher(
                            props.lessonAddTeacher._id,
                            props.lessonAddTeacher.version
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
    div {
        +"Добавить преподавателя на урок: "
        input {
            ref = inputRef
            onChange = {
                input = it.target.value
            }
        }
        CTeacherSelect {
            startName = input.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            onPick = {
                addTeacherMutation.mutateAsync(it, null)
            }
            onNoPick = {
                deleteMutation.mutateAsync(it, null)
            }
        }
    }
}
