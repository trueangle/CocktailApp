package io.github.trueangle.cocktail.ui.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.trueangle.cocktail.domain.model.Drink
import io.github.trueangle.cocktail.domain.model.SearchSource
import io.github.trueangle.cocktail.ui.drinks.DrinkListItem
import io.github.trueangle.cocktail.ui.search.Search

@Composable
fun FavoritesScreen(
    modifier: Modifier,
    vm: FavoritesViewModel,
    onItemClick: (Drink) -> Unit
) {
    val state by vm.stateFlow.collectAsStateWithLifecycle()

    Scaffold(modifier = modifier, topBar = {
        Search(
            onItemClick = onItemClick,
            source = SearchSource.LOCAL,
            placeholder = "Search cocktail in favorites"
        )
    }) {
        if (state.list.isEmpty()) {
            Box(
                modifier = modifier.padding(top = it.calculateTopPadding()),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    text = "Favorite cocktails will appear here",
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyVerticalGrid(
                modifier = modifier.padding(top = it.calculateTopPadding()),
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp,
                    top = 16.dp
                )
            ) {
                item(span = {
                    GridItemSpan(2)
                }) {
                    Text(
                        text = "Favorites",
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                item(span = {
                    GridItemSpan(2)
                }) {
                    Spacer(modifier = Modifier.size(2.dp))
                }

                items(state.list, key = { item -> item.id }) { drink ->
                    DrinkListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 100.dp),
                        Drink = drink,
                        onItemClick = onItemClick
                    )
                }
            }
        }
    }
}