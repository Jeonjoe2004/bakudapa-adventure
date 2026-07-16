package com.bakudapa.adventure.feature.auth.ui.onboarding

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MountainLogo() {
    Canvas(modifier = Modifier.size(200.dp), onDraw = {
        val green = Color(0xFF2E7D32)
        val amber = Color(0xFFFFA000)

        // Gunung kiri
        val p1 = Path().apply {
            moveTo(size.width * 0.1f, size.height * 0.9f)
            lineTo(size.width * 0.4f, size.height * 0.2f)
            lineTo(size.width * 0.7f, size.height * 0.9f)
            close()
        }
        drawPath(p1, green.copy(alpha = 0.6f), style = Fill)
        drawPath(p1, green, style = Stroke(width = 4f))

        // Gunung kanan (lebih kecil)
        val p2 = Path().apply {
            moveTo(size.width * 0.5f, size.height * 0.9f)
            lineTo(size.width * 0.75f, size.height * 0.35f)
            lineTo(size.width, size.height * 0.9f)
            close()
        }
        drawPath(p2, green.copy(alpha = 0.4f), style = Fill)
        drawPath(p2, green, style = Stroke(width = 4f))

        // Matahari
        drawCircle(
            color = amber,
            radius = size.minDimension * 0.12f,
            center = Offset(size.width * 0.5f, size.height * 0.12f)
        )
    })
}

@Composable
fun OnboardingScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Illustration: icon gunung + matahari buatan sendiri
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            MountainLogo()
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Discover the Beauty of Nature",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Join thousands of explorers and share your journey with the world.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.outline
            )
            
            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onNavigateToRegister,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Get Started", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedButton(
                onClick = onNavigateToLogin,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("I already have an account", fontSize = 16.sp)
            }
        }
    }
}
