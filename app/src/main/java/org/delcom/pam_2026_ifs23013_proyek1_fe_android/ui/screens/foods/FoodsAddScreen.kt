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
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.ui.components.BottomNavComponent
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.ui.components.LoadingUI
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.ui.components.TopAppBarComponent
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.ui.viewmodels.*

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

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (uiStateAuth.auth !is AuthUIState.Success) {
            RouteHelper.to(navController, ConstHelper.RouteNames.Home.path, true)
            return@LaunchedEffect
        }
        authToken.value = (uiStateAuth.auth as AuthUIState.Success).data.authToken
    }

    fun onSave(name: String, description: String, price: Int, quantity: Int, category: String, available: Boolean, imageUri: Uri?) {
        if (authToken.value == null) return
        isLoading = true

        val imageFile = imageUri?.let { ToolsHelper.uriToFile(context, it) }

        foodViewModel.postFood(
            authToken = authToken.value!!,
            name = name,
            description = description,
            price = price,
            quantity = quantity,
            category = category,
            available = available,
            imageFile = imageFile
        )
    }

    LaunchedEffect(uiStateFood.foodAdd) {
        when (val state = uiStateFood.foodAdd) {
            is FoodActionUIState.Success -> {
                SuspendHelper.showSnackBar(snackbarHost, SuspendHelper.SnackBarType.SUCCESS, state.message)
                RouteHelper.to(navController, ConstHelper.RouteNames.Foods.path, true)
                isLoading = false
            }
            is FoodActionUIState.Error -> {
                SuspendHelper.showSnackBar(snackbarHost, SuspendHelper.SnackBarType.ERROR, state.message)
                isLoading = false
            }
            else -> {}
        }
    }

    if (isLoading) { LoadingUI(); return }

    Column(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)) {
        TopAppBarComponent(navController = navController, title = "Tambah Makanan", showBackButton = true)
        Box(modifier = Modifier.weight(1f)) {
            FoodsAddUI(onSave = ::onSave)
        }
        BottomNavComponent(navController = navController)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodsAddUI(onSave: (String, String, Int, Int, String, Boolean, Uri?) -> Unit) {
    val alertState = remember { mutableStateOf(AlertState()) }

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Makanan") }
    var available by remember { mutableStateOf(true) }
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
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // --- BAGIAN FOTO DENGAN TEMA DINAMIS ---
        Text(
            text = "Tekan gambar untuk menambahkan foto",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant // Perbaikan warna Dark Mode
        )

        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant) // Perbaikan warna Dark Mode
                .clickable {
                    imagePicker.launch("image/*")
                }
        ) {
            AsyncImage(
                model = imageUri,
                placeholder = painterResource(R.drawable.img_placeholder),
                error = painterResource(R.drawable.img_placeholder),
                contentDescription = "Food Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        OutlinedTextField(
            value = name, onValueChange = { name = it }, label = { Text("Nama Makanan") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = price, onValueChange = { price = it }, label = { Text("Harga") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
            )
            OutlinedTextField(
                value = quantity, onValueChange = { quantity = it }, label = { Text("Kuantitas") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
            )
        }

        ExposedDropdownMenuBox(
            expanded = expandedCategory,
            onExpandedChange = { expandedCategory = !expandedCategory }
        ) {
            OutlinedTextField(
                value = category, onValueChange = {}, readOnly = true,
                label = { Text("Kategori") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expandedCategory, onDismissRequest = { expandedCategory = false }) {
                categoryOptions.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            category = selectionOption
                            expandedCategory = false
                        }
                    )
                }
            }
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            Text("Status Ketersediaan")
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = available, onClick = { available = true })
                Text("Tersedia")
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(selected = !available, onClick = { available = false })
                Text("Tidak Tersedia")
            }
        }

        OutlinedTextField(
            value = description, onValueChange = { description = it }, label = { Text("Deskripsi") },
            modifier = Modifier.fillMaxWidth().height(120.dp), maxLines = 5
        )

        Spacer(modifier = Modifier.height(80.dp))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        FloatingActionButton(
            onClick = {
                if (name.isEmpty() || description.isEmpty() || price.isEmpty() || quantity.isEmpty()) {
                    AlertHelper.show(alertState, AlertType.ERROR, "Data tidak boleh ada yang kosong!")
                    return@FloatingActionButton
                }
                onSave(name, description, price.toInt(), quantity.toInt(), category, available, imageUri)
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