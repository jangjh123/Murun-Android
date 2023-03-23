package com.jh.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jh.presentation.ui.theme.MainColor
import com.jh.presentation.ui.theme.Shapes
import com.jh.presentation.ui.theme.Typography

fun Modifier.clickableWithoutRipple(onClick: () -> Unit): Modifier {
    return this.clickable(
        interactionSource = MutableInteractionSource(),
        indication = null
    ) {
        onClick()
    }
}

@Composable
fun MurunSpacer(
    width: Dp = 0.dp,
    height: Dp = 0.dp,
) {
    Spacer(
        modifier = Modifier
            .width(width)
            .height(height)
    )
}

@Composable
fun RoundedCornerButton(
    modifier: Modifier,
    backgroundColor: Color,
    text: String,
    textColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(shape = Shapes.large)
            .background(backgroundColor)
            .clickable { onClick() }
    ) {
        Text(
            modifier = Modifier.align(Center),
            text = text,
            style = Typography.body1,
            color = textColor
        )
    }
}

@Composable
fun BorderedRoundedCornerButton(
    modifier: Modifier,
    borderColor: Color,
    backgroundColor: Color,
    text: String,
    textColor: Color,
    onClick: (() -> Unit)? = null
) {
    if (onClick != null) {
        Box(
            modifier = modifier
                .clip(shape = Shapes.large)
                .border(
                    shape = Shapes.large,
                    width = 1.dp,
                    color = borderColor
                )
                .background(backgroundColor)
                .clickable { onClick() }
        ) {
            Text(
                modifier = Modifier.align(Center),
                text = text,
                style = Typography.body1,
                color = textColor
            )
        }
    } else {
        Box(
            modifier = modifier
                .clip(shape = Shapes.large)
                .border(
                    shape = Shapes.large,
                    width = 1.dp,
                    color = borderColor
                )
                .background(backgroundColor)
        ) {
            Text(
                modifier = Modifier.align(Center),
                text = text,
                style = Typography.body1,
                color = textColor
            )
        }
    }
}


@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0x09000000))
            .clickableWithoutRipple { }
    ) {
        CircularProgressIndicator(
            modifier = Modifier.align(Center),
            color = MainColor
        )
    }
}