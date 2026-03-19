package org.delcom.pam_2026_ifs23013_proyek1_fe_android.network.foods.service

import okhttp3.MultipartBody
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.helper.SuspendHelper
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.network.data.ResponseMessage
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.network.foods.data.*

class FoodRepository(
    private val apiService: FoodApiService
) : IFoodRepository {

    // ----------------------------------
    // Auth
    // ----------------------------------

    override suspend fun postRegister(
        request: RequestAuthRegister
    ): ResponseMessage<ResponseAuthRegister?> {
        return SuspendHelper.safeApiCall {
            apiService.postRegister(request)
        }
    }

    override suspend fun postLogin(
        request: RequestAuthLogin
    ): ResponseMessage<ResponseAuthLogin?> {
        return SuspendHelper.safeApiCall {
            apiService.postLogin(request)
        }
    }

    override suspend fun postLogout(
        request: RequestAuthLogout
    ): ResponseMessage<String?> {
        return SuspendHelper.safeApiCall {
            apiService.postLogout(request)
        }
    }

    override suspend fun postRefreshToken(
        request: RequestAuthRefreshToken
    ): ResponseMessage<ResponseAuthLogin?> {
        return SuspendHelper.safeApiCall {
            apiService.postRefreshToken(request)
        }
    }

    // ----------------------------------
    // Users
    // ----------------------------------

    override suspend fun getUserMe(
        authToken: String
    ): ResponseMessage<ResponseUser?> {
        return SuspendHelper.safeApiCall {
            apiService.getUserMe("Bearer $authToken")
        }
    }

    override suspend fun putUserMe(
        authToken: String,
        request: RequestUserChange
    ): ResponseMessage<String?> {
        return SuspendHelper.safeApiCall {
            apiService.putUserMe("Bearer $authToken", request)
        }
    }

    override suspend fun putUserMePassword(
        authToken: String,
        request: RequestUserChangePassword
    ): ResponseMessage<String?> {
        return SuspendHelper.safeApiCall {
            apiService.putUserMePassword("Bearer $authToken", request)
        }
    }

    override suspend fun putUserMePhoto(
        authToken: String,
        file: MultipartBody.Part
    ): ResponseMessage<String?> {
        return SuspendHelper.safeApiCall {
            apiService.putUserMePhoto("Bearer $authToken", file)
        }
    }

    // ----------------------------------
    // Foods
    // ----------------------------------

    override suspend fun getFoods(
        authToken: String,
        search: String?
    ): ResponseMessage<ResponseFoods?> {
        return SuspendHelper.safeApiCall {
            apiService.getFoods("Bearer $authToken", search)
        }
    }

    override suspend fun postFood(
        authToken: String,
        request: RequestFood
    ): ResponseMessage<ResponseFoodAdd?> {
        return SuspendHelper.safeApiCall {
            apiService.postFood("Bearer $authToken", request)
        }
    }

    override suspend fun getFoodById(
        authToken: String,
        foodId: String
    ): ResponseMessage<ResponseFood?> {
        return SuspendHelper.safeApiCall {
            apiService.getFoodById("Bearer $authToken", foodId)
        }
    }

    override suspend fun putFood(
        authToken: String,
        foodId: String,
        request: RequestFood
    ): ResponseMessage<String?> {
        return SuspendHelper.safeApiCall {
            apiService.putFood("Bearer $authToken", foodId, request)
        }
    }

    // 🔥 PERBAIKAN DISINI
    // 🔥 PERBAIKAN DISINI
    override suspend fun putFoodImage(
        token: String,
        foodId: String,
        file: MultipartBody.Part
    ): ResponseMessage<String?> {
        return SuspendHelper.safeApiCall {
            // Wajib menggunakan "Bearer $token" agar Ktor bisa mengenalinya
            apiService.putFoodImage("Bearer $token", foodId, file)
        }
    }

    override suspend fun deleteFood(
        authToken: String,
        foodId: String
    ): ResponseMessage<String?> {
        return SuspendHelper.safeApiCall {
            apiService.deleteFood("Bearer $authToken", foodId)
        }
    }
}