package glue.di

import follows.FollowsDao
import org.koin.dsl.module
import post.PostDao
import post_like.PostLikeDao
import user.UserDao

val dataModule = module {
    factory<UserDao> { UserDao() }

    factory<FollowsDao> { FollowsDao() }

    factory<PostDao> { PostDao() }

    factory<PostLikeDao> { PostLikeDao() }
}