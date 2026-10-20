# SaBuddy

SaBuddy is an Android app for planning and drafting social media content. It brings content ideas, short-form video scripts, post and carousel outlines, captions, hashtags, a calendar, saved content, brand settings, and manually maintained analytics into one local workspace.


## Features

- Create ideas, Reels scripts, posts and carousel outlines, captions, and hashtag groups.
- Chat with SaBuddy and generate content with Gemini when an API key is configured.
- Use built-in local generation when no key is configured or a Gemini request fails.
- Save content locally, search the library, and organize items in the content calendar.
- Configure a brand kit and customize the app theme and navigation.
- Review analytics entered and maintained by the user.

The platform options are for content formatting and planning. The app does not connect social accounts through OAuth, synchronize platform metrics, or publish posts automatically.

## Technology

- Kotlin 2.2.10 and Jetpack Compose with Material 3
- Android Gradle Plugin 9.1.1 and Gradle 9.3.1
- Room 2.7.0 for local structured data
- OkHttp for Gemini REST requests
- Minimum Android API 24; target and compile API 36 (compile minor API level 1)

Versions and SDK values are taken from the Gradle version catalog and app build configuration.

## Project layout

```text
app/src/main/java/com/example/
├── MainActivity.kt
└── sabuddy/
    ├── data/          # Room database and preferences
    ├── models/        # App and content models
    ├── services/      # Gemini requests and local fallback generation
    ├── ui/            # Navigation, theme, components, and screens
    └── viewmodels/    # Screen state and app operations
```

## Requirements

- Android Studio with support for the Android Gradle Plugin 9.1.1
- JDK 17 or newer, as required by the Android Gradle Plugin
- Android SDK platform 36

The repository contains `gradle/wrapper/gradle-wrapper.properties`, but currently does not contain the Gradle wrapper scripts or wrapper JAR. Open the repository in Android Studio and let it import the project using Gradle 9.3.1, or install Gradle 9.3.1 separately before using the commands below.

## Quick start

1. Clone this repository and open it in Android Studio.
2. Allow Gradle sync to download the required dependencies and Android SDK components.
3. Run the `app` configuration on an Android device or emulator running API 24 or newer.
4. During setup, enter a Gemini API key to use Gemini, or choose **Explore Offline First** to use local generation.

The API key can also be added or changed in the app's settings. Gemini requests send prompts, and images attached to chat, to Google's Gemini API. Review Google's current terms and data practices before using the online features.

## Build and tests

With Gradle 9.3.1 installed and Android SDK API 36 available:

```sh
gradle :app:assembleDebug
gradle :app:testDebugUnitTest
```

The repository includes basic example unit and Robolectric tests, an example instrumented test, and a Robolectric screenshot fixture. These do not constitute broad coverage of SaBuddy's application workflows. No build or test result is claimed by this README.

To build a release APK, configure `KEYSTORE_PATH`, `STORE_PASSWORD`, and `KEY_PASSWORD` in the environment and run:

```sh
gradle :app:assembleRelease
```

The release signing configuration uses the alias `upload`. Do not commit keystores or passwords. The resulting APK is written under `app/build/outputs/apk/release/`. Release builds have not been verified as installable from this documentation update.

## Configuration and limitations

A Gemini key is optional for using local generation. The checked-in `.env.example` documents the `GEMINI_API_KEY` name for the Gradle Secrets plugin; do not commit a real `.env` file or API key. The app also provides an in-app setup flow for entering a key.

SaBuddy stores its content and preferences locally. Scheduling is an organizational calendar; it does not create background publishing jobs for social networks. Analytics are user-entered and are not synchronized from social accounts.

## Repository notes

- The app version in Gradle is `1.0` with version code `1`; the repository has a published `v1.0.0` release.
- The repository does not currently include a `LICENSE` file. No license is implied by this README.
- There is no separate contribution guide yet. Changes should preserve the existing app behavior and be submitted for review.

## Release

The existing [v1.0.0 GitHub release](https://github.com/hmmlavi/SaBuddy/releases/tag/v1.0.0) includes an APK. This repository update does not replace that release or verify the APK. Prepare future releases from a clean build, verify installation on a supported Android version, and publish versioned APKs as GitHub Release assets.
