package org.delcom.pam_2026_ifs23013_proyek1_fe_android.network.foods.data

import kotlinx.serialization.SerialName
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
    @SerialName("quantity") val quantity: Int = 0, // 🔥 Pastikan ada quantity
    val category: String,
    val imageUrl: String? = null,
    @SerialName("is_available") val isAvailable: Boolean = true, // 🔥 Kunci nama JSON
    val createdAt: String = "",
    val updatedAt: String = ""
)

@Serializable
data class ResponseFoodAdd(
    val foodId: String
)