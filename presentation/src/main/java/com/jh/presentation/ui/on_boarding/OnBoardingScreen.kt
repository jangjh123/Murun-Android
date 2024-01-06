package com.jh.presentation.ui.on_boarding

import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.jh.murun.presentation.R
import com.jh.presentation.base.use
import com.jh.presentation.ui.MurunSpacer
import com.jh.presentation.ui.RoundedCornerButton
import com.jh.presentation.ui.main.MainActivity
import com.jh.presentation.ui.on_boarding.OnBoardingContract.Effect.GoToMainActivity
import com.jh.presentation.ui.on_boarding.OnBoardingContract.Event.OnClickGoToMain
import com.jh.presentation.ui.theme.DarkFilter0
import com.jh.presentation.ui.theme.Gray0
import com.jh.presentation.ui.theme.MainColor
import com.jh.presentation.ui.theme.Typography
import kotlinx.coroutines.flow.collectLatest

private val titles = arrayOf(
    "러닝에 음악은 꽤 중요합니다.",
    "박자를 지켜드릴게요.",
    "호흡에만 집중하세요."
)

private val bodys = arrayOf(
    "지루할 수 있는 당신의 러닝을 음악으로 채워보세요.\n적당한 크기의 볼륨은 러너스 하이에 쉽게 도달하게 해줘요.",
    "뮤런은 당신의 케이던스를 추적합니다.\n물론 원하는 음악의 속도를 지정할 수도 있어요.",
    "오늘도 운동화 끈을 묶고 집을 나서 봐요.\n신나는 러닝을 만들어드릴게요!"
)

@OptIn(ExperimentalPagerApi::class)
@Composable
fun OnBoardingScreen(viewModel: OnBoardingViewModel = hiltViewModel()) {
    val (state, event, effect) = use(viewModel)
    val context = LocalContext.current as ComponentActivity

    LaunchedEffect(effect) {
        effect.collectLatest { effect ->
            when (effect) {
                is GoToMainActivity -> {
                    context.startActivity(MainActivity.newIntent(context))
                    context.finish()
                }
            }
        }
    }

    val painterResources = arrayOf(
        painterResource(id = R.drawable.on_boarding_image_0),
        painterResource(id = R.drawable.on_boarding_image_1),
        painterResource(id = R.drawable.on_boarding_image_2)
    )

    HorizontalPager(
        modifier = Modifier.fillMaxSize(),
        count = 3
    ) { index ->
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResources[index],
                contentScale = ContentScale.Crop,
                contentDescription = "onBoardingBackgroundImage"
            )

            Column(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(0.7f))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.3f)
                        .background(color = DarkFilter0),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MurunSpacer(height = 24.dp)

                    Text(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        text = titles[index],
                        style = Typography.h3,
                        color = Color.White
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
                        .align(Alignment.BottomCenter),
                    backgroundColor = MainColor,
                    text = "뮤런과 함께 달리기",
                    textColor = Color.White,
                    onClick = { event(OnClickGoToMain) }
                )
            }
        }
    }
}