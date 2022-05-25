package me.mking.biirr.domain.usecase

import kotlinx.coroutines.flow.Flow

interface FlowUseCase<S : UseCaseInput, T : UseCaseResult> {
    fun execute(input: S): Flow<T>
}

interface UseCaseInput
interface UseCaseResult

sealed class ResultState {
    object Success : ResultState()
    data class Error(val throwable: Throwable) : ResultState() {
        override fun toString() = throwable.message ?: "Something went wrong"
    }
}
