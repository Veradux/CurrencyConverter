package com.example.currencyconverter.service.repository

import com.example.currencyconverter.domain.model.CurrencyResult
import com.example.currencyconverter.domain.model.DomainError
import com.example.currencyconverter.service.remote.ExchangeRatesApi
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import java.math.BigDecimal

class RemoteExchangeRatesRepositoryTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var repository: RemoteExchangeRatesRepository

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
            coerceInputValues = true
        }

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        val api = retrofit.create(ExchangeRatesApi::class.java)
        repository = RemoteExchangeRatesRepository(api)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun getSupportedCurrenciesSuccessMapsToCurrencyListWithFlags() = runBlocking {
        val responseJson = """
            {
                "result": "success",
                "supported_codes": [
                    ["USD", "US Dollar"],
                    ["EUR", "Euro"],
                    ["GBP", "British Pound"]
                ]
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(responseJson)
        )

        val result = repository.getSupportedCurrencies(forceRefresh = true)

        assertTrue(result is CurrencyResult.Success)
        val currencies = (result as CurrencyResult.Success).data
        assertEquals(3, currencies.size)
        assertEquals("USD", currencies[0].code.value)
        assertEquals("US Dollar", currencies[0].displayName)
        assertEquals("\uD83C\uDDFA\uD83C\uDDF8", currencies[0].flagEmoji)
        assertEquals("EUR", currencies[1].code.value)
        assertEquals("Euro", currencies[1].displayName)
        assertEquals("\uD83C\uDDEA\uD83C\uDDFA", currencies[1].flagEmoji)
    }

    @Test
    fun getSupportedCurrenciesWithErrorTypeMapsToApiError() = runBlocking {
        val responseJson = """
            {
                "result": "error",
                "error-type": "malformed-request",
                "supported_codes": []
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(responseJson)
        )

        val result = repository.getSupportedCurrencies(forceRefresh = true)

        assertTrue(result is CurrencyResult.Error)
        val error = (result as CurrencyResult.Error).error
        assertTrue(error is DomainError.ApiError)
        assertEquals("malformed-request", (error as DomainError.ApiError).code)
    }

    @Test
    fun getSupportedCurrenciesInvalidJsonReturnsError() = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("this is not valid json{{{")
        )

        val result = repository.getSupportedCurrencies(forceRefresh = true)

        assertTrue(result is CurrencyResult.Error)
    }

    @Test
    fun getSupportedCurrenciesHttpErrorReturnsError() = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error")
        )

        val result = repository.getSupportedCurrencies(forceRefresh = true)

        assertTrue(result is CurrencyResult.Error)
    }

    @Test
    fun getSupportedCurrenciesConnectionFailureReturnsNetworkError() = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setSocketPolicy(SocketPolicy.DISCONNECT_AFTER_REQUEST)
        )

        val result = repository.getSupportedCurrencies(forceRefresh = true)

        assertTrue(result is CurrencyResult.Error)
        assertTrue((result as CurrencyResult.Error).error is DomainError.NetworkError)
    }

    @Test
    fun convertCurrencySuccessReturnsConversionQuote() = runBlocking {
        val responseJson = """
            {
                "result": "success",
                "base_code": "USD",
                "target_code": "EUR",
                "conversion_rate": 0.925,
                "conversion_result": 92.50
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(responseJson)
        )

        val from = com.example.currencyconverter.domain.model.CurrencyCode("USD")
        val to = com.example.currencyconverter.domain.model.CurrencyCode("EUR")
        val result = repository.convertCurrency(from, to, BigDecimal("100"))

        assertTrue(result is CurrencyResult.Success)
        val quote = (result as CurrencyResult.Success).data
        assertEquals("USD", quote.fromCurrency.value)
        assertEquals("EUR", quote.toCurrency.value)
        assertEquals(0, BigDecimal("100").compareTo(quote.sourceAmount))
        assertEquals(0, BigDecimal("92.50").compareTo(quote.convertedAmount))
        assertEquals(0, BigDecimal("0.925").compareTo(quote.conversionRate))
    }

    @Test
    fun convertCurrencyWithErrorTypeMapsToApiError() = runBlocking {
        val responseJson = """
            {
                "result": "error",
                "error-type": "unsupported-code",
                "base_code": "USD",
                "target_code": "XXX",
                "conversion_rate": 0,
                "conversion_result": 0
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(responseJson)
        )

        val from = com.example.currencyconverter.domain.model.CurrencyCode("USD")
        val to = com.example.currencyconverter.domain.model.CurrencyCode("XXX")
        val result = repository.convertCurrency(from, to, BigDecimal("100"))

        assertTrue(result is CurrencyResult.Error)
        val error = (result as CurrencyResult.Error).error
        assertTrue(error is DomainError.UnsupportedCurrency)
    }

    @Test
    fun getSupportedCurrenciesCachingOnlyHitsServerOnce() = runBlocking {
        val responseJson = """
            {
                "result": "success",
                "supported_codes": [
                    ["USD", "US Dollar"]
                ]
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(responseJson)
        )

        val result1 = repository.getSupportedCurrencies(forceRefresh = true)
        assertTrue(result1 is CurrencyResult.Success)

        val result2 = repository.getSupportedCurrencies(forceRefresh = false)
        assertTrue(result2 is CurrencyResult.Success)

        assertEquals(1, mockWebServer.requestCount)
    }

    @Test
    fun forceRefreshHitsServerAgain() = runBlocking {
        val responseJson = """
            {
                "result": "success",
                "supported_codes": [
                    ["USD", "US Dollar"]
                ]
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(responseJson)
        )
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(responseJson)
        )

        val result1 = repository.getSupportedCurrencies(forceRefresh = true)
        assertTrue(result1 is CurrencyResult.Success)

        val result2 = repository.getSupportedCurrencies(forceRefresh = true)
        assertTrue(result2 is CurrencyResult.Success)

        assertEquals(2, mockWebServer.requestCount)
    }

    @Test
    fun convertCurrencyNetworkError() = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setSocketPolicy(SocketPolicy.DISCONNECT_AFTER_REQUEST)
        )

        val from = com.example.currencyconverter.domain.model.CurrencyCode("USD")
        val to = com.example.currencyconverter.domain.model.CurrencyCode("EUR")
        val result = repository.convertCurrency(from, to, BigDecimal("100"))

        assertTrue(result is CurrencyResult.Error)
        assertTrue((result as CurrencyResult.Error).error is DomainError.NetworkError)
    }
}
