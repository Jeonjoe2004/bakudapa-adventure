package com.bakudapa.adventure.feature.auth.ui.splash

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Path
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SplashLogo() {
    val amber = Color(0xFFFFA000)
    Canvas(modifier = Modifier.size(120.dp), onDraw = {
        val p = Path().apply {
            moveTo(size.width * 0.1f, size.height * 0.9f)
            lineTo(size.width * 0.5f, size.height * 0.15f)
            lineTo(size.width * 0.9f, size.height * 0.9f)
            close()
        }
        drawPath(p, Color.White.copy(alpha = 0.85f), style = Fill)
        drawPath(p, Color.White, style = Stroke(width = 4f))

        drawCircle(
            color = amber.copy(alpha = 0.8f),
            radius = size.minDimension * 0.1f,
            center = center.copy(y = size.height * 0.12f)
        )
    })
}

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToOnboarding: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = true) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                SplashEffect.NavigateToHome -> onNavigateToHome()
                SplashEffect.NavigateToOnboarding -> onNavigateToOnboarding()
                SplashEffect.NavigateToLogin -> onNavigateToLogin()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            SplashLogo()
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Bakudapa Adventure",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
