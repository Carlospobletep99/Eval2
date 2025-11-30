package com.example.eval2.model

// ¡Ya no hay dependencias de Android!
class AuthValidator(private val isEmailValid: (String) -> Boolean) {

    fun validateEmail(email: String): String? {
        if (!isEmailValid(email.trim())) {
            return "El email no es válido."
        }
        return null
    }

    fun validatePasswordForLogin(password: String): String? {
        val passwordRecortada = password.trim()
        if (passwordRecortada.isBlank()) {
            return "La contraseña es requerida."
        }
        return null
    }

    fun validateFirstName(firstName: String): String? {
        if (firstName.trim().isBlank()) {
            return "El nombre es requerido."
        }
        return null
    }

    fun validateLastName(lastName: String): String? {
        if (lastName.trim().isBlank()) {
            return "El apellido es requerido."
        }
        return null
    }

    fun validatePasswordForRegister(password: String): String? {
        if (password.trim().length < 6) {
            return "La contraseña debe tener al menos 6 caracteres."
        }
        return null
    }

    fun validateConfirmPassword(password: String, confirm: String): String? {
        if (password.trim() != confirm.trim()) {
            return "Las contraseñas no coinciden."
        }
        return null
    }
}