package com.palhelper.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.palhelper.app.ui.PalHelperRoot
import com.palhelper.app.ui.theme.PalHelperTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PalHelperTheme {
                PalHelperRoot()
            }
        }
    }
}
