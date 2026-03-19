package org.delcom.pam_2026_ifs23013_proyek1_fe_android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.helper.ConstHelper
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.helper.RouteHelper
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.network.foods.data.ResponseFoodData
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.ui.components.BottomNavComponent
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.ui.components.LoadingUI
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.ui.components.TopAppBarComponent
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.ui.viewmodels.AuthUIState
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.ui.viewmodels.AuthViewModel
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.ui.viewmodels.FoodViewModel
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.ui.viewmodels.FoodsUIState

@Composable
fun HomeScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    foodViewModel: FoodViewModel
) {
    val uiStateAuth by authViewModel.uiState.collectAsState()
    val uiStateFood by foodViewModel.uiState.collectAsState()

    var isLoading by remember { mutableStateOf(true) }
    var foods by remember { mutableStateOf<List<ResponseFoodData>>(emptyList()) }
    var authToken by remember { mutableStateOf<String?>(null) }

    // PERBAIKAN: Gunakan LaunchedEffect(Unit) agar langsung dieksekusi 1 kali saat buka aplikasi
    LaunchedEffect(Unit) {
        isLoading = true
        if (uiStateAuth.auth !is AuthUIState.Success) {
            // Jika belum punya token/belum sukses login, PAKSA pindah ke layar Login
            RouteHelper.to(navController, ConstHelper.RouteNames.AuthLogin.path, true)
            return@LaunchedEffect
        }

        // Jika sukses, tarik data makanannya
        authToken = (uiStateAuth.auth as AuthUIState.Success).data.authToken
        foodViewModel.getAllFoods(authToken ?: "")
    }

    // Menangkap hasil respons dari server untuk mematikan loading
    LaunchedEffect(uiStateFood.foods) {
        if (uiStateFood.foods !is FoodsUIState.Loading) {
            isLoading = false
            if (uiStateFood.foods is FoodsUIState.Success) {
                foods = (uiStateFood.foods as FoodsUIState.Success).data
            }
        }
    }

    if (isLoading) { LoadingUI(); return }

    // ----------------------------------------
    // MENGHITUNG STATISTIK (LOGIKA FILTERING)
    // ----------------------------------------
    val totalMakanan = foods.count { it.category.equals("Makanan", ignoreCase = true) }
    val totalMinuman = foods.count { it.category.equals("Minuman", ignoreCase = true) }
    val totalTersedia = foods.count { it.available }
    val totalHabis = foods.count { !it.available }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        TopAppBarComponent(
            navController = navController,
            title = "Beranda",
            showBackButton = false
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Ringkasan Menu",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Baris Pertama: Makanan & Minuman
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Makanan",
                    count = totalMakanan,
                    icon = Icons.Filled.Fastfood,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Minuman",
                    count = totalMinuman,
                    icon = Icons.Filled.LocalCafe,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            // Baris Kedua: Tersedia & Habis (Tidak Tersedia)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Tersedia",
                    count = totalTersedia,
                    icon = Icons.Filled.CheckCircle,
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Habis",
                    count = totalHabis,
                    icon = Icons.Filled.Cancel,
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        BottomNavComponent(navController = navController)
    }
}

// ----------------------------------------------------
// KOMPONEN REUSABLE UNTUK KOTAK STATISTIK (CARD)
// ----------------------------------------------------
@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    count: Int,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color
) {
    Card(
        modifier = modifier.height(120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = contentColor,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = contentColor,
                    modifier = Modifier.size(28.dp)
                )
            }
            Text(
                text = "$count",
                style = MaterialTheme.typography.headlineLarge,
                color = contentColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}