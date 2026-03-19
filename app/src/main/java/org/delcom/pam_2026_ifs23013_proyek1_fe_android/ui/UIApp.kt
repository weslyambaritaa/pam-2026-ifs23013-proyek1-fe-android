package org.delcom.pam_2026_ifs23013_proyek1_fe_android.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.helper.ConstHelper
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.ui.components.CustomSnackbar
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.ui.screens.HomeScreen
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.ui.screens.ProfileScreen
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.ui.screens.auth.AuthLoginScreen
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.ui.screens.auth.AuthRegisterScreen
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.ui.screens.foods.*
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.ui.viewmodels.AuthViewModel
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.ui.viewmodels.TodoViewModel
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.ui.viewmodels.FoodViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun UIApp(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel,
    foodViewModel: FoodViewModel,
    todoViewModel: TodoViewModel,
) {

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { snackbarData ->
                CustomSnackbar(
                    snackbarData,
                    onDismiss = {
                        snackbarHostState.currentSnackbarData?.dismiss()
                    }
                )
            }
        }
    ) { _ ->

        NavHost(
            navController = navController,
            startDestination = ConstHelper.RouteNames.Home.path,
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF7F8FA))
        ) {

            // ----------------------
            // Auth
            // ----------------------

            composable(ConstHelper.RouteNames.AuthLogin.path) {
                AuthLoginScreen(
                    navController,
                    snackbarHostState,
                    authViewModel
                )
            }

            composable(ConstHelper.RouteNames.AuthRegister.path) {
                AuthRegisterScreen(
                    navController,
                    snackbarHostState,
                    authViewModel
                )
            }

            // ----------------------
            // Home
            // ----------------------

            composable(ConstHelper.RouteNames.Home.path) {
                HomeScreen(
                    navController = navController,
                    authViewModel = authViewModel,
                    foodViewModel = foodViewModel
                )
            }

            // ----------------------
            // Profile
            // ----------------------

            composable(ConstHelper.RouteNames.Profile.path) {
                ProfileScreen(
                    navController = navController,
                    authViewModel = authViewModel,
                    todoViewModel = todoViewModel,
                    foodViewModel = foodViewModel
                )
            }

            // ----------------------
            // Foods
            // ----------------------

            composable(ConstHelper.RouteNames.Foods.path) {
                FoodsScreen(
                    navController = navController,
                    authViewModel = authViewModel,
                    foodViewModel = foodViewModel
                )
            }

            composable(ConstHelper.RouteNames.FoodsAdd.path) {
                FoodsAddScreen(
                    navController,
                    snackbarHostState,
                    authViewModel,
                    foodViewModel
                )
            }

            composable(
                route = ConstHelper.RouteNames.FoodsDetail.path,
                arguments = listOf(
                    navArgument("foodId") { type = NavType.StringType }
                )
            ) { backStackEntry ->

                val foodId =
                    backStackEntry.arguments?.getString("foodId") ?: ""

                FoodsDetailScreen(
                    navController,
                    snackbarHostState,
                    authViewModel,
                    foodViewModel,
                    foodId
                )
            }

            composable(
                route = ConstHelper.RouteNames.FoodsEdit.path,
                arguments = listOf(
                    navArgument("foodId") { type = NavType.StringType }
                )
            ) { backStackEntry ->

                val foodId =
                    backStackEntry.arguments?.getString("foodId") ?: ""

                FoodsEditScreen(
                    navController,
                    snackbarHostState,
                    authViewModel,
                    foodViewModel,
                    foodId
                )
            }
        }
    }
}