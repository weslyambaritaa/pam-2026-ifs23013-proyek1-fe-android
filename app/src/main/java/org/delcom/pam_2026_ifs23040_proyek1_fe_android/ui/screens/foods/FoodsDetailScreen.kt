package org.delcom.pam_proyek1_ifs23013.ui.screens.foods

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import org.delcom.pam_proyek1_ifs23013.R
import org.delcom.pam_proyek1_ifs23013.helper.*
import org.delcom.pam_proyek1_ifs23013.network.foods.data.ResponseFoodData
import org.delcom.pam_proyek1_ifs23013.ui.components.*
import org.delcom.pam_proyek1_ifs23013.ui.viewmodels.*
import kotlin.time.Clock

@Composable
fun FoodsDetailScreen(
    navController: NavHostController,
    snackbarHost: SnackbarHostState,
    authViewModel: AuthViewModel,
    foodViewModel: FoodViewModel,
    foodId: String
) {

    val uiStateFood by foodViewModel.uiState.collectAsState()
    val uiStateAuth by authViewModel.uiState.collectAsState()

    var isLoading by remember { mutableStateOf(false) }
    var isConfirmDelete by remember { mutableStateOf(false) }

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
            }
        }
    }

    fun onDelete() {

        if (authToken.value == null) return

        isLoading = true

        foodViewModel.deleteFood(
            authToken.value!!,
            foodId
        )
    }

    LaunchedEffect(uiStateFood.foodDelete) {

        when (val state = uiStateFood.foodDelete) {

            is FoodActionUIState.Success -> {

                SuspendHelper.showSnackBar(
                    snackbarHost,
                    SuspendHelper.SnackBarType.SUCCESS,
                    state.message
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
                    snackbarHost,
                    SuspendHelper.SnackBarType.ERROR,
                    state.message
                )

                isLoading = false
            }

            else -> {}
        }
    }

    fun onChangeImage(
        context: Context,
        file: Uri
    ) {

        if (authToken.value == null) return

        isLoading = true

        val filePart = ToolsHelper.uriToMultipart(
            context,
            file,
            "file"
        )

        foodViewModel.putFoodImage(
            authToken = authToken.value!!,
            foodId = foodId,
            file = filePart
        )
    }

    if (isLoading || food == null) {
        LoadingUI()
        return
    }

    val detailMenuItems = listOf(

        TopAppBarMenuItem(
            text = "Ubah Data",
            icon = Icons.Filled.Edit,
            route = null,
            onClick = {

                RouteHelper.to(
                    navController,
                    ConstHelper.RouteNames.FoodsEdit.path
                        .replace("{foodId}", food!!.id)
                )
            }
        ),

        TopAppBarMenuItem(
            text = "Hapus Data",
            icon = Icons.Filled.Delete,
            route = null,
            onClick = {

                isConfirmDelete = true
            }
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {

        TopAppBarComponent(
            navController = navController,
            title = food!!.name,
            showBackButton = true,
            customMenuItems = detailMenuItems
        )

        Box(
            modifier = Modifier.weight(1f)
        ) {

            FoodsDetailUI(
                food = food!!,
                onChangeImage = ::onChangeImage
            )

            BottomDialog(
                type = BottomDialogType.ERROR,
                show = isConfirmDelete,
                onDismiss = { isConfirmDelete = false },
                title = "Konfirmasi Hapus Data",
                message = "Apakah Anda yakin ingin menghapus data ini?",
                confirmText = "Ya, Hapus",
                onConfirm = { onDelete() },
                cancelText = "Batal",
                destructiveAction = true
            )
        }

        BottomNavComponent(navController = navController)
    }
}

@Composable
fun FoodsDetailUI(
    food: ResponseFoodData,
    onChangeImage: (Context, Uri) -> Unit
) {

    var dataFile by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        dataFile = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Box(
            modifier = Modifier
                .size(180.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable {

                    imagePicker.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                }
        ) {

            AsyncImage(
                model = dataFile ?: ToolsHelper.getFoodImage(
                    food.id,
                    food.updatedAt
                ),
                placeholder = painterResource(R.drawable.img_placeholder),
                error = painterResource(R.drawable.img_placeholder),
                contentDescription = "Food Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = food.name,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Price : Rp ${food.price}",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Category : ${food.category}",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = food.description,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (dataFile != null) {

            Button(
                onClick = {

                    onChangeImage(context, dataFile!!)
                },
                modifier = Modifier.fillMaxWidth()
            ) {

                Text("Simpan Gambar")
            }
        }
    }
}