package org.delcom.pam_2026_ifs23013_proyek1_fe_android.network.data

import kotlinx.serialization.Serializable

@Serializable
data class ResponseMessage<T>(
    val status: String,
    val message: String,
    val data: T? = null
)