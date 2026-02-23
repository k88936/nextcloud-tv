## Project Overview

NextcloudTV is an Android TV application built with Jetpack Compose for TV. It provides access to
Nextcloud Memories (photos/videos), Files, and authentication features.

**Tech Stack:**

- Kotlin 2.3.10 with Java 21
- Jetpack Compose for TV (androidx.tv)
- Ktor client for networking
- Koin for dependency injection
- Kotlin Serialization for JSON/XML
- KotlinTest with JUnit5 for testing

## Dev Commands

```bash
# Run all tests
./gradlew test

# Run a single test class
./gradlew test --tests "top.k88936.nextcloud.app.MemoriesRepositoryTest"

# Run a single test method
./gradlew test --tests "top.k88936.nextcloud.app.MemoriesRepositoryTest.getDays returns list of days"

```

## Project Structure

```
app/src/main/kotlin/top/k88936/nextcloud_tv/
├── data/
│   ├── local/          # CredentialStore, local storage
│   ├── model/          # Data classes (MemoriesModels.kt)
│   ├── network/        # NextcloudClient, API clients
│   └── repository/     # Repository pattern implementations
├── di/                 # Koin DI modules
├── navigation/         # Navigation utilities
├── ui/
│   ├── app/            # Main app screens (memories, files, settings)
│   ├── auth/           # Authentication screens
│   ├── components/     # Reusable UI components
│   ├── modal/          # Modal/dialog screens
│   └── Icon/           # Icon assets organized by feature
└── MainActivity.kt     # Entry point
```