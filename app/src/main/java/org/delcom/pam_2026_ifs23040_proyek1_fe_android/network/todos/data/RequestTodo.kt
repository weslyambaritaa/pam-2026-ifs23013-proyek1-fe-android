package org.delcom.pam_proyek1_ifs23013.network.todos.data

import kotlinx.serialization.Serializable

@Serializable
data class RequestTodo (
    val title: String,
    val description: String,
    val isDone: Boolean = false
)
