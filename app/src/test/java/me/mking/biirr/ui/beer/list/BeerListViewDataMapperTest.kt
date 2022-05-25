package me.mking.biirr.ui.beer.list

import com.google.common.truth.Truth.assertThat
import me.mking.biirr.domain.entities.Beer
import me.mking.biirr.domain.repository.Page
import me.mking.biirr.domain.usecase.GetAllBeersUseCaseResult
import me.mking.biirr.domain.usecase.ResultState
import me.mking.biirr.me.mking.biirr.ui.beer.list.BeerListViewDataMapper
import me.mking.biirr.me.mking.biirr.ui.beer.list.BeerListViewItem
import me.mking.biirr.me.mking.biirr.ui.beer.list.ImageHolder
import org.junit.Test

class BeerListViewDataMapperTest {
    private val subject = BeerListViewDataMapper()

    @Test
    fun `two beers mapped`() {
        val result = mapResult()
        assertThat(result.items.filterIsInstance<BeerListViewItem.Beer>()).hasSize(2)
    }

    @Test
    fun `ImageHolder mapped when url not null`() {
        val result = mapResult()
        assertThat((result.items.first() as BeerListViewItem.Beer).imageUrl).isInstanceOf(
            ImageHolder.Url::class.java
        )
    }

    @Test
    fun `ImageHolder mapped when url null`() {
        val result = mapResult()
        assertThat((result.items[1] as BeerListViewItem.Beer).imageUrl).isInstanceOf(
            ImageHolder.Drawable::class.java
        )
    }

    @Test
    fun `no More item mapped when page null`() {
        val result = mapResult()
        assertThat(result.items.filterIsInstance<BeerListViewItem.More>()).hasSize(0)
    }

    @Test
    fun `More item mapped when page not null`() {
        val result = mapResult(result = SOME_RESULT.copy(nextPage = Page()))
        assertThat(result.items.filterIsInstance<BeerListViewItem.More>()).hasSize(1)
    }

    private fun mapResult(
        result: GetAllBeersUseCaseResult = SOME_RESULT,
        loadingMore: Boolean = false
    ) = subject.map(
        result = result,
        loadingMore = loadingMore
    )

    private companion object {
        val SOME_BEER = Beer(
            id = 1,
            name = "some-name",
            tagLine = "some-tagline",
            imageUrl = "some-url",
            description = "some-description",
            alcoholByVolume = 4.0,
            ibu = Beer.Ibu.Smooth(30.0)
        )
        val SOME_OTHER_BEER = Beer(
            id = 2,
            name = "some-other-name",
            tagLine = "some-other-tagline",
            imageUrl = null,
            description = "some-other-description",
            alcoholByVolume = 5.0,
            ibu = Beer.Ibu.HipsterPlus(80.0)
        )
        val SOME_BEERS = listOf(SOME_BEER, SOME_OTHER_BEER)
        val SOME_RESULT = GetAllBeersUseCaseResult(
            beers = SOME_BEERS,
            nextPage = null,
            resultState = ResultState.Success
        )
    }
}
