package com.jh.presentation.ui.splash

import android.annotation.SuppressLint
import android.os.Bundle
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
import com.jh.murun.R
import com.jh.presentation.base.BaseActivity
import com.jh.presentation.base.BaseViewModel
import com.jh.presentation.ui.on_boarding.OnBoardingActivity
import com.jh.presentation.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {
    override val viewModel: BaseViewModel
        get() = TODO("Not yet implemented")

    override fun onCreate(savedInstanceState: Bundle?) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                delay(1500L)
                startActivity(OnBoardingActivity.newIntent(this@SplashActivity))
                finish()
            }
        }

        super.onCreate(savedInstanceState)
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