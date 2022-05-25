package me.mking.biirr.me.mking.biirr.ui.beer.list

import me.mking.biirr.R
import me.mking.biirr.domain.usecase.GetAllBeersUseCaseResult
import javax.inject.Inject

class BeerListViewDataMapper @Inject constructor() {

    fun map(
        result: GetAllBeersUseCaseResult,
        loadingMore: Boolean
    ) = BeerListViewData(
        items = buildList {
            addAll(
                result.beers.map {
                    BeerListViewItem.Beer(
                        id = it.id,
                        name = it.name,
                        imageUrl = it.imageUrl?.let { url -> ImageHolder.Url(url) }
                            ?: ImageHolder.Drawable(R.drawable.ic_beer_placeholder),
                        clickEvent = BeerListClickEvent.Beer(it.id)
                    )
                }
            )
            result.nextPage?.let {
                add(
                    BeerListViewItem.More(
                        clickEvent = BeerListClickEvent.More(it),
                        loadingMore = loadingMore
                    )
                )
            }
        }
    )
}
