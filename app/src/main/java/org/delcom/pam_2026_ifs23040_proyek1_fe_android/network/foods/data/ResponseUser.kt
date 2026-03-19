package org.delcom.pam_proyek1_ifs23013.network.foods.data

import kotlinx.serialization.Serializable

@Serializable
data class ResponseUser (
    val user: ResponseUserData
)

@Serializable
data class ResponseUserData(
    val id: String,
    val name: String,
    val username: String,
    val createdAt: String,
    val updatedAt: String
)