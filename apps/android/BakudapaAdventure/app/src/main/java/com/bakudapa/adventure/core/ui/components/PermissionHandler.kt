package com.bakudapa.adventure.core.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import com.bakudapa.adventure.R
import com.google.accompanist.permissions.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HandlePermissions(
    permissions: List<String>,
    rationaleMessage: String,
    onPermissionGranted: @Composable () -> Unit,
    onPermissionDenied: @Composable () -> Unit = {}
) {
    val permissionState = rememberMultiplePermissionsState(permissions = permissions)

    if (permissionState.allPermissionsGranted) {
        onPermissionGranted()
    } else {
        var showRationale by remember { mutableStateOf(false) }

        if (showRationale || permissionState.shouldShowRationale) {
            PermissionRationaleDialog(
                message = rationaleMessage,
                onConfirm = {
                    showRationale = false
                    permissionState.launchMultiplePermissionRequest()
                },
                onDismiss = {
                    showRationale = false
                }
            )
        } else {
            LaunchedEffect(Unit) {
                permissionState.launchMultiplePermissionRequest()
            }
            onPermissionDenied()
        }
    }
}

@Composable
fun PermissionRationaleDialog(
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.permission_required_title)) },
        text = { Text(text = message) },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(stringResource(R.string.grant_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel_button))
            }
        }
    )
}
