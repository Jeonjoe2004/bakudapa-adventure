package com.bakudapa.adventure.feature.tracking.data.local

import androidx.room.TypeConverter
import com.bakudapa.adventure.feature.tracking.domain.model.TrackingPoint
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class TrackingTypeConverters {
    @TypeConverter
    fun fromTrackingPointList(value: List<TrackingPoint>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toTrackingPointList(value: String): List<TrackingPoint> {
        return Json.decodeFromString(value)
    }
}
