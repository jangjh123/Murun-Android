package com.jh.presentation.ui.splash

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jh.murun.presentation.R
import com.jh.presentation.base.BaseActivity
import com.jh.presentation.base.use
import com.jh.presentation.ui.main.MainActivity
import com.jh.presentation.ui.on_boarding.OnBoardingActivity
import com.jh.presentation.ui.splash.SplashContract.Effect.NoSkipOnBoarding
import com.jh.presentation.ui.splash.SplashContract.Effect.SkipOnBoarding
import com.jh.presentation.ui.splash.SplashContract.Event.OnStarted
import com.jh.presentation.ui.theme.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BaseActivity() {

    @Composable
    override fun InitComposeUi() {
        SplashActivityContent()
    }
}

@Composable
private fun SplashActivityContent(viewModel: SplashViewModel = hiltViewModel()) {
    val (state, event, effect) = use(viewModel)
    val context = (LocalContext.current as ComponentActivity)

    LaunchedEffect(effect) {
        effect.collectLatest { effect ->
            when (effect) {
                is SkipOnBoarding -> {
                    context.startActivity(
                        MainActivity.newIntent(
                            context = context,
                            isRunningStarted = false
                        )
                    )
                }

                is NoSkipOnBoarding -> {
                    context.startActivity(
                        OnBoardingActivity.newIntent(context)
                    )
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        delay(1500L)
        event(OnStarted)
    }

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