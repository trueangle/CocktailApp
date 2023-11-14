package io.github.trueangle.cocktail.ui.drinkdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.trueangle.cocktail.design.ErrorView
import io.github.trueangle.cocktail.domain.model.Drink
import io.github.trueangle.cocktail.domain.model.Ingredient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrinkDetailScreen(modifier: Modifier, vm: DrinkDetailViewModel, onNavUp: () -> Unit) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val state by vm.stateFlow.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onNavUp) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Navigate up",
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                actions = {
                    IconButton(onClick = {
                        vm.dispatch(DrinkDetailIntent.OnFavoriteClick)
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Star,
                            contentDescription = null,
                            tint = if (state.drink?.favorite == true) MaterialTheme.colorScheme.tertiary else Color.Gray
                        )
                    }
                }
            )
        },
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
                error = state.error!!,
                onReload = {
                    vm.dispatch(DrinkDetailIntent.OnRetry)
                }
            )

            else -> Content(
                modifier = Modifier.padding(top = it.calculateTopPadding()),
                drink = state.drink!!
            ) {
                vm.dispatch(DrinkDetailIntent.OnFavoriteClick)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Content(modifier: Modifier, drink: Drink, onFavoriteClick: () -> Unit) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            text = drink.name,
            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 36.sp),
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.size(8.dp))

        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            val chipModifier = Modifier.padding(horizontal = 4.dp)

            Chip(modifier = chipModifier, text = "id: ${drink.id}")
            Chip(modifier = chipModifier, text = drink.category)

            drink.glass?.let {
                Chip(modifier = chipModifier, text = it)
            }

            drink.type?.let {
                Chip(modifier = chipModifier, text = it)
            }
        }

        Spacer(modifier = Modifier.size(16.dp))

        AsyncImage(
            model = ImageRequest
                .Builder(LocalContext.current)
                .data(drink.thumbUrl)
                .crossfade(true)
                .build(),
            contentDescription = null,
            placeholder = ColorPainter(MaterialTheme.colorScheme.primary), // todo
            error = ColorPainter(MaterialTheme.colorScheme.primary), // todo
            fallback = ColorPainter(MaterialTheme.colorScheme.primary), // todo
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .height(350.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(75.dp))
        )

        Spacer(modifier = Modifier.size(16.dp))

        if (drink.ingredients.isNotEmpty()) {
            Text(
                modifier = Modifier.padding(horizontal = 22.dp),
                text = "Ingredients",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.size(8.dp))

            LazyRow(contentPadding = PaddingValues(horizontal = 16.dp)) {
                items(drink.ingredients) {
                    IngredientItem(ingredient = it)
                    Spacer(modifier = Modifier.size(8.dp))
                }
            }

            Spacer(modifier = Modifier.size(24.dp))
        }

        drink.instructions?.let {
            Text(
                modifier = Modifier.padding(horizontal = 22.dp),
                text = "Instructions",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.size(4.dp))

            Text(
                modifier = Modifier.padding(horizontal = 22.dp),
                text = it,
            )

            Spacer(modifier = Modifier.size(24.dp))
        }

        Spacer(modifier = Modifier.size(24.dp))

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Button(
                onClick = onFavoriteClick
            ) {
                val text = if (!drink.favorite) {
                    "Add to favorites"
                } else {
                    "Remove from favorites"
                }

                Icon(imageVector = Icons.Outlined.Star, contentDescription = text)
                Text(text = text)
            }
        }

        Spacer(modifier = Modifier.size(56.dp))
    }
}

@Composable
private fun Chip(modifier: Modifier = Modifier, text: String) {
    SuggestionChip(
        modifier = modifier,
        border = SuggestionChipDefaults.suggestionChipBorder(borderColor = Color.Transparent),
        colors = SuggestionChipDefaults.suggestionChipColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5F)
        ),
        onClick = {},
        label = { Text(text) },
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
private fun IngredientItem(modifier: Modifier = Modifier, ingredient: Ingredient) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = ingredient.name, style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = ingredient.measure,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8F)
                )
            )
        }
    }
}