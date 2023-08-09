package com.jh.presentation.ui.main.favorite

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.ModalBottomSheetValue.Expanded
import androidx.compose.material.ModalBottomSheetValue.Hidden
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale.Companion.FillBounds
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jh.murun.presentation.R
import com.jh.presentation.base.BaseActivity
import com.jh.presentation.ui.*
import com.jh.presentation.ui.main.MainActivity
import com.jh.presentation.ui.theme.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.*

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
                        startActivity(MainActivity.newIntent(this@FavoriteActivity, true))
                    }
                    is FavoriteSideEffect.ShowToast -> {
                        Toast.makeText(this@FavoriteActivity, sideEffect.text, Toast.LENGTH_SHORT).show()
                    }
                    is FavoriteSideEffect.UpdateReorderedFavoriteList -> {
                        viewModel.updateReorderedFavoriteList(sideEffect.musics)
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
                        Column(modifier = Modifier.weight(1f)) {
                            chosenMusic.let { music ->
                                Text(
                                    text = music?.title ?: "",
                                    style = Typography.body2,
                                    color = Gray1,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Text(
                                    text = music?.artist ?: "",
                                    style = Typography.body1,
                                    color = Gray2,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
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
                            .fillMaxWidth()
                            .clickable { chosenMusic.let { viewModel.onClickDeleteMusic() } },
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

                if (!isLoading) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        val isReordered = remember { mutableStateOf(false) }
                        val musics = remember { mutableStateOf(favoriteList) }
                        val reorderableState = rememberReorderableLazyListState(onMove = { from, to ->
                            musics.value = musics.value.toMutableList().apply {
                                add(to.index, removeAt(from.index))
                                isReordered.value = true
                            }
                        })
                        val lifecycle = LocalLifecycleOwner.current.lifecycle
                        val lifecycleEvent = remember { mutableStateOf(Lifecycle.Event.ON_ANY) }

                        DisposableEffect(lifecycle) {
                            val observer = LifecycleEventObserver { _, event ->
                                lifecycleEvent.value = event
                            }

                            lifecycle.addObserver(observer)

                            onDispose {
                                lifecycle.removeObserver(observer)
                            }
                        }

                        if (lifecycleEvent.value == Lifecycle.Event.ON_PAUSE && isReordered.value) {
                            viewModel.onReordered(musics.value.apply {
                                forEachIndexed { index, music ->
                                    music.newIndex = index
                                }
                            })
                        }

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 72.dp)
                                .reorderable(reorderableState)
                                .detectReorderAfterLongPress(reorderableState),
                            state = reorderableState.listState
                        ) {
                            items(musics.value, { it }) { music ->
                                ReorderableItem(reorderableState = reorderableState, key = music) { isDragging ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = SpaceBetween,
                                        verticalAlignment = CenterVertically
                                    ) {
                                        Row(
                                            modifier = Modifier.weight(1f),
                                            verticalAlignment = CenterVertically
                                        ) {
                                            Image(
                                                modifier = Modifier
                                                    .alpha(if (isDragging) 0.5f else 1f)
                                                    .padding(all = 12.dp)
                                                    .clip(shape = Shapes.large)
                                                    .size(60.dp),
                                                painter = if (music.image != null) BitmapPainter(convertImage(music.image!!))
                                                else painterResource(id = R.drawable.music_default),
                                                contentDescription = "albumCover",
                                                contentScale = FillBounds
                                            )

                                            Column {
                                                Text(
                                                    text = music.title,
                                                    style = Typography.body2,
                                                    color = Gray1,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )

                                                Text(
                                                    text = music.artist,
                                                    style = Typography.body1,
                                                    color = Gray2,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }

                                        Icon(
                                            modifier = Modifier
                                                .padding(end = 12.dp)
                                                .clickableWithoutRipple { viewModel.onClickShowMusicOption(music) },
                                            painter = painterResource(id = R.drawable.ic_option),
                                            contentDescription = "optionIcon",
                                            tint = Gray1
                                        )
                                    }
                                }

                                Divider(
                                    thickness = 1.dp,
                                    color = Gray0
                                )
                            }
                        }

                        RoundedCornerButton(
                            modifier = Modifier
                                .padding(all = 12.dp)
                                .fillMaxWidth()
                                .height(48.dp)
                                .align(BottomCenter),
                            backgroundColor = if (favoriteList.isNotEmpty()) MainColor else Color.LightGray,
                            text = "러닝 시작",
                            textColor = Color.White,
                            onClick = {
                                if (favoriteList.isNotEmpty()) {
                                    viewModel.onClickGoToMain()
                                }
                            }
                        )
                    }
                }
            }

            if (isLoading) {
                LoadingScreen()
            }
        }
    }
}
