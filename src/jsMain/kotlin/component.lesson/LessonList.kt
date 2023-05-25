package component.lesson

import react.FC
import react.Props
import react.dom.html.ReactHTML.h3
import react.dom.html.ReactHTML.li
import react.dom.html.ReactHTML.ol
import react.router.dom.Link
import ru.altmanea.webapp.data.Lesson

external interface LessonListProps : Props {
    var lessons: List<Lesson>
}

val CLessonList = FC<LessonListProps>("Lessons") { props ->
    h3 { +"Уроки" }
    ol {
        props.lessons.forEach { lessons ->
            li {
                Link {
                    +lessons.fullname()
                    to = lessons._id
                }
            }
        }
    }
}

