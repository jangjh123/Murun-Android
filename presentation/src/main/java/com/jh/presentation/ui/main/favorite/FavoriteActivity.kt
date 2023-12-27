package com.jh.presentation.ui.main.favorite

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import com.jh.presentation.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteActivity : BaseActivity() {

    @Composable
    override fun InitComposeUi() {
        FavoriteScreen()
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, FavoriteActivity::class.java)
        }
    }
}