package org.delcom.pam_2026_ifs23013_proyek1_fe_android.ui.viewmodels

import androidx.annotation.Keep
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.network.foods.data.RequestFood
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.network.foods.data.ResponseFoodData
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.network.foods.service.IFoodRepository
import javax.inject.Inject

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

    private val _uiState = MutableStateFlow(UIStateFood())
    val uiState = _uiState.asStateFlow()

    fun getAllFoods(authToken: String, search: String? = null) {

        viewModelScope.launch {

            _uiState.update { it.copy(foods = FoodsUIState.Loading) }

            try {

                val response = repository.getFoods(authToken, search)

                if (response.status == "success") {

                    _uiState.update {
                        it.copy(
                            foods = FoodsUIState.Success(response.data!!.foods)
                        )
                    }

                } else {

                    _uiState.update {
                        it.copy(
                            foods = FoodsUIState.Error(response.message)
                        )
                    }
                }

            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                        foods = FoodsUIState.Error(e.message ?: "Unknown error")
                    )
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
                        it.copy(
                            food = FoodUIState.Success(response.data!!.food)
                        )
                    }

                } else {

                    _uiState.update {
                        it.copy(
                            food = FoodUIState.Error(response.message)
                        )
                    }
                }

            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                        food = FoodUIState.Error(e.message ?: "Unknown error")
                    )
                }
            }
        }
    }

    fun postFood(
        authToken: String,
        name: String,
        description: String,
        price: Int,
        category: String
    ) {

        viewModelScope.launch {

            _uiState.update { it.copy(foodAdd = FoodActionUIState.Loading) }

            try {

                val response = repository.postFood(
                    authToken,
                    RequestFood(
                        name,
                        description,
                        price,
                        category,
                        true
                    )
                )

                if (response.status == "success") {

                    _uiState.update {
                        it.copy(
                            foodAdd = FoodActionUIState.Success(response.message)
                        )
                    }

                } else {

                    _uiState.update {
                        it.copy(
                            foodAdd = FoodActionUIState.Error(response.message)
                        )
                    }
                }

            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                        foodAdd = FoodActionUIState.Error(e.message ?: "Unknown error")
                    )
                }
            }
        }
    }

    fun putFood(
        authToken: String,
        foodId: String,
        name: String,
        description: String,
        price: Int,
        category: String,
        isAvailable: Boolean
    ) {

        viewModelScope.launch {

            _uiState.update { it.copy(foodChange = FoodActionUIState.Loading) }

            try {

                val response = repository.putFood(
                    authToken,
                    foodId,
                    RequestFood(
                        name,
                        description,
                        price,
                        category,
                        isAvailable
                    )
                )

                if (response.status == "success") {

                    _uiState.update {
                        it.copy(
                            foodChange = FoodActionUIState.Success(response.message)
                        )
                    }

                } else {

                    _uiState.update {
                        it.copy(
                            foodChange = FoodActionUIState.Error(response.message)
                        )
                    }
                }

            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                        foodChange = FoodActionUIState.Error(e.message ?: "Unknown error")
                    )
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
                        it.copy(
                            foodDelete = FoodActionUIState.Success(response.message)
                        )
                    }

                } else {

                    _uiState.update {
                        it.copy(
                            foodDelete = FoodActionUIState.Error(response.message)
                        )
                    }
                }

            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                        foodDelete = FoodActionUIState.Error(e.message ?: "Unknown error")
                    )
                }
            }
        }
    }

    fun putFoodImage(
        authToken: String,
        foodId: String,
        file: MultipartBody.Part
    ) {

        viewModelScope.launch {

            _uiState.update { it.copy(foodChangeImage = FoodActionUIState.Loading) }

            try {

                val response = repository.putFoodImage(
                    authToken,
                    foodId,
                    file
                )

                if (response.status == "success") {

                    _uiState.update {
                        it.copy(
                            foodChangeImage = FoodActionUIState.Success(response.message)
                        )
                    }

                } else {

                    _uiState.update {
                        it.copy(
                            foodChangeImage = FoodActionUIState.Error(response.message)
                        )
                    }
                }

            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                        foodChangeImage = FoodActionUIState.Error(e.message ?: "Unknown error")
                    )
                }
            }
        }
    }
}