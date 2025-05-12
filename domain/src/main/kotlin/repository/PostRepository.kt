package repository

import Response
import model.request.PostTextRequest
import model.response.PostResponse
import model.response.PostsResponse

interface PostRepository {

    suspend fun createPost(imageUrl: String, postTextRequest: PostTextRequest): Response<PostResponse>

    suspend fun getFeedPosts(userId: String, pageNumber: Int, pageSize: Int): Response<PostsResponse>

    suspend fun getPostsByUser(userId: String, currentUserId: String, pageNumber: Int, pageSize: Int) : Response<PostsResponse>

    suspend fun getPost(postId: String, userId: String): Response<PostResponse>

    suspend fun getPost(postId: String): Response<PostResponse>

    suspend fun deletePost(postId: String): Response<PostResponse>
}