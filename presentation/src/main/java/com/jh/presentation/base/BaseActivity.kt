package com.jh.presentation.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.jh.presentation.ui.theme.MurunTheme

abstract class BaseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MurunTheme {
                InitComposeUi()
            }
        }
    }

    @Composable
    abstract fun InitComposeUi()
}