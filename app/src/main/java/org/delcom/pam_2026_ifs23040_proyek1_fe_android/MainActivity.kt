package org.delcom.pam_proyek1_ifs23013

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.delcom.pam_proyek1_ifs23013.ui.UIApp
import org.delcom.pam_proyek1_ifs23013.ui.theme.DelcomTheme
import org.delcom.pam_proyek1_ifs23013.ui.viewmodels.AuthViewModel
import org.delcom.pam_proyek1_ifs23013.ui.viewmodels.TodoViewModel
import org.delcom.pam_proyek1_ifs23013.ui.viewmodels.FoodViewModel

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