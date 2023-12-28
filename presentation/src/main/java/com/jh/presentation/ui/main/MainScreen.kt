package com.jh.presentation.ui.main

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
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jh.murun.presentation.R
import com.jh.presentation.base.use
import com.jh.presentation.enums.LoadingMusicType.*
import com.jh.presentation.service.cadence_tracking.CadenceTrackingService
import com.jh.presentation.ui.*
import com.jh.presentation.ui.main.MainContract.Effect.AddFavoriteMusic
import com.jh.presentation.ui.main.MainContract.Effect.ChangeRepeatMode
import com.jh.presentation.ui.main.MainContract.Effect.GoToFavorite
import com.jh.presentation.ui.main.MainContract.Effect.LaunchMusicPlayer
import com.jh.presentation.ui.main.MainContract.Effect.PlayOrPause
import com.jh.presentation.ui.main.MainContract.Effect.QuitMusicPlayer
import com.jh.presentation.ui.main.MainContract.Effect.ShowToast
import com.jh.presentation.ui.main.MainContract.Effect.SkipToNext
import com.jh.presentation.ui.main.MainContract.Effect.SkipToPrev
import com.jh.presentation.ui.main.MainContract.Effect.StopTrackingCadence
import com.jh.presentation.ui.main.MainContract.Effect.TrackCadence
import com.jh.presentation.ui.main.MainContract.Event.OnClickAddFavoriteMusic
import com.jh.presentation.ui.main.MainContract.Event.OnClickTrackCadence
import com.jh.presentation.ui.theme.*
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel()) {
    val (state, event, effect) = use(viewModel)
    val focusManager = LocalFocusManager.current
    val cadenceAssignTextState = remember { mutableStateOf("") }

    LaunchedEffect(effect) {
        effect.collectLatest { effect ->
            when (effect) {
                is GoToFavorite -> {

                }

                is TrackCadence -> {

                }

                is StopTrackingCadence -> {

                }

                is LaunchMusicPlayer -> {

                }

                is QuitMusicPlayer -> {

                }

                is ChangeRepeatMode -> {

                }

                is SkipToPrev -> {

                }

                is PlayOrPause -> {

                }

                is SkipToNext -> {

                }

                is AddFavoriteMusic -> {

                }

                is ShowToast -> {

                }
            }
        }
    }

    CadenceTrackingService.cadenceLiveData.observe(LocalContext.current as MainActivity) {
//        trackedCadence.value = it
    }

    with(state) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
//                Image(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(200.dp)
//                        .blur(
//                            radiusX = 2.dp,
//                            radiusY = 2.dp
//                        ),
//                    painter = if (player.currentMusic?.mediaMetadata?.artworkData != null) BitmapPainter(convertImage(player.currentMusic.mediaMetadata.artworkData!!))
//                    else painterResource(id = R.drawable.music_default),
//                    contentDescription = "songInfoBackground",
//                    contentScale = Crop,
//                )

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
//                        Image(
//                            modifier = Modifier
//                                .clip(shape = Shapes.large)
//                                .size(120.dp),
//                            painter = if (player.currentMusic?.mediaMetadata?.artworkData != null) BitmapPainter(convertImage(player.currentMusic.mediaMetadata.artworkData!!))
//                            else painterResource(id = R.drawable.music_default),
//                            contentDescription = "albumCover",
//                            contentScale = if (player.currentMusic?.mediaMetadata?.artworkData?.isNotEmpty() == true) FillBounds else Crop
//                        )
//
//                        if (player.currentMusic == null) {
//                            Text(
//                                modifier = Modifier.align(Center),
//                                text = "No Music",
//                                style = Typography.body1,
//                                color = Gray0
//                            )
//                        }
                    }

                    Column(
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .height(120.dp),
                        verticalArrangement = SpaceBetween
                    ) {
                        Column {
//                            Text(
//                                text = if (player.currentMusic != null) player.currentMusic.mediaMetadata.title.toString() else "",
//                                style = Typography.h3,
//                                color = Color.White,
//                                maxLines = 2,
//                                overflow = TextOverflow.Ellipsis
//                            )
//
//                            Text(
//                                text = if (player.currentMusic != null) player.currentMusic.mediaMetadata.artist.toString() else "",
//                                style = Typography.body1,
//                                color = Gray0,
//                                maxLines = 1,
//                                overflow = TextOverflow.Ellipsis
//                            )
                        }

//                        if (player.currentMusic != null) {
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
                                    .clickableWithoutRipple { event(OnClickAddFavoriteMusic) },
                                painter = painterResource(id = R.drawable.ic_add),
                                contentDescription = "favoriteIcon",
                                tint = Red
                            )
                        }
//                        }
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

//                    Icon(
//                        modifier = Modifier.clickableWithoutRipple { if (playerUiState.value.isLaunched) viewModel.onClickSkipToPrev() else Unit },
//                        painter = painterResource(id = R.drawable.ic_skip_prev),
//                        contentDescription = "skipToPrevIcon",
//                        tint = iconColorState.value
//                    )
//
//                    Icon(
//                        modifier = Modifier.clickableWithoutRipple { if (playerUiState.value.isLaunched) viewModel.onClickPlayOrPause() else Unit },
//                        painter = painterResource(id = if (player.isPlaying) R.drawable.ic_pause else R.drawable.ic_play),
//                        contentDescription = "playOrPauseIcon",
//                        tint = iconColorState.value
//                    )
//
//                    Icon(
//                        modifier = Modifier.clickableWithoutRipple { if (playerUiState.value.isLaunched) viewModel.onClickSkipToNext() else Unit },
//                        painter = painterResource(id = R.drawable.ic_skip_next),
//                        contentDescription = "skipToNextIcon",
//                        tint = iconColorState.value
//                    )
//
//                    Icon(
//                        modifier = Modifier.clickableWithoutRipple { if (playerUiState.value.isLaunched) viewModel.onClickChangeRepeatMode() else Unit },
//                        painter = painterResource(id = if (player.isRepeatingOne) R.drawable.ic_repeat_one else R.drawable.ic_repeat_all),
//                        contentDescription = "repeatIcon",
//                        tint = iconColorState.value
//                    )
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
//                                    Text(
//                                        modifier = Modifier.align(Center),
//                                        text = "${trackedCadence.value}",
//                                        style = Typography.h5,
//                                        color = cadenceTrackingColorState.value,
//                                    )
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .alpha(cadenceAssignAlphaState.value)
                            ) {
//                                BorderedRoundedCornerButton(
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .height(48.dp),
//                                    borderColor = cadenceAssignColorState.value,
//                                    backgroundColor = Color.White,
//                                    text = "케이던스 입력",
//                                    textColor = cadenceAssignColorState.value,
//                                    onClick = { if (!isRunning) viewModel.onClickAssignCadence() }
//                                )

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
                                    onClick = {
                                        if (!isRunning) {
                                            when (loadingMusicType) {
                                                TRACKING_CADENCE -> {
//                                                    viewModel.onClickStartRunning(null)
                                                }

                                                ASSIGN_CADENCE -> {
                                                    if (cadenceAssignTextState.value.isNotEmpty() &&
                                                        cadenceAssignTextState.value.toInt() in 60..180
                                                    ) {
//                                                        viewModel.onClickStartRunning(cadenceAssignTextState.value.toInt())
                                                    }
                                                }

                                                NONE -> {
//                                                    viewModel.showToast("케이던스 타입을 지정해 주세요.")
                                                }

                                                FAVORITE_LIST -> Unit
                                            }
                                        }
                                    },
                                    onLongClick = {
                                        if (isRunning) {
//                                            viewModel.onClickStopRunning()
                                        }
                                    }
                                ),
                            borderColor = buttonBorderColorState.value,
                            backgroundColor = buttonBackgroundColorState.value,
                            text = if (isRunning) "길게 눌러 러닝 종료" else "러닝 시작",
                            textColor = buttonTextColorState.value
                        )

                        if (!isRunning) {
//                            FloatingActionButton(
//                                modifier = Modifier
//                                    .padding(
//                                        end = 24.dp,
//                                        bottom = 48.dp
//                                    )
//                                    .size(48.dp)
//                                    .align(BottomEnd),
//                                onClick = { viewModel.onClickFavorite() }) {
//                                Icon(
//                                    modifier = Modifier
//                                        .size(24.dp)
//                                        .align(Center),
//                                    painter = painterResource(id = R.drawable.ic_favorite),
//                                    contentDescription = "favoriteIcon",
//                                    tint = MainColor
//                                )
                        }
                    }
                }
            }
        }

//            if (player.isLoading) {
//                LoadingScreen()
//            }
    }
}