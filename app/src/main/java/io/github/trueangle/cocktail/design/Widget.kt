package io.github.trueangle.cocktail.design

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
                style = MaterialTheme.typography.titleLarge
            )
            Text(modifier = Modifier.fillMaxWidth(), text = subtitle, textAlign = TextAlign.Center)
        }
    }
}
