package org.delcom.pam_2026_ifs23013_proyek1_fe_android.network.todos.data

import kotlinx.serialization.Serializable

@Serializable
data class RequestTodo (
    val title: String,
    val description: String,
    val isDone: Boolean = false
)
