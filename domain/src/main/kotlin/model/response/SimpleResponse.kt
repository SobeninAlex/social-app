package model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SimpleResponse(
    @SerialName("is_success") val isSuccess: Boolean,
    @SerialName("error_message") val errorMessage: String? = null,
)
