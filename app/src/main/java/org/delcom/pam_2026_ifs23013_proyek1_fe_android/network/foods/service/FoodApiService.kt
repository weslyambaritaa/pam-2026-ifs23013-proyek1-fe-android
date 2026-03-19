package org.delcom.pam_2026_ifs23013_proyek1_fe_android.network.foods.service

import okhttp3.MultipartBody
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.network.data.ResponseMessage
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.network.foods.data.*
import retrofit2.http.*

interface FoodApiService {

    // ----------------------------------
    // Auth
    // ----------------------------------

    @POST("auth/register")
    suspend fun postRegister(
        @Body request: RequestAuthRegister
    ): ResponseMessage<ResponseAuthRegister?>

    @POST("auth/login")
    suspend fun postLogin(
        @Body request: RequestAuthLogin
    ): ResponseMessage<ResponseAuthLogin?>

    @POST("auth/logout")
    suspend fun postLogout(
        @Body request: RequestAuthLogout
    ): ResponseMessage<String?>

    @POST("auth/refresh-token")
    suspend fun postRefreshToken(
        @Body request: RequestAuthRefreshToken
    ): ResponseMessage<ResponseAuthLogin?>

    // ----------------------------------
    // Users
    // ----------------------------------

    @GET("users/me")
    suspend fun getUserMe(
        @Header("Authorization") authToken: String
    ): ResponseMessage<ResponseUser?>

    @PUT("users/me")
    suspend fun putUserMe(
        @Header("Authorization") authToken: String,
        @Body request: RequestUserChange
    ): ResponseMessage<String?>

    @PUT("users/me/password")
    suspend fun putUserMePassword(
        @Header("Authorization") authToken: String,
        @Body request: RequestUserChangePassword
    ): ResponseMessage<String?>

    @Multipart
    @PUT("users/me/photo")
    suspend fun putUserMePhoto(
        @Header("Authorization") authToken: String,
        @Part file: MultipartBody.Part
    ): ResponseMessage<String?>

    // ----------------------------------
    // Foods
    // ----------------------------------

    @GET("foods")
    suspend fun getFoods(
        @Header("Authorization") authToken: String,
        @Query("search") search: String? = null
    ): ResponseMessage<ResponseFoods?>

    @POST("foods")
    suspend fun postFood(
        @Header("Authorization") authToken: String,
        @Body request: RequestFood
    ): ResponseMessage<ResponseFoodAdd?>

    @GET("foods/{foodId}")
    suspend fun getFoodById(
        @Header("Authorization") authToken: String,
        @Path("foodId") foodId: String
    ): ResponseMessage<ResponseFood?>

    @PUT("foods/{foodId}")
    suspend fun putFood(
        @Header("Authorization") authToken: String,
        @Path("foodId") foodId: String,
        @Body request: RequestFood
    ): ResponseMessage<String?>

    // 🔥 PERBAIKAN DISINI
    @Multipart
    @PUT("foods/{foodId}/image")
    suspend fun putFoodImage(
        @Header("Authorization") authToken: String,
        @Path("foodId") foodId: String,
        @Part file: MultipartBody.Part
    ): ResponseMessage<String?>

    @DELETE("foods/{foodId}")
    suspend fun deleteFood(
        @Header("Authorization") authToken: String,
        @Path("foodId") foodId: String
    ): ResponseMessage<String?>
}