package me.mking.biirr.me.mking.biirr.ui.beer.detail

import me.mking.biirr.R
import me.mking.biirr.domain.entities.Beer
import me.mking.biirr.me.mking.biirr.domain.usecase.GetBeerUseCaseResult
import me.mking.biirr.me.mking.biirr.ui.beer.list.ImageHolder
import javax.inject.Inject

class BeerDetailViewDataMapper @Inject constructor() {
    fun map(result: GetBeerUseCaseResult) = BeerDetailViewData(
        title = result.beer.name,
        tagLine = result.beer.tagLine,
        description = result.beer.description,
        abvIbu = listOfNotNull(
            String.format(
                "%.2f%%ABV",
                result.beer.alcoholByVolume
            ), when (result.beer.ibu) {
                is Beer.Ibu.Bitter -> "Bitter"
                is Beer.Ibu.HipsterPlus -> "Hipster+"
                is Beer.Ibu.Smooth -> "Smooth"
                Beer.Ibu.Unknown -> null
            }
        ).joinToString(" - "),
        image = result.beer.imageUrl?.let { url -> ImageHolder.Url(url) }
            ?: ImageHolder.Drawable(R.drawable.ic_beer_placeholder)
    )

}
