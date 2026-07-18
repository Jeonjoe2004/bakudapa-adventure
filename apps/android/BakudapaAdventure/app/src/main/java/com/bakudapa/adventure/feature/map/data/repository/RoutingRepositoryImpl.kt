package com.bakudapa.adventure.feature.map.data.repository

import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.map.data.remote.OSRMService
import com.bakudapa.adventure.feature.map.domain.model.Route
import com.bakudapa.adventure.feature.map.domain.model.RouteLeg
import com.bakudapa.adventure.feature.map.domain.model.RouteStep
import com.bakudapa.adventure.feature.map.domain.repository.RoutingRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoutingRepositoryImpl @Inject constructor(
    private val osrmService: OSRMService
) : RoutingRepository {

    override suspend fun getRoute(
        originLat: Double,
        originLng: Double,
        destLat: Double,
        destLng: Double
    ): DataResult<Route> {
        return try {
            val response = osrmService.getRoute(
                "$originLng,$originLat;$destLng,$destLat",
                "true",
                "true",
                "false"
            )

            if (response.code == "Ok" && response.routes.isNotEmpty()) {
                val osrmRoute = response.routes.first()
                val route = Route(
                    distance = osrmRoute.distance,
                    duration = osrmRoute.duration.toLong(),
                    geometry = osrmRoute.geometry,
                    legs = osrmRoute.legs.map { leg ->
                        com.bakudapa.adventure.feature.map.domain.model.RouteLeg(
                            distance = leg.distance,
                            duration = leg.duration.toLong(),
                            steps = leg.steps.map { step ->
                                com.bakudapa.adventure.feature.map.domain.model.RouteStep(
                                    distance = step.distance,
                                    duration = step.duration.toLong(),
                                    instruction = step.maneuver.instruction ?: step.name ?: "",
                                    name = step.name ?: "",
                                    geometry = step.geometry ?: ""
                                )
                            },
                            summary = leg.summary ?: ""
                        )
                    }
                )
                DataResult.Success(route)
            } else {
                DataResult.Error(Exception("No route found"))
            }
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }
}