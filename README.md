# Hue Go

A simple Android app to control Philips Hue lights with holiday-themed color patterns.

## Features
- Automatic Hue Bridge discovery
- Christmas and Hanukkah light patterns
- Remembers selected lights
- Automatic light shutdown when app closes
- Specifically targets any Philips Hue light
- Shows connection status to your Hue Bridge

## Prerequisites

- Android Studio Hedgehog (2023.1.1) or newer
- Android device or emulator running Android 5.0 (API level 21) or higher
- Philips Hue Bridge connected to your local network
- Philips Hue lights connected to your Bridge
- Both your Android device and Hue Bridge must be on the same network

## Setup

1. Clone this repository
2. Open the project in Android Studio
3. Sync the project with Gradle files
4. Build and run the app on your device

## First-Time Usage

1. Launch the app
2. When prompted, press the link button on your Philips Hue Bridge
3. Wait for the app to show "Connected to Bridge"
4. Select the lights you want to control
5. Choose either Christmas or Hanukkah light pattern

## Technical Details

- Built with Kotlin and Jetpack Compose
- Uses MVVM architecture pattern
- Uses Room for persistent storage
- Uses Kotlin Coroutines for asynchronous operations
- Material Design 3 theming

## Project Structure

    app/
    ├── build.gradle           # Project dependencies
    ├── src/main/
        ├── AndroidManifest.xml
        └── java/com/example/huego/
            ├── MainActivity.kt     # Main activity
            ├── HueViewModel.kt     # Business logic
            ├── data/              # Database and repository
            ├── discovery/         # Bridge discovery
            ├── model/            # Data models
            └── ui/               # UI components

## Dependencies

- Jetpack Compose BOM: 2024.02.00
- Room Database
- AndroidX Lifecycle ViewModel Compose
- Material Design 3

## Local Development Setup

1. **System Requirements**
   - JDK 17 or newer
   - Android Studio Hedgehog (2023.1.1) or newer
   - Git

2. **Clone the Repository**
   ```bash
   git clone https://github.com/ilyakatz/huego.git
   cd huego
   ```

3. **Android Studio Setup**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to and select the cloned project directory
   - Wait for the Gradle sync to complete

4. **Run the App**
   - Connect an Android device via USB with USB debugging enabled
     OR
   - Set up an Android Emulator through Android Studio
   - Click the "Run" button (green play icon)

## Testing with Hue Bridge

- Ensure your development device and Hue Bridge are on the same network
- For emulator testing:
  - Use Android Studio's built-in emulator
  - Enable network access in the emulator settings
  - Make sure the emulator can access your local network

## Permissions

The app requires the following permissions:
- `INTERNET`: For connecting to the Hue Bridge
- `ACCESS_NETWORK_STATE`: For discovering the Hue Bridge
- `ACCESS_WIFI_STATE`: For network discovery
- `CHANGE_WIFI_MULTICAST_STATE`: For mDNS discovery

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details. 