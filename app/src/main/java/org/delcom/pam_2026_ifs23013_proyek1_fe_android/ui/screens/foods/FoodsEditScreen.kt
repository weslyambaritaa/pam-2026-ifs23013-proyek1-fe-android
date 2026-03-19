package org.delcom.pam_2026_ifs23013_proyek1_fe_android.ui.screens.foods

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.R
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.helper.*
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.network.foods.data.ResponseFoodData
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.ui.components.BottomNavComponent
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.ui.components.LoadingUI
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.ui.components.TopAppBarComponent
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.ui.viewmodels.*

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

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        isLoading = true
        if (uiStateAuth.auth !is AuthUIState.Success) {
            RouteHelper.to(navController, ConstHelper.RouteNames.Home.path, true)
            return@LaunchedEffect
        }
        authToken.value = (uiStateAuth.auth as AuthUIState.Success).data.authToken
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

    fun onSave(name: String, description: String, price: Int, quantity: Int, category: String, isAvailable: Boolean, imageUri: Uri?) {
        isLoading = true

        val imageFile = imageUri?.let { ToolsHelper.uriToFile(context, it) }

        foodViewModel.putFood(
            authToken = authToken.value!!,
            foodId = foodId,
            name = name,
            description = description,
            price = price,
            quantity = quantity,
            category = category,
            isAvailable = isAvailable,
            imageFile = imageFile
        )
    }

    LaunchedEffect(uiStateFood.foodChange) {
        when (val state = uiStateFood.foodChange) {
            is FoodActionUIState.Success -> {
                SuspendHelper.showSnackBar(snackbarHost, SuspendHelper.SnackBarType.SUCCESS, state.message)
                RouteHelper.to(navController, ConstHelper.RouteNames.FoodsDetail.path.replace("{foodId}", foodId), true)
                isLoading = false
            }
            is FoodActionUIState.Error -> {
                SuspendHelper.showSnackBar(snackbarHost, SuspendHelper.SnackBarType.ERROR, state.message)
                isLoading = false
            }
            else -> {}
        }
    }

    if (isLoading || food == null) { LoadingUI(); return }

    Column(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)) {
        TopAppBarComponent(navController = navController, title = "Ubah Data Makanan", showBackButton = true)
        Box(modifier = Modifier.weight(1f)) {
            FoodsEditUI(food = food!!, onSave = ::onSave)
        }
        BottomNavComponent(navController = navController)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodsEditUI(food: ResponseFoodData, onSave: (String, String, Int, Int, String, Boolean, Uri?) -> Unit) {
    val alertState = remember { mutableStateOf(AlertState()) }

    var dataName by remember { mutableStateOf(food.name) }
    var dataDescription by remember { mutableStateOf(food.description) }
    var dataPrice by remember { mutableStateOf(food.price.toString()) }
    var dataQuantity by remember { mutableStateOf(food.quantity.toString()) }
    var dataCategory by remember { mutableStateOf(food.category) }
    var dataAvailable by remember { mutableStateOf(food.isAvailable) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    var expandedCategory by remember { mutableStateOf(false) }
    val categoryOptions = listOf("Makanan", "Minuman")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally // Posisikan elemen ke tengah
    ) {

        // --- BAGIAN FOTO ---
        Text(
            text = "Tekan gambar untuk mengganti foto (Opsional)",
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )

        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.LightGray.copy(alpha = 0.3f))
                .clickable {
                    imagePicker.launch("image/*")
                }
        ) {
            AsyncImage(
                // Jika imageUri baru belum dipilih, tampilkan foto lama dari server
                model = imageUri ?: ToolsHelper.getFoodImage(food.id, food.updatedAt),
                placeholder = painterResource(R.drawable.img_placeholder),
                error = painterResource(R.drawable.img_placeholder),
                contentDescription = "Food Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        // --- FORM INPUT ---
        OutlinedTextField(
            value = dataName, onValueChange = { dataName = it }, label = { Text("Nama Makanan") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = dataPrice, onValueChange = { dataPrice = it }, label = { Text("Harga") },
                modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = dataQuantity, onValueChange = { dataQuantity = it }, label = { Text("Kuantitas") },
                modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        ExposedDropdownMenuBox(
            expanded = expandedCategory,
            onExpandedChange = { expandedCategory = !expandedCategory }
        ) {
            OutlinedTextField(
                value = dataCategory, onValueChange = {}, readOnly = true,
                label = { Text("Kategori") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expandedCategory, onDismissRequest = { expandedCategory = false }) {
                categoryOptions.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            dataCategory = selectionOption
                            expandedCategory = false
                        }
                    )
                }
            }
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            Text("Status Ketersediaan")
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = dataAvailable, onClick = { dataAvailable = true })
                Text("Tersedia")
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(selected = !dataAvailable, onClick = { dataAvailable = false })
                Text("Tidak Tersedia")
            }
        }

        OutlinedTextField(
            value = dataDescription, onValueChange = { dataDescription = it }, label = { Text("Deskripsi") },
            modifier = Modifier.fillMaxWidth().height(120.dp), maxLines = 5
        )

        Spacer(modifier = Modifier.height(80.dp))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        FloatingActionButton(
            onClick = {
                if (dataName.isEmpty() || dataDescription.isEmpty() || dataPrice.isEmpty() || dataQuantity.isEmpty()) {
                    AlertHelper.show(alertState, AlertType.ERROR, "Data tidak boleh kosong!")
                    return@FloatingActionButton
                }
                onSave(dataName, dataDescription, dataPrice.toInt(), dataQuantity.toInt(), dataCategory, dataAvailable, imageUri)
            },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Icon(imageVector = Icons.Default.Save, contentDescription = "Save Food")
        }
    }

    if (alertState.value.isVisible) {
        AlertDialog(
            onDismissRequest = { AlertHelper.dismiss(alertState) },
            title = { Text(alertState.value.type.title) },
            text = { Text(alertState.value.message) },
            confirmButton = { TextButton(onClick = { AlertHelper.dismiss(alertState) }) { Text("OK") } }
        )
    }
}