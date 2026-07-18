# ROLE

Act as a senior Android application engineer and software architect with expert-level knowledge of:

- Kotlin
- Jetpack Compose and Material 3
- Modern Android architecture
- Coroutines and Flow
- Retrofit and HTTP API integration
- Koin dependency injection
- Accessibility
- Responsive and adaptive Android layouts
- Unit testing and Compose UI testing

Your task is to create a complete, production-quality demonstration Android application—not merely a prototype, architecture outline, or collection of disconnected snippets.

Before producing code, inspect all requirements for conflicts or missing details. Resolve non-blocking ambiguities using sensible Android best practices and state those assumptions briefly. Ask a question only when implementation would otherwise be impossible.

Use this priority order when requirements conflict:

1. Correctness and security
2. Explicit functional requirements
3. Maintainable architecture and testability
4. Accessibility and Android platform conventions
5. Minimalism


# PROJECT OBJECTIVE

Build a small but polished Android currency-conversion application named:

Currency Converter

The app should intentionally contain only one main feature, but its implementation must demonstrate strong modern Android engineering practices.

The deliverable must be a runnable project rather than pseudocode. Do not leave TODOs, omitted implementations, placeholder methods, ellipses, or comments such as “implement this later.” The only permitted external placeholder is the ExchangeRate-API key.


# VISUAL REFERENCE

Use this image link as the primary visual reference for the conversion screen:
https://media.discordapp.net/attachments/1273367584863027322/1527456442590957628/CurrencyConversionScreen_design.png?ex=6a5aba2a&is=6a5968aa&hm=51abbcf03e36a05510e09d83b4e884879a9ac4865d1df9f9bb2ff9f8d4bcd676&=&format=webp&quality=lossless&width=481&height=1003

Also use the general interaction and visual language of Paysera’s currency exchange experience as inspiration:

https://www.paysera.com/v2/en/fees/currency-conversion-calculator#/

Requirements:

- Interpret the design natively for Android rather than copying an iOS screen literally.
- Do not reproduce the iOS status bar, home indicator, or iOS-specific navigation conventions.
- Do not include Paysera logos, trademarks, branded assets, or proprietary text.
- The result should be an original interface inspired by the reference, not a pixel-for-pixel clone.
- Preserve the reference’s main visual characteristics:
    - Purple-to-deep-navy gradient
    - Large currency amounts
    - White primary typography
    - Circular swap control
    - Spacious minimal layout
    - Clear currency flags and codes
    - Smooth bottom-sheet transitions


# PLATFORM AND BUILD CONFIGURATION

Create a single-module Android project using the `:app` module.

Use:

- Kotlin
- Gradle Kotlin DSL
- Version Catalogs through `libs.versions.toml`
- Jetpack Compose
- Material 3
- minSdk = 29
- compileSdk = 36
- targetSdk = 36
- The current stable Android Gradle Plugin, Kotlin, Compose, and library versions that are mutually compatible
- Stable dependency releases only; do not use alpha, beta, RC, snapshot, or deprecated libraries unless absolutely required and explicitly justified

Use the JDK version required by the selected stable Android Gradle Plugin.

The project must:

- Compile successfully
- Pass Android lint without critical errors
- Include the INTERNET permission
- Use no unnecessary permissions
- Contain no XML layouts
- Contain no Fragments
- Contain no legacy Views
- Store user-facing strings in Android string resources rather than hardcoding them in composables


# REQUIRED DEPENDENCIES

Use:

- Jetpack Compose Material 3
- AndroidX Lifecycle ViewModel
- `collectAsStateWithLifecycle`
- Navigation Compose
- Kotlin Coroutines and Flow
- Retrofit
- OkHttp
- A supported Retrofit JSON converter, preferably Kotlinx Serialization
- Koin for Android and Compose
- Koin BOM for dependency-version alignment
- JUnit
- Kotlin Coroutines Test
- AndroidX Compose UI Test
- MockWebServer for service-layer tests

Keep dependencies minimal. Do not introduce Room, DataStore, RxJava, Dagger/Hilt, Accompanist, or another navigation framework.


# API-KEY SECURITY

Never hardcode the API key in Kotlin source code, XML resources, or version-controlled Gradle files.

Read it from:

local.properties

using a property such as:

EXCHANGE_RATE_API_KEY=your_api_key_here

Expose it to the application through a generated BuildConfig field.

Requirements:

- Include `local.properties` in `.gitignore`.
- Provide setup instructions in the README.
- Fail with a clear developer-facing message in debug builds when the key is missing.
- Never log the API key or a complete URL containing it.
- Add a short README note explaining that keys embedded in client applications can ultimately be extracted and that a production financial application should normally proxy sensitive credentials through a backend.


# ARCHITECTURE

Use clean architecture principles inside the single `:app` module.

The feature must be organized primarily under exactly these three top-level packages:

- `presentation`
- `domain`
- `service`

Root-level application bootstrap classes such as `CurrencyConverterApplication` and `MainActivity` may remain at the application package root.

Use the following dependency direction:

presentation -> domain
service -> domain

The domain package must not depend on presentation, Retrofit, Compose, Android UI classes, or service DTOs.

Suggested structure:

com.example.currencyconverter
├── CurrencyConverterApplication.kt
├── MainActivity.kt
├── presentation
│   ├── navigation
│   ├── theme
│   ├── components
│   ├── currencyinput
│   ├── currencyselection
│   └── currencyconversion
├── domain
│   ├── model
│   ├── repository
│   ├── usecase
│   └── util
└── service
├── remote
├── dto
├── mapper
├── repository
└── di

The final structure may vary slightly when necessary, but it must preserve the three-layer separation.


# DOMAIN LAYER

The domain layer should contain pure Kotlin business abstractions.

Include appropriately named models such as:

- `Currency`
    - code
    - displayName
    - flagEmoji
- `CurrencyCode`
- `ConversionQuote`
    - fromCurrency
    - toCurrency
    - sourceAmount
    - convertedAmount
    - conversionRate
    - inverseRate
- A domain-level error model

Define the repository interface in the domain layer:
```
interface ExchangeRatesRepository {
    suspend fun getSupportedCurrencies(
        forceRefresh: Boolean = false
    ): CurrencyResult<List<Currency>>

    suspend fun convertCurrency(
        from: CurrencyCode,
        to: CurrencyCode,
        amount: BigDecimal
    ): CurrencyResult<ConversionQuote>
}
```

Use `BigDecimal` in domain and presentation logic for entered and displayed monetary values.

A service DTO may temporarily deserialize numeric values into another numeric type when required by the serializer, but convert them to the domain representation at the service/domain boundary.

Create focused use cases where they add business value, for example:

- `GetSupportedCurrenciesUseCase`
- `ConvertCurrencyUseCase`

Do not create unnecessary one-line wrapper classes merely to appear “clean.”


# CURRENCY FLAG HANDLING

Create a reusable pure-Kotlin component such as:

CurrencyFlagProvider

with a function similar to:

fun flagFor(currencyCode: String): String

Requirements:

- Use a deterministic local currency-to-region mapping.
- Do not derive a flag by blindly taking the first two letters of a currency code.
- Include explicit mappings for common currencies.
- Use suitable special mappings such as:
    - EUR -> European Union flag
    - USD -> United States flag
    - GBP -> United Kingdom flag
- For multinational or ambiguous currencies, use a sensible regional or globe fallback.
- Return a neutral fallback such as `🌐` for unknown codes.
- Normalize lowercase input safely.
- Unit-test normal mappings, special mappings, invalid input, lowercase input, and fallback behavior.

The supported-codes API response provides currency codes and names, not a reliable unique country flag for every currency. Keep flag resolution separate from the network DTO.


# SERVICE LAYER

Implement the domain `ExchangeRatesRepository` through Retrofit.

Base URL:

https://v6.exchangerate-api.com/v6/

Create an `ExchangeRatesApi` interface using suspend functions.

## Supported currencies request

Use the dedicated supported-codes endpoint:

GET `{apiKey}/codes`

Equivalent complete URL:

https://v6.exchangerate-api.com/v6/YOUR-API-KEY/codes

Parse:

- `result`
- `supported_codes`
- `error-type`, when present

The API returns entries containing a currency code and display name.

Map these entries into domain `Currency` objects and obtain flags through `CurrencyFlagProvider`.

Requirements:

- Sort currencies alphabetically by currency code.
- Remove malformed entries safely.
- Remove duplicate currency codes.
- Cache the successfully loaded list in memory for the application session.
- Respect `forceRefresh`.
- Do not introduce persistent caching or a database.

## Pair conversion request

Use the pair conversion endpoint including the amount:

GET `{apiKey}/pair/{baseCode}/{targetCode}/{amount}`

Equivalent example:

https://v6.exchangerate-api.com/v6/YOUR-API-KEY/pair/EUR/USD/10.50

Parse relevant fields such as:

- `result`
- `base_code`
- `target_code`
- `conversion_rate`
- `conversion_result`
- `error-type`, when present

Validate API-level success rather than assuming every HTTP 200 response represents a successful conversion.

Map failures into domain errors such as:

- No network connection or timeout
- Invalid API key
- Inactive account
- Quota reached
- Unsupported currency
- Malformed request
- HTTP/server failure
- Invalid or incomplete response
- Unknown failure

Do not expose technical exception messages directly to users.

Do not perform an unnecessary connectivity pre-check. Handle request exceptions and API responses as the source of truth.

## Repository implementations

Provide:

1. `RemoteExchangeRatesRepository`
2. `FakeExchangeRatesRepository`

The fake implementation should primarily live in the relevant test source sets rather than being shipped unnecessarily in the production application.

It must support configurable:

- Currency data
- Conversion results
- Artificial delay
- Network errors
- API errors

Use it for ViewModel and UI tests so tests never call the real API.


# DEPENDENCY INJECTION

Use Koin.

Create Koin modules for:

- Network configuration
- Retrofit API
- Repository binding
- Domain use cases
- ViewModels
- Utility providers where injection is useful

Bind the domain repository interface to `RemoteExchangeRatesRepository`.

Use Koin’s Compose integration for ViewModel retrieval.

Avoid:

- Service locators
- Global mutable objects
- Manually constructing repositories in composables
- Passing Koin objects into stateless UI composables


# PRESENTATION ARCHITECTURE

Use MVVM combined with unidirectional data flow.

Create three screen-level ViewModels:

- `CurrencyInputViewModel`
- `CurrencySelectionViewModel`
- `CurrencyConversionViewModel`

Each ViewModel must:

- Expose immutable `StateFlow<UiState>`
- Keep mutable flows private
- Accept clearly named user actions or functions
- Contain presentation/business coordination, not Compose UI code
- Use `viewModelScope`
- Avoid exposing mutable collections
- Survive configuration changes
- Use `SavedStateHandle` for state that must also survive process recreation or navigation restoration

Each screen should define clear states such as:

- Initial or idle
- Loading
- Content/success
- Error

Prefer a stable state data class with explicit properties when that makes partial loading and form state easier than a sealed hierarchy.

Collect flows using:

collectAsStateWithLifecycle()

Do not pass `NavController` into ViewModels.

Navigation and sheet-opening behavior should be executed by the presentation route layer through callbacks.

Separate route-level and reusable UI composables:

- Route composables obtain ViewModels, collect state, and connect navigation.
- Screen composables receive state and callbacks and remain previewable and testable.
- Leaf composables must not know about ViewModels, repositories, Retrofit, or navigation.


# NAVIGATION

Use Navigation Compose.

Create two normal navigation destinations:

1. Currency input
2. Currency conversion

Implement `CurrencySelectionScreen` as a screen-level composable displayed inside a Material 3 `ModalBottomSheet`.

Use the same reusable selection sheet for both From and To selection.

Use a type such as:

enum class CurrencySelectionTarget {
FROM,
TO
}

Navigation requirements:

- Start on `CurrencyInputScreen`.
- Pass only the minimum necessary arguments to the conversion destination:
    - From currency code
    - To currency code
    - Amount as a normalized string
- Do not pass repository objects or large serialized domain objects through navigation.
- Restore the conversion state from navigation arguments through `SavedStateHandle`.
- The Android system back action and the visible back button must behave consistently.
- Preserve input selections when returning from the conversion screen.


# SHARED COMPOSABLES

Place general reusable composables under:

presentation.components

Create reusable components where they reduce real duplication, for example:

- `CurrencySelector`
- `CurrencyFlag`
- `CurrencyAmountRow`
- `SwapCurrenciesButton`
- `ConversionRateLabel`
- `LoadingContent`
- `ErrorContent`
- `CurrencyListItem`
- `CurrencyKeypad`
- `PrimaryActionButton`

Do not split every few lines into a separate composable without a clear reuse, readability, preview, or testability benefit.


# THEMING AND VISUAL DESIGN

Implement a small custom design system built on Material 3.

Create explicit tokens for:

- Colors
- Typography
- Shapes
- Spacing
- Elevation where needed

Follow these references:

https://developer.android.com/develop/ui/compose/designsystems/anatomy
https://developer.android.com/develop/ui/compose/designsystems/custom

Requirements:

- Support system light and dark themes.
- Do not add a theme-switching settings screen.
- Do not enable dynamic color by default because it would override the reference visual identity.
- The dark theme should closely reflect the supplied purple-to-navy image.
- The light theme should use a lighter lavender/indigo treatment while maintaining the same hierarchy and adequate contrast.
- Use edge-to-edge drawing.
- Handle status-bar and navigation-bar insets correctly.
- Set system-bar icon appearance appropriately for the current background.
- Use rounded shapes and subtle separators rather than heavy cards or shadows.
- Avoid default-looking Material components when a carefully styled Material component can achieve the intended design.
- Do not sacrifice accessibility for exact visual matching.


# ADAPTIVE LAYOUT REQUIREMENTS

Support:

- Portrait phones
- Landscape phones
- Resizable windows
- Compact, medium, and expanded window widths where practical

Base layout decisions on available window size rather than checking orientation alone.

Portrait conversion layout:

- Currency information and rates in the upper area
- Swap button between currency sections
- Custom numeric keypad in the lower area

Landscape conversion layout:

- Currency and conversion content on the left
- Numeric keypad on the right
- Avoid excessive stretching
- Apply a reasonable maximum content width

Currency selection sheet:

- Must remain usable in landscape
- Must not extend beyond safe window bounds
- Should use a sensible maximum width on large windows

All screens must retain their state during rotation, resizing, and ordinary configuration changes.


# ACCESSIBILITY

Apply Android accessibility best practices.

Requirements:

- Minimum practical touch-target size of approximately 48dp
- Meaningful content descriptions for icon-only buttons
- No unnecessary descriptions for decorative elements
- Clear TalkBack reading order
- Currency codes must remain available as text; never communicate currency only by flag
- Sufficient color contrast in light and dark themes
- Do not rely solely on red and green to communicate meaning
- Support increased system font sizes without clipping essential content
- Support keyboard and switch navigation where Compose provides it
- Add useful semantics to currency selectors, keypad buttons, loading states, and error actions


# SCREEN 1: CURRENCY INPUT SCREEN

Name:

CurrencyInputScreen

Responsibilities:

- Select the source currency
- Select the target currency
- Enter the amount
- Validate the form
- Continue to conversion

## From currency selector

Display:

- Corresponding flag emoji
- Label: `From`
- Selected three-letter currency code

Unselected state:

- Neutral globe or placeholder icon
- Label: `From`
- Text: `Select currency`

On press:

- Open `CurrencySelectionScreen` in a modal bottom sheet
- Set the selection target to FROM

## To currency selector

Use the same reusable component.

Display:

- Corresponding flag emoji
- Label: `To`
- Selected three-letter currency code

Unselected state:

- Neutral globe or placeholder icon
- Label: `To`
- Text: `Select currency`

On press:

- Open the selection sheet
- Set the selection target to TO

## Amount field

Requirements:

- Label or placeholder: `Enter amount`
- Custom numeric decimal keyboard inspired by the design of the attached image
- Accept one locale-appropriate decimal separator
- Normalize comma and period safely for domain parsing
- Reject negative values
- Reject zero as a convertible amount
- Prevent multiple decimal separators
- Prevent scientific notation
- Use a reasonable maximum length
- Preserve the user-entered text while typing
- Do not convert through `Double` before validation

## Validation

Enable Continue only when:

- From currency is selected
- To currency is selected
- The currencies are different
- The amount is valid
- The amount is greater than zero

Show concise inline validation text when appropriate.

## Continue button

Label:

Continue

On press:

- Normalize the valid amount
- Navigate to `CurrencyConversionScreen`
- Pass the source code, target code, and normalized amount


# SCREEN 2: CURRENCY SELECTION SCREEN

Name:

CurrencySelectionScreen

Present it through a Material 3 modal bottom sheet.

Behavior:

- Animate smoothly from the bottom.
- Allow dismissal through:
    - Downward swipe
    - System back
    - Tapping outside the sheet when appropriate
- Preserve the input-screen form while it is open.
- Load supported currencies through `ExchangeRatesRepository`.
- Do not repeat a successful request every time the sheet opens if the in-memory cache is available.

## Loading state

Display:

- Progress indicator
- Accessible loading description
- Stable sheet layout without abrupt jumps

## Success state

At the top, when the opposite currency is already selected:

- Display its flag
- Display its currency code
- Explain briefly that it is already selected for the other side
- Disable or omit that currency from selectable results so From and To cannot become identical

When there is no opposite currency:

- Do not display an empty placeholder section

A search field used for filtering the available currency list.

Display the available currencies in a lazy list.

Each item must contain:

- Flag emoji
- Currency code
- Currency display name

Sort by currency code.

Selecting a valid item must:

- Update the appropriate From or To value
- Close the bottom sheet
- Preserve all other input state


## Error state

Differentiate user-facing messages where practical:

- Unable to connect to the internet
- API error

Include:

- Retry button
- Dismiss option
- Accessible error announcement

## Empty state

If the request succeeds but produces no valid currencies, show an explicit empty-state message and Retry action.


# SCREEN 3: CURRENCY CONVERSION SCREEN

Name:

CurrencyConversionScreen

Receive from CurrencyInputScreen:

- From currency code
- To currency code
- Initial amount

Immediately request the pair conversion including the amount.

## Top navigation

Display an Android-style back button with a left arrow.

Behavior:

- Return to `CurrencyInputScreen`
- Preserve the previous input state

## Source currency section

Display:

- Source flag emoji
- Source currency code
- Editable source amount

Style:

- Source amount should use the coral red accent inspired by the reference when the amount is lower than the target amount, or it should use the teal green when the source is higher than the target amount.

The amount remains editable after navigation.

## Target currency section

Display:

- Target flag emoji
- Target currency code
- Converted amount as read-only text

Style:

- Converted amount should use the coral red accent inspired by the reference when the amount is lower than the source amount, or it should use the teal green when the target is higher than the source amount.

## Custom keypad

To reflect the attached visual reference, implement a reusable in-app numeric keypad for entering the amounts in the currency input screen and currency conversion screen.

Include:

- Digits 0–9
- Locale-appropriate decimal separator
- Backspace
- Accessible labels
- Press feedback
- Consistent spacing and touch targets

Do not open the system keyboard for the conversion amount field when this custom keypad is active.

The keypad must use events rather than directly mutating UI state inside the composable.

## Conversion updates

When the source amount changes:

- Validate it immediately
- Debounce valid network conversion requests by approximately 400 milliseconds
- Cancel obsolete in-flight requests through Flow operators such as `flatMapLatest`, or an equivalent structured-coroutine implementation
- Avoid requests for empty, invalid, zero, or negative values
- Do not issue duplicate requests for identical inputs

For the first conversion:

- Show a clear loading state

For subsequent edits:

- Keep the entered source amount visible
- Show loading near the target result
- Avoid replacing the entire screen with a blocking loader

## Swap button

Place a small circular button with vertically opposed arrows between the currency sections.

On press:

- Swap the source and target currency codes
- When a valid successful quote exists, use the previous converted amount as the new source amount
- Otherwise retain the existing source amount
- Trigger a fresh conversion
- Provide an accessible description such as `Swap currencies`

## Success state

Display:

- Source amount
- Converted amount
- Direct rate, for example `1 EUR = 1.08 USD`
- Inverse rate, for example `1 USD = 0.93 EUR`
- Last-update information only when it is useful and available

Format values with appropriate grouping and decimal precision without losing domain precision.

## Error state

For an initial request failure:

- Show a visible error state
- Keep the selected currencies and source amount visible
- Provide Retry and Back actions

For a later failure after a successful result:

- Keep the last successful result visible when reasonable
- Clearly mark it as not refreshed
- Show a non-blocking error message and Retry action

Never display raw stack traces, HTTP bodies, API keys, or internal exception messages.


# STATE RESTORATION

State that must survive configuration change:

- From currency
- To currency
- Typed amount
- Active selection target
- Whether the selection sheet is open when reasonably restorable
- Current conversion input
- Last successful conversion quote
- Validation state

Use:

- ViewModel for configuration-change survival
- SavedStateHandle or `rememberSaveable` for state that must survive process recreation
- Clear ownership so the same state is not independently duplicated in several places


# PREVIEWS

Add Compose previews for every composable that can logically be previewed without running real application infrastructure.

Requirements:

- Use sample immutable state
- Never perform network calls in previews
- Include light and dark previews for major components
- Include portrait and landscape previews for all three screen-level composables
- Include loading, success, empty, and error previews where useful
- Keep preview fixtures separate from production networking logic


# TESTING

Aim for at least 60% line coverage of meaningful production logic.

Exclude generated code, BuildConfig, previews, theme constants, and trivial DTO property holders from coverage calculations when appropriate.

## Required unit tests

Test at minimum:

### CurrencyInputViewModel

- Initial state
- Selecting From
- Selecting To
- Valid amount
- Invalid amount
- Zero amount
- Same-currency validation
- Continue eligibility
- State restoration

### CurrencySelectionViewModel

- Loading state
- Successful list loading
- Sorting
- Duplicate removal
- Opposite currency disabling
- Empty response
- Network error
- API error
- Retry
- Cached response behavior
- Forced refresh

### CurrencyConversionViewModel

- Initial request
- Successful conversion
- Direct and inverse rates
- Invalid amount prevents request
- Debounced amount changes
- Cancellation of obsolete requests
- Network error
- API error
- Retry
- Currency swap
- Swap before a result exists
- State restoration

### Domain utilities

- Currency flag mappings
- Fallback flag
- Lowercase normalization
- Amount parsing
- Locale separator normalization
- Number formatting where isolated and testable

### Service repository

Use MockWebServer to test:

- Codes success response
- Pair conversion success response
- API error in HTTP 200 response
- Invalid JSON
- Missing required fields
- Non-2xx response
- Timeout or connection failure
- DTO-to-domain mapping
- API key not appearing in logs

## Compose UI tests

Add focused tests for critical user journeys:

1. Select From and To currencies, enter an amount, and continue
2. Display and dismiss the selection bottom sheet
3. Display conversion success and swap currencies
4. Display an error and retry

Use stable semantic selectors or test tags only where normal accessible text and roles are insufficient.

Tests must use fake repositories and deterministic coroutine dispatchers. They must not depend on live network access.


# DOCUMENTATION AND COMMENTS

Keep inline documentation and comments minimal.

Add comments only when explaining:

- A non-obvious workaround
- Security-sensitive behavior
- A complex Flow chain
- An unusual API limitation
- A decision that cannot be expressed clearly through naming

Use clear, descriptive naming so the code explains itself.

Include a concise README containing:

- Project purpose
- Screenshots placeholder section
- Architecture summary
- Package structure
- API-key setup
- Build instructions
- Test instructions
- Coverage instructions
- Important API-key security limitation
- Major design assumptions


# NON-GOALS

Do not add:

- Authentication
- User profiles
- Transaction execution
- Payment functionality
- Conversion history
- Favorites
- Analytics
- Advertising
- Persistent databases
- Background synchronization
- Widgets
- Notifications
- Unrequested permissions
- Complex multi-module architecture
- Excessive abstraction intended only to inflate the codebase


# OUTPUT FORMAT

Produce the result in this order:

1. Brief assumptions and technical decisions
2. Complete project directory tree
3. Root Gradle files
4. Version catalog
5. App Gradle configuration
6. Android manifest
7. Application and Activity
8. Domain layer
9. Service layer
10. Koin modules
11. Presentation theme and components
12. Navigation
13. CurrencyInput implementation
14. CurrencySelection implementation
15. CurrencyConversion implementation
16. Unit tests
17. Compose UI tests
18. Coverage configuration
19. README
20. Final requirement-verification checklist

For every file:

- Print its full relative path as a heading
- Provide the complete file content
- Include all required imports
- Do not omit unchanged or “obvious” sections
- Do not provide pseudocode
- Do not use ellipses
- Do not leave unresolved references

If the answer exceeds the response limit:

- Stop only at a file boundary
- State the exact next file to generate
- Continue without repeating previous files when prompted


# FINAL SELF-REVIEW

Before completing the answer, perform a final consistency review and correct any issues found.

Verify that:

- Gradle dependencies are compatible
- All imports resolve
- Package declarations match the directory tree
- Koin bindings match constructor dependencies
- Repository interfaces and implementations match
- Retrofit paths and serialized field names are correct
- Navigation arguments are restored correctly
- No composable performs a direct network request
- No ViewModel owns a NavController
- No API key is hardcoded or logged
- Currency amounts avoid careless floating-point handling
- Loading, success, empty, and error states are reachable
- Rotation and resizing preserve state
- Light and dark themes are implemented
- Portrait and landscape layouts are usable
- Accessibility requirements are addressed
- Tests use fakes rather than the real API
- The app can be assembled with the documented command

Include a concise traceability checklist mapping every major requirement to the files or classes that implement it.