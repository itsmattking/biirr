package me.mking.biirr.ui.beer.detail

import com.google.common.truth.Truth.assertThat
import me.mking.biirr.R
import me.mking.biirr.domain.entities.Beer
import me.mking.biirr.me.mking.biirr.domain.usecase.GetBeerUseCaseResult
import me.mking.biirr.me.mking.biirr.ui.beer.detail.BeerDetailViewDataMapper
import me.mking.biirr.me.mking.biirr.ui.beer.list.ImageHolder
import org.junit.Test

class BeerDetailViewDataMapperTest {
    private val subject = BeerDetailViewDataMapper()

    @Test
    fun `title matches`() {
        val result = mapResult()
        assertThat(result.title).isEqualTo(SOME_BEER.name)
    }

    @Test
    fun `tagLine matches`() {
        val result = mapResult()
        assertThat(result.tagLine).isEqualTo(SOME_BEER.tagLine)
    }

    @Test
    fun `description matches`() {
        val result = mapResult()
        assertThat(result.description).isEqualTo(SOME_BEER.description)
    }

    @Test
    fun `abvIbu matches`() {
        val result = mapResult()
        assertThat(result.abvIbu).isEqualTo("4.00%ABV - Smooth")
    }

    @Test
    fun `abvIbu does not include abu when abu is Unknown`() {
        val result = mapResult(SOME_RESULT.copy(beer = SOME_BEER.copy(ibu = Beer.Ibu.Unknown)))
        assertThat(result.abvIbu).isEqualTo("4.00%ABV")
    }

    @Test
    fun `image matches`() {
        val result = mapResult()
        assertThat(result.image).isInstanceOf(ImageHolder.Url::class.java)
        assertThat((result.image as ImageHolder.Url).url).isEqualTo(SOME_BEER.imageUrl)
    }

    @Test
    fun `image is Drawable when imageUrl is null`() {
        val result = mapResult(SOME_RESULT.copy(beer = SOME_BEER.copy(imageUrl = null)))
        assertThat(result.image).isInstanceOf(ImageHolder.Drawable::class.java)
        assertThat((result.image as ImageHolder.Drawable).resId).isEqualTo(R.drawable.ic_beer_placeholder)
    }

    private fun mapResult(result: GetBeerUseCaseResult = SOME_RESULT) = subject.map(result)

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
        val SOME_RESULT = GetBeerUseCaseResult(
            beer = SOME_BEER
        )
    }
}
