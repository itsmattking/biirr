package me.mking.biirr.me.mking.biirr.domain.usecase

import kotlinx.coroutines.flow.map
import me.mking.biirr.domain.entities.Beer
import me.mking.biirr.domain.repository.BeerRepository
import me.mking.biirr.domain.usecase.FlowUseCase
import me.mking.biirr.domain.usecase.UseCaseInput
import me.mking.biirr.domain.usecase.UseCaseResult
import javax.inject.Inject

class GetBeerUseCase @Inject constructor(
    private val beerRepository: BeerRepository
) : FlowUseCase<GetBeerUseCaseInput, GetBeerUseCaseResult> {
    override fun execute(input: GetBeerUseCaseInput) =
        beerRepository.getBeer(input.id).map { GetBeerUseCaseResult(it) }
}

data class GetBeerUseCaseInput(val id: Int) : UseCaseInput
data class GetBeerUseCaseResult(val beer: Beer) : UseCaseResult
