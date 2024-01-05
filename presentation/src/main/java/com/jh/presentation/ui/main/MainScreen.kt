package com.jh.presentation.ui.main

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.layout.ContentScale.Companion.FillBounds
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.Player.REPEAT_MODE_ONE
import com.jh.murun.presentation.R
import com.jh.presentation.base.use
import com.jh.presentation.enums.LoadingMusicType.*
import com.jh.presentation.service.music_player.MusicPlayerState
import com.jh.presentation.ui.*
import com.jh.presentation.ui.main.MainContract.Effect.AssignCadence
import com.jh.presentation.ui.main.MainContract.Effect.ChangeRepeatMode
import com.jh.presentation.ui.main.MainContract.Effect.GoToFavorite
import com.jh.presentation.ui.main.MainContract.Effect.PlayOrPause
import com.jh.presentation.ui.main.MainContract.Effect.QuitRunning
import com.jh.presentation.ui.main.MainContract.Effect.ShowToast
import com.jh.presentation.ui.main.MainContract.Effect.SkipToNext
import com.jh.presentation.ui.main.MainContract.Effect.SkipToPrev
import com.jh.presentation.ui.main.MainContract.Effect.TrackCadence
import com.jh.presentation.ui.main.MainContract.Event.OnClickAddFavoriteMusic
import com.jh.presentation.ui.main.MainContract.Event.OnClickAssignCadence
import com.jh.presentation.ui.main.MainContract.Event.OnClickChangeRepeatMode
import com.jh.presentation.ui.main.MainContract.Event.OnClickFavorite
import com.jh.presentation.ui.main.MainContract.Event.OnClickPlayOrPause
import com.jh.presentation.ui.main.MainContract.Event.OnClickSkipToNext
import com.jh.presentation.ui.main.MainContract.Event.OnClickSkipToPrev
import com.jh.presentation.ui.main.MainContract.Event.OnClickStartRunning
import com.jh.presentation.ui.main.MainContract.Event.OnClickTrackCadence
import com.jh.presentation.ui.main.MainContract.Event.OnLongClickQuitRunning
import com.jh.presentation.ui.main.favorite.FavoriteActivity
import com.jh.presentation.ui.theme.*
import com.jh.presentation.util.convertImage
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalFoundationApi::class)
@Composable
inline fun MainScreen(
    musicPlayerState: MusicPlayerState,
    viewModel: MainViewModel = hiltViewModel(),
    crossinline onClickTrackCadence: () -> Unit,
    crossinline onClickAssignCadence: () -> Unit,
    crossinline onClickSkipToPrev: () -> Unit,
    crossinline onClickPlayOrPause: () -> Unit,
    crossinline onClickSkipToNext: () -> Unit,
    crossinline onClickChangeRepeatMode: () -> Unit,
    crossinline onQuitRunning: () -> Unit
) {
    val (state, event, effect) = use(viewModel)
    val context = LocalContext.current as ComponentActivity
    val focusManager = LocalFocusManager.current
    val cadenceAssignTextState = remember { mutableStateOf("") }

    LaunchedEffect(effect) {
        effect.collectLatest { effect ->
            when (effect) {
                is GoToFavorite -> {
                    context.startActivity(FavoriteActivity.newIntent(context))
                }

                is TrackCadence -> {
                    onClickTrackCadence()
                }

                is AssignCadence -> {
                    onClickAssignCadence()
                }

                is SkipToPrev -> {
                    onClickSkipToPrev()
                }

                is PlayOrPause -> {
                    onClickPlayOrPause()
                }

                is SkipToNext -> {
                    onClickSkipToNext()
                }

                is ChangeRepeatMode -> {
                    onClickChangeRepeatMode()
                }

                is QuitRunning -> {
                    onQuitRunning()
                }

                is ShowToast -> {
                    Toast.makeText(context, effect.text, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    with(state) {
        Box(modifier = Modifier.fillMaxSize()) {
            val bitmapByteArray = musicPlayerState.currentMediaItem?.mediaMetadata?.artworkData
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .blur(
                            radiusX = 2.dp,
                            radiusY = 2.dp
                        ),
                    painter = if (bitmapByteArray != null) BitmapPainter(convertImage(bitmapByteArray))
                    else painterResource(id = R.drawable.music_default),
                    contentDescription = "songInfoBackground",
                    contentScale = Crop,
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(color = DarkFilter1)
                )
            }

            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .padding(
                            vertical = 24.dp,
                            horizontal = 24.dp
                        )
                        .fillMaxWidth()
                ) {
                    Box {
                        Image(
                            modifier = Modifier
                                .clip(shape = Shapes.large)
                                .size(120.dp),
                            painter = if (bitmapByteArray != null) BitmapPainter(convertImage(bitmapByteArray))
                            else painterResource(id = R.drawable.music_default),
                            contentDescription = "albumCover",
                            contentScale = if (bitmapByteArray != null) FillBounds else Crop
                        )

                        if (musicPlayerState.currentMediaItem == null) {
                            Text(
                                modifier = Modifier.align(Center),
                                text = "No Music",
                                style = Typography.body1,
                                color = Gray0
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .height(120.dp),
                        verticalArrangement = SpaceBetween
                    ) {
                        musicPlayerState.currentMediaItem?.let { currentMediaItem ->
                            Column {
                                Text(
                                    text = currentMediaItem.mediaMetadata.title.toString(),
                                    style = Typography.h3,
                                    color = Color.White,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Text(
                                    text = currentMediaItem.mediaMetadata.artist.toString(),
                                    style = Typography.body1,
                                    color = Gray0,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .width(60.dp)
                                    .height(32.dp)
                                    .border(
                                        width = 2.dp,
                                        color = Red,
                                        shape = RoundedCornerShape(24.dp)
                                    )
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .align(Center)
                                        .clickableWithoutRipple { event(OnClickAddFavoriteMusic(musicPlayerState.currentMediaItem)) },
                                    painter = painterResource(id = R.drawable.ic_add),
                                    contentDescription = "favoriteIcon",
                                    tint = Red
                                )
                            }
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .padding(top = 168.dp)
                    .align(BottomCenter)
                    .clip(
                        shape = RoundedCornerShape(
                            topStart = 24.dp,
                            topEnd = 24.dp
                        )
                    )
                    .fillMaxSize()
                    .background(color = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .padding(vertical = 36.dp)
                        .height(48.dp)
                        .align(CenterHorizontally),
                    horizontalArrangement = Arrangement.spacedBy(36.dp)
                ) {
                    val iconColorState = animateColorAsState(targetValue = if (isRunning) MainColor else Color.LightGray)

                    Icon(
                        modifier = Modifier.clickableWithoutRipple { event(OnClickSkipToPrev) },
                        painter = painterResource(id = R.drawable.ic_skip_prev),
                        contentDescription = "skipToPrevIcon",
                        tint = iconColorState.value
                    )

                    Icon(
                        modifier = Modifier.clickableWithoutRipple { event(OnClickPlayOrPause) },
                        painter = painterResource(id = if (musicPlayerState.isPlaying) R.drawable.ic_pause else R.drawable.ic_play),
                        contentDescription = "playOrPauseIcon",
                        tint = iconColorState.value
                    )

                    Icon(
                        modifier = Modifier.clickableWithoutRipple { event(OnClickSkipToNext) },
                        painter = painterResource(id = R.drawable.ic_skip_next),
                        contentDescription = "skipToNextIcon",
                        tint = iconColorState.value
                    )

                    Icon(
                        modifier = Modifier.clickableWithoutRipple { event(OnClickChangeRepeatMode) },
                        painter = painterResource(id = if (musicPlayerState.repeatMode == REPEAT_MODE_ONE) R.drawable.ic_repeat_one else R.drawable.ic_repeat_all),
                        contentDescription = "repeatIcon",
                        tint = iconColorState.value
                    )
                }

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = SpaceBetween
                ) {
                    Column {
                        val cadenceTrackingColorState = animateColorAsState(targetValue = if (loadingMusicType == TRACKING_CADENCE) MainColor else Color.LightGray)
                        val cadenceTrackingAlphaState = animateFloatAsState(targetValue = if (loadingMusicType == ASSIGN_CADENCE && isRunning) 0.3f else 1f)
                        val cadenceAssignColorState = animateColorAsState(targetValue = if (loadingMusicType == ASSIGN_CADENCE) MainColor else Color.LightGray)
                        val cadenceAssignAlphaState = animateFloatAsState(targetValue = if (loadingMusicType == ASSIGN_CADENCE && isRunning) 0.3f else 1f)

                        Row(
                            modifier = Modifier
                                .padding(horizontal = 12.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .alpha(cadenceTrackingAlphaState.value)
                            ) {
                                BorderedRoundedCornerButton(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp),
                                    borderColor = cadenceTrackingColorState.value,
                                    backgroundColor = Color.White,
                                    text = "케이던스 트래킹",
                                    textColor = cadenceTrackingColorState.value,
                                    onClick = { if (!isRunning) event(OnClickTrackCadence) }
                                )

                                Box(
                                    modifier = Modifier
                                        .padding(top = 12.dp)
                                        .border(
                                            shape = Shapes.large,
                                            width = 1.dp,
                                            color = cadenceTrackingColorState.value,
                                        )
                                        .fillMaxWidth()
                                        .height(200.dp)
                                ) {
                                    Text(
                                        modifier = Modifier.align(Center),
                                        text = "", // cadence
                                        style = Typography.h5,
                                        color = cadenceTrackingColorState.value,
                                    )
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .alpha(cadenceAssignAlphaState.value)
                            ) {
                                BorderedRoundedCornerButton(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp),
                                    borderColor = cadenceAssignColorState.value,
                                    backgroundColor = Color.White,
                                    text = "케이던스 입력",
                                    textColor = cadenceAssignColorState.value,
                                    onClick = { if (!isRunning) event(OnClickAssignCadence) }
                                )

                                Box(
                                    modifier = Modifier
                                        .padding(top = 12.dp)
                                        .border(
                                            shape = Shapes.large,
                                            width = 1.dp,
                                            color = cadenceAssignColorState.value,
                                        )
                                        .fillMaxWidth()
                                        .height(200.dp)
                                ) {
                                    if (cadenceAssignTextState.value.length >= 3 && cadenceAssignTextState.value.toInt() > 180) {
                                        cadenceAssignTextState.value = ""
                                    }

                                    Column(
                                        modifier = Modifier.align(Center),
                                        horizontalAlignment = CenterHorizontally
                                    ) {
                                        Text(
                                            text = "60 이상 180 이하",
                                            style = Typography.body1,
                                            color = Gray3
                                        )

                                        CompositionLocalProvider(
                                            LocalTextSelectionColors.provides(
                                                TextSelectionColors(
                                                    handleColor = MainColor,
                                                    backgroundColor = Gray0
                                                )
                                            )
                                        ) {
                                            TextField(
                                                value = cadenceAssignTextState.value,
                                                onValueChange = { cadenceAssignTextState.value = it.filter { it.isDigit() } },
                                                placeholder = {
                                                    Text(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        text = "입력",
                                                        style = Typography.h6,
                                                        color = cadenceAssignColorState.value,
                                                    )
                                                },
                                                textStyle = Typography.h6,
                                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                                                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                                                singleLine = true,
                                                colors = TextFieldDefaults.textFieldColors(
                                                    textColor = MainColor,
                                                    backgroundColor = Color.White,
                                                    cursorColor = MainColor,
                                                    focusedIndicatorColor = Color.Transparent,
                                                    unfocusedIndicatorColor = Color.Transparent,
                                                    disabledIndicatorColor = Color.Transparent,
                                                ),
                                                enabled = loadingMusicType == ASSIGN_CADENCE
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    val buttonTextColorState = animateColorAsState(targetValue = if (isRunning) Red else Color.White)
                    val buttonBackgroundColorState = animateColorAsState(targetValue = if (isRunning) Color.White else MainColor)
                    val buttonBorderColorState = animateColorAsState(targetValue = if (isRunning) Red else MainColor)

                    Box {
                        BorderedRoundedCornerButton(
                            modifier = Modifier
                                .padding(all = 12.dp)
                                .fillMaxWidth()
                                .height(48.dp)
                                .align(BottomCenter)
                                .combinedClickable(
                                    interactionSource = MutableInteractionSource(),
                                    indication = null,
                                    onClick = { event(OnClickStartRunning) },
                                    onLongClick = { event(OnLongClickQuitRunning) }
                                ),
                            borderColor = buttonBorderColorState.value,
                            backgroundColor = buttonBackgroundColorState.value,
                            text = if (isRunning) "길게 눌러 러닝 종료" else "러닝 시작",
                            textColor = buttonTextColorState.value
                        )

                        if (!isRunning) {
                            FloatingActionButton(
                                modifier = Modifier
                                    .padding(
                                        end = 24.dp,
                                        bottom = 48.dp
                                    )
                                    .size(48.dp)
                                    .align(BottomEnd),
                                onClick = { event(OnClickFavorite) }) {
                                Icon(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .align(Center),
                                    painter = painterResource(id = R.drawable.ic_favorite),
                                    contentDescription = "favoriteIcon",
                                    tint = MainColor
                                )
                            }
                        }
                    }
                }
            }

            if (musicPlayerState.isLoading) {
                LoadingScreen()
            }
        }
    }
}