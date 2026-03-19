package org.delcom.pam_2026_ifs23013_proyek1_fe_android.network.foods.data

import kotlinx.serialization.Serializable

@Serializable
data class ResponseFoods(
    val foods: List<ResponseFoodData>
)

@Serializable
data class ResponseFood(
    val food: ResponseFoodData
)

@Serializable
data class ResponseFoodData(
    val id: String = "",
    val name: String,
    val description: String,
    val price: Int,
    val category: String,
    val imageUrl: String? = null,
    val isAvailable: Boolean = true,
    val createdAt: String = "",
    val updatedAt: String = ""
)

@Serializable
data class ResponseFoodAdd(
    val foodId: String
)