package com.carlos.ismartshell

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.carlos.ismartshell.core.navigation.AppNavHost
import com.carlos.ismartshell.ui.theme.IsmartshellTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IsmartshellTheme {
                AppNavHost()
            }
        }
    }
}