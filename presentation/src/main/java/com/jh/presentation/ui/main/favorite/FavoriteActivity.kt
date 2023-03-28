package com.jh.presentation.ui.main.favorite

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.ModalBottomSheetValue.Expanded
import androidx.compose.material.ModalBottomSheetValue.Hidden
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale.Companion.FillBounds
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jh.murun.presentation.R
import com.jh.presentation.base.BaseActivity
import com.jh.presentation.ui.MurunSpacer
import com.jh.presentation.ui.RoundedCornerButton
import com.jh.presentation.ui.clickableWithoutRipple
import com.jh.presentation.ui.main.MainActivity
import com.jh.presentation.ui.repeatOnStarted
import com.jh.presentation.ui.theme.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun FavoriteActivityContent(
    viewModel: FavoriteViewModel
) {
    val bottomSheetState = rememberModalBottomSheetState(initialValue = Hidden)
    val coroutineScope = rememberCoroutineScope()

    with(viewModel.state.collectAsStateWithLifecycle().value) {
        coroutineScope.launch {
            if (bottomSheetStateValue == Hidden) {
                bottomSheetState.hide()
            } else if (bottomSheetStateValue == Expanded) {
                bottomSheetState.show()
            }
        }

        viewModel.onInitBottomSheetState()

        ModalBottomSheetLayout(
            sheetState = bottomSheetState,
            scrimColor = DarkFilter0,
            sheetContent = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color.White),
                ) {
                    Row(
                        modifier = Modifier
                            .padding(all = 12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Music Title",
                                style = Typography.body2,
                                color = Gray1
                            )

                            Text(
                                text = "Artist Name",
                                style = Typography.body1,
                                color = Gray2
                            )
                        }

                        Icon(
                            modifier = Modifier.clickableWithoutRipple { viewModel.onClickHideMusicOption() },
                            painter = painterResource(id = R.drawable.ic_close),
                            contentDescription = "closeIcon",
                            tint = Gray1
                        )
                    }


                    Row(
                        modifier = Modifier
                            .padding(all = 12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(id = R.drawable.ic_remove),
                            contentDescription = "removeIcon",
                            tint = Gray2
                        )

                        MurunSpacer(width = 12.dp)

                        Text(
                            text = "삭제하기",
                            style = Typography.subtitle1,
                            color = Gray1
                        )
                    }
                }
            }
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
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = SpaceBetween,
                                verticalAlignment = CenterVertically
                            ) {
                                Row(verticalAlignment = CenterVertically) {
                                    Image(
                                        modifier = Modifier
                                            .padding(all = 12.dp)
                                            .clip(shape = Shapes.large)
                                            .size(60.dp),
                                        painter = painterResource(id = R.drawable.dummy_cover),
                                        contentDescription = "albumCover",
                                        contentScale = FillBounds
                                    )

                                    Column {
                                        Text(
                                            text = "Music Title",
                                            style = Typography.body2,
                                            color = Gray1
                                        )

                                        Text(
                                            text = "Artist Name",
                                            style = Typography.body1,
                                            color = Gray2
                                        )
                                    }
                                }

                                Icon(
                                    modifier = Modifier
                                        .padding(end = 12.dp)
                                        .clickableWithoutRipple { viewModel.onClickShowMusicOption() },
                                    painter = painterResource(id = R.drawable.ic_option),
                                    contentDescription = "optionIcon",
                                    tint = Gray1
                                )
                            }
                        }
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
    }
}
