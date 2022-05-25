package me.mking.biirr.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import me.mking.biirr.domain.entities.Beer
import me.mking.biirr.domain.repository.BeerRepository
import me.mking.biirr.domain.repository.Page
import javax.inject.Inject
import kotlin.random.Random

class GetAllBeersUseCase @Inject constructor(
    private val beerRepository: BeerRepository
) : FlowUseCase<GetAllBeersUseCaseInput, GetAllBeersUseCaseResult> {

    private val _beersCache: MutableList<Beer> = mutableListOf()
    private val beersCache: List<Beer> = _beersCache

    override fun execute(input: GetAllBeersUseCaseInput): Flow<GetAllBeersUseCaseResult> {
        return beerRepository.getBeers(page = input.page).map { result ->
            GetAllBeersUseCaseResult(
                beers = _beersCache + result,
                nextPage = if (result.size >= input.page.size) input.page.next() else null,
                resultState = ResultState.Success
            ).also { _beersCache.addAll(result) }
        }.catch {
            emit(
                GetAllBeersUseCaseResult(
                    beers = beersCache,
                    nextPage = input.page.copy(),
                    resultState = ResultState.Error(it)
                )
            )
        }
    }
}

data class GetAllBeersUseCaseInput(val page: Page) : UseCaseInput
data class GetAllBeersUseCaseResult(
    val beers: List<Beer>,
    val nextPage: Page?,
    val resultState: ResultState
) : UseCaseResult
