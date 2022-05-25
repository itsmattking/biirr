package me.mking.biirr.domain.usecase

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import me.mking.biirr.domain.entities.Beer
import me.mking.biirr.domain.repository.BeerRepository
import me.mking.biirr.domain.repository.Page
import me.mking.biirr.withFlow
import org.junit.Test

@ExperimentalCoroutinesApi
class GetAllBeersUseCaseTest {

    private val mockBeerRepository: BeerRepository = mockk {
        every { getBeers(eq(SOME_PAGE)) } returns flowOf(SOME_BEERS)
    }

    private val subject: GetAllBeersUseCase = GetAllBeersUseCase(mockBeerRepository)

    @Test
    fun `beers list returned`() = runTest {
        withFlow(subject.execute(SOME_INPUT)) {
            assertThat(result.first().beers).hasSize(SOME_BEERS.size)
        }
    }

    @Test
    fun `first beer id matches`() = runTest {
        withFlow(subject.execute(SOME_INPUT)) {
            assertThat(result.first().beers.first().id).isEqualTo(SOME_BEER.id)
        }
    }

    @Test
    fun `page is incremented when result size less than page size`() = runTest {
        withFlow(subject.execute(SOME_INPUT)) {
            assertThat(result.first().nextPage?.num).isEqualTo(SOME_PAGE.num + 1)
        }
    }

    @Test
    fun `beers list is empty when exception thrown from beer repository`() = runTest {
        every { mockBeerRepository.getBeers(eq(SOME_PAGE)) } returns flow { throw Exception("uh oh") }
        withFlow(subject.execute(SOME_INPUT)) {
            assertThat(result.first().beers).isEmpty()
        }
    }

    @Test
    fun `result is error when exception thrown from beer repository`() = runTest {
        every { mockBeerRepository.getBeers(eq(SOME_PAGE)) } returns flow { throw Exception("uh oh") }
        withFlow(subject.execute(SOME_INPUT)) {
            assertThat(result.first().resultState).isInstanceOf(ResultState.Error::class.java)
        }
    }

    private companion object {
        val SOME_PAGE = Page(1, 1)
        val SOME_INPUT = GetAllBeersUseCaseInput(page = SOME_PAGE)
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
            imageUrl = "some-other-url",
            description = "some-other-description",
            alcoholByVolume = 5.0,
            ibu = Beer.Ibu.HipsterPlus(80.0)
        )
        val SOME_BEERS = listOf(SOME_BEER, SOME_OTHER_BEER)
    }
}
