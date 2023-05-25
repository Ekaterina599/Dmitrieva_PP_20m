package component.lesson

import react.FC
import react.Props
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h4
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.label
import react.dom.html.ReactHTML.option
import react.dom.html.ReactHTML.select
import react.useRef
import ru.altmanea.webapp.data.Lesson
import web.html.HTMLInputElement
import web.html.HTMLSelectElement

external interface AddLessonProps: Props {
    var addLesson: (Lesson) -> Unit
}

val CAddLesson = FC<AddLessonProps>("AddLesson") { props ->
    val lessonNameRef = useRef<HTMLInputElement>()
    val lessonTypeRef = useRef<HTMLSelectElement>()
    val lessonHoursRef = useRef<HTMLInputElement>()

    h4 { +"Добавить новый урок:" }
    div {
        div {
            label { +"Название" }
            input { ref = lessonNameRef }
        }
        div {
            label { +"Тип урока" }
            select {
                ref = lessonTypeRef
                listOf("Лекция", "Лабораторная", "Практика").map {
                    option {
                        +it
                        value = it
                    }
                }
            }
        }
        div {
            label { +"Количество часов" }
            input { ref = lessonHoursRef }
        }
        button {
            +"Add"
            onClick = {
                lessonNameRef.current?.value?.let { name ->
                    lessonTypeRef.current?.value?.let { type->
                        lessonHoursRef.current?.value?.let { hours ->
                            props.addLesson(Lesson(name.capitalize(), type, hours.toInt(), "", emptyList(), "", 0))
                        }
                    }
                }
            }
        }
    }
}
