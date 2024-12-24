# Hue Christmas Colors

A simple Android app that cycles your Philips Hue Go light through Christmas colors (red and green). Built with Jetpack Compose and the Philips Hue SDK.

## Features

- Automatically discovers Philips Hue Bridge on your network
- Simple one-button interface to start/stop color cycling
- Cycles between festive red and green colors every 2 seconds
- Shows connection status to your Hue Bridge
- Specifically targets Hue Go lights

## Prerequisites

- Android Studio Arctic Fox or newer
- Android device or emulator running Android 5.0 (API level 21) or higher
- Philips Hue Bridge connected to your local network
- Philips Hue Go light connected to your Bridge
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
4. Press "Start Christmas Colors" to begin the light show

## Technical Details

- Built with Kotlin and Jetpack Compose
- Uses MVVM architecture pattern
- Implements the Philips Hue SDK for light control
- Uses Kotlin Coroutines for asynchronous operations

## Project Structure

    app/
    ├── build.gradle           # Project dependencies
    ├── src/main/
        ├── AndroidManifest.xml
        └── java/com/example/huechristmas/
            ├── MainActivity.kt    # UI implementation
            └── HueViewModel.kt    # Business logic

## Dependencies

- Jetpack Compose BOM: 2024.02.00
- Philips Hue SDK: 1.24.0
- AndroidX Lifecycle ViewModel Compose

## Local Development Setup

1. **System Requirements**
   - JDK 17 or newer
   - Android Studio Hedgehog (2023.1.1) or newer
   - Git

2. **Clone the Repository**
   ```bash
   git clone https://github.com/yourusername/hue-christmas.git
   cd hue-christmas
   ```

3. **Android Studio Setup**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to and select the cloned project directory
   - Wait for the Gradle sync to complete

4. **Configure Local Properties**
   - Create a file named `local.properties` in the project root if it doesn't exist
   - Add your Android SDK path:
     ```properties
     sdk.dir=/path/to/your/Android/Sdk
     ```

5. **Run the App**
   - Connect an Android device via USB with USB debugging enabled
     OR
   - Set up an Android Emulator through Android Studio
   - Click the "Run" button (green play icon) or press Shift+F10
   - Select your target device and click OK

6. **Testing with Hue Bridge**
   - Ensure your development device and Hue Bridge are on the same network
   - For emulator testing:
     - Use Android Studio's built-in emulator
     - Enable network access in the emulator settings
     - Make sure the emulator can access your local network

7. **Debugging Tips**
   - Enable USB debugging on your Android device
   - Use Android Studio's Logcat to view logs
   - Check the "Connected to Bridge" status in the app
   - If connection fails, verify:
     - Network connectivity
     - Bridge is powered on and connected
     - You're on the same network as the bridge

8. **Common Issues**
   - Bridge not found: Check network connectivity and bridge power
   - Build errors: Run "File > Invalidate Caches / Restart"
   - Gradle sync fails: Check your internet connection and try "File > Sync Project with Gradle Files"

## Development Workflow

1. Create a new branch for your feature:
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. Make your changes and test thoroughly

3. Submit a pull request with a clear description of your changes

## Permissions

The app requires the following permissions:
- `INTERNET`: For connecting to the Hue Bridge
- `ACCESS_NETWORK_STATE`: For discovering the Hue Bridge

## Contributing

Feel free to submit issues and enhancement requests!

## License

This project is licensed under the MIT License - see the LICENSE file for details. 