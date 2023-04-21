package com.jh.presentation.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.jh.murun.presentation.R
import com.jh.presentation.base.BaseActivity
import com.jh.presentation.ui.main.MainActivity
import com.jh.presentation.ui.on_boarding.OnBoardingActivity
import com.jh.presentation.ui.repeatOnStarted
import com.jh.presentation.ui.theme.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BaseActivity() {
    override val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            repeatOnStarted {
                viewModel.sideEffectChannelFlow.collectLatest { sideEffect ->
                    when (sideEffect) {
                        is SplashSideEffect.SkipOnBoarding -> {
                            startActivity(Intent(MainActivity.newIntent(this@SplashActivity)))
                        }
                        is SplashSideEffect.NoSkipOnBoarding -> {
                            startActivity(Intent(OnBoardingActivity.newIntent(this@SplashActivity)))
                        }
                    }
                    finish()
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                delay(1500L)
                viewModel.checkToSkipOnBoarding()
            }
        }

        initComposeUi {
            SplashActivityContent()
        }
    }
}

@Composable
private fun SplashActivityContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {

        Column(modifier = Modifier.align(Center), horizontalAlignment = CenterHorizontally) {
            Image(
                modifier = Modifier.size(128.dp),
                painter = painterResource(id = R.drawable.icon),
                contentDescription = null
            )

            Text(
                text = "당신의 러닝 뮤직, 뮤런",
                fontFamily = pyeongchangPeace,
                style = Typography.h2,
                color = MainColor
            )
        }
    }
}