package com.jh.presentation.ui.on_boarding

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import com.jh.presentation.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnBoardingActivity : BaseActivity() {

    @Composable
    override fun InitComposeUi() {
        OnBoardingScreen()
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, OnBoardingActivity::class.java)
        }
    }
}