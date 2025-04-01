package glue.domain

import org.koin.dsl.module
import repository.UserRepository

val domainModule = module {
    single<UserRepository> {
        UserRepositoryImpl(
            userDao = get()
        )
    }
}