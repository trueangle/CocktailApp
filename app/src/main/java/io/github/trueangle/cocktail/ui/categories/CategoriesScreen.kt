package io.github.trueangle.cocktail.ui.categories

import android.annotation.SuppressLint
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
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.trueangle.cocktail.design.ErrorView
import io.github.trueangle.cocktail.domain.model.Category
import io.github.trueangle.cocktail.domain.model.Drink
import io.github.trueangle.cocktail.domain.model.SearchSource
import io.github.trueangle.cocktail.ui.search.Search

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CategoriesScreen(
    modifier: Modifier,
    vm: CategoriesViewModel,
    onItemClick: (Category) -> Unit,
    onSearchItemClick: (Drink) -> Unit
) {
    val state by vm.stateFlow.collectAsStateWithLifecycle()

    Scaffold(modifier = modifier, topBar = {
        Search(onItemClick = onSearchItemClick, source = SearchSource.NETWORK)
    }) {
        Content(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = it.calculateTopPadding()),
            state = state,
            onReload = { vm.dispatch(CategoriesIntent.OnRetry) },
            onItemClick = onItemClick
        )
    }
}

@Composable
private fun Content(
    modifier: Modifier,
    state: CategoriesState,
    onReload: () -> Unit,
    onItemClick: (Category) -> Unit
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

        else -> LazyColumn(modifier = modifier, contentPadding = PaddingValues(16.dp)) {
            item { Text(text = "Categories", style = MaterialTheme.typography.titleLarge) }

            item { Spacer(modifier = Modifier.size(18.dp)) }

            items(state.list) { cat ->
                CategoryItem(
                    modifier = Modifier.fillMaxWidth(),
                    category = cat,
                    onClick = onItemClick
                )
                Spacer(modifier = Modifier.size(8.dp))
            }
        }
    }
}

@Composable
private fun CategoryItem(modifier: Modifier, category: Category, onClick: (Category) -> Unit) {
    Card(
        modifier = modifier.clickable { onClick(category) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2F)
        )
    ) {
        Row(modifier = modifier.padding(16.dp)) {
            Text(
                modifier = Modifier.weight(1F),
                text = category.name,
                style = MaterialTheme.typography.titleMedium
            )
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowRight,
                contentDescription = "Open ${category.name}"
            )
        }
    }
}
