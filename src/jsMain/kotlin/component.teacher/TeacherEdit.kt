package component.teacher

import react.FC
import react.Props
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.span
import react.useRef
import ru.altmanea.webapp.data.Teacher
import web.html.HTMLInputElement

external interface EditTeacherProps : Props {
    var oldTeacher: Teacher
    var saveTeacher: (Pair<Teacher, Long>) -> Unit
}

val CEditTeacher = FC<EditTeacherProps>("Edit teacher") { props ->
    val firstnameRef = useRef<HTMLInputElement>()
    val surnameRef = useRef<HTMLInputElement>()
    val patronymicRef = useRef<HTMLInputElement>()
    span {
        input {
            placeholder = "Фамилия"
            ref = surnameRef
        }
        input {
            placeholder = "Имя"
            ref = firstnameRef
        }
        input {
            placeholder = "Отчество"
            ref = patronymicRef
        }

    }
    button {
        +"✓"
        onClick = {
            firstnameRef.current?.value?.let { firstname ->
                surnameRef.current?.value?.let { surname ->
                    patronymicRef.current?.value?.let { patronymic ->
                        val regex = "^[А-ЯЁ][а-яё]*\$".toRegex()
                        if (firstname.matches(regex) && surname.matches(regex) && patronymic.matches(regex)) {
                            props.saveTeacher(
                                Pair(Teacher(surname, firstname, patronymic, props.oldTeacher._id, 0),
                                    props.oldTeacher.version)
                            )
                        }
                    }
                }
            }
        }
    }
}
