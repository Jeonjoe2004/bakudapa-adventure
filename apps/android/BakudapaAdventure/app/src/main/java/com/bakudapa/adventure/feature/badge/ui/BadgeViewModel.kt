package com.bakudapa.adventure.feature.badge.ui

import androidx.lifecycle.viewModelScope
import com.bakudapa.adventure.core.base.BaseViewModel
import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.badge.domain.repository.BadgeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BadgeViewModel @Inject constructor(
    private val repository: BadgeRepository
) : BaseViewModel<BadgeState, BadgeEvent, BadgeEffect>(BadgeState()) {

    init {
        loadBadges()
    }

    override fun onEvent(event: BadgeEvent) {
        when (event) {
            BadgeEvent.LoadBadges -> loadBadges()
            is BadgeEvent.OnBadgeClicked -> sendEffect(BadgeEffect.ShowBadgeDetail(event.badge))
        }
    }

    private fun loadBadges() {
        viewModelScope.launch {
            setState { it.copy(isLoading = true) }
            
            launch {
                repository.getMyBadges().collectLatest { result ->
                    if (result is DataResult.Success) {
                        setState { it.copy(myBadges = result.data) }
                    }
                }
            }
            
            launch {
                repository.getAllBadges().collectLatest { result ->
                    if (result is DataResult.Success) {
                        setState { it.copy(allBadges = result.data, isLoading = false) }
                    }
                }
            }
        }
    }
}
