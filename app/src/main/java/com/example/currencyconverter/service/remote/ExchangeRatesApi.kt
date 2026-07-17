package com.example.currencyconverter.service.remote

import com.example.currencyconverter.service.dto.PairConversionResponse
import com.example.currencyconverter.service.dto.SupportedCodesResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ExchangeRatesApi {

    @GET("v6/{apiKey}/codes")
    suspend fun getSupportedCodes(
        @Path("apiKey") apiKey: String
    ): SupportedCodesResponse

    @GET("v6/{apiKey}/pair/{baseCode}/{targetCode}/{amount}")
    suspend fun getPairConversion(
        @Path("apiKey") apiKey: String,
        @Path("baseCode") baseCode: String,
        @Path("targetCode") targetCode: String,
        @Path("amount") amount: String
    ): PairConversionResponse
}
