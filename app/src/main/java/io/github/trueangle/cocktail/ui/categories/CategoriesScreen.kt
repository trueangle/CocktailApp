package io.github.trueangle.cocktail.ui.categories

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.trueangle.cocktail.util.MviViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    modifier: Modifier,
    store: MviViewModel<CategoriesState, CategoriesAction, CategoriesEffect>
) {
    Scaffold(modifier = modifier, topBar = {
        //AppBar(title = "Categories")

        SearchBar(
            query = "",
            onQueryChange = {},
            placeholder = {
                Text(text = "Search cocktail by name")
            },
            onSearch = {}, //the callback to be invoked when the input service triggers the ImeAction.Search action
            active = false, //whether the user is searching or not
            onActiveChange = { }, //the callback to be invoked when this search bar's active state is changed
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            // todo
            LazyColumn {
                items(10) { country ->
                    Text(
                        text = "Cocktail",
                        modifier = Modifier.padding(
                            start = 8.dp,
                            top = 4.dp,
                            end = 8.dp,
                            bottom = 4.dp
                        )
                    )
                }
            }
        }
    }) { _ ->
        Column(modifier = Modifier) {

        }
    }
}