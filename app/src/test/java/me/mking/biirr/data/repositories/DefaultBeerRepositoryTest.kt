package me.mking.biirr.data.repositories

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.ExperimentalSerializationApi
import me.mking.biirr.data.sources.BeersApi
import me.mking.biirr.data.sources.BeersResponse
import me.mking.biirr.domain.entities.Beer
import me.mking.biirr.domain.repository.Page
import me.mking.biirr.withFlow
import org.junit.Test

@ExperimentalCoroutinesApi
@ExperimentalSerializationApi
class DefaultBeerRepositoryTest {

    private val mockApiClient: BeersApi = mockk {
        coEvery {
            getBeers(
                eq(SOME_DEFAULT_PAGE.num),
                eq(SOME_DEFAULT_PAGE.size)
            )
        } returns SOME_BEERS
        coEvery {
            getBeer(eq(SOME_OTHER_BEER.id))
        } returns listOf(SOME_OTHER_BEER)
    }

    private val subject = DefaultBeerRepository(mockApiClient)

    @Test
    fun `getBeers() - result size matches`() = runTest {
        withFlow(subject.getBeers()) {
            assertThat(result.last()).hasSize(SOME_BEERS.size)
        }
    }

    @Test
    fun `getBeers() - id matches`() = runTest {
        withFlow(subject.getBeers()) {
            assertThat(result.last().first().id).isEqualTo(SOME_BEER.id)
        }
    }

    @Test
    fun `getBeers() - name matches`() = runTest {
        withFlow(subject.getBeers()) {
            assertThat(result.last().first().name).isEqualTo(SOME_BEER.name)
        }
    }

    @Test
    fun `getBeers() - tagLine matches`() = runTest {
        withFlow(subject.getBeers()) {
            assertThat(result.last().first().tagLine).isEqualTo(SOME_BEER.tagline)
        }
    }

    @Test
    fun `getBeers() - imageUrl matches`() = runTest {
        withFlow(subject.getBeers()) {
            assertThat(result.last().first().imageUrl).isEqualTo(SOME_BEER.imageUrl)
        }
    }

    @Test
    fun `getBeers() - description matches`() = runTest {
        withFlow(subject.getBeers()) {
            assertThat(result.last().first().description).isEqualTo(SOME_BEER.description)
        }
    }

    @Test
    fun `getBeers() - alcoholByVolume matches`() = runTest {
        withFlow(subject.getBeers()) {
            assertThat(result.last().first().alcoholByVolume).isEqualTo(SOME_BEER.abv)
        }
    }

    @Test
    fun `getBeers() - ibu matches`() = runTest {
        withFlow(subject.getBeers()) {
            assertThat(result.last().first().ibu).isInstanceOf(Beer.Ibu.HipsterPlus::class.java)
        }
    }

    @Test
    fun `getBeers() - different Page arguments are passed`() = runTest {
        val page = Page(2, 100)
        coEvery { mockApiClient.getBeers(eq(page.num), eq(page.size)) }.returns(listOf(SOME_BEER))
        withFlow(subject.getBeers(page)) {
            assertThat(result.last()).hasSize(1)
        }
    }

    @Test
    fun `getBeer() - correct result returned when argument passed`() = runTest {
        withFlow(subject.getBeer(SOME_OTHER_BEER.id)) {
            assertThat(result.last().id).isEqualTo(SOME_OTHER_BEER.id)
        }
    }

    private companion object {
        val SOME_BEER = BeersResponse.Beer(
            id = 1,
            name = "some-name",
            tagline = "some-tagline",
            imageUrl = "some-image-url",
            description = "some-description",
            abv = 3.0,
            ibu = 80.0
        )
        val SOME_OTHER_BEER = BeersResponse.Beer(
            id = 2,
            name = "some-other-name",
            tagline = "some-other-tagline",
            imageUrl = "some-other-image-url",
            description = "some-other-description",
            abv = 5.0,
            ibu = 30.0
        )
        val SOME_BEERS = listOf(SOME_BEER, SOME_OTHER_BEER)
        val SOME_DEFAULT_PAGE = Page(1, 50)
    }
}
