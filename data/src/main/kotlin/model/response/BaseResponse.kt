package model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BaseResponse(
    @SerialName("is_success") val isSuccess: Boolean,
    @SerialName("message") val message: String? = null,
)
