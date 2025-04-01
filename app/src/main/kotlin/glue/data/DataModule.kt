package glue.data

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import user.UserDao

val dataModule = module {
    singleOf(::UserDaoImp).bind<UserDao>()
}