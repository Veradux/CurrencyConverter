package com.example.currencyconverter

import android.app.Application
import com.example.currencyconverter.service.di.domainModule
import com.example.currencyconverter.service.di.serviceModule
import com.example.currencyconverter.service.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class CurrencyConverterApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@CurrencyConverterApplication)
            modules(
                serviceModule,
                domainModule,
                viewModelModule
            )
        }
    }
}
