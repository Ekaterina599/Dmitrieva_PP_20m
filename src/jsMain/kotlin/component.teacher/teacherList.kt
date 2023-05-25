package component.teacher

import react.FC
import react.Props
import react.dom.html.ReactHTML.h3
import react.dom.html.ReactHTML.li
import react.dom.html.ReactHTML.ol
import react.router.dom.Link
import ru.altmanea.webapp.data.Teacher

external interface TeacherListProps : Props {
    var teachers: List<Teacher>
}

val CTeacherList = FC<TeacherListProps>("TeacherList") { props ->
    h3 { +"Преподаватели" }
    ol {
        props.teachers.forEach { teachers ->
            li {
                Link {
                    +teachers.fullname()
                    to = teachers._id
                }
            }
        }
    }
}

