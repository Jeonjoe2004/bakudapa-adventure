package com.bakudapa.adventure.feature.home.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.bakudapa.adventure.feature.home.domain.model.Weather

@Composable
fun WeatherCard(
    weather: Weather,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = "Current Weather", style = MaterialTheme.typography.labelMedium)
                Text(text = "${weather.temperature.toInt()}°C", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                Text(text = weather.condition.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.bodyMedium)
            }
            
            Image(
                painter = rememberAsyncImagePainter(weather.iconUrl),
                contentDescription = null,
                modifier = Modifier.size(64.dp)
            )
        }
    }
}
