package me.mking.biirr.domain.repository

import kotlinx.coroutines.flow.Flow
import me.mking.biirr.domain.entities.Beer

interface BeerRepository {
    fun getBeers(page: Page = Page()): Flow<List<Beer>>
    fun getBeer(id: Int): Flow<Beer>
}

class Page(val num: Int = 1, val size: Int = 50) {
    fun next() = Page(num = num + 1, size = size)
    fun copy() = Page(num = num, size = size)
}
