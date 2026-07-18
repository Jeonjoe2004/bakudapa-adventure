package com.bakudapa.adventure.feature.map.data.remote

import com.bakudapa.adventure.feature.map.data.remote.model.OSRMResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OSRMService {
    @GET("route/v1/foot/")
    suspend fun getRoute(
        @Query("coordinates") coordinates: String,
        @Query("overview") overview: String = "full",
        @Query("steps") steps: String = "true",
        @Query("annotations") annotations: String = "false"
    ): OSRMResponse

    companion object {
        const val BASE_URL = "https://router.project-osrm.org/"
    }
}