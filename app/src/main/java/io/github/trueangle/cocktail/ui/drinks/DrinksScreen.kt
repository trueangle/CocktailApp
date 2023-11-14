package io.github.trueangle.cocktail.ui.drinks

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.trueangle.cocktail.design.AppBar
import io.github.trueangle.cocktail.design.ErrorView
import io.github.trueangle.cocktail.domain.model.Drink

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrinksScreen(
    modifier: Modifier,
    vm: DrinksViewModel,
    onItemClick: (Drink) -> Unit,
    onNavUp: () -> Unit
) {
    val state by vm.stateFlow.collectAsStateWithLifecycle()

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        AppBar(title = state.categoryName, onNavUp = onNavUp, scrollBehavior = scrollBehavior)
    }) {
        Content(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = it.calculateTopPadding()),
            state = state,
            onReload = { vm.dispatch(DrinksIntent.OnRetry) },
            onItemClick = onItemClick
        )
    }
}

@Composable
private fun Content(
    modifier: Modifier,
    state: DrinksState,
    onReload: () -> Unit,
    onItemClick: (Drink) -> Unit
) {
    when {
        state.progress -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(42.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        state.error != null -> ErrorView(
            modifier = modifier,
            error = state.error,
            onReload = onReload
        )

        else -> LazyVerticalGrid(
            modifier = modifier,
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            items(state.list) {
                DrinkListItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 100.dp),
                    Drink = it,
                    onItemClick
                )
            }
        }
    }
}

@Composable
fun DrinkListItem(modifier: Modifier, Drink: Drink, onItemClick: (Drink) -> Unit) {
    Card(
        modifier = modifier.clickable { onItemClick(Drink) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column {
            AsyncImage(
                model = ImageRequest
                    .Builder(LocalContext.current)
                    .data(Drink.thumbUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                placeholder = ColorPainter(MaterialTheme.colorScheme.primary), // todo
                error = ColorPainter(MaterialTheme.colorScheme.primary), // todo
                fallback = ColorPainter(MaterialTheme.colorScheme.primary), // todo
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
            )

            Row(
                modifier = Modifier
                    .height(80.dp)
                    .padding(16.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    modifier = Modifier.weight(1F),
                    text = Drink.name,
                    maxLines = 2,
                    style = MaterialTheme.typography.titleMedium,
                )
                Icon(
                    imageVector = Icons.Outlined.KeyboardArrowRight,
                    contentDescription = "Open ${Drink.name}"
                )
            }
        }
    }
}