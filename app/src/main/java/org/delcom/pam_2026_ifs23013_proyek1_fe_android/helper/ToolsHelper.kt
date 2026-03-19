package org.delcom.pam_2026_ifs23013_proyek1_fe_android.helper

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.BuildConfig
import java.io.File

object ToolsHelper {

    // -----------------------------
    // Food Image
    // -----------------------------
    fun getFoodImage(foodId: String, t: String = "0"): String{
        return "${BuildConfig.BASE_URL}images/foods/$foodId?t=$t"
    }

    // -----------------------------
    // Todo Image
    // -----------------------------
    fun getTodoImage(todoId: String, t: String? = "0"): String {
        // We use the safe call or provide a fallback for 't' (updatedAt)
        val timestamp = t ?: "0"
        return "${BuildConfig.BASE_URL}images/todos/$todoId?t=$timestamp"
    }

    // -----------------------------
    // User Image
    // -----------------------------
    fun getUserImage(userId: String, t: String = "0"): String{
        return "${BuildConfig.BASE_URL}images/users/$userId?t=$t"
    }

    // -----------------------------
    // Convert String → RequestBody
    // -----------------------------
    fun String.toRequestBodyText(): RequestBody {
        return this.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    // -----------------------------
    // Uri → Multipart
    // -----------------------------
    fun uriToMultipart(
        context: Context,
        uri: Uri,
        partName: String
    ): MultipartBody.Part {

        val file = uriToFile(context, uri)

        val requestFile = file
            .asRequestBody("image/*".toMediaTypeOrNull())

        return MultipartBody.Part.createFormData(
            partName,
            file.name,
            requestFile
        )
    }

    // -----------------------------
    // Uri → File
    // -----------------------------
    fun uriToFile(
        context: Context,
        uri: Uri
    ): File {

        val file = File.createTempFile(
            "upload",
            ".tmp",
            context.cacheDir
        )

        context.contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        return file
    }
}