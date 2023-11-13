package io.github.trueangle.cocktail.design

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    LargeTopAppBar(
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
                        //tint = MaterialTheme.colorScheme.onSurface.copy(0.6F)
                    )
                }
            }
        },
        scrollBehavior = scrollBehavior
    )
}

@Composable
fun ErrorView(modifier: Modifier, error: RequestException, onReload: () -> Unit) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center
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

        Button(
            modifier = Modifier.padding(horizontal = 32.dp),
            onClick = onReload
        ) {
            Text(text = "Retry")
        }
    }
}
