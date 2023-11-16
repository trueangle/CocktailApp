package io.github.trueangle.cocktail.ui.search

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import io.github.trueangle.cocktail.CocktailApplication
import io.github.trueangle.cocktail.design.EmptyView
import io.github.trueangle.cocktail.design.ErrorView
import io.github.trueangle.cocktail.design.shimmerBrush
import io.github.trueangle.cocktail.domain.model.Drink
import io.github.trueangle.cocktail.domain.model.SearchSource
import io.github.trueangle.cocktail.util.viewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Search(
    modifier: Modifier = Modifier,
    onItemClick: (Drink) -> Unit,
    source: SearchSource,
    placeholder: String
) {
    val appComponent = (LocalContext.current.applicationContext as CocktailApplication).appComponent
    val vm = viewModel<SearchViewModel>(
        key = "searchViewModel${source.name}",
        factory = viewModelFactory {
            SearchViewModel(drinkRepository = appComponent.drinkRepository, source)
        })

    val state by vm.stateFlow.collectAsStateWithLifecycle()

    val barPadding by animateDpAsState(
        if (state.active) 0.dp else 16.dp,
        tween(300),
        label = "SearchBarPaddingAnim"
    )

    SearchBar(
        query = state.query,
        onQueryChange = { vm.dispatch(SearchIntent.Search(it)) },
        placeholder = { Text(text = placeholder) },
        trailingIcon = {
            if (state.active) {
                Icon(
                    modifier = Modifier.clickable { vm.dispatch(SearchIntent.OnClear) },
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close search"
                )
            }
        },
        onSearch = { vm.dispatch(SearchIntent.Search(it)) },
        active = state.active,
        onActiveChange = { vm.dispatch(SearchIntent.ToggleSearch(it)) },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = barPadding)
            .padding(bottom = if (state.active) 0.dp else 8.dp)
    ) {
        when {
            state.progress -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(42.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            state.error != null -> ErrorView(
                modifier = Modifier.fillMaxSize(),
                error = state.error!!,
                onReload = null
            )

            state.searchResults.isEmpty() -> {
                EmptyView(
                    modifier = Modifier.fillMaxSize(),
                    title = "No data found"
                )
            }

            else -> LazyColumn(contentPadding = PaddingValues(bottom = 16.dp)) {
                items(items = state.searchResults, key = { it.id }) { drink ->
                    SearchItem(
                        modifier = Modifier.fillMaxWidth(),
                        drink = drink,
                        onClick = onItemClick
                    )

                    Divider()
                }
            }
        }
    }
}

@Composable
private fun SearchItem(modifier: Modifier, drink: Drink, onClick: (Drink) -> Unit) {
    Card(
        modifier = modifier.clickable { onClick(drink) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2F)
        )
    ) {
        Row(modifier = modifier.padding(16.dp)) {
            SubcomposeAsyncImage(
                model = ImageRequest
                    .Builder(LocalContext.current)
                    .data(drink.thumbUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                loading = {
                    Box(
                        Modifier
                            .size(50.dp)
                            .background(shimmerBrush(targetValue = 1300f))
                    )
                },
                error = {
                    Box(
                        Modifier
                            .size(50.dp)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                },
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(50.dp)
            )

            Spacer(modifier = Modifier.size(16.dp))

            Text(
                modifier = Modifier.weight(1F),
                text = drink.name,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Icon(
                imageVector = Icons.Outlined.KeyboardArrowRight,
                contentDescription = "Open ${drink.name}"
            )
        }
    }
}