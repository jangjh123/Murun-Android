package com.jh.presentation.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import com.jh.presentation.ui.LoadingScreen
import com.jh.presentation.ui.theme.MurunTheme

abstract class BaseActivity : ComponentActivity() {

    protected abstract val viewModel: BaseViewModel
    private val loadingVisibilityState = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupCollect()
    }

    protected fun initComposeUi(block: @Composable () -> Unit) {
        setContent {
            MurunTheme {
                block()

                if (loadingVisibilityState.value) {
                    LoadingScreen()
                }
            }
        }
    }

    protected fun showLoadingScreen() {
        loadingVisibilityState.value = true
    }

    protected fun dismissLoadingScreen() {
        loadingVisibilityState.value = false
    }

    protected abstract fun setupCollect()
}