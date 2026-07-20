package com.example.currencyconverter.domain.model

import org.junit.Assert.*
import org.junit.Test

class DomainErrorTest {

    @Test
    fun `NetworkError has correct default message`() {
        val error = DomainError.NetworkError()
        assertEquals("Unable to connect. Please check your internet connection.", error.message)
    }

    @Test
    fun `NetworkError can be constructed with custom message`() {
        val error = DomainError.NetworkError("Custom network error message")
        assertEquals("Custom network error message", error.message)
    }

    @Test
    fun `ApiError has correct default message`() {
        val error = DomainError.ApiError("ERR_500")
        assertEquals("An error occurred while fetching data.", error.message)
    }

    @Test
    fun `ApiError can be constructed with custom message`() {
        val error = DomainError.ApiError("ERR_404", "Custom API error message")
        assertEquals("Custom API error message", error.message)
    }

    @Test
    fun `ApiError stores code correctly`() {
        val error = DomainError.ApiError("ERR_500")
        assertEquals("ERR_500", error.code)
    }

    @Test
    fun `ApiError stores code correctly with custom message`() {
        val error = DomainError.ApiError("ERR_404", "Custom API error message")
        assertEquals("ERR_404", error.code)
    }

    @Test
    fun `InvalidApiKey has correct default message`() {
        val error = DomainError.InvalidApiKey()
        assertEquals("Invalid API key configured.", error.message)
    }

    @Test
    fun `InvalidApiKey can be constructed with custom message`() {
        val error = DomainError.InvalidApiKey("Your API key has expired.")
        assertEquals("Your API key has expired.", error.message)
    }

    @Test
    fun `QuotaReached has correct default message`() {
        val error = DomainError.QuotaReached()
        assertEquals("API quota reached. Please try again later.", error.message)
    }

    @Test
    fun `QuotaReached can be constructed with custom message`() {
        val error = DomainError.QuotaReached("Daily limit exceeded.")
        assertEquals("Daily limit exceeded.", error.message)
    }

    @Test
    fun `UnsupportedCurrency has correct default message`() {
        val error = DomainError.UnsupportedCurrency()
        assertEquals("This currency is not supported.", error.message)
    }

    @Test
    fun `UnsupportedCurrency can be constructed with custom message`() {
        val error = DomainError.UnsupportedCurrency("XYZ is not a valid currency code.")
        assertEquals("XYZ is not a valid currency code.", error.message)
    }

    @Test
    fun `ServerError has correct default message`() {
        val error = DomainError.ServerError()
        assertEquals("A server error occurred. Please try again.", error.message)
    }

    @Test
    fun `ServerError can be constructed with custom message`() {
        val error = DomainError.ServerError("Internal server error: 500")
        assertEquals("Internal server error: 500", error.message)
    }

    @Test
    fun `UnknownError has correct default message`() {
        val error = DomainError.UnknownError()
        assertEquals("An unexpected error occurred.", error.message)
    }

    @Test
    fun `UnknownError can be constructed with custom message`() {
        val error = DomainError.UnknownError("Something went wrong.")
        assertEquals("Something went wrong.", error.message)
    }

    @Test
    fun `message property returns the message`() {
        val error = DomainError.NetworkError("Test message")
        assertEquals("Test message", error.message)
    }

    @Test
    fun `api Error Stores Code`() {
        val error = DomainError.ApiError("test-error")
        assertEquals("test-error", error.code)
    }
}
