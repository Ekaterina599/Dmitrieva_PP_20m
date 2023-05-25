package component.teacher

import QueryError
import js.core.jso
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import react.FC
import react.Props
import react.dom.html.ReactHTML
import react.dom.html.ReactHTML.div
import react.useContext
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
import kotlin.js.json

val ContainerTeacherList = FC<Props>("QueryTeacherList") {
    val queryClient = useQueryClient()
    val teacherListQueryKey = arrayOf("TeacherList").unsafeCast<QueryKey>()
    val userInfo = useContext(userInfoContext)
    val query = useQuery<String, QueryError, String, QueryKey>(
        queryKey = teacherListQueryKey,
        queryFn = { fetchText(
            Config.teachersPath,
            jso {
                headers = json("Authorization" to userInfo?.second?.authHeader)
            }
        ) }
    )

    val addTeacherMutation = useMutation<HTTPResult, Any, Teacher, Any>(
        mutationFn = { teacher: Teacher ->
            fetch(
                Config.teachersPath,
                jso {
                    method = "POST"
                    headers = json(
                        "Content-Type" to "application/json",
                        "Authorization" to userInfo?.second?.authHeader)
                    body = Json.encodeToString(teacher)
                }
            )
        },
        options = jso {
            onSuccess = { _: Any, _: Any, _: Any? ->
                queryClient.invalidateQueries<Any>(teacherListQueryKey)
            }
        }
    )

    if (query.isLoading) div { +"Loading .." }
    else if (query.isError) div { +"Error!" }
    else {
        val teachersList = Json.decodeFromString<List<Teacher>>(query.data ?: "")
        CAddTeacher {
            addTeacher = {
                addTeacherMutation.mutateAsync(it, null)
            }
        }
        CRemoveTeacher{}
        CTeacherList{
            teachers = teachersList
        }
    }
}
