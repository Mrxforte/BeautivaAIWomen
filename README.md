# Beautiva AI — Women Edition (Android starter)

Native Android starter project built with Kotlin, Jetpack Compose and Material 3.

## Current first-preview scope
- Home / Discover screen with premium beauty-studio styling.
- Hairstyle, Makeup, Outfits and Nails categories.
- Studio screen with selectable looks.
- Android system photo picker (image selection from device).
- Preview state and honest placeholder message until the real AI image backend is configured.
- My Looks and Profile tabs (starter states).
- Light ivory / rose palette, responsive Compose layout and Material 3 components.

## Important status
This is a **UI and interaction starter**, not yet a production AI makeover app. The transformation action does not claim to generate an image. A secure backend must be connected before real AI image editing can work.

## Requirements
- Android Studio (recent stable version)
- JDK 17
- Android SDK Platform 35
- Internet connection for Gradle dependencies on first sync

## Open and run
1. Extract the ZIP.
2. Open the `BeautivaAIWomen` folder in Android Studio.
3. Let Gradle sync finish.
4. Select an emulator or connect an Android device with USB debugging enabled.
5. Press **Run**.

The environment used to prepare these source files did not have Android SDK / Gradle installed, so an APK could not be built or device-tested here. Android Studio will perform the actual build after dependency sync.

## Next implementation stage
1. Verify build and interactions on emulator.
2. Add image upload / secure storage workflow.
3. Create authenticated backend endpoint (e.g. Firebase Cloud Functions).
4. Store the AI provider key only in server-side Secret Manager.
5. Connect a supported image-editing model, validate image responses, and add loading / retry / error states.
6. Add privacy consent, retention controls, and production tests.

## Package
`com.beautiva.aiwomen`
