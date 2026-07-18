package com.bakudapa.adventure.feature.summit.ui

import androidx.lifecycle.viewModelScope
import com.bakudapa.adventure.core.base.BaseViewModel
import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.summit.domain.repository.SummitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SummitLogListViewModel @Inject constructor(
    private val repository: SummitRepository
) : BaseViewModel<SummitLogListState, SummitLogListEvent, SummitLogListEffect>(SummitLogListState()) {

    override fun onEvent(event: SummitLogListEvent) {
        when (event) {
            is SummitLogListEvent.LoadLogs -> load(event.mountainId)
        }
    }

    private fun load(mountainId: String) {
        viewModelScope.launch {
            repository.getSummitLogs(mountainId).collect { result ->
                when (result) {
                    is DataResult.Loading -> setState { it.copy(isLoading = true) }
                    is DataResult.Success -> setState { it.copy(isLoading = false, logs = result.data) }
                    is DataResult.Error -> setState { it.copy(isLoading = false, error = result.exception.message) }
                }
            }
        }
    }
}

@HiltViewModel
class CreateSummitLogViewModel @Inject constructor(
    private val repository: SummitRepository
) : BaseViewModel<CreateSummitLogState, CreateSummitLogEvent, CreateSummitLogEffect>(CreateSummitLogState()) {

    override fun onEvent(event: CreateSummitLogEvent) {
        when (event) {
            is CreateSummitLogEvent.OnPhotoSelected -> setState { it.copy(photoUri = event.uri) }
            is CreateSummitLogEvent.OnCaptionChanged -> setState { it.copy(caption = event.text) }
            is CreateSummitLogEvent.OnSubmit -> submit(event.mountainId, event.mountainName)
        }
    }

    private fun submit(mountainId: String, mountainName: String) {
        val state = uiState.value
        viewModelScope.launch {
            setState { it.copy(isSaving = true) }
            when (repository.createSummitLog(mountainId, mountainName, state.caption, state.photoUri)) {
                is DataResult.Success -> {
                    setState { it.copy(isSaving = false) }
                    sendEffect(CreateSummitLogEffect.SummitLogCreated)
                }
                is DataResult.Error -> {
                    setState { it.copy(isSaving = false) }
                    sendEffect(CreateSummitLogEffect.ShowError("Gagal menyimpan summit log"))
                }
                DataResult.Loading -> {}
            }
        }
    }
}
