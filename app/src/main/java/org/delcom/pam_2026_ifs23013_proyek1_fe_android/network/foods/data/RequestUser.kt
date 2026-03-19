package org.delcom.pam_2026_ifs23013_proyek1_fe_android.network.foods.data

import kotlinx.serialization.Serializable

@Serializable
data class RequestUserChange (
    val name: String,
    val username: String
)

@Serializable
data class RequestUserChangePassword (
    val newPassword: String,
    val password: String
)