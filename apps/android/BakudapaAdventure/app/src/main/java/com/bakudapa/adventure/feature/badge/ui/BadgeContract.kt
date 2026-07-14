package com.bakudapa.adventure.feature.badge.ui

import com.bakudapa.adventure.core.base.UiEffect
import com.bakudapa.adventure.core.base.UiEvent
import com.bakudapa.adventure.core.base.UiState
import com.bakudapa.adventure.feature.badge.domain.model.Badge

data class BadgeState(
    val isLoading: Boolean = false,
    val myBadges: List<Badge> = emptyList(),
    val allBadges: List<Badge> = emptyList(),
    val error: String? = null
) : UiState

sealed class BadgeEvent : UiEvent {
    object LoadBadges : BadgeEvent()
    data class OnBadgeClicked(val badge: Badge) : BadgeEvent()
}

sealed class BadgeEffect : UiEffect {
    data class ShowBadgeDetail(val badge: Badge) : BadgeEffect()
}
