package component.lesson

import QueryError
import js.core.get
import js.core.jso
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h2
import react.dom.html.ReactHTML.h3
import react.dom.html.ReactHTML.h4
import react.dom.html.ReactHTML.li
import react.dom.html.ReactHTML.ol
import react.router.useParams
import react.useContext
import ru.altmanea.webapp.config.Config
import ru.altmanea.webapp.data.Lesson
import tanstack.query.core.QueryKey
import tanstack.react.query.useQuery
import tools.fetchText
import userInfoContext
import kotlin.js.json

val pageLessonConatiner = FC<Props>("ContainerLessonProfile") {
    val profileLessonParams = useParams()
    val lessonId = profileLessonParams["id"]
    val lessonListQueryKey = arrayOf(lessonId).unsafeCast<QueryKey>()
    val userInfo = useContext(userInfoContext)
    val queryLesson = useQuery<String, QueryError, String, QueryKey>(
        queryKey = lessonListQueryKey,
        queryFn = {
            fetchText(
                Config.lessonsPath + lessonId,
                jso {
                    headers = json("Authorization" to userInfo?.second?.authHeader)
                }
            )
        }
    )
    if (queryLesson.isLoading)
        div { +"Loading ..." }
    else if (queryLesson.isError)
        div { +"Query error" }
    else {
        val lessonProfile = Json.decodeFromString<Lesson>(queryLesson.data ?: "")
        CPageLesson{
            lesson = lessonProfile
        }
    }
}

external interface LessonProfileProps : Props {
    var lesson: Lesson
}

val CPageLesson = FC<LessonProfileProps>("Profile lesson") { props ->
    h4 {
        +"Страница урока '${props.lesson.fullname()}'"
    }

    h3 {
        CAddTeacherToLesson {
            lessonAddTeacher = props.lesson
        }
    }
    h3 {
        CAddGroupToLesson {
            lessonAddGroup = props.lesson
        }
    }


    h2 { +"Преподаватели, добавленные на урок:"}
        div {
            ol {
                li {
                    +props.lesson.teacher
                }
            }
        }

    h2 {+"Группы, добавленные на урок:"}
    div {
        ol {
            props.lesson.groups.map {
                li {
                    +it.name
                }
            }
        }
    }
}