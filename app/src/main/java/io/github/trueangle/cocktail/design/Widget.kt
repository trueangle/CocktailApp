package io.github.trueangle.cocktail.design

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.trueangle.cocktail.domain.model.RequestException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    modifier: Modifier = Modifier,
    title: String,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onNavUp: (() -> Unit)? = null
) {
    MediumTopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = title,
                modifier = Modifier,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            titleContentColor = MaterialTheme.colorScheme.onBackground,
        ),
        navigationIcon = {
            onNavUp?.let {
                IconButton(onClick = it) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Navigate up",
                    )
                }
            }
        },
        scrollBehavior = scrollBehavior
    )
}

@Composable
fun ErrorView(modifier: Modifier, error: RequestException, onReload: (() -> Unit)? = null) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val message = when (error) {
            is RequestException.HttpException -> error.message ?: "Http exception"
            is RequestException.Network -> "Network error occurred. Please try again"
            else -> "Error loading data. Please try again"
        }

        Text(
            modifier = Modifier
                .padding(vertical = 32.dp)
                .align(Alignment.CenterHorizontally),
            text = message,
        )

        onReload?.let { reload ->
            Button(
                modifier = Modifier.padding(horizontal = 32.dp),
                onClick = reload
            ) {
                Text(text = "Retry")
            }
        }
    }
}

@Composable
fun EmptyView(modifier: Modifier, title: String, subtitle: String = "") {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(modifier = Modifier.fillMaxWidth(), text = subtitle, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSecondaryContainer)
        }
    }
}

@Composable
fun shimmerBrush(showShimmer: Boolean = true, targetValue: Float = 1000f): Brush =
    if (showShimmer) {
        val shimmerColors = listOf(
            Color.LightGray.copy(alpha = 0.5f),
            Color.LightGray.copy(alpha = 0.2f),
            Color.LightGray.copy(alpha = 0.5f),
        )

        val transition = rememberInfiniteTransition(label = "")
        val translateAnimation = transition.animateFloat(
            initialValue = 0f,
            targetValue = targetValue,
            animationSpec = infiniteRepeatable(
                animation = tween(800), repeatMode = RepeatMode.Reverse
            ), label = ""
        )
        Brush.linearGradient(
            colors = shimmerColors,
            start = Offset.Zero,
            end = Offset(x = translateAnimation.value, y = translateAnimation.value)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color.Transparent, Color.Transparent),
            start = Offset.Zero,
            end = Offset.Zero
        )
    }
