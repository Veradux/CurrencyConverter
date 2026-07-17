package com.example.currencyconverter.service.di

import com.example.currencyconverter.presentation.currencyconversion.CurrencyConversionViewModel
import com.example.currencyconverter.presentation.currencyinput.CurrencyInputViewModel
import com.example.currencyconverter.presentation.currencyselection.CurrencySelectionViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::CurrencyInputViewModel)
    viewModelOf(::CurrencySelectionViewModel)
    viewModelOf(::CurrencyConversionViewModel)
}
