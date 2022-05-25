package me.mking.biirr.data.sources

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BeersApi {
    @GET("beers")
    suspend fun getBeers(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): List<BeersResponse.Beer>

    @GET("beers/{id}")
    suspend fun getBeer(@Path("id") id: Int): List<BeersResponse.Beer>
}

object BeersResponse {
    @ExperimentalSerializationApi
    @Serializable
    data class Beer(
        val id: Int,
        val name: String,
        val tagline: String,
        @JsonNames("image_url")
        val imageUrl: String?,
        val description: String,
        val abv: Double,
        val ibu: Double?
    )
}
