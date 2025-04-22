package glue.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import repository.AuthRepository
import glue.repository.AuthRepositoryImpl
import glue.repository.UserRepositoryImpl
import glue.repository.FollowersRepositoryImpl
import glue.repository.PostCommentsRepositoryImpl
import glue.repository.PostLikesRepositoryImpl
import glue.repository.PostRepositoryImpl
import repository.FollowersRepository
import repository.PostCommentsRepository
import repository.PostLikesRepository
import repository.PostRepository
import repository.UserRepository

val domainModule = module {
    singleOf(::AuthRepositoryImpl).bind<AuthRepository>()

    singleOf(::UserRepositoryImpl).bind<UserRepository>()

    singleOf(::FollowersRepositoryImpl).bind<FollowersRepository>()

    singleOf(::PostRepositoryImpl).bind<PostRepository>()

    singleOf(::PostCommentsRepositoryImpl).bind<PostCommentsRepository>()

    singleOf(::PostLikesRepositoryImpl).bind<PostLikesRepository>()
}