package org.delcom.pam_2026_ifs23013_proyek1_fe_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dagger.hilt.android.AndroidEntryPoint
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.ui.UIApp
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.ui.theme.DelcomTheme
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.ui.viewmodels.AuthViewModel
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.ui.viewmodels.TodoViewModel
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.ui.viewmodels.FoodViewModel

val LocalThemeIsDark = compositionLocalOf { false }
val LocalThemeToggle = compositionLocalOf { {} }

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val todoViewModel: TodoViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private val foodViewModel: FoodViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔥 MEMBACA TOKEN DARI DATASTORE SAAT APLIKASI PERTAMA DIBUKA
        authViewModel.loadTokenFromPreferences()

        enableEdgeToEdge()
        setContent {
            val systemTheme = isSystemInDarkTheme()
            var isDarkMode by remember { mutableStateOf(systemTheme) }

            CompositionLocalProvider(
                LocalThemeIsDark provides isDarkMode,
                LocalThemeToggle provides { isDarkMode = !isDarkMode }
            ) {
                DelcomTheme(darkTheme = isDarkMode) {
                    UIApp(
                        todoViewModel = todoViewModel,
                        foodViewModel = foodViewModel,
                        authViewModel = authViewModel
                    )
                }
            }
        }
    }
}