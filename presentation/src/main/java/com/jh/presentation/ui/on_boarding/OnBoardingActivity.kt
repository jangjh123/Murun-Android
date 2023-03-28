package com.jh.presentation.ui.on_boarding

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.jh.murun.presentation.R
import com.jh.presentation.base.BaseActivity
import com.jh.presentation.ui.MurunSpacer
import com.jh.presentation.ui.RoundedCornerButton
import com.jh.presentation.ui.main.MainActivity
import com.jh.presentation.ui.repeatOnStarted
import com.jh.presentation.ui.theme.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class OnBoardingActivity : BaseActivity() {
    override val viewModel: OnBoardingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initComposeUi {
            OnBoardingActivityContent(
                viewModel = viewModel
            )
        }

        repeatOnStarted {
            viewModel.sideEffectChannelFlow.collectLatest { sideEffect ->
                when(sideEffect) {
                    is OnBoardingSideEffect.GoToMainActivity -> {
                        startActivity(MainActivity.newIntent(this@OnBoardingActivity))
                        finish()
                    }
                }
            }
        }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, OnBoardingActivity::class.java)
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun OnBoardingActivityContent(
    viewModel: OnBoardingViewModel
) {
    val painterResources = arrayOf(
        painterResource(id = R.drawable.on_boarding_image_0),
        painterResource(id = R.drawable.on_boarding_image_1),
        painterResource(id = R.drawable.on_boarding_image_2)
    )

    val titles = arrayOf(
        "러닝에 음악은 꽤 중요합니다.",
        "박자를 지켜드릴게요.",
        "호흡에만 집중하세요."
    )

    val bodys = arrayOf(
        "지루할 수 있는 당신의 러닝을 음악으로 채워보세요.\n적당한 크기의 볼륨은 러너스 하이에 쉽게 도달하게 해줘요.",
        "뮤런은 당신의 케이던스를 추적합니다.\n물론 원하는 음악의 속도를 지정할 수도 있어요.",
        "오늘도 운동화 끈을 묶고 집을 나서 봐요.\n신나는 러닝을 만들어드릴게요!"
    )

    HorizontalPager(
        modifier = Modifier.fillMaxSize(),
        count = 3
    ) { index ->
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResources[index],
                contentScale = Crop,
                contentDescription = "onBoardingBackgroundImage"
            )

            Column(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(0.7f))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.3f)
                        .background(color = DarkFilter0),
                    horizontalAlignment = CenterHorizontally
                ) {
                    MurunSpacer(height = 24.dp)

                    Text(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        text = titles[index],
                        style = Typography.h3,
                        color = White
                    )

                    MurunSpacer(height = 4.dp)

                    Text(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        text = bodys[index],
                        style = Typography.body1,
                        textAlign = TextAlign.Center,
                        color = Gray0
                    )
                }
            }

            if (index == 2) {
                RoundedCornerButton(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth()
                        .height(48.dp)
                        .align(BottomCenter),
                    backgroundColor = MainColor,
                    text = "뮤런과 함께 달리기",
                    textColor = White,
                    onClick = { viewModel.onClickGoToMain() }
                )
            }
        }
    }
}