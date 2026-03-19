package org.delcom.pam_2026_ifs23013_proyek1_fe_android.network.foods.data

import kotlinx.serialization.Serializable

@Serializable
data class RequestAuthRegister (
    val name: String,
    val username: String,
    val password: String
)

@Serializable
data class RequestAuthLogin (
    val username: String,
    val password: String
)

@Serializable
data class RequestAuthLogout (
    val authToken: String
)

@Serializable
data class RequestAuthRefreshToken (
    val authToken: String,
    val refreshToken: String,
)