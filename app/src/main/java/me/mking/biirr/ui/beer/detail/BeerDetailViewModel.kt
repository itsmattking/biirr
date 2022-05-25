package me.mking.biirr.me.mking.biirr.ui.beer.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import me.mking.biirr.R
import me.mking.biirr.domain.entities.Beer
import me.mking.biirr.me.mking.biirr.domain.usecase.GetBeerUseCase
import me.mking.biirr.me.mking.biirr.domain.usecase.GetBeerUseCaseInput
import me.mking.biirr.me.mking.biirr.ui.beer.list.ImageHolder
import me.mking.biirr.ui.State
import me.mking.biirr.ui.collectInto
import me.mking.biirr.ui.mapReadyState
import javax.inject.Inject

@HiltViewModel
class BeerDetailViewModel @Inject constructor(
    private val getBeerUseCase: GetBeerUseCase,
    private val beerDetailViewDataMapper: BeerDetailViewDataMapper
) : ViewModel() {

    private val _state: MutableStateFlow<State<BeerDetailViewData>> =
        MutableStateFlow(State.Loading())
    val state: StateFlow<State<BeerDetailViewData>> = _state

    fun load(beerId: Int) = viewModelScope.launch {
        getBeerUseCase.execute(GetBeerUseCaseInput(id = beerId)).map {
            beerDetailViewDataMapper.map(it)
        }.mapReadyState().collectInto(_state)
    }
}

data class BeerDetailViewData(
    val title: String,
    val tagLine: String,
    val image: ImageHolder,
    val description: String,
    val abvIbu: String
)
