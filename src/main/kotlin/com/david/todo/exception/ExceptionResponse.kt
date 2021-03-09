package com.david.todo.exception

import kotlinx.serialization.Serializable

@Serializable
data class ExceptionResponse(
    val code: Int,
    val exception: String,
    val description: String
)