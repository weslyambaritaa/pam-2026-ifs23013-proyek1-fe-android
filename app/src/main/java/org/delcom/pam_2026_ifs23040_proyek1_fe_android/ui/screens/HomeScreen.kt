package org.delcom.pam_proyek1_ifs23013.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.delcom.pam_proyek1_ifs23013.helper.ConstHelper
import org.delcom.pam_proyek1_ifs23013.helper.RouteHelper
import org.delcom.pam_proyek1_ifs23013.ui.components.BottomNavComponent
import org.delcom.pam_proyek1_ifs23013.ui.components.LoadingUI
import org.delcom.pam_proyek1_ifs23013.ui.components.StatusCard
import org.delcom.pam_proyek1_ifs23013.ui.components.TopAppBarComponent
import org.delcom.pam_proyek1_ifs23013.ui.components.TopAppBarMenuItem
import org.delcom.pam_proyek1_ifs23013.ui.viewmodels.*
import androidx.compose.ui.tooling.preview.Preview
import org.delcom.pam_proyek1_ifs23013.ui.theme.DelcomTheme

@Composable
fun HomeScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    foodViewModel: FoodViewModel
) {

    val uiStateAuth by authViewModel.uiState.collectAsState()

    var isLoading by remember { mutableStateOf(false) }
    var isFreshToken by remember { mutableStateOf(false) }
    var authToken by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {

        if (isLoading) return@LaunchedEffect

        isLoading = true
        isFreshToken = true

        uiStateAuth.authLogout = AuthLogoutUIState.Loading

        authViewModel.loadTokenFromPreferences()
    }

    fun onLogout(token: String) {

        isLoading = true
        authViewModel.logout(token)
    }

    LaunchedEffect(uiStateAuth.auth) {

        if (!isLoading) return@LaunchedEffect

        if (uiStateAuth.auth !is AuthUIState.Loading) {

            if (uiStateAuth.auth is AuthUIState.Success) {

                if (isFreshToken) {

                    val dataToken =
                        (uiStateAuth.auth as AuthUIState.Success).data

                    authViewModel.refreshToken(
                        dataToken.authToken,
                        dataToken.refreshToken
                    )

                    isFreshToken = false

                } else if (uiStateAuth.authRefreshToken is AuthActionUIState.Success) {

                    val newToken =
                        (uiStateAuth.auth as AuthUIState.Success).data.authToken

                    if (authToken != newToken) {
                        authToken = newToken
                    }

                    isLoading = false
                }

            } else {

                onLogout("")
            }
        }
    }

    LaunchedEffect(uiStateAuth.authLogout) {

        if (uiStateAuth.authLogout !is AuthLogoutUIState.Loading) {

            RouteHelper.to(
                navController,
                ConstHelper.RouteNames.AuthLogin.path,
                true
            )
        }
    }

    if (isLoading || authToken == null || isFreshToken) {

        LoadingUI()
        return
    }

    val menuItems = listOf(

        TopAppBarMenuItem(
            text = "Profile",
            icon = Icons.Filled.Person,
            route = ConstHelper.RouteNames.Profile.path
        ),

        TopAppBarMenuItem(
            text = "Logout",
            icon = Icons.AutoMirrored.Filled.Logout,
            route = null,
            onClick = {
                onLogout(authToken ?: "")
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
            title = "Canteen Del",
            showBackButton = false,
            customMenuItems = menuItems
        )

        Box(
            modifier = Modifier.weight(1f)
        ) {

            HomeUI()
        }

        BottomNavComponent(navController = navController)
    }
}

@Composable
fun HomeUI() {

    val totalFoods = 0
    val availableFoods = 0
    val unavailableFoods = 0

    Column(
        modifier = Modifier.padding(top = 16.dp)
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(6.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {

            Text(
                text = "🍽️ Canteen Del",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            StatusCard(
                title = "Foods",
                value = totalFoods.toString(),
                icon = Icons.Default.Fastfood
            )

            StatusCard(
                title = "Available",
                value = availableFoods.toString(),
                icon = Icons.Default.Restaurant
            )

            StatusCard(
                title = "Unavailable",
                value = unavailableFoods.toString(),
                icon = Icons.AutoMirrored.Filled.List
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeUI() {

    DelcomTheme {

        HomeUI()
    }
}