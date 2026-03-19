package org.delcom.pam_2026_ifs23013_proyek1_fe_android.network.foods.service

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.BuildConfig
import java.util.concurrent.TimeUnit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory

class FoodAppContainer: IFoodAppContainer {
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    val okHttpClient = OkHttpClient.Builder().apply {
        if (BuildConfig.DEBUG) {
            addInterceptor(loggingInterceptor)
        }

        connectTimeout(2, TimeUnit.MINUTES)
        readTimeout(2, TimeUnit.MINUTES)
        writeTimeout(2, TimeUnit.MINUTES)
    }.build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    private val retrofitService: FoodApiService by lazy {
        retrofit.create(FoodApiService::class.java)
    }

    override val repository: IFoodRepository by lazy {
        FoodRepository(retrofitService)
    }
}