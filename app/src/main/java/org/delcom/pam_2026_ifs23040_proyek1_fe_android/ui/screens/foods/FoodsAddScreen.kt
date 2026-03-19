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
import org.delcom.pam_proyek1_ifs23013.ui.components.BottomNavComponent
import org.delcom.pam_proyek1_ifs23013.ui.components.LoadingUI
import org.delcom.pam_proyek1_ifs23013.ui.components.TopAppBarComponent
import org.delcom.pam_proyek1_ifs23013.ui.viewmodels.*

@Composable
fun FoodsAddScreen(
    navController: NavHostController,
    snackbarHost: SnackbarHostState,
    authViewModel: AuthViewModel,
    foodViewModel: FoodViewModel
) {

    val uiStateAuth by authViewModel.uiState.collectAsState()
    val uiStateFood by foodViewModel.uiState.collectAsState()

    var isLoading by remember { mutableStateOf(false) }
    val authToken = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {

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
    }

    fun onSave(
        name: String,
        description: String,
        price: Int,
        category: String
    ) {

        if (authToken.value == null) return

        isLoading = true

        foodViewModel.postFood(
            authToken = authToken.value!!,
            name = name,
            description = description,
            price = price,
            category = category
        )
    }

    LaunchedEffect(uiStateFood.foodAdd) {

        when (val state = uiStateFood.foodAdd) {

            is FoodActionUIState.Success -> {

                SuspendHelper.showSnackBar(
                    snackbarHost = snackbarHost,
                    type = SuspendHelper.SnackBarType.SUCCESS,
                    message = state.message
                )

                RouteHelper.to(
                    navController,
                    ConstHelper.RouteNames.Foods.path,
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

    if (isLoading) {
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
            title = "Tambah Food",
            showBackButton = true
        )

        Box(
            modifier = Modifier.weight(1f)
        ) {

            FoodsAddUI(
                onSave = ::onSave
            )
        }

        BottomNavComponent(navController = navController)
    }
}

@Composable
fun FoodsAddUI(
    onSave: (String, String, Int, String) -> Unit
) {

    val alertState = remember { mutableStateOf(AlertState()) }

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nama Food") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            maxLines = 5
        )

        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Price") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            )
        )

        OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Category") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text
            )
        )

        Spacer(modifier = Modifier.height(80.dp))
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        FloatingActionButton(
            onClick = {

                if (name.isEmpty()) {
                    AlertHelper.show(
                        alertState,
                        AlertType.ERROR,
                        "Nama food tidak boleh kosong!"
                    )
                    return@FloatingActionButton
                }

                if (description.isEmpty()) {
                    AlertHelper.show(
                        alertState,
                        AlertType.ERROR,
                        "Deskripsi tidak boleh kosong!"
                    )
                    return@FloatingActionButton
                }

                if (price.isEmpty()) {
                    AlertHelper.show(
                        alertState,
                        AlertType.ERROR,
                        "Harga tidak boleh kosong!"
                    )
                    return@FloatingActionButton
                }

                if (category.isEmpty()) {
                    AlertHelper.show(
                        alertState,
                        AlertType.ERROR,
                        "Kategori tidak boleh kosong!"
                    )
                    return@FloatingActionButton
                }

                onSave(
                    name,
                    description,
                    price.toInt(),
                    category
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