package com.jh.presentation.ui.main.favorite

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.jh.murun.presentation.R
import com.jh.presentation.base.use
import com.jh.presentation.ui.LoadingScreen
import com.jh.presentation.ui.MurunSpacer
import com.jh.presentation.ui.RoundedCornerButton
import com.jh.presentation.ui.clickableWithoutRipple
import com.jh.presentation.ui.main.MainActivity
import com.jh.presentation.ui.main.favorite.FavoriteContract.Effect.ShowToast
import com.jh.presentation.ui.main.favorite.FavoriteContract.Effect.StartRunning
import com.jh.presentation.ui.main.favorite.FavoriteContract.Effect.UpdateReorderedFavoriteList
import com.jh.presentation.ui.main.favorite.FavoriteContract.Event.OnClickDeleteMusic
import com.jh.presentation.ui.main.favorite.FavoriteContract.Event.OnClickHideMusicOption
import com.jh.presentation.ui.main.favorite.FavoriteContract.Event.OnClickShowMusicOption
import com.jh.presentation.ui.main.favorite.FavoriteContract.Event.OnClickStartRunning
import com.jh.presentation.ui.main.favorite.FavoriteContract.Event.OnFavoriteListReordered
import com.jh.presentation.ui.main.favorite.FavoriteContract.Event.OnStarted
import com.jh.presentation.ui.theme.DarkFilter0
import com.jh.presentation.ui.theme.Gray0
import com.jh.presentation.ui.theme.Gray1
import com.jh.presentation.ui.theme.Gray2
import com.jh.presentation.ui.theme.MainColor
import com.jh.presentation.ui.theme.Shapes
import com.jh.presentation.ui.theme.SubColor
import com.jh.presentation.ui.theme.Typography
import com.jh.presentation.util.convertImage
import kotlinx.coroutines.flow.collectLatest
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FavoriteScreen(viewModel: FavoriteViewModel = hiltViewModel()) {
    val (state, event, effect) = use(viewModel)
    val context = LocalContext.current as ComponentActivity


    LaunchedEffect(effect) {
        effect.collectLatest { effect ->
            when (effect) {
                is StartRunning -> {
                    context.setResult(FavoriteActivity.RESULT_CODE_START_RUN)
                    context.finish()
                }

                is ShowToast -> {
                    Toast.makeText(context, effect.text, Toast.LENGTH_SHORT).show()
                }

                is UpdateReorderedFavoriteList -> {

                }
            }
        }
    }

    LaunchedEffect(Unit) {
        event(OnStarted)
    }

    val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

    with(state) {
        LaunchedEffect(isBottomSheetShowing) {
            if (isBottomSheetShowing) {
                bottomSheetState.show()
            } else {
                bottomSheetState.hide()
            }
        }

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
                        horizontalArrangement = Arrangement.SpaceBetween
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
                            modifier = Modifier.clickableWithoutRipple { event(OnClickHideMusicOption) },
                            painter = painterResource(id = R.drawable.ic_close),
                            contentDescription = "closeIcon",
                            tint = Gray1
                        )
                    }


                    Row(
                        modifier = Modifier
                            .padding(all = 12.dp)
                            .fillMaxWidth()
                            .clickable { chosenMusic.let { event(OnClickDeleteMusic) } },
                        verticalAlignment = Alignment.CenterVertically
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
                        modifier = Modifier.align(Alignment.Center),
                        text = "나의 플레이 리스트",
                        style = Typography.h4,
                        color = MainColor
                    )
                }

                Divider(color = SubColor)

                if (!isLoading) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        var isReordered by remember { mutableStateOf(false) }
                        val musics = remember { mutableStateOf(favoriteList) }
                        val reorderableState = rememberReorderableLazyListState(onMove = { from, to ->
                            musics.value = musics.value.toMutableList().apply {
                                add(to.index, removeAt(from.index))
                                isReordered = true
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

                        if (lifecycleEvent.value == Lifecycle.Event.ON_PAUSE && isReordered) {
                            event(OnFavoriteListReordered(musics.value.apply {
                                forEachIndexed { index, music ->
                                    music.newIndex = index
                                }
                            }))
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
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            modifier = Modifier.weight(1f),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Image(
                                                modifier = Modifier
                                                    .alpha(if (isDragging) 0.5f else 1f)
                                                    .padding(all = 12.dp)
                                                    .clip(shape = Shapes.large)
                                                    .size(60.dp),
                                                painter = if (music.image != null) BitmapPainter(convertImage(music.image!!)) else painterResource(id = R.drawable.music_default),
                                                contentDescription = "albumCover",
                                                contentScale = ContentScale.FillBounds
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
                                                .clickableWithoutRipple { event(OnClickShowMusicOption(music)) },
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
                                .align(Alignment.BottomCenter),
                            backgroundColor = if (favoriteList.isNotEmpty()) MainColor else Color.LightGray,
                            text = "러닝 시작",
                            textColor = Color.White,
                            onClick = {
                                if (favoriteList.isNotEmpty()) {
                                    event(OnClickStartRunning)
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