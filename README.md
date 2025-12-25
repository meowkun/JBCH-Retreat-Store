# JBCH Market Manager

A cross-platform point-of-sale (POS) application built with **Kotlin Multiplatform** and **Jetpack
Compose Multiplatform**. Designed for managing retail operations at retreat stores and small
markets.

![Kotlin](https://img.shields.io/badge/Kotlin-2.2.21-7F52FF?logo=kotlin)
![Compose Multiplatform](https://img.shields.io/badge/Compose_Multiplatform-1.9.3-4285F4?logo=jetpackcompose)
![Platform](https://img.shields.io/badge/Platform-Android%20%7C%20iOS-green)
![Architecture](https://img.shields.io/badge/Architecture-MVI-orange)
![License](https://img.shields.io/badge/License-MIT-blue)

## ✨ Features

### 🛒 Shop Management

- **Product Catalog** - Display and manage items with customizable variants (size, color, etc.)
- **Dynamic Pricing** - Set individual prices per product
- **Search & Filter** - Quickly find items in your inventory
- **Add/Edit/Remove Items** - Full CRUD operations for inventory management
- **Drag & Drop Reordering** - Organize product variants with intuitive gestures

### 🛍️ Shopping Cart

- **Add to Cart** - Select products with variant options and quantities
- **Cart Management** - View, modify quantities, and remove items
- **Real-time Totals** - Automatic price calculation as you shop

### 💳 Checkout

- **Multiple Payment Methods** - Support for Cash, Zelle, and Venmo
- **Buyer Information** - Track customer names with purchases
- **Order Confirmation** - Clear checkout flow with confirmation dialogs

### 📋 Purchase History

- **Transaction Records** - Complete history of all purchases
- **Date Grouping** - Receipts organized by date (most recent first)
- **Edit Capabilities** - Modify buyer names, payment methods, and item quantities
- **CSV Export** - Share purchase history via CSV for record-keeping
- **Delete Records** - Remove old or incorrect transactions

### 🎨 Modern UI/UX

- **Material Design 3** - Clean, modern interface following Material guidelines
- **Dark/Light Theme** - Adaptive theming support
- **Responsive Layout** - Optimized for various screen sizes
- **Smooth Animations** - Polished transitions and micro-interactions

## 🏗️ Architecture

This project follows **Clean Architecture** principles with the **MVI (Model-View-Intent)** pattern:

```
┌─────────────────────────────────────────────────────────────┐
│                      Presentation Layer                      │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │   Screens   │  │  ViewModels │  │  UI States/Intents  │  │
│  │  (Compose)  │  │    (MVI)    │  │                     │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
├─────────────────────────────────────────────────────────────┤
│                        Domain Layer                          │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │   Models    │  │  Use Cases  │  │    Repository       │  │
│  │             │  │             │  │    (Interface)      │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
├─────────────────────────────────────────────────────────────┤
│                         Data Layer                           │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │   Mappers   │  │    DTOs     │  │    DataStore        │  │
│  │             │  │             │  │   (Persistence)     │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

### Key Architectural Decisions

- **Unidirectional Data Flow** - State flows down, events flow up
- **Immutable State** - All UI states are data classes with derived properties
- **Repository Pattern** - Abstraction over data sources
- **Dependency Injection** - Koin for service location
- **Platform Abstraction** - Expect/actual for platform-specific implementations

## 🛠️ Tech Stack

| Category                 | Technology                          |
|--------------------------|-------------------------------------|
| **Language**             | Kotlin 2.2.21                       |
| **UI Framework**         | Jetpack Compose Multiplatform 1.9.3 |
| **Architecture**         | MVI (Model-View-Intent)             |
| **Dependency Injection** | Koin 4.1.1                          |
| **Navigation**           | Compose Navigation 2.9.1            |
| **Local Storage**        | DataStore Preferences 1.1.7         |
| **Serialization**        | Kotlinx Serialization 1.9.0         |
| **Date/Time**            | Kotlinx DateTime 0.7.1              |
| **Async**                | Kotlin Coroutines 1.10.2            |
| **Image Loading**        | Coil 3.3.0                          |
| **Networking**           | Ktor 3.3.3                          |
| **Testing**              | Kotlin Test, Turbine 1.2.1          |
| **UI Components**        | Reorderable 3.0.0 (drag & drop)     |

## 📁 Project Structure

```
JBCH-Retreat-Store/
├── composeApp/                    # Main application module
│   └── src/
│       ├── commonMain/            # Shared Kotlin code
│       │   └── kotlin/
│       │       └── bookstore/
│       │           ├── data/      # Data layer (DTOs, mappers, repository impl)
│       │           ├── di/        # Dependency injection modules
│       │           ├── domain/    # Domain layer (models, use cases, repository interface)
│       │           └── presentation/
│       │               ├── navigation/   # Navigation setup
│       │               ├── shared/       # Shared state holders
│       │               └── ui/           # UI components and screens
│       │                   ├── checkout/
│       │                   ├── components/
│       │                   ├── dialog/
│       │                   ├── purchasehistory/
│       │                   ├── shop/
│       │                   └── theme/
│       ├── commonTest/            # Shared unit tests
│       ├── androidMain/           # Android-specific code
│       └── iosMain/               # iOS-specific code
├── iosApp/                        # iOS application wrapper
├── server/                        # Backend server module (Ktor)
└── shared/                        # Shared library module
```

## 🚀 Getting Started

### Prerequisites

- **Android Studio** Ladybug or newer (for Android development)
- **Xcode 15+** (for iOS development)
- **JDK 11+**
- **Kotlin 2.2.21+**

### Clone the Repository

```bash
git clone https://github.com/yourusername/JBCH-Retreat-Store.git
cd JBCH-Retreat-Store
```

### Run on Android

```bash
./gradlew :composeApp:installDebug
```

Or open in Android Studio and run the `composeApp` configuration.

### Run on iOS

1. Open `iosApp/iosApp.xcodeproj` in Xcode
2. Select your target device/simulator
3. Click Run (⌘R)

### Run Tests

```bash
# Run all unit tests
./gradlew :composeApp:testDebugUnitTest

# Run specific test class
./gradlew :composeApp:testDebugUnitTest --tests "com.example.jbchretreatstore.bookstore.domain.usecase.CheckoutUseCaseTest"
```

## 🧪 Testing

The project includes comprehensive unit tests covering:

- **Domain Models** - CheckoutItem, DisplayItem, ReceiptData, PaymentMethod, CheckoutStatus
- **Use Cases** - CheckoutUseCase, ManageCartUseCase, ManageDisplayItemsUseCase,
  PurchaseHistoryUseCase
- **Data Layer** - Mappers, DTOs, Repository implementation
- **UI States** - All derived properties and business logic in UI state classes
- **Utilities** - CSV generation, currency formatting, input validation
- **Error Handling** - Comprehensive error scenarios and edge cases

### Test Coverage Highlights

| Layer         | Test Files   | Coverage   |
|---------------|--------------|------------|
| Domain Models | 6 test files | ✅ Complete |
| Use Cases     | 5 test files | ✅ Complete |
| Data Mappers  | 3 test files | ✅ Complete |
| UI States     | 5 test files | ✅ Complete |
| Utilities     | 2 test files | ✅ Complete |

## 📱 Screenshots

*Coming soon*

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
- [Jetpack Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Koin](https://insert-koin.io/)
- [Material Design 3](https://m3.material.io/)

---

<p align="center">Made with ❤️ for JBCH Retreat Store</p>
