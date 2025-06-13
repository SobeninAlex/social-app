package repository

import Response
import model.request.PostTextRequest
import model.response.PostResponse
import model.response.PostsResponse

interface PostRepository {

    suspend fun createPost(imageUrls: List<String>, postTextRequest: PostTextRequest): Response<PostResponse>

    suspend fun getFeedPosts(userId: String, page: Int, pageSize: Int): Response<PostsResponse>

    suspend fun getPostsByUser(userId: String, currentUserId: String, page: Int, pageSize: Int) : Response<PostsResponse>

    suspend fun getPost(postId: String, currentUserId: String): Response<PostResponse>

    suspend fun deletePost(postId: String): Response<PostResponse>
}