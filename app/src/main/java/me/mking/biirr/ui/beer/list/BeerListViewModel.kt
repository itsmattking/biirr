package me.mking.biirr.me.mking.biirr.ui.beer.list

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.mking.biirr.domain.repository.Page
import me.mking.biirr.domain.usecase.GetAllBeersUseCase
import me.mking.biirr.domain.usecase.GetAllBeersUseCaseInput
import me.mking.biirr.domain.usecase.ResultState
import me.mking.biirr.ui.State
import me.mking.biirr.ui.collectInto
import me.mking.biirr.ui.mapReadyState
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
@HiltViewModel
class BeerListViewModel @Inject constructor(
    private val getAllBeersUseCase: GetAllBeersUseCase,
    private val beerListViewDataMapper: BeerListViewDataMapper
) : ViewModel() {

    private val _page: MutableStateFlow<Page> = MutableStateFlow(Page(size = 36))
    private val _loadingMore: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _state: MutableStateFlow<State<BeerListViewData>> =
        MutableStateFlow(State.Loading())
    val state: StateFlow<State<BeerListViewData>> = _state

    private val _viewAction: Channel<BeerListViewAction> = Channel(Channel.CONFLATED)
    val viewAction: Flow<BeerListViewAction> = _viewAction.receiveAsFlow()

    fun load() = viewModelScope.launch {
        _state.value = State.Loading()
        combine(
            _page.flatMapConcat {
                getAllBeersUseCase.execute(GetAllBeersUseCaseInput(it))
                    .onStart { _loadingMore.value = true }
                    .onCompletion { _loadingMore.value = false }
            },
            _loadingMore
        ) { result, loadingMore ->
            if (result.resultState is ResultState.Error) {
                _viewAction.trySend(
                    BeerListViewAction.ToError(
                        result.resultState.toString()
                    )
                )
            }
            beerListViewDataMapper.map(
                result,
                loadingMore
            )
        }
            .mapReadyState()
            .collectInto(_state)
    }

    fun onClickEvent(clickEvent: BeerListClickEvent) {
        when (clickEvent) {
            is BeerListClickEvent.Beer -> _viewAction.trySend(BeerListViewAction.ShowBeer(clickEvent.id))
            is BeerListClickEvent.More -> _page.value = clickEvent.page
        }
    }
}

data class BeerListViewData(val items: List<BeerListViewItem>)

sealed class BeerListViewItem {
    data class Beer(
        val id: Int,
        val name: String,
        val imageUrl: ImageHolder,
        val clickEvent: BeerListClickEvent
    ) : BeerListViewItem()

    data class More(
        val clickEvent: BeerListClickEvent.More,
        val loadingMore: Boolean = false
    ) : BeerListViewItem()
}

sealed class ImageHolder {
    data class Url(val url: String) : ImageHolder()
    data class Drawable(@DrawableRes val resId: Int) : ImageHolder()
}

sealed class BeerListClickEvent {
    data class Beer(val id: Int) : BeerListClickEvent()
    data class More(val page: Page) : BeerListClickEvent()
}

sealed class BeerListViewAction {
    data class ShowBeer(val beerId: Int) : BeerListViewAction()
    data class ToError(val message: String) : BeerListViewAction()
}
