package io.github.trueangle.cocktail.util

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow

interface State
interface Action
interface Effect

@Stable
abstract class MviViewModel<S : State, A : Action, E : Effect>() : ViewModel() {
    private val effectChannel = Channel<E>(Channel.CONFLATED)

    protected abstract val viewState: StateFlow<S>

    val effect: Flow<Effect> = effectChannel.receiveAsFlow()
    val state: StateFlow<S> get() = viewState

    abstract fun dispatch(action: A)
}