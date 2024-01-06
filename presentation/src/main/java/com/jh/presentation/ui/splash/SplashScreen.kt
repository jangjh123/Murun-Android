package com.jh.presentation.ui.splash

import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jh.murun.presentation.R
import com.jh.presentation.base.use
import com.jh.presentation.ui.main.MainActivity
import com.jh.presentation.ui.on_boarding.OnBoardingActivity
import com.jh.presentation.ui.theme.MainColor
import com.jh.presentation.ui.theme.Typography
import com.jh.presentation.ui.theme.pyeongchangPeace
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SplashScreen(viewModel: SplashViewModel = hiltViewModel()) {
    val (state, event, effect) = use(viewModel)
    val context = (LocalContext.current as ComponentActivity)

    LaunchedEffect(effect) {
        effect.collectLatest { effect ->
            when (effect) {
                is SplashContract.Effect.SkipOnBoarding -> {
                    context.startActivity(MainActivity.newIntent(context))
                    context.finish()
                }

                is SplashContract.Effect.NoSkipOnBoarding -> {
                    context.startActivity(
                        OnBoardingActivity.newIntent(context)
                    )

                    context.finish()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        delay(1500L)
        event(SplashContract.Event.OnStarted)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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