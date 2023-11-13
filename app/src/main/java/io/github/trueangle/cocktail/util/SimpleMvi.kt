package io.github.trueangle.cocktail.util

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow

interface State
interface Intent
interface Effect

@Stable
abstract class MviViewModel<S : State, I : Intent, E : Effect>() : ViewModel() {
    private val effectChannel = Channel<E>(Channel.CONFLATED)

    private val initialState: S by lazy { setInitialState() }
    private val mutableStateFlow by lazy { MutableStateFlow(initialState) }

    protected var viewState: S
        get() = mutableStateFlow.value
        set(value) {
            mutableStateFlow.value = value
        }

    val effectFlow: Flow<Effect> = effectChannel.receiveAsFlow()
    val stateFlow: StateFlow<S> get() = mutableStateFlow

    abstract fun setInitialState(): S

    abstract fun dispatch(intent: I)
}