package org.delcom.pam_proyek1_ifs23013.ui.screens.foods

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.delcom.pam_proyek1_ifs23013.helper.*
import org.delcom.pam_proyek1_ifs23013.network.foods.data.ResponseFoodData
import org.delcom.pam_proyek1_ifs23013.ui.components.BottomNavComponent
import org.delcom.pam_proyek1_ifs23013.ui.components.LoadingUI
import org.delcom.pam_proyek1_ifs23013.ui.components.TopAppBarComponent
import org.delcom.pam_proyek1_ifs23013.ui.viewmodels.*

@Composable
fun FoodsEditScreen(
    navController: NavHostController,
    snackbarHost: SnackbarHostState,
    authViewModel: AuthViewModel,
    foodViewModel: FoodViewModel,
    foodId: String
) {

    val uiStateAuth by authViewModel.uiState.collectAsState()
    val uiStateFood by foodViewModel.uiState.collectAsState()

    var isLoading by remember { mutableStateOf(false) }

    var food by remember { mutableStateOf<ResponseFoodData?>(null) }
    val authToken = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {

        isLoading = true

        if (uiStateAuth.auth !is AuthUIState.Success) {

            RouteHelper.to(
                navController,
                ConstHelper.RouteNames.Home.path,
                true
            )

            return@LaunchedEffect
        }

        authToken.value =
            (uiStateAuth.auth as AuthUIState.Success).data.authToken

        foodViewModel.getFoodById(authToken.value!!, foodId)
    }

    LaunchedEffect(uiStateFood.food) {

        if (uiStateFood.food !is FoodUIState.Loading) {

            if (uiStateFood.food is FoodUIState.Success) {

                food = (uiStateFood.food as FoodUIState.Success).data
                isLoading = false

            } else {

                RouteHelper.back(navController)
                isLoading = false
            }
        }
    }

    fun onSave(
        name: String,
        description: String,
        price: Int,
        category: String,
        isAvailable: Boolean
    ) {

        isLoading = true

        foodViewModel.putFood(
            authToken = authToken.value!!,
            foodId = foodId,
            name = name,
            description = description,
            price = price,
            category = category,
            isAvailable = isAvailable
        )
    }

    LaunchedEffect(uiStateFood.foodChange) {

        when (val state = uiStateFood.foodChange) {

            is FoodActionUIState.Success -> {

                SuspendHelper.showSnackBar(
                    snackbarHost = snackbarHost,
                    type = SuspendHelper.SnackBarType.SUCCESS,
                    message = state.message
                )

                RouteHelper.to(
                    navController,
                    ConstHelper.RouteNames.FoodsDetail.path
                        .replace("{foodId}", foodId),
                    true
                )

                isLoading = false
            }

            is FoodActionUIState.Error -> {

                SuspendHelper.showSnackBar(
                    snackbarHost = snackbarHost,
                    type = SuspendHelper.SnackBarType.ERROR,
                    message = state.message
                )

                isLoading = false
            }

            else -> {}
        }
    }

    if (isLoading || food == null) {

        LoadingUI()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {

        TopAppBarComponent(
            navController = navController,
            title = "Ubah Food",
            showBackButton = true
        )

        Box(
            modifier = Modifier.weight(1f)
        ) {

            FoodsEditUI(
                food = food!!,
                onSave = ::onSave
            )
        }

        BottomNavComponent(navController = navController)
    }
}

@Composable
fun FoodsEditUI(
    food: ResponseFoodData,
    onSave: (
        String,
        String,
        Int,
        String,
        Boolean
    ) -> Unit
) {

    val alertState = remember { mutableStateOf(AlertState()) }

    var dataName by remember { mutableStateOf(food.name) }
    var dataDescription by remember { mutableStateOf(food.description) }
    var dataPrice by remember { mutableStateOf(food.price.toString()) }
    var dataCategory by remember { mutableStateOf(food.category) }
    var dataAvailable by remember { mutableStateOf(food.isAvailable) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        OutlinedTextField(
            value = dataName,
            onValueChange = { dataName = it },
            label = { Text("Nama Food") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )

        OutlinedTextField(
            value = dataPrice,
            onValueChange = { dataPrice = it },
            label = { Text("Price") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            )
        )

        OutlinedTextField(
            value = dataCategory,
            onValueChange = { dataCategory = it },
            label = { Text("Category") },
            modifier = Modifier.fillMaxWidth()
        )

        Column {

            Text("Available?")

            Row(verticalAlignment = Alignment.CenterVertically) {

                RadioButton(
                    selected = dataAvailable,
                    onClick = { dataAvailable = true }
                )

                Text("Yes")

                Spacer(modifier = Modifier.width(16.dp))

                RadioButton(
                    selected = !dataAvailable,
                    onClick = { dataAvailable = false }
                )

                Text("No")
            }
        }

        OutlinedTextField(
            value = dataDescription,
            onValueChange = { dataDescription = it },
            label = { Text("Description") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            maxLines = 5
        )

        Spacer(modifier = Modifier.height(80.dp))
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        FloatingActionButton(
            onClick = {

                if (dataName.isEmpty()) {

                    AlertHelper.show(
                        alertState,
                        AlertType.ERROR,
                        "Nama food tidak boleh kosong!"
                    )

                    return@FloatingActionButton
                }

                if (dataDescription.isEmpty()) {

                    AlertHelper.show(
                        alertState,
                        AlertType.ERROR,
                        "Deskripsi tidak boleh kosong!"
                    )

                    return@FloatingActionButton
                }

                onSave(
                    dataName,
                    dataDescription,
                    dataPrice.toInt(),
                    dataCategory,
                    dataAvailable
                )
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {

            Icon(
                imageVector = Icons.Default.Save,
                contentDescription = "Save Food"
            )
        }
    }

    if (alertState.value.isVisible) {

        AlertDialog(
            onDismissRequest = {
                AlertHelper.dismiss(alertState)
            },
            title = {
                Text(alertState.value.type.title)
            },
            text = {
                Text(alertState.value.message)
            },
            confirmButton = {

                TextButton(
                    onClick = {
                        AlertHelper.dismiss(alertState)
                    }
                ) {

                    Text("OK")
                }
            }
        )
    }
}