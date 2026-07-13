package com.bakudapa.adventure.feature.auth.utils

import android.util.Patterns

object AuthValidator {
    fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }
    
    fun isValidDisplayName(name: String): Boolean {
        return name.length >= 3
    }
}
