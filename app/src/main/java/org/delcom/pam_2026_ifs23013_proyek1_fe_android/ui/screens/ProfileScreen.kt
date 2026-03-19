package org.delcom.pam_2026_ifs23013_proyek1_fe_android.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.R
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.helper.*
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.network.todos.data.ResponseUserData
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.ui.components.*
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.ui.viewmodels.*

@Composable
fun ProfileScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    todoViewModel: TodoViewModel,
    foodViewModel: FoodViewModel
) {
    val uiStateAuth by authViewModel.uiState.collectAsState()
    val uiStateTodo by todoViewModel.uiState.collectAsState()

    var isLoading by remember { mutableStateOf(false) }
    var profile by remember { mutableStateOf<ResponseUserData?>(null) }
    var authToken by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val snackbarHost = remember { SnackbarHostState() }
    var isEditing by remember { mutableStateOf(false) }

    // 1. Pantau status Auth
    LaunchedEffect(uiStateAuth.auth) {
        when (uiStateAuth.auth) {
            is AuthUIState.Success -> {
                authToken = (uiStateAuth.auth as AuthUIState.Success).data.authToken
                todoViewModel.getProfile(authToken ?: "")
            }
            is AuthUIState.Error -> {
                RouteHelper.to(navController, ConstHelper.RouteNames.AuthLogin.path, true)
            }
            else -> {}
        }
    }

    // 2. Pantau hasil API Profil agar tidak loading terus-menerus saat Error
    LaunchedEffect(uiStateTodo.profile) {
        when (val state = uiStateTodo.profile) {
            is ProfileUIState.Loading -> {
                isLoading = true
            }
            is ProfileUIState.Success -> {
                isLoading = false
                profile = state.data
            }
            is ProfileUIState.Error -> {
                isLoading = false
                SuspendHelper.showSnackBar(snackbarHost, SuspendHelper.SnackBarType.ERROR, state.message)
            }
        }
    }

    LaunchedEffect(uiStateTodo.profileChange) {
        when (val state = uiStateTodo.profileChange) {
            is ProfileActionUIState.Success -> {
                isLoading = false
                todoViewModel.getProfile(authToken ?: "")
                SuspendHelper.showSnackBar(snackbarHost, SuspendHelper.SnackBarType.SUCCESS, state.message)
                todoViewModel.clearProfileMessage()
            }
            is ProfileActionUIState.Error -> {
                isLoading = false
                SuspendHelper.showSnackBar(snackbarHost, SuspendHelper.SnackBarType.ERROR, state.message)
                todoViewModel.clearProfileMessage()
            }
            else -> {}
        }
    }

    LaunchedEffect(uiStateTodo.profileChangePhoto) {
        when (val state = uiStateTodo.profileChangePhoto) {
            is ProfileActionUIState.Success -> {
                isLoading = false
                todoViewModel.getProfile(authToken ?: "")
                SuspendHelper.showSnackBar(snackbarHost, SuspendHelper.SnackBarType.SUCCESS, state.message)
                todoViewModel.clearProfileMessage()
            }
            is ProfileActionUIState.Error -> {
                isLoading = false
                SuspendHelper.showSnackBar(snackbarHost, SuspendHelper.SnackBarType.ERROR, state.message)
                todoViewModel.clearProfileMessage()
            }
            else -> {}
        }
    }

    fun onLogout() {
        isLoading = true
        authViewModel.logout(authToken ?: "")
    }

    // --- HANDLING LAYAR ERROR / LOADING ---
    if (isLoading) {
        LoadingUI()
        return
    }

    if (profile == null) {
        // Tampilkan layar darurat ini jika API gagal (misal karena token tidak valid)
        Column(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Sesi berakhir atau token tidak valid.", color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { onLogout() }) {
                Text("Log Out & Login Ulang")
            }
            SnackbarHost(hostState = snackbarHost)
        }
        return
    }

    // --- LAYAR UTAMA PROFIL ---
    val menuItems = listOf(
        TopAppBarMenuItem(text = "Edit Profil", icon = Icons.Filled.Edit, route = null, onClick = { isEditing = !isEditing }),
        TopAppBarMenuItem(text = "Logout", icon = Icons.AutoMirrored.Filled.Logout, route = null, onClick = { onLogout() })
    )

    Column(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)) {
        TopAppBarComponent(navController = navController, title = "Profile", showBackButton = false, customMenuItems = menuItems)

        Box(modifier = Modifier.weight(1f)) {
            ProfileUI(
                profile = profile!!,
                isEditing = isEditing,
                onSaveProfile = { newName ->
                    isLoading = true
                    isEditing = false
                    todoViewModel.putUserMe(authToken ?: "", newName, profile!!.username)
                },
                onChangePhoto = { uri ->
                    isLoading = true
                    val filePart = ToolsHelper.uriToMultipart(context, uri, "file")
                    todoViewModel.putUserMePhoto(authToken ?: "", filePart)
                }
            )

            SnackbarHost(
                hostState = snackbarHost,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
        BottomNavComponent(navController = navController)
    }
}

@Composable
fun ProfileUI(
    profile: ResponseUserData,
    isEditing: Boolean,
    onSaveProfile: (String) -> Unit,
    onChangePhoto: (Uri) -> Unit
){
    var editName by remember { mutableStateOf(profile.name) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePicker = rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            onChangePhoto(uri)
        }
    }

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Box(modifier = Modifier.fillMaxWidth().padding(top = 32.dp, bottom = 16.dp), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                AsyncImage(
                    model = selectedImageUri ?: ToolsHelper.getUserImage(profile.id, profile.updatedAt),
                    contentDescription = "Photo Profil",
                    placeholder = painterResource(R.drawable.img_placeholder),
                    error = painterResource(R.drawable.img_placeholder),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .border(3.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                        .clickable(enabled = isEditing) {
                            imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }
                )

                if (isEditing) {
                    Text("Ketuk foto untuk mengubah", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, modifier = Modifier.padding(top=8.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isEditing) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Nama") },
                        modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { onSaveProfile(editName) }) {
                        Text("Simpan Perubahan")
                    }
                } else {
                    Text(text = profile.name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    Text(text = "@${profile.username}", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}