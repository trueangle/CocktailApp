package io.github.trueangle.cocktail.ui.drinks

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
import androidx.compose.material3.ExperimentalMaterial3Api
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

    Scaffold(modifier = modifier, topBar = {
        AppBar(title = state.categoryName, onNavUp = onNavUp)
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

        else -> LazyColumn(modifier = modifier, contentPadding = PaddingValues(16.dp)) {
            items(state.list) {
                DrinkItem(modifier = Modifier.fillMaxWidth(), drink = it, onItemClick)
                Spacer(modifier = Modifier.size(8.dp))
            }
        }
    }
}

@Composable
private fun DrinkItem(modifier: Modifier, drink: Drink, onItemClick: (Drink) -> Unit) {
    Card(
        modifier = modifier.clickable { onItemClick(drink) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2F)
        )
    ) {
        Row(modifier = modifier.padding(16.dp)) {
            Text(
                modifier = Modifier.weight(1F),
                text = drink.name,
                style = MaterialTheme.typography.titleMedium
            )
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowRight,
                contentDescription = "Open ${drink.name}"
            )
        }
    }
}