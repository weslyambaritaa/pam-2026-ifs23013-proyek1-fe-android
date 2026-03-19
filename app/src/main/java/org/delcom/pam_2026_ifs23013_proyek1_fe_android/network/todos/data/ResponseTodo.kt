package org.delcom.pam_2026_ifs23013_proyek1_fe_android.network.todos.data

import kotlinx.serialization.Serializable

@Serializable
data class ResponseTodos (
    val todos: List<ResponseTodoData>
)

@Serializable
data class ResponseTodo (
    val todo: ResponseTodoData
)

@Serializable
data class ResponseTodoData(
    val id: String = "",
    val userId: String = "",
    val title: String,
    val description: String,
    val isDone: Boolean = false,
    val cover: String? = null,
    val createdAt: String = "",
    var updatedAt: String = ""
)

@Serializable
data class ResponseTodoAdd (
    val todoId: String
)