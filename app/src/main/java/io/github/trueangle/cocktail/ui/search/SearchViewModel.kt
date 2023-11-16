package io.github.trueangle.cocktail.ui.search

import androidx.lifecycle.viewModelScope
import com.github.kittinunf.result.onFailure
import com.github.kittinunf.result.onSuccess
import io.github.trueangle.cocktail.domain.model.Drink
import io.github.trueangle.cocktail.domain.model.RequestException
import io.github.trueangle.cocktail.domain.model.SearchSource
import io.github.trueangle.cocktail.domain.repository.DrinkRepository
import io.github.trueangle.cocktail.util.Effect
import io.github.trueangle.cocktail.util.Intent
import io.github.trueangle.cocktail.util.MviViewModel
import io.github.trueangle.cocktail.util.State
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

data class SearchState(
    val active: Boolean = false,
    val query: String = "",
    val searchResults: ImmutableList<Drink> = persistentListOf(),
    val progress: Boolean = false,
    val error: RequestException? = null,
    val searchHappened: Boolean = false
) : State

sealed interface SearchIntent : Intent {
    class Search(val query: String) : SearchIntent
    class ToggleSearch(val active: Boolean) : SearchIntent
    data object OnClear : SearchIntent
}

sealed interface SearchEffect : Effect

@OptIn(FlowPreview::class)
class SearchViewModel(
    private val drinkRepository: DrinkRepository,
    private val source: SearchSource,
    override val initialState: SearchState = SearchState()
) : MviViewModel<SearchState, SearchIntent, SearchEffect>() {

    private val searchInput = MutableStateFlow("")

    init {
        viewModelScope.launch {
            searchInput
                .debounce(300)
                .collectLatest {
                    search(it)
                }
        }
    }

    override fun dispatch(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.Search -> {
                viewState = viewState.copy(query = intent.query)
                searchInput.value = intent.query
            }

            is SearchIntent.ToggleSearch -> toggleSearch(intent.active)
            SearchIntent.OnClear -> clearSearch()
        }
    }

    private fun search(query: String) {
        val name = query.trim()

        if (name.isEmpty()) {
            viewState = viewState.copy(searchResults = persistentListOf())

            return
        }

        viewState = viewState.copy(error = null, progress = true)

        viewModelScope.launch(Dispatchers.IO) {
            drinkRepository
                .searchByName(name, source)
                .onSuccess { results ->
                    viewState = viewState.copy(
                        progress = false,
                        error = null,
                        searchResults = results.toPersistentList(),
                        searchHappened = true
                    )
                }.onFailure {
                    viewState = viewState.copy(
                        progress = false,
                        error = it,
                        searchResults = persistentListOf(),
                        searchHappened = true
                    )
                }
        }
    }

    private fun toggleSearch(active: Boolean) {
        viewState = viewState.copy(active = active)
    }

    private fun clearSearch() {
        searchInput.value = ""
        viewState = SearchState()
    }
}