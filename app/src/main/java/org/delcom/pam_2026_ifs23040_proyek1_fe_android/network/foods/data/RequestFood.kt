package org.delcom.pam_proyek1_ifs23013.network.foods.data

import kotlinx.serialization.Serializable

@Serializable
data class RequestFood(
    val name: String,
    val description: String,
    val price: Int,
    val category: String,
    val isAvailable: Boolean = true
)