package com.jh.presentation.ui.main

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import androidx.activity.viewModels
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.jh.murun.domain.model.Music
import com.jh.murun.presentation.R
import com.jh.presentation.base.BaseActivity
import com.jh.presentation.enums.LoadingMusicType.*
import com.jh.presentation.service.cadence_tracking.CadenceTrackingService
import com.jh.presentation.service.cadence_tracking.CadenceTrackingService.CadenceTrackingServiceBinder
import com.jh.presentation.service.music_player.MusicPlayerService
import com.jh.presentation.service.music_player.MusicPlayerService.MusicPlayerServiceBinder
import com.jh.presentation.service.music_player.MusicPlayerState
import com.jh.presentation.ui.*
import com.jh.presentation.ui.main.MainEvent.*
import com.jh.presentation.ui.main.favorite.FavoriteActivity
import com.jh.presentation.ui.theme.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    override val viewModel: MainViewModel by viewModels()

    private lateinit var musicPlayerService: MusicPlayerService
    private var isMusicPlayerServiceBinding = false
    private val playerUiState = mutableStateOf(MusicPlayerState())
    private val musicPlayerServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder: MusicPlayerServiceBinder = service as MusicPlayerServiceBinder
            musicPlayerService = binder.getServiceInstance()
            isMusicPlayerServiceBinding = true
            musicPlayerService.setState(mainState = viewModel.state.value)

            lifecycleScope.launch {
                repeatOnResumed {
                    musicPlayerService.state.collectLatest { playerState ->
                        playerUiState.value = playerState
                    }
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isMusicPlayerServiceBinding = false
        }
    }

    private lateinit var cadenceTrackingService: CadenceTrackingService
    private var isCadenceTrackingServiceBinding = false
    private val cadenceTrackingServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder: CadenceTrackingServiceBinder = service as CadenceTrackingServiceBinder
            cadenceTrackingService = binder.getServiceInstance()
            isCadenceTrackingServiceBinding = true
            trackCadence()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isCadenceTrackingServiceBinding = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initComposeUi {
            MainActivityContent(playerUiState)
        }

        repeatOnStarted {
            viewModel.sideEffectChannelFlow.collectLatest { sideEffect ->
                when (sideEffect) {
                    is MainSideEffect.SkipToPrev -> {
                        musicPlayerService.skipToPrev()
                    }
                    is MainSideEffect.PlayOrPause -> {
                        musicPlayerService.playOrPause()
                    }
                    is MainSideEffect.SkipToNext -> {
                        musicPlayerService.skipToNext()
                    }
                    is MainSideEffect.GoToFavorite -> {
                        startActivity(FavoriteActivity.newIntent(this@MainActivity))
                    }
                    is MainSideEffect.TrackCadence -> {
                        if (!isCadenceTrackingServiceBinding) {
                            bindService(Intent(this@MainActivity, CadenceTrackingService::class.java), cadenceTrackingServiceConnection, Context.BIND_AUTO_CREATE)
                        }
                    }
                    is MainSideEffect.StopTrackingCadence -> {
                        cadenceTrackingService.stop()
                        unbindService(cadenceTrackingServiceConnection)
                    }
                    is MainSideEffect.LaunchMusicPlayer -> {
                        if (!isMusicPlayerServiceBinding) {
                            bindService(Intent(this@MainActivity, MusicPlayerService::class.java), musicPlayerServiceConnection, Context.BIND_AUTO_CREATE)
                        }
                    }
                    is MainSideEffect.QuitMusicPlayer -> {
                        if (isMusicPlayerServiceBinding) {
                            unbindService(musicPlayerServiceConnection)
                        }
                    }
                    is MainSideEffect.ChangeRepeatMode -> {
                        musicPlayerService.changeRepeatMode()
                    }
                    is MainSideEffect.LikeMusic -> {
                        viewModel.likeMusic(
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                playerUiState.value.currentMusic?.mediaMetadata?.extras?.getParcelable("music", Music::class.java)
                            } else {
                                playerUiState.value.currentMusic?.mediaMetadata?.extras?.getParcelable("music")!!
                            }
                        )
                    }
                    is MainSideEffect.DislikeMusic -> {
                        viewModel.dislikeMusic(
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                playerUiState.value.currentMusic?.mediaMetadata?.extras?.getParcelable("music", Music::class.java)?.id ?: ""
                            } else {
                                playerUiState.value.currentMusic?.mediaMetadata?.extras?.getParcelable<Music>("music")?.id ?: ""
                            }
                        )
                    }
                    is MainSideEffect.ShowToast -> {
                        Toast.makeText(this@MainActivity, sideEffect.text, Toast.LENGTH_SHORT).show()
                    }
                    is MainSideEffect.UpdateLikeIcon -> {
                        musicPlayerService.setCurrentMusicIsStoredOrNot(sideEffect.isStored)
                    }
                }
            }
        }
    }

    private fun trackCadence() {
        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
            cadenceTrackingService.start(this@MainActivity)
            cadenceTrackingService.cadenceLiveData.observe(this@MainActivity) { cadence ->
                viewModel.onCadenceMeasured(cadence)
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                requestPermissions(arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), PackageManager.PERMISSION_GRANTED)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getIsStartedRunningWithFavoriteList()
    }

    override fun onDestroy() {
        if (isCadenceTrackingServiceBinding) {
            unbindService(cadenceTrackingServiceConnection)
        }

        if (isMusicPlayerServiceBinding) {
            unbindService(musicPlayerServiceConnection)
        }
        super.onDestroy()
    }

    companion object {
        const val KEY_IS_RUNNING_STARTED = "isRunningStarted"

        fun newIntent(context: Context, isRunningStarted: Boolean?): Intent {
            return Intent(context, MainActivity::class.java).apply {
                putExtra(KEY_IS_RUNNING_STARTED, isRunningStarted)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MainActivityContent(
    playerUiState: MutableState<MusicPlayerState>,
    viewModel: MainViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    val cadenceAssignTextState = remember { mutableStateOf("") }
    val player = playerUiState.value

    with(viewModel.state.collectAsStateWithLifecycle().value) {
        Box(modifier = Modifier.fillMaxSize()) {
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
                    painter = if (player.currentMusic?.mediaMetadata?.artworkData != null) BitmapPainter(convertImage(player.currentMusic.mediaMetadata.artworkData!!))
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
                            painter = if (player.currentMusic?.mediaMetadata?.artworkData != null) BitmapPainter(convertImage(player.currentMusic.mediaMetadata.artworkData!!))
                            else painterResource(id = R.drawable.music_default),
                            contentDescription = "albumCover",
                            contentScale = if (player.currentMusic?.mediaMetadata?.artworkData?.isNotEmpty() == true) FillBounds else Crop
                        )

                        if (player.currentMusic == null) {
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
                        Column {
                            Text(
                                text = if (player.currentMusic != null) player.currentMusic.mediaMetadata.title.toString() else "",
                                style = Typography.h3,
                                color = Color.White
                            )

                            Text(
                                text = if (player.currentMusic != null) player.currentMusic.mediaMetadata.artist.toString() else "",
                                style = Typography.body1,
                                color = Gray0
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = SpaceBetween
                        ) {
                            Text(
                                text = "BPM", // TODO : Should insert bpm to metadata
                                style = Typography.h4,
                                color = MainColor
                            )

                            Icon(
                                modifier = Modifier.clickableWithoutRipple { viewModel.onClickLikeOrDislike(player.isCurrentMusicStored) },
                                painter = painterResource(id = if (player.isCurrentMusicStored) R.drawable.ic_favorite_fill else R.drawable.ic_favorite_empty),
                                contentDescription = "favoriteIcon",
                                tint = if (player.isCurrentMusicStored) Red else Color.Gray
                            )
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
                    val iconColorState = animateColorAsState(targetValue = if (player.isLaunched) MainColor else Color.LightGray)

                    Icon(
                        modifier = Modifier.clickableWithoutRipple { viewModel.onClickSkipToPrev() },
                        painter = painterResource(id = R.drawable.ic_skip_prev),
                        contentDescription = "skipToPrevIcon",
                        tint = iconColorState.value
                    )

                    Icon(
                        modifier = Modifier.clickableWithoutRipple { viewModel.onClickPlayOrPause() },
                        painter = painterResource(id = if (player.isPlaying) R.drawable.ic_pause else R.drawable.ic_play),
                        contentDescription = "playOrPauseIcon",
                        tint = iconColorState.value
                    )

                    Icon(
                        modifier = Modifier.clickableWithoutRipple { viewModel.onClickSkipToNext() },
                        painter = painterResource(id = R.drawable.ic_skip_next),
                        contentDescription = "skipToNextIcon",
                        tint = iconColorState.value
                    )

                    Icon(
                        modifier = Modifier.clickableWithoutRipple { viewModel.onClickChangeRepeatMode() },
                        painter = painterResource(id = if (player.isRepeatingOne) R.drawable.ic_repeat_one else R.drawable.ic_repeat_all),
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
                                    onClick = { if (!isRunning) viewModel.onClickTrackCadence() }
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
                                        text = "$measuredCadence",
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
                                    onClick = { if (!isRunning) viewModel.onClickAssignCadence() }
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
                                                onValueChange = { cadenceAssignTextState.value = it },
                                                placeholder = {
                                                    Text(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        text = "입력",
                                                        style = Typography.h6,
                                                        color = cadenceAssignColorState.value,
                                                    )
                                                },
                                                textStyle = Typography.h6,
                                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
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
                                                    viewModel.onClickStartRunning(null)
                                                }
                                                ASSIGN_CADENCE -> {
                                                    if (cadenceAssignTextState.value.isNotEmpty() &&
                                                        cadenceAssignTextState.value.toInt() in 60..180
                                                    ) {
                                                        viewModel.onClickStartRunning(cadenceAssignTextState.value.toInt())
                                                    }
                                                }
                                                NONE -> {
                                                    viewModel.showToast("케이던스 타입을 지정해 주세요.")
                                                }
                                                FAVORITE_LIST -> Unit
                                            }
                                        }
                                    },
                                    onLongClick = {
                                        if (isRunning) {
                                            viewModel.onClickStopRunning()
                                        }
                                    }
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
                                onClick = { viewModel.onClickFavorite() }) {
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

            if (player.isLoading) {
                LoadingScreen()
            }
        }
    }
}