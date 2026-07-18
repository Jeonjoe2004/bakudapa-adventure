package com.bakudapa.adventure.feature.summit.ui

import com.bakudapa.adventure.core.base.UiEffect
import com.bakudapa.adventure.core.base.UiEvent
import com.bakudapa.adventure.core.base.UiState
import com.bakudapa.adventure.feature.summit.domain.model.SummitLog

data class SummitLogListState(
    val isLoading: Boolean = false,
    val logs: List<SummitLog> = emptyList(),
    val error: String? = null
) : UiState

data class CreateSummitLogState(
    val isLoading: Boolean = false,
    val photoUri: android.net.Uri? = null,
    val caption: String = "",
    val isSaving: Boolean = false,
    val error: String? = null
) : UiState

sealed class SummitLogListEvent : UiEvent {
    data class LoadLogs(val mountainId: String) : SummitLogListEvent()
}

sealed class CreateSummitLogEvent : UiEvent {
    data class OnPhotoSelected(val uri: android.net.Uri?) : CreateSummitLogEvent()
    data class OnCaptionChanged(val text: String) : CreateSummitLogEvent()
    data class OnSubmit(val mountainId: String, val mountainName: String) : CreateSummitLogEvent()
}

sealed class SummitLogListEffect : UiEffect {
    data class ShowError(val message: String) : SummitLogListEffect()
}

sealed class CreateSummitLogEffect : UiEffect {
    object SummitLogCreated : CreateSummitLogEffect()
    data class ShowError(val message: String) : CreateSummitLogEffect()
}
