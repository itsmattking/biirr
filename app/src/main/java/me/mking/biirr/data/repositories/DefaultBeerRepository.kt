package me.mking.biirr.data.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.ExperimentalSerializationApi
import me.mking.biirr.data.sources.BeersApi
import me.mking.biirr.data.sources.BeersResponse
import me.mking.biirr.domain.entities.Beer
import me.mking.biirr.domain.repository.BeerRepository
import me.mking.biirr.domain.repository.Page
import javax.inject.Inject

@ExperimentalSerializationApi
class DefaultBeerRepository @Inject constructor(
    private val beersApi: BeersApi
) : BeerRepository {
    override fun getBeers(page: Page): Flow<List<Beer>> = flow {
        emit(
            beersApi.getBeers(page = page.num, perPage = page.size).map {
                mapBeer(it)
            }
        )
    }

    override fun getBeer(id: Int): Flow<Beer> = flow {
        emit(mapBeer(beersApi.getBeer(id).first()))
    }

    private fun mapBeer(beer: BeersResponse.Beer) = Beer(
        id = beer.id,
        name = beer.name,
        tagLine = beer.tagline,
        imageUrl = beer.imageUrl,
        description = beer.description,
        ibu = Beer.ibuFrom(beer.ibu ?: 0.0),
        alcoholByVolume = beer.abv
    )
}
