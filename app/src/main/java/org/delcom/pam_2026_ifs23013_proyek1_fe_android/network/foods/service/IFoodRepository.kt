package org.delcom.pam_2026_ifs23013_proyek1_fe_android.network.foods.service

import okhttp3.MultipartBody
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.network.data.ResponseMessage
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.network.foods.data.*

interface IFoodRepository {

    // ----------------------------------
    // Auth
    // ----------------------------------

    suspend fun postRegister(
        request: RequestAuthRegister
    ): ResponseMessage<ResponseAuthRegister?>

    suspend fun postLogin(
        request: RequestAuthLogin
    ): ResponseMessage<ResponseAuthLogin?>

    suspend fun postLogout(
        request: RequestAuthLogout
    ): ResponseMessage<String?>

    suspend fun postRefreshToken(
        request: RequestAuthRefreshToken
    ): ResponseMessage<ResponseAuthLogin?>

    // ----------------------------------
    // Users
    // ----------------------------------

    suspend fun getUserMe(
        authToken: String
    ): ResponseMessage<ResponseUser?>

    suspend fun putUserMe(
        authToken: String,
        request: RequestUserChange
    ): ResponseMessage<String?>

    suspend fun putUserMePassword(
        authToken: String,
        request: RequestUserChangePassword
    ): ResponseMessage<String?>

    suspend fun putUserMePhoto(
        authToken: String,
        file: MultipartBody.Part
    ): ResponseMessage<String?>

    // ----------------------------------
    // Foods
    // ----------------------------------

    suspend fun getFoods(
        authToken: String,
        search: String? = null
    ): ResponseMessage<ResponseFoods?>

    suspend fun postFood(
        authToken: String,
        request: RequestFood
    ): ResponseMessage<ResponseFoodAdd?>

    suspend fun getFoodById(
        authToken: String,
        foodId: String
    ): ResponseMessage<ResponseFood?>

    suspend fun putFood(
        authToken: String,
        foodId: String,
        request: RequestFood
    ): ResponseMessage<String?>

    // 🔥 PERBAIKAN DISINI
    suspend fun putFoodImage(
        authToken: String,
        foodId: String,
        file: MultipartBody.Part
    ): ResponseMessage<String?>

    suspend fun deleteFood(
        authToken: String,
        foodId: String
    ): ResponseMessage<String?>
}