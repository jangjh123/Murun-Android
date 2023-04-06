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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.layout.ContentScale.Companion.FillBounds
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jh.murun.presentation.R
import com.jh.presentation.base.BaseActivity
import com.jh.presentation.enums.CadenceType.*
import com.jh.presentation.ui.*
import com.jh.presentation.ui.main.MainEvent.*
import com.jh.presentation.ui.main.favorite.FavoriteActivity
import com.jh.presentation.ui.service.CadenceTrackingService
import com.jh.presentation.ui.service.CadenceTrackingService.CadenceTrackingServiceBinder
import com.jh.presentation.ui.theme.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    override val viewModel: MainViewModel by viewModels()

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
            MainActivityContent(viewModel = viewModel)
        }

        repeatOnStarted {
            viewModel.sideEffectChannelFlow.collectLatest { sideEffect ->
                when (sideEffect) {
                    is MainSideEffect.GoToFavorite -> {
                        startActivity(FavoriteActivity.newIntent(this@MainActivity))
                    }
                    is MainSideEffect.TrackCadence -> {
                        bindService(Intent(this@MainActivity, CadenceTrackingService::class.java), cadenceTrackingServiceConnection, Context.BIND_AUTO_CREATE)
                    }
                    is MainSideEffect.StopTrackingCadence -> {
                        cadenceTrackingService.stop()
                        unbindService(cadenceTrackingServiceConnection)
                    }
                }
            }
        }
    }

    private fun trackCadence() {
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACTIVITY_RECOGNITION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
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

    override fun onDestroy() {
        if (::cadenceTrackingService.isInitialized && isCadenceTrackingServiceBinding) {
            unbindService(cadenceTrackingServiceConnection)
        }

        super.onDestroy()
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MainActivityContent(
    viewModel: MainViewModel
) {
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val cadenceAssignTextState = remember { mutableStateOf("") }

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
                    painter = if (image != null) BitmapPainter(image) else painterResource(id = R.drawable.music_default),
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
                            painter = if (image != null) BitmapPainter(image) else painterResource(id = R.drawable.music_default),
                            contentDescription = "albumCover",
                            contentScale = if (image != null) FillBounds else Crop
                        )

                        Text(
                            modifier = Modifier.align(Center),
                            text = "No Music",
                            style = Typography.body1,
                            color = Gray0
                        )
                    }

                    Column(
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .height(120.dp),
                        verticalArrangement = SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = title,
                                style = Typography.h3,
                                color = Color.White
                            )

                            Text(
                                text = artist,
                                style = Typography.body1,
                                color = Gray0
                            )
                        }

                        Text(
                            text = "$bpm BPM",
                            style = Typography.h4,
                            color = MainColor
                        )
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
                    Icon(
                        modifier = Modifier.clickableWithoutRipple { viewModel.onClickSkipToPrev() },
                        painter = painterResource(id = R.drawable.ic_skip_prev),
                        contentDescription = "skipToPrevIcon",
                        tint = Color.LightGray
                    )

                    Icon(
                        modifier = Modifier.clickableWithoutRipple { viewModel.onClickPlayOrPause() },
                        painter = painterResource(id = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play),
                        contentDescription = "playOrPauseIcon",
                        tint = MainColor
                    )

                    Icon(
                        modifier = Modifier.clickableWithoutRipple { viewModel.onClickSkipToNext() },
                        painter = painterResource(id = R.drawable.ic_skip_next),
                        contentDescription = "skipToNextIcon",
                        tint = Color.LightGray
                    )

                    Icon(
                        modifier = Modifier.clickableWithoutRipple { viewModel.onClickRepeatOne() },
                        painter = painterResource(id = R.drawable.ic_repeat_one),
                        contentDescription = "repeatIcon",
                        tint = if (isRepeatingOne) MainColor else Color.LightGray
                    )
                }

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = SpaceBetween
                ) {
                    Column {
                        val cadenceTrackingColorState = animateColorAsState(targetValue = if (cadenceType == TRACKING) MainColor else Color.LightGray)
                        val cadenceTrackingAlphaState = animateFloatAsState(targetValue = if (cadenceType == ASSIGN && isRunning) 0.3f else 1f)
                        val cadenceAssignColorState = animateColorAsState(targetValue = if (cadenceType == ASSIGN) MainColor else Color.LightGray)
                        val cadenceAssignAlphaState = animateFloatAsState(targetValue = if (cadenceType == TRACKING && isRunning) 0.3f else 1f)

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
                                                enabled = cadenceType == ASSIGN
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
                                            when (cadenceType) {
                                                TRACKING -> {
                                                    viewModel.onClickStartRunning(null)
                                                }
                                                ASSIGN -> {
                                                    if (cadenceAssignTextState.value.isNotEmpty() &&
                                                        cadenceAssignTextState.value.toInt() in 60..180) {
                                                        viewModel.onClickStartRunning(cadenceAssignTextState.value.toInt())
                                                    }
                                                }
                                                NONE -> {
                                                    viewModel.showSnackBar()
                                                    scope.launch {
                                                        delay(3000L)
                                                        viewModel.hideSnackBar()
                                                    }
                                                }
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

            if (isLoading) {
                LoadingScreen()
            }

            if (isSnackBarVisible) {
                Snackbar(
                    backgroundColor = MainColor,
                    shape = RectangleShape
                ) {
                    Text(
                        modifier = Modifier.padding(all = 12.dp),
                        text = "케이던스 타입을 지정해 주세요.",
                        style = Typography.body1,
                        color = Color.White
                    )
                }
            }
        }
    }
}
