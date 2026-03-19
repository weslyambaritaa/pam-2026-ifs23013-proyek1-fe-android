package org.delcom.pam_2026_ifs23013_proyek1_fe_android.network.foods.data

import kotlinx.serialization.Serializable

@Serializable
data class RequestFood(
    val name: String,
    val description: String,
    val price: Int,
    val category: String,
    val isAvailable: Boolean = true
)