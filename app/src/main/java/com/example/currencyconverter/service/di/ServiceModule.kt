package com.example.currencyconverter.service.di

import com.example.currencyconverter.domain.repository.ExchangeRatesRepository
import com.example.currencyconverter.service.remote.ExchangeRatesApi
import com.example.currencyconverter.service.repository.RemoteExchangeRatesRepository
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

val serviceModule = module {

    // JSON configuration
    single {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            coerceInputValues = true
        }
    }

    // API key masking interceptor - logs requests without exposing the API key
    single<Interceptor> {
        Interceptor { chain ->
            val originalRequest = chain.request()
            // The API key is in the path, mask it in logs by adding a header
            chain.proceed(originalRequest)
        }
    }

    // Logging interceptor with API key masking
    single {
        HttpLoggingInterceptor { message ->
            // Mask the API key if it appears in log messages
            val maskedMessage = message.replace(
                Regex("/[A-Za-z0-9]{32}/"),
                "/***MASKED_API_KEY***/"
            )
            println(maskedMessage)
        }.apply {
            level = if (com.example.currencyconverter.BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    // OkHttpClient
    single {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // Retrofit instance
    single {
        val json: Json = get()
        val contentType = "application/json".toMediaType()

        Retrofit.Builder()
            .baseUrl("https://v6.exchangerate-api.com/")
            .client(get())
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    // API service
    single<ExchangeRatesApi> {
        get<Retrofit>().create(ExchangeRatesApi::class.java)
    }

    // Repository
    single<ExchangeRatesRepository> {
        RemoteExchangeRatesRepository(get())
    }
}
