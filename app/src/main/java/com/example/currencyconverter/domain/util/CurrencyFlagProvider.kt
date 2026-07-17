package com.example.currencyconverter.domain.util

/**
 * Provides flag emojis for currency codes based on regional indicators.
 * Each currency is mapped to its primary country/region.
 */
class CurrencyFlagProvider {
    companion object {
        private val currencyToFlag: Map<String, String> = mapOf(
            "EUR" to "\uD83C\uDDEA\uD83C\uDDFA", // 🇪🇺 European Union
            "USD" to "\uD83C\uDDFA\uD83C\uDDF8", // 🇺🇸 United States
            "GBP" to "\uD83C\uDDEC\uD83C\uDDE7", // 🇬🇧 United Kingdom
            "JPY" to "\uD83C\uDDEF\uD83C\uDDF5", // 🇯🇵 Japan
            "CHF" to "\uD83C\uDDE8\uD83C\uDDED", // 🇨🇭 Switzerland
            "CAD" to "\uD83C\uDDE8\uD83C\uDDE6", // 🇨🇦 Canada
            "AUD" to "\uD83C\uDDE6\uD83C\uDDFA", // 🇦🇺 Australia
            "CNY" to "\uD83C\uDDE8\uD83C\uDDF3", // 🇨🇳 China
            "SEK" to "\uD83C\uDDF8\uD83C\uDDEA", // 🇸🇪 Sweden
            "NZD" to "\uD83C\uDDF3\uD83C\uDDFF", // 🇳🇿 New Zealand
            "MXN" to "\uD83C\uDDF2\uD83C\uDDFD", // 🇲🇽 Mexico
            "SGD" to "\uD83C\uDDF8\uD83C\uDDEC", // 🇸🇬 Singapore
            "HKD" to "\uD83C\uDDED\uD83C\uDDF0", // 🇭🇰 Hong Kong
            "NOK" to "\uD83C\uDDF3\uD83C\uDDF4", // 🇳🇴 Norway
            "KRW" to "\uD83C\uDDF0\uD83C\uDDF7", // 🇰🇷 South Korea
            "TRY" to "\uD83C\uDDF9\uD83C\uDDF7", // 🇹🇷 Turkey
            "INR" to "\uD83C\uDDEE\uD83C\uDDF3", // 🇮🇳 India
            "RUB" to "\uD83C\uDDF7\uD83C\uDDFA", // 🇷🇺 Russia
            "BRL" to "\uD83C\uDDE7\uD83C\uDDF7", // 🇧🇷 Brazil
            "ZAR" to "\uD83C\uDDFF\uD83C\uDDE6", // 🇿🇦 South Africa
            "DKK" to "\uD83C\uDDE9\uD83C\uDDF0", // 🇩🇰 Denmark
            "PLN" to "\uD83C\uDDF5\uD83C\uDDF1", // 🇵🇱 Poland
            "THB" to "\uD83C\uDDF9\uD83C\uDDED", // 🇹🇭 Thailand
            "IDR" to "\uD83C\uDDEE\uD83C\uDDE9", // 🇮🇩 Indonesia
            "HUF" to "\uD83C\uDDED\uD83C\uDDFA", // 🇭🇺 Hungary
            "CZK" to "\uD83C\uDDE8\uD83C\uDDFF", // 🇨🇿 Czech Republic
            "ILS" to "\uD83C\uDDEE\uD83C\uDDF1", // 🇮🇱 Israel
            "CLP" to "\uD83C\uDDE8\uD83C\uDDF1", // 🇨🇱 Chile
            "PHP" to "\uD83C\uDDF5\uD83C\uDDED", // 🇵🇭 Philippines
            "AED" to "\uD83C\uDDE6\uD83C\uDDEA", // 🇦🇪 UAE
            "COP" to "\uD83C\uDDE8\uD83C\uDDF4", // 🇨🇴 Colombia
            "SAR" to "\uD83C\uDDF8\uD83C\uDDE6", // 🇸🇦 Saudi Arabia
            "MYR" to "\uD83C\uDDF2\uD83C\uDDFE", // 🇲🇾 Malaysia
            "RON" to "\uD83C\uDDF7\uD83C\uDDF4", // 🇷🇴 Romania
            "BGN" to "\uD83C\uDDE7\uD83C\uDDEC", // 🇧🇬 Bulgaria
            "ISK" to "\uD83C\uDDEE\uD83C\uDDF8", // 🇮🇸 Iceland
            "HRK" to "\uD83C\uDDED\uD83C\uDDF7", // 🇭🇷 Croatia
            "ARS" to "\uD83C\uDDE6\uD83C\uDDF7", // 🇦🇷 Argentina
            "EGP" to "\uD83C\uDDEA\uD83C\uDDEC", // 🇪🇬 Egypt
            "NGN" to "\uD83C\uDDF3\uD83C\uDDEC", // 🇳🇬 Nigeria
            "KWD" to "\uD83C\uDDF0\uD83C\uDDFC", // 🇰🇼 Kuwait
            "QAR" to "\uD83C\uDDF6\uD83C\uDDE6", // 🇶🇦 Qatar
            "VND" to "\uD83C\uDDFB\uD83C\uDDF3", // 🇻🇳 Vietnam
            "PKR" to "\uD83C\uDDF5\uD83C\uDDF0", // 🇵🇰 Pakistan
            "BDT" to "\uD83C\uDDE7\uD83C\uDDE9", // 🇧🇩 Bangladesh
            "KZT" to "\uD83C\uDDF0\uD83C\uDDFF", // 🇰🇿 Kazakhstan
            "UAH" to "\uD83C\uDDFA\uD83C\uDDE6", // 🇺🇦 Ukraine
            "MAD" to "\uD83C\uDDF2\uD83C\uDDE6", // 🇲🇦 Morocco
            "TWD" to "\uD83C\uDDF9\uD83C\uDDFC", // 🇹🇼 Taiwan
            "PEN" to "\uD83C\uDDF5\uD83C\uDDEA", // 🇵🇪 Peru
            "CRC" to "\uD83C\uDDE8\uD83C\uDDF7", // 🇨🇷 Costa Rica
            "UYU" to "\uD83C\uDDFA\uD83C\uDDFE", // 🇺🇾 Uruguay
            "DOP" to "\uD83C\uDDE9\uD83C\uDDF4", // 🇩🇴 Dominican Republic
            "GTQ" to "\uD83C\uDDEC\uD83C\uDDF9", // 🇬🇹 Guatemala
            "HNL" to "\uD83C\uDDED\uD83C\uDDF3", // 🇭🇳 Honduras
            "NIO" to "\uD83C\uDDF3\uD83C\uDDEE", // 🇳🇮 Nicaragua
            "BOB" to "\uD83C\uDDE7\uD83C\uDDF4", // 🇧🇴 Bolivia
            "PYG" to "\uD83C\uDDF5\uD83C\uDDFE", // 🇵🇾 Paraguay
            "LKR" to "\uD83C\uDDF1\uD83C\uDDF0", // 🇱🇰 Sri Lanka
            "KES" to "\uD83C\uDDF0\uD83C\uDDEA", // 🇰🇪 Kenya
            "GHS" to "\uD83C\uDDEC\uD83C\uDDED", // 🇬🇭 Ghana
            "TZS" to "\uD83C\uDDF9\uD83C\uDDFF", // 🇹🇿 Tanzania
            "UGX" to "\uD83C\uDDFA\uD83C\uDDEC", // 🇺🇬 Uganda
            "XOF" to "\uD83C\uDDE8\uD83C\uDDEE", // 🇨🇮 Côte d'Ivoire (West African CFA)
            "XAF" to "\uD83C\uDDE8\uD83C\uDDF2", // 🇨🇲 Cameroon (Central African CFA)
            "XPF" to "\uD83C\uDDF5\uD83C\uDDEB", // 🇵🇫 French Polynesia
            "BHD" to "\uD83C\uDDE7\uD83C\uDDED", // 🇧🇭 Bahrain
            "OMR" to "\uD83C\uDDF4\uD83C\uDDF2", // 🇴🇲 Oman
            "JOD" to "\uD83C\uDDEF\uD83C\uDDF4", // 🇯🇴 Jordan
            "LBP" to "\uD83C\uDDF1\uD83C\uDDE7", // 🇱🇧 Lebanon
            "BND" to "\uD83C\uDDE7\uD83C\uDDF3", // 🇧🇳 Brunei
            "MMK" to "\uD83C\uDDF2\uD83C\uDDF2", // 🇲🇲 Myanmar
            "KHR" to "\uD83C\uDDF0\uD83C\uDDED", // 🇰🇭 Cambodia
            "LAK" to "\uD83C\uDDF1\uD83C\uDDE6", // 🇱🇦 Laos
            "MNT" to "\uD83C\uDDF2\uD83C\uDDF3", // 🇲🇳 Mongolia
            "NPR" to "\uD83C\uDDF3\uD83C\uDDF5", // 🇳🇵 Nepal
            "ISK_ALT" to "\uD83C\uDDEE\uD83C\uDDF8", // 🇮🇸 Iceland (redundant, supressed by map)
        )

        /**
         * Returns the flag emoji for the given currency code.
         * Normalizes to uppercase. Falls back to 🌐 for unknown currencies.
         */
        fun flagFor(currencyCode: String): String {
            val normalized = currencyCode.uppercase().trim()
            return currencyToFlag[normalized] ?: "\uD83C\uDF10" // 🌐
        }
    }
}
