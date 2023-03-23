package com.jh.presentation.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.jh.murun.R
import com.jh.presentation.base.BaseActivity
import com.jh.presentation.base.BaseViewModel
import com.jh.presentation.ui.BorderedRoundedCornerButton
import com.jh.presentation.ui.clickableWithoutRipple
import com.jh.presentation.ui.main.favorite.FavoriteActivity
import com.jh.presentation.ui.theme.*

class MainActivity : BaseActivity() {
    private val cadenceTextFieldState = mutableStateOf("")

    override val viewModel: BaseViewModel
        get() = TODO("Not yet implemented")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initComposeUi {
            MainActivityContent(
                cadenceTextFieldState = cadenceTextFieldState,
                onClickFavorite = {
                    startActivity(FavoriteActivity.newIntent(this@MainActivity))
                }
            )
        }
    }

    override fun setupCollect() {

    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}

@Composable
private inline fun MainActivityContent(
    cadenceTextFieldState: MutableState<String>,
    crossinline onClickFavorite: () -> Unit
) {
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
                painter = painterResource(id = R.drawable.dummy_cover),
                contentDescription = "songInfoBackground",
                contentScale = ContentScale.Crop,
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
                Image(
                    modifier = Modifier
                        .clip(shape = Shapes.large)
                        .size(120.dp),
                    painter = painterResource(id = R.drawable.dummy_cover),
                    contentDescription = "albumCover",
                    contentScale = ContentScale.FillBounds
                )

                Column(
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .height(120.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Higher",
                            style = Typography.h3,
                            color = Color.White
                        )

                        Text(
                            text = "Tobu",
                            style = Typography.body1,
                            color = Gray0
                        )
                    }

                    Text(
                        text = "130 BPM",
                        style = Typography.h4,
                        color = MainColor
                    )
                }
            }
        }

        Box(
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
            Column(Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .padding(vertical = 36.dp)
                        .align(CenterHorizontally),
                    horizontalArrangement = Arrangement.spacedBy(36.dp)
                ) {
                    Icon(
                        modifier = Modifier.clickableWithoutRipple { },
                        painter = painterResource(id = R.drawable.ic_skip_prev),
                        contentDescription = "playIcon",
                        tint = Color.LightGray
                    )

                    Icon(
                        modifier = Modifier.clickableWithoutRipple { },
                        painter = painterResource(id = R.drawable.ic_play),
                        contentDescription = "playIcon",
                        tint = MainColor
                    )

                    Icon(
                        modifier = Modifier.clickableWithoutRipple { },
                        painter = painterResource(id = R.drawable.ic_skip_next),
                        contentDescription = "playIcon",
                        tint = Color.LightGray
                    )
                }

                Row(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BorderedRoundedCornerButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        borderColor = MainColor,
                        backgroundColor = Color.White,
                        text = "케이던스 트래킹",
                        textColor = MainColor,
                        onClick = {

                        }
                    )

                    BorderedRoundedCornerButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        borderColor = Color.LightGray,
                        backgroundColor = Color.White,
                        text = "케이던스 입력",
                        textColor = Color.LightGray,
                        onClick = {

                        }
                    )
                }

                Row(
                    modifier = Modifier
                        .padding(
                            horizontal = 12.dp,
                            vertical = 12.dp
                        )
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .border(
                                shape = Shapes.large,
                                width = 1.dp,
                                color = MainColor
                            )
                            .weight(1f)
                            .height(200.dp)
                    ) {
                        Text(
                            modifier = Modifier.align(Center),
                            text = "130", // cadence
                            style = Typography.h5,
                            color = MainColor
                        )
                    }

                    Box(
                        modifier = Modifier
                            .border(
                                shape = Shapes.large,
                                width = 1.dp,
                                color = Color.LightGray
                            )
                            .weight(1f)
                            .height(200.dp)
                    ) {
                        val focusManager = LocalFocusManager.current

                        CompositionLocalProvider(
                            LocalTextSelectionColors.provides(
                                TextSelectionColors(
                                    handleColor = MainColor,
                                    backgroundColor = Gray0
                                )
                            )
                        ) {
                            TextField(
                                modifier = Modifier.align(Center),
                                value = cadenceTextFieldState.value,
                                onValueChange = { cadenceTextFieldState.value = it },
                                placeholder = {
                                    Text(
                                        modifier = Modifier.fillMaxWidth(),
                                        text = "입력",
                                        style = Typography.h6,
                                        color = Color.LightGray
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
                                )
                            )
                        }
                    }
                }
            }

            BorderedRoundedCornerButton(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth()
                    .height(48.dp)
                    .align(BottomCenter),
                borderColor = MainColor,
                backgroundColor = MainColor,
                text = "러닝 시작",
                textColor = Color.White,
                onClick = {

                }
            )
        }

        FloatingActionButton(
            modifier = Modifier
                .padding(
                    end = 24.dp,
                    bottom = 48.dp
                )
                .size(48.dp)
                .align(BottomEnd),
            onClick = { onClickFavorite() }) {
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