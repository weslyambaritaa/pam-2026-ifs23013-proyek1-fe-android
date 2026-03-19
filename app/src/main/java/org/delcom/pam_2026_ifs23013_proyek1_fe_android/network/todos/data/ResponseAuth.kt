package org.delcom.pam_2026_ifs23013_proyek1_fe_android.network.todos.data

import kotlinx.serialization.Serializable

@Serializable
data class ResponseAuthRegister (
    val userId: String
)

@Serializable
data class ResponseAuthLogin (
    val authToken: String,
    val refreshToken: String
)