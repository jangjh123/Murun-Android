package com.jh.presentation.ui.splash

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import com.jh.presentation.base.BaseActivity
import com.jh.presentation.ui.theme.*
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BaseActivity() {

    @Composable
    override fun InitComposeUi() {
        SplashScreen()
    }
}