package org.delcom.pam_2026_ifs23013_proyek1_fe_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.ui.UIApp
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.ui.theme.DelcomTheme
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.ui.viewmodels.AuthViewModel
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.ui.viewmodels.TodoViewModel
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.ui.viewmodels.FoodViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val todoViewModel: TodoViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private val foodViewModel: FoodViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DelcomTheme {
                UIApp(
                    todoViewModel = todoViewModel,
                    foodViewModel = foodViewModel,
                    authViewModel = authViewModel
                )
            }
        }
    }
}