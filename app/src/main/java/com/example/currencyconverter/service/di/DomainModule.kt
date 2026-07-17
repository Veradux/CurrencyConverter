package com.example.currencyconverter.service.di

import com.example.currencyconverter.domain.usecase.ConvertCurrencyUseCase
import com.example.currencyconverter.domain.usecase.GetSupportedCurrenciesUseCase
import com.example.currencyconverter.domain.util.CurrencyFlagProvider
import org.koin.dsl.module

val domainModule = module {

    // Use cases
    factory {
        GetSupportedCurrenciesUseCase(get())
    }

    factory {
        ConvertCurrencyUseCase(get())
    }

    // Utility
    single {
        CurrencyFlagProvider()
    }
}
