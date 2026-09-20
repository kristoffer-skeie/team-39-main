# IN2000 Project - Team 39

## Overview

This project is a weather forecasting application built using Jetpack Compose and Room for data persistence. The application fetches and displays weather data, allowing users to view forecasts, evaluate weather conditions, and manage favorite forecasts.


## Members
Jonas Solberg  
Kristoffer Skeie  
Omar Massfih  
William Dannstrøm  
Trym Sveen  
Mohammad Taghi Moaddeli


## Documentation

The documentation for this project is located within the source code, including `ARCHITECTURE.md` and `MODELING.md` in the root folder. Each class and function is documented with comments explaining their purpose.


## Why?

### Motivation

The primary motivation behind this project is to develop a weather forecast tool for planning rocket launches in collaboration with the rocket building group at the University of Oslo. Since accurate weather data is crucial for the safety and success of rocket launches.

### Goal

The goal of the IN2000 Weather Forecasting App is to leverage modern Android development tools and libraries to create an efficient and visually appealing weather forecasting application. By using Jetpack Compose for the UI and Room for data management, the app ensures a smooth and responsive user experience.


## Quick Start

To run the app, follow these steps:

1. **Clone the Repository**:
    ```sh
    git clone https://github.uio.no/IN2000-V24/team-39.git
    cd in2000_project
    ```

2. **Open in Android Studio**:
    - Open Android Studio.
    - Select `Open an existing Android Studio project`.
    - Navigate to the cloned repository and select the project directory.

3. **Build the Project**:
    - Let Android Studio download and configure the required dependencies.
    - Once the sync is complete, build the project by selecting `Build` > `Make Project`.

4. **Run the App**:
    - Connect an Android device or start an emulator.
    - Click the `Run` button or select `Run` > `Run 'app'`.


## Usage

The app is designed to be user-friendly and intuitive. Here are the key features and how to use them:

- **Weather Forecasts**: View detailed weather forecasts for your location. The main screen displays current weather conditions and future forecasts.
- **Favorite Forecasts**: Save your favorite locations to quickly access their weather data. Use the "Add to Favorites" feature to manage your favorite locations.
- **Map View**: Select longitude and latitude for the area you want a weather forecast through the map or input fields. Navigate through different regions and view weather information for specific areas.
- **Settings**: Customize the app settings according to your preferences. Change theme to dark mode.
- **Contact**: Enables quick access to check restricted airspace and contact the appropriate authorities for permissions.


## Project Structure

The project is organized into several packages:

### Data
Contains data-related classes, including repositories, data sources, and database definitions.

- **favorite**: Manages favorite forecasts, including database access and repository logic.
- **grib**: Handles GRIB (Gridded Binary) weather data, including data sources and repository logic.
- **weather**: Manages weather data, including data sources and repository logic.

### Model
Contains data models used throughout the app.

- **favorite**: Data models for favorite forecasts.
- **grib**: Data models for GRIB weather data.
- **weather**: Data models for weather data.

### UI
Contains the UI components and screens.

- **contact**: UI components for contact-related features, including screens and view models.
- **favorite**: UI components for favorite forecasts, including screens and view models.
- **home**: UI components for the home screen, including main content and screens.
- **map**: UI components for map-related features, including screens and view models.
- **navigation**: Manages navigation between different screens in the app.
- **settings**: UI components for settings, including screens and view models.
- **shared**: Shared UI components and themes used across different screens.
- **components**: Shared UI components such as navigation bars.
- **date**: Utility classes and functions for date handling.
- **network**: Network-related utilities and configurations.
- **theme**: Theme definitions and styles.
- **weatherforecast**: UI components for weather forecasts, including screens, content, and view models.

### MainActivity.kt
The main entry point of the application.


## Libraries Used

This project uses several libraries to achieve its functionality:

- **Jetpack Compose**: Used for building the user interface. Provides a modern and reactive approach to UI development in Android.
- **Room**: Used for local database management. Provides an abstraction layer over SQLite to allow fluent database access while harnessing the full power of SQLite.
- **Navigation**: Used for setting up and managing navigation within the app. Simplifies the implementation of navigation patterns in the app.
- **Lifecycle**: Provides components to help manage Android lifecycle-aware components.
- **Ktor**: Used for making HTTP network requests.
- **GRIB**: Used for processing GRIB (Gridded Binary) weather data.
- **Google Maps**: Used for integrating Google Maps into the app.
- **Coil**: Used for image loading in Compose.
- **Datastore**: Used for data storage in Android.
- **TestNG**: Used for writing and running unit tests.
- **AndroidX Test**: Provides testing libraries for Android.
- **JUnit**: Used for writing and running unit tests.
- **Coroutines Test**: Provides testing libraries for Kotlin Coroutines.


## Use Of AI

This project has used AI for help with ideas for refactoring and documentation.