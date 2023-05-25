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
import ru.altmanea.webapp.command.commandLesson.ComAddGroup
import ru.altmanea.webapp.command.commandLesson.ComDelGroup
import ru.altmanea.webapp.config.Config
import ru.altmanea.webapp.data.Group
import ru.altmanea.webapp.data.Lesson
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

external interface GroupSelectLessonProps : Props {
    var startName: String
    var onPick: (String) -> Unit
    var onNoPick: (String) -> Unit
}

val CGroupSelectLesson = FC<GroupSelectLessonProps>("GroupSelect") { props ->
    val selectQueryKey = arrayOf("GroupSelectLessonAdd", props.startName).unsafeCast<QueryKey>()
    val userInfo = useContext(userInfoContext)
    val query = useQuery<String, QueryError, String, QueryKey>(
        queryKey = selectQueryKey,
        queryFn = {
            fetchText(
                "${Config.groupsPath}ByStartName/${props.startName}",
                jso {
                    headers = json("Authorization" to userInfo?.second?.authHeader)
                }
            )
        }
    )
    val selectRef = useRef<HTMLSelectElement>()
    val groups: List<Group> =
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
        groups.map {
            option {
                +it.name
                value = it.name
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

external interface AddGroupToLessonProps : Props {
    var lessonAddGroup: Lesson
}

val CAddGroupToLesson = FC<AddGroupToLessonProps>("AddGroupToLesson") { props ->
    val queryClient = useQueryClient()
    val invalidateRepoKey = useContext(invalidateRepoKey)
    var input by useState("")
    val userInfo = useContext(userInfoContext)
    val inputRef = useRef<HTMLInputElement>()

    val addGroupMutation = useMutation<HTTPResult, Any, String, Any>(
        mutationFn = { groupId: String ->
            fetch(
                "${Config.lessonsPath}${props.lessonAddGroup._id}/${ComAddGroup.path}",
                jso {
                    method = "POST"
                    headers = json(
                        "Content-Type" to "application/json",
                        "Authorization" to userInfo?.second?.authHeader
                    )
                    body = Json.encodeToString(
                        ComAddGroup(
                            props.lessonAddGroup._id,
                            groupId,
                            props.lessonAddGroup.version
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
        { groupId: String ->
            fetch(
                "${Config.lessonsPath}${props.lessonAddGroup._id}/${ComDelGroup.path}",
                jso {
                    method = "DELETE"
                    headers = json(
                        "Content-Type" to "application/json",
                        "Authorization" to userInfo?.second?.authHeader
                    )
                    body = Json.encodeToString(
                        ComDelGroup(
                            props.lessonAddGroup._id,
                            groupId,
                            props.lessonAddGroup.version
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
        +"Добавить группу на урок: "
        input {
            ref = inputRef
            onChange = {
                input = it.target.value
            }
        }
        CGroupSelectLesson {
            startName = input
            onPick = {
                addGroupMutation.mutateAsync(it, null)
            }
            onNoPick = {
                deleteMutation.mutateAsync(it, null)
            }
        }
    }
}
