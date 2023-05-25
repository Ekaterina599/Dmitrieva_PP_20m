package component.teacher

import react.FC
import react.Props
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h4
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.label
import react.useRef
import ru.altmanea.webapp.data.Teacher
import web.html.HTMLInputElement

external interface AddTeacherProps: Props {
    var addTeacher: (Teacher) -> Unit
}

val CAddTeacher = FC<AddTeacherProps>("AddTeacher") { props ->
    val firstnameRef = useRef<HTMLInputElement>()
    val lastnameRef = useRef<HTMLInputElement>()
    val patronymicRef = useRef<HTMLInputElement>()
    h4 { +"Добавить нового преподавателя:" }
    div {
        div {
            label { +"Фамилия" }
            input { ref = lastnameRef }
        }
        div {
            label { +"Имя " }
            input { ref = firstnameRef }
        }
        div {
            label { +"Отчетство" }
            input { ref = patronymicRef }
        }
        button {
            +"Добавить преподавателя"
            onClick = {
                firstnameRef.current?.value?.let { firstname ->
                    lastnameRef.current?.value?.let { lastname->
                        patronymicRef.current?.value?.let { patronymic ->
                            val regex = "^[А-ЯЁ][а-яё]*\$".toRegex()
                            if(firstname.matches(regex) && lastname.matches(regex) && patronymic.matches(regex)) {
                                props.addTeacher(
                                    Teacher(
                                        firstname,
                                        lastname,
                                        patronymic,
                                        "",
                                        0
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
