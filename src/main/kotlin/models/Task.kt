package com.example.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Task(
    val id: Long,
    val title: String,
    val completed: Boolean = false,
    @SerialName("created_at")
    val createdAt: String
)

@Serializable
data class CreateTaskRequest(
    val title: String
)

@Serializable
data class UpdateTaskRequest(
    val title: String? = null,
    val completed: Boolean? = null
)

@Serializable
data class TaskUpdate(
    val title: String? = null,
    val completed: Boolean? = null
)
