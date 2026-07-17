package com.example.currencyconverter.service.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SupportedCodesResponse(
    val result: String,
    @SerialName("supported_codes")
    val supportedCodes: List<List<String>>,
    @SerialName("error-type")
    val errorType: String? = null
)

@Serializable
data class PairConversionResponse(
    val result: String,
    @SerialName("base_code")
    val baseCode: String,
    @SerialName("target_code")
    val targetCode: String,
    @SerialName("conversion_rate")
    val conversionRate: Double,
    @SerialName("conversion_result")
    val conversionResult: Double,
    @SerialName("error-type")
    val errorType: String? = null
)

@Serializable
data class ErrorResponse(
    @SerialName("error-type")
    val errorType: String? = null,
    val result: String? = null
)
