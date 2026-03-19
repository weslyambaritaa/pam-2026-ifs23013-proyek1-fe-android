package org.delcom.pam_proyek1_ifs23013.network.foods.data

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