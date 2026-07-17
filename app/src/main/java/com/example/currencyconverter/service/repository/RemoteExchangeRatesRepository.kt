package com.example.currencyconverter.service.repository

import com.example.currencyconverter.BuildConfig
import com.example.currencyconverter.domain.model.Currency
import com.example.currencyconverter.domain.model.CurrencyCode
import com.example.currencyconverter.domain.model.CurrencyResult
import com.example.currencyconverter.domain.model.ConversionQuote
import com.example.currencyconverter.domain.model.DomainError
import com.example.currencyconverter.domain.repository.ExchangeRatesRepository
import com.example.currencyconverter.service.dto.ErrorResponse
import com.example.currencyconverter.service.mapper.DomainMappers.mapErrorType
import com.example.currencyconverter.service.mapper.DomainMappers.toConversionQuote
import com.example.currencyconverter.service.mapper.DomainMappers.toCurrencies
import com.example.currencyconverter.service.remote.ExchangeRatesApi
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import java.io.IOException
import java.math.BigDecimal

class RemoteExchangeRatesRepository(
    private val api: ExchangeRatesApi
) : ExchangeRatesRepository {

    private val apiKey: String get() = BuildConfig.EXCHANGE_RATE_API_KEY

    // In-memory cache for supported currencies
    private var cachedCurrencies: List<Currency>? = null
    private val cacheMutex = Mutex()

    override suspend fun getSupportedCurrencies(forceRefresh: Boolean): CurrencyResult<List<Currency>> {
        // Return cached data if available and refresh not forced
        if (!forceRefresh) {
            cacheMutex.withLock {
                cachedCurrencies?.let { return CurrencyResult.Success(it) }
            }
        }

        return try {
            val response = api.getSupportedCodes(apiKey)

            if (response.result == "error") {
                return CurrencyResult.Error(mapErrorType(response.errorType))
            }

            val currencies = response.toCurrencies()
            cacheMutex.withLock {
                cachedCurrencies = currencies
            }
            CurrencyResult.Success(currencies)
        } catch (e: HttpException) {
            return try {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = errorBody?.let {
                    Json.decodeFromString<ErrorResponse>(it)
                }
                val errorType = errorResponse?.errorType ?: ""
                CurrencyResult.Error(mapErrorType(errorType))
            } catch (_: Exception) {
                CurrencyResult.Error(DomainError.ServerError())
            }
        } catch (_: IOException) {
            // Network error - return cached data if available as fallback
            cacheMutex.withLock {
                cachedCurrencies?.let {
                    return CurrencyResult.Success(it)
                }
            }
            CurrencyResult.Error(DomainError.NetworkError())
        } catch (_: Exception) {
            CurrencyResult.Error(DomainError.UnknownError())
        }
    }

    override suspend fun convertCurrency(
        from: CurrencyCode,
        to: CurrencyCode,
        amount: BigDecimal
    ): CurrencyResult<ConversionQuote> {
        return try {
            val response = api.getPairConversion(
                apiKey = apiKey,
                baseCode = from.value,
                targetCode = to.value,
                amount = amount.toPlainString()
            )

            if (response.result == "error") {
                return CurrencyResult.Error(mapErrorType(response.errorType))
            }

            CurrencyResult.Success(response.toConversionQuote(amount))
        } catch (e: HttpException) {
            return try {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = errorBody?.let {
                    Json.decodeFromString<ErrorResponse>(it)
                }
                val errorType = errorResponse?.errorType ?: ""
                CurrencyResult.Error(mapErrorType(errorType))
            } catch (_: Exception) {
                CurrencyResult.Error(DomainError.ServerError())
            }
        } catch (_: IOException) {
            CurrencyResult.Error(DomainError.NetworkError())
        } catch (_: Exception) {
            CurrencyResult.Error(DomainError.UnknownError())
        }
    }
}
