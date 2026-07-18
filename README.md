# Currency Converter

An Android currency conversion app built with Jetpack Compose and Material Design 3.
Converts currencies in real-time using the [ExchangeRate-API](https://www.exchangerate-api.com/). 
Contains features such as 
* currency search and selection
* adaptive layouts for phones and tablets
* light and dark themes
* a custom numeric keypad
* emoji country flag display

## Architecture

Three-layer clean architecture:

| Layer | Responsibility | Technology |
|-------|---------------|------------|
| **Presentation** | UI screens, ViewModels, Compose components | Jetpack Compose, M3, Navigation Compose |
| **Domain** | Business models, use cases, repository interfaces | Pure Kotlin |
| **Service** | Remote data, API, DTOs, dependency injection | Retrofit, OkHttp, Koin |

## Package Structure

```
com.example.currencyconverter/
├── domain/
│   ├── model/         # CurrencyCode, Currency, ConversionQuote, CurrencyResult, DomainError
│   ├── repository/    # ExchangeRatesRepository interface
│   ├── usecase/       # ConvertCurrencyUseCase, GetSupportedCurrenciesUseCase
│   └── util/          # CurrencyFlagProvider (emoji flag mapping)
├── presentation/
│   ├── components/    # Shared UI: CurrencyKeypad, CurrencySelector, AdaptiveLayout, etc.
│   ├── currencyconversion/  # Conversion result screen + ViewModel
│   ├── currencyinput/       # Currency selection + amount input screen + ViewModel
│   ├── currencyselection/   # Currency picker bottom sheet + ViewModel
│   ├── navigation/    # Navigation graph
│   └── theme/         # Material 3 theme with dynamic colors
└── service/
    ├── di/            # Koin modules
    ├── dto/           # API response DTOs (kotlinx.serialization)
    ├── mapper/        # DTO → Domain model mappers
    ├── remote/        # Retrofit API interface
    └── repository/    # RemoteExchangeRatesRepository implementation
```

## API Key Setup

1. Get a free API key from [ExchangeRate-API](https://www.exchangerate-api.com/)
2. Add the key to `local.properties`:

```properties
EXCHANGE_RATE_API_KEY=your_api_key_here
```

3. Rebuild the project.

## Build Instructions

```bash
# Assemble debug APK
./gradlew :app:assembleDebug

# Run unit tests
./gradlew :app:testDebugUnitTest

# Run instrumented (UI) tests (requires connected device/emulator)
./gradlew :app:connectedDebugAndroidTest
```

## Testing

- **63 unit tests** covering domain models, utilities, repository (with MockWebServer), and all three ViewModels
- **Compose UI tests** for CurrencyInputScreen and CurrencyConversionScreen (in `app/src/androidTest/`)
- Uses `FakeExchangeRatesRepository` for ViewModel tests — no real network calls
- Uses `kotlinx-coroutines-test` with `StandardTestDispatcher` for deterministic coroutine testing

## Development timeline
9 hours total across 2 days.
#### July 16 Thursday
- Spent 2 hours writing and refining main AI prompt with assistance from GPT-5.6 Sol, describing all specifications. [You can find the prompt here.](mainPrompt.md)
- Spent 1 hour running prompt with Deepseek v4 Pro (these model choices are not personal preference, they were picked experimentally).

#### July 17 Friday
- Spent 2 hours validating prompt results, fixing issues, and steering the model to strictly follow the specifications.
- Spent 4 hours polishing design, refactoring code, fixing edge cases, and manually testing.
