package com.example.eval2.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class AuthValidatorTest {

    private val validator = AuthValidator { email -> "@" in email }

    @Test
    fun `validateEmail con email válido devuelve null`() {
        assertNull(validator.validateEmail("test@example.com"))
    }

    @Test
    fun `validateEmail con email inválido devuelve mensaje de error`() {
        assertNotNull(validator.validateEmail("invalido"))
    }

    @Test
    fun `validateEmail con email vacío devuelve mensaje de error`() {
        assertNotNull(validator.validateEmail(" "))
    }

    @Test
    fun `validatePasswordForLogin con contraseña vacía devuelve mensaje de error`() {
        assertNotNull(validator.validatePasswordForLogin(" "))
    }

    @Test
    fun `validatePasswordForLogin con contraseña válida devuelve null`() {
        assertNull(validator.validatePasswordForLogin("password123"))
    }

    @Test
    fun `validateFirstName con nombre vacío devuelve mensaje de error`() {
        assertNotNull(validator.validateFirstName(" "))
    }

    @Test
    fun `validateFirstName con nombre válido devuelve null`() {
        assertNull(validator.validateFirstName("John"))
    }

    @Test
    fun `validateLastName con apellido vacío devuelve mensaje de error`() {
        assertNotNull(validator.validateLastName(" "))
    }

    @Test
    fun `validateLastName con apellido válido devuelve null`() {
        assertNull(validator.validateLastName("Doe"))
    }

    @Test
    fun `validatePasswordForRegister con contraseña corta devuelve mensaje de error`() {
        assertNotNull(validator.validatePasswordForRegister("12345"))
    }

    @Test
    fun `validatePasswordForRegister con contraseña de longitud correcta devuelve null`() {
        assertNull(validator.validatePasswordForRegister("123456"))
    }

    @Test
    fun `validateConfirmPassword con contraseñas que no coinciden devuelve mensaje de error`() {
        assertNotNull(validator.validateConfirmPassword("password123", "password456"))
    }

    @Test
    fun `validateConfirmPassword con contraseñas que coinciden devuelve null`() {
        assertNull(validator.validateConfirmPassword("password123", "password123"))
    }
}