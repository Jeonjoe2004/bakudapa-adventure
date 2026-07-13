package com.bakudapa.adventure.feature.auth.ui.verify

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun VerifyEmailScreen(
    onNavigateToProfileCompletion: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: VerifyEmailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                VerifyEmailEffect.NavigateToProfileCompletion -> onNavigateToProfileCompletion()
                VerifyEmailEffect.NavigateToLogin -> onNavigateToLogin()
                is VerifyEmailEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "📧", fontSize = 80.sp)
            
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Verify your email",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "We've sent a verification link to ${state.email}. Please check your inbox and follow the instructions.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.outline
            )
            
            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = { viewModel.onEvent(VerifyEmailEvent.CheckVerificationClicked) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("I've Verified My Email")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TextButton(
                onClick = { viewModel.onEvent(VerifyEmailEvent.ResendEmailClicked) },
                enabled = !state.isLoading
            ) {
                Text("Resend verification email")
            }
            
            TextButton(
                onClick = { viewModel.onEvent(VerifyEmailEvent.BackToLoginClicked) }
            ) {
                Text("Back to Login")
            }
        }
    }
}
