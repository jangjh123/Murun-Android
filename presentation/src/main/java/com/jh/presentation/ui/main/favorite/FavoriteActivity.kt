package com.jh.presentation.ui.main.favorite

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jh.presentation.base.BaseActivity
import com.jh.presentation.ui.RoundedCornerButton
import com.jh.presentation.ui.main.MainActivity
import com.jh.presentation.ui.repeatOnStarted
import com.jh.presentation.ui.theme.MainColor
import com.jh.presentation.ui.theme.SubColor
import com.jh.presentation.ui.theme.Typography
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class FavoriteActivity : BaseActivity() {
    override val viewModel: FavoriteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initComposeUi {
            FavoriteActivityContent(
                viewModel = viewModel
            )
        }

        repeatOnStarted {
            viewModel.sideEffectChannelFlow.collectLatest { sideEffect ->
                when (sideEffect) {
                    is FavoriteSideEffect.StartRunning -> {
                        startActivity(MainActivity.newIntent(this@FavoriteActivity))
                        finish()
                    }
                }
            }
        }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, FavoriteActivity::class.java)
        }
    }
}

@Composable
private fun FavoriteActivityContent(
    viewModel: FavoriteViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(color = Color.White)
        ) {
            Text(
                modifier = Modifier.align(Center),
                text = "나의 플레이 리스트",
                style = Typography.h4,
                color = MainColor
            )
        }

        Divider(color = SubColor)

        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 72.dp)
            ) {

            }

            RoundedCornerButton(
                modifier = Modifier
                    .padding(all = 12.dp)
                    .fillMaxWidth()
                    .height(48.dp)
                    .align(BottomCenter),
                backgroundColor = MainColor,
                text = "러닝 시작",
                textColor = Color.White,
                onClick = { viewModel.onClickGoToMain() }
            )
        }
    }
}
