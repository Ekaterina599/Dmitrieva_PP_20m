package component.teacher

import QueryError
import csstype.px
import emotion.react.css
import invalidateRepoKey
import js.core.jso
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import react.*
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h4
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.option
import react.dom.html.ReactHTML.select
import ru.altmanea.webapp.config.Config
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
    var onNoPick: (String) -> Unit
}

val CTeacherSelect = FC<TeacherSelectProps>("TeacherSelect") { props ->
    val selectQueryKey = arrayOf("TeacherSelectRemove", props.startName).unsafeCast<QueryKey>()
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
    val teachers: List<Teacher> =
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
        teachers.map {
            option {
                +it.fullname()
                value = it._id
            }
        }
    }
    button{
        +"Удалить преподавателя"
        onClick = {
            selectRef.current?.value?.let {
                props.onNoPick(it)
            }
        }
    }
}


val CRemoveTeacher = FC("RemoveStudent") { _:Props ->
    val queryClient = useQueryClient()
    val invalidateRepoKey = useContext(invalidateRepoKey)
    var input by useState("")
    val inputRef = useRef<HTMLInputElement>()
    val userInfo = useContext(userInfoContext)
    val deleteMutation = useMutation<HTTPResult, Any, String, Any>(
        { id: String ->
            fetch(
                "${Config.teachersPath}teacherDelete/$id",
                jso {
                    method = "DELETE"
                    headers = json("Authorization" to userInfo?.second?.authHeader)
                }
            )
        },
        options = jso {
            onSuccess = { _: Any, _: Any, _: Any? ->
                queryClient.invalidateQueries<Any>(invalidateRepoKey)
            }
        }
    )
    h4 { +"Удалить преподавателя:" }
    div {
        div {
            input {
                ref = inputRef
                onChange = { input = it.target.value }
            }
            CTeacherSelect {
                startName = input
                onNoPick = {
                    deleteMutation.mutateAsync(it, null)
                }
            }
        }
    }
}


