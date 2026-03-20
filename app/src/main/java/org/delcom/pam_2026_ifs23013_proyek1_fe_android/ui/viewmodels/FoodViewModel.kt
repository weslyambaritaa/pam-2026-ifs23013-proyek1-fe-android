package org.delcom.pam_2026_ifs23013_proyek1_fe_android.ui.viewmodels

import androidx.annotation.Keep
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.network.foods.data.ResponseFoodData
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.network.foods.service.IFoodRepository
import java.io.File
import javax.inject.Inject
import android.util.Log // Pastikan Anda meng-import Log

sealed interface FoodsUIState {
    data class Success(val data: List<ResponseFoodData>) : FoodsUIState
    data class Error(val message: String) : FoodsUIState
    object Loading : FoodsUIState
}

sealed interface FoodUIState {
    data class Success(val data: ResponseFoodData) : FoodUIState
    data class Error(val message: String) : FoodUIState
    object Loading : FoodUIState
}

sealed interface FoodActionUIState {
    data class Success(val message: String) : FoodActionUIState
    data class Error(val message: String) : FoodActionUIState
    object Loading : FoodActionUIState
}

data class UIStateFood(
    val foods: FoodsUIState = FoodsUIState.Loading,
    var food: FoodUIState = FoodUIState.Loading,
    var foodAdd: FoodActionUIState = FoodActionUIState.Loading,
    var foodChange: FoodActionUIState = FoodActionUIState.Loading,
    var foodDelete: FoodActionUIState = FoodActionUIState.Loading,
    var foodChangeImage: FoodActionUIState = FoodActionUIState.Loading
)

@HiltViewModel
@Keep
class FoodViewModel @Inject constructor(
    private val repository: IFoodRepository
) : ViewModel() {

    fun insertFood(name: String, price: Int, available: Int /* atau Boolean */) {

        // 1. TAMBAHKAN LOG DI SINI
        Log.d("StatusFood", "Nilai status sebelum dikirim: $available")

        viewModelScope.launch {
            // 2. Kode pemanggilan API di bawahnya (contoh)
            // val request = RequestFood(name, price, available)
            // repository.insertFood(request)
        }
    }

    private val _uiState = MutableStateFlow(UIStateFood())
    val uiState = _uiState.asStateFlow()

    fun getAllFoods(authToken: String, search: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(foods = FoodsUIState.Loading) }
            try {
                val response = repository.getFoods(authToken, search)
                if (response.status == "success") {
                    _uiState.update {
                        it.copy(foods = FoodsUIState.Success(response.data!!.foods))
                    }
                } else {
                    _uiState.update {
                        it.copy(foods = FoodsUIState.Error(response.message))
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(foods = FoodsUIState.Error(e.message ?: "Unknown error"))
                }
            }
        }
    }

    fun getFoodById(authToken: String, foodId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(food = FoodUIState.Loading) }
            try {
                val response = repository.getFoodById(authToken, foodId)
                if (response.status == "success") {
                    _uiState.update {
                        it.copy(food = FoodUIState.Success(response.data!!.food))
                    }
                } else {
                    _uiState.update {
                        it.copy(food = FoodUIState.Error(response.message))
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(food = FoodUIState.Error(e.message ?: "Unknown error"))
                }
            }
        }
    }

    // 🔴 PERUBAHAN: Ditambahkan parameter quantity, available, dan imageFile
    fun postFood(
        authToken: String,
        name: String,
        description: String,
        price: Int,
        quantity: Int,
        category: String,
        available: Boolean = true,
        imageFile: File? = null
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(foodAdd = FoodActionUIState.Loading) }
            try {
                // 1. Post data teks
                val response = repository.postFood(
                    authToken,
                    RequestFood(
                        name = name,
                        description = description,
                        price = price,
                        quantity = quantity,
                        category = category,
                        available = available
                    )
                )

                if (response.status == "success") {
                    // 2. Jika sukses dan ada gambar, lakukan proses upload
                    if (imageFile != null && response.data != null) {
                        val foodId = response.data!!.foodId

                        val requestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                        val multipartBody = MultipartBody.Part.createFormData("file", imageFile.name, requestBody)

                        val imageResponse = repository.putFoodImage(authToken, foodId, multipartBody)

                        // Cek status upload image
                        if (imageResponse.status != "success") {
                            _uiState.update {
                                it.copy(foodAdd = FoodActionUIState.Error("Makanan tersimpan, tapi gagal upload foto: ${imageResponse.message}"))
                            }
                            return@launch
                        }
                    }

                    _uiState.update {
                        it.copy(foodAdd = FoodActionUIState.Success(response.message))
                    }
                } else {
                    _uiState.update {
                        it.copy(foodAdd = FoodActionUIState.Error(response.message))
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(foodAdd = FoodActionUIState.Error(e.message ?: "Unknown error"))
                }
            }
        }
    }

    // 🔴 PERUBAHAN: Ditambahkan parameter quantity dan imageFile
    fun putFood(
        authToken: String,
        foodId: String,
        name: String,
        description: String,
        price: Int,
        quantity: Int,
        category: String,
        available: Boolean,
        imageFile: File? = null
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(foodChange = FoodActionUIState.Loading) }
            try {
                // 1. Update data teks
                val response = repository.putFood(
                    authToken,
                    foodId,
                    RequestFood(
                        name = name,
                        description = description,
                        price = price,
                        quantity = quantity,
                        category = category,
                        available = available
                    )
                )

                if (response.status == "success") {
                    // 2. Jika sukses dan user memilih gambar baru, lakukan upload
                    if (imageFile != null) {
                        val requestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                        val multipartBody = MultipartBody.Part.createFormData("file", imageFile.name, requestBody)

                        val imageResponse = repository.putFoodImage(authToken, foodId, multipartBody)

                        if (imageResponse.status != "success") {
                            _uiState.update {
                                it.copy(foodChange = FoodActionUIState.Error("Data tersimpan, tapi gagal ganti foto: ${imageResponse.message}"))
                            }
                            return@launch
                        }
                    }

                    _uiState.update {
                        it.copy(foodChange = FoodActionUIState.Success(response.message))
                    }
                } else {
                    _uiState.update {
                        it.copy(foodChange = FoodActionUIState.Error(response.message))
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(foodChange = FoodActionUIState.Error(e.message ?: "Unknown error"))
                }
            }
        }
    }

    fun deleteFood(authToken: String, foodId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(foodDelete = FoodActionUIState.Loading) }
            try {
                val response = repository.deleteFood(authToken, foodId)
                if (response.status == "success") {
                    _uiState.update {
                        it.copy(foodDelete = FoodActionUIState.Success(response.message))
                    }
                } else {
                    _uiState.update {
                        it.copy(foodDelete = FoodActionUIState.Error(response.message))
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(foodDelete = FoodActionUIState.Error(e.message ?: "Unknown error"))
                }
            }
        }
    }

    // Fungsi bawaan (standalone image upload) tetap dibiarkan
    // jika kamu butuh memanggilnya secara terpisah (tanpa submit form penuh).
    fun putFoodImage(
        authToken: String,
        foodId: String,
        file: MultipartBody.Part
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(foodChangeImage = FoodActionUIState.Loading) }
            try {
                val response = repository.putFoodImage(authToken, foodId, file)
                if (response.status == "success") {
                    _uiState.update {
                        it.copy(foodChangeImage = FoodActionUIState.Success(response.message))
                    }
                } else {
                    _uiState.update {
                        it.copy(foodChangeImage = FoodActionUIState.Error(response.message))
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(foodChangeImage = FoodActionUIState.Error(e.message ?: "Unknown error"))
                }
            }
        }
    }
}