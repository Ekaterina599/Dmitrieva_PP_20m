
import auth.authProvider
import component.group.CPageGroup
import component.group.ContainerGroupList
import component.group.pageGroupContainer
import component.lesson.ContainerLessonList
import component.lesson.pageLessonConatiner
import component.student.pageStudentContainer
import component.student.studentContainer
import component.teacher.ContainerTeacherList
import component.teacher.pageTeacherContainer
/*import component.lesson.fcContainerLessonList
import component.student.studentContainer
import component.teacher.fcContainerTeacherList*/

import csstype.*
import emotion.react.css
import react.FC
import react.Props
import react.create
import react.createContext
import react.dom.client.createRoot
import react.router.Route
import react.router.Routes
import react.router.dom.HashRouter
import react.router.dom.Link
import ru.altmanea.webapp.access.Token
import ru.altmanea.webapp.access.User
import ru.altmanea.webapp.config.Config
import tanstack.query.core.QueryClient
import tanstack.query.core.QueryKey
import tanstack.react.query.QueryClientProvider
import web.dom.document

typealias  UserInfo = Pair<User, Token>?

val invalidateRepoKey = createContext<QueryKey>()
val userInfoContext = createContext<UserInfo>(null)

fun main() {
    val container = document.getElementById("root")!!
    createRoot(container).render(app.create())
}

val app = FC<Props> ("App") {
    HashRouter {
        authProvider {
            QueryClientProvider {
                client = QueryClient()
                //--------------Cтуденты--------------//
                Link {
                    css {
                        background = NamedColor.lightpink
                        padding = Padding(vertical = 10.px, horizontal = 10.px)
                        border = Border(width = 2.px, style = LineStyle.solid)
                    }
                    +"Cтуденты"
                    to = Config.studentsPath
                }
                //--------------Группы--------------//
                Link {
                    css {
                        background = NamedColor.aqua
                        padding = Padding(vertical = 10.px, horizontal = 10.px)
                        border = Border(width = 2.px, style = LineStyle.solid)
                    }
                    +"Группы"
                    to = Config.groupsPath
                }
                //--------------Уроки--------------//
                Link {
                    css {
                        background = NamedColor.lightgreen
                        padding = Padding(vertical = 10.px, horizontal = 10.px)
                        border = Border(width = 2.px, style = LineStyle.solid)
                    }
                    +"Уроки"
                    to = Config.lessonsPath
                }
                //--------------Учителя--------------//
                Link {
                    css {
                        background = NamedColor.lightsteelblue
                        padding = Padding(vertical = 10.px, horizontal = 10.px)
                        border = Border(width = 2.px, style = LineStyle.solid)
                    }
                    +"Преподаватели"
                    to = Config.teachersPath
                }

                Routes {
                    //--------------Список всех студентов--------------//
                    Route {
                        path = Config.studentsPath
                        element = studentContainer.create()

                    }
                    //--------------Профиль студента--------------//
                    Route {
                        path = Config.studentsPath + ":id"
                        element = pageStudentContainer.create()
                    }
                    //--------------Список всех групп--------------//
                    Route {
                        path = Config.groupsPath
                        element = ContainerGroupList.create()

                    }
                    //--------------Профиль группы--------------//
                    Route {
                        path = Config.groupsPath + ":group"
                        element = pageGroupContainer.create()
                    }
                    //--------------Список всех уроков--------------//
                    Route {
                        path = Config.lessonsPath
                        element = ContainerLessonList.create()
                    }
                    //--------------Профиль урока--------------//
                    Route {
                        path = Config.lessonsPath + ":id"
                        element = pageLessonConatiner.create()
                    }
                    //--------------Список всех преподавателей--------------//
                    Route {
                        path = Config.teachersPath
                        element = ContainerTeacherList.create()
                    }
                    //--------------Профиль преподавателя--------------//
                    Route {
                        path = Config.teachersPath + ":id"
                        element = pageTeacherContainer.create()
                    }
                }
            }
        }
    }
}