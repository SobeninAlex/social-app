package glue.di

import glue.repository.AuthRepositoryImpl
import glue.repository.FollowersRepositoryImpl
import glue.repository.UserRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import repository.AuthRepository
import repository.FollowersRepository
import repository.UserRepository
import table.FollowsTable
import table.UserTable

val dataModule = module {
    single<UserTable> { UserTable }

    single<FollowsTable> { FollowsTable }

    singleOf(::AuthRepositoryImpl).bind<AuthRepository>()

    singleOf(::UserRepositoryImpl).bind<UserRepository>()

    singleOf(::FollowersRepositoryImpl).bind<FollowersRepository>()
}