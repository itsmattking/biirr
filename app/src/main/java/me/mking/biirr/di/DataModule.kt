package me.mking.biirr.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.mking.biirr.data.repositories.DefaultBeerRepository
import me.mking.biirr.data.sources.BeersApi
import me.mking.biirr.domain.repository.BeerRepository
import retrofit2.Retrofit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DataModule {

    @Provides
    @Singleton
    fun provideBeersApi(retrofit: Retrofit): BeersApi = retrofit.create(BeersApi::class.java)

    @Provides
    @Singleton
    fun provideBeerRepository(
        defaultBeerRepository: DefaultBeerRepository
    ): BeerRepository = defaultBeerRepository
}
