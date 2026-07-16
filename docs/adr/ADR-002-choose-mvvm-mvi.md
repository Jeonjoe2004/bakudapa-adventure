# ADR-002: Choose MVVM + MVI for Android

**Status:** Accepted

## Context
Android app needs a predictable state management pattern for complex UI screens (map, tracking, chat).

## Options
1. **MVVM** — standard pattern, good separation of concerns
2. **MVI** — unidirectional data flow, predictable state
3. **MVP** — older pattern, more boilerplate

## Decision
Use a **hybrid MVVM + MVI** pattern:
- `BaseViewModel<State, Event, Effect>` enforces unidirectional flow
- State is single `StateFlow` (immutable updates via `.copy()`)
- One-shot effects go through `Channel<Effect>`
- Events flow `Screen → ViewModel → UseCase → Repository`

## Consequences
- Positive: state changes are predictable and testable
- Positive: effects (navigation, toast) are separated from state
- Negative: more files per feature (State, Event, Effect, ViewModel)
- Mitigation: use `Contract.kt` convention to keep it manageable

## Code Example
```kotlin
// Contract
data class LoginState(...) : UiState
sealed class LoginEvent : UiEvent
sealed class LoginEffect : UiEffect

// Usage
viewModel.onEvent(LoginEvent.LoginClicked)
viewModel.effect.collect { ... }
```
