package com.example.currencyconverter.domain.model

sealed class DomainError(val message: String) {
    class NetworkError(message: String = "Unable to connect. Please check your internet connection.") : DomainError(message)
    class ApiError(val code: String, message: String = "An error occurred while fetching data.") : DomainError(message)
    class InvalidApiKey(message: String = "Invalid API key configured.") : DomainError(message)
    class QuotaReached(message: String = "API quota reached. Please try again later.") : DomainError(message)
    class UnsupportedCurrency(message: String = "This currency is not supported.") : DomainError(message)
    class ServerError(message: String = "A server error occurred. Please try again.") : DomainError(message)
    class UnknownError(message: String = "An unexpected error occurred.") : DomainError(message)
}
