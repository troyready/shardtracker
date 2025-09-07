## Overview

Simple Android app for health tracking in a deck-building game.

## Build & Install

(ensure Android SDK is configured as noted below)

```bash
./gradlew clean assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Requirements

* JRE 21+
* [Android SDK Tools](https://developer.android.com/studio#command-tools)
  * Included with Android Studio, or can be downloaded on their own
    * When downloading manually, move to `<sdk>/cmdline-tools/latest/`, i.e. `~/Android/sdk/cmdline-tools/latest`

### Android SDK

TBD: are any of the sdkmanager manual installs needed (probably not)

```
export ANDROID_SDK_ROOT=~/Android/sdk
export PATH=$PATH:$ANDROID_SDK_ROOT/emulator:$ANDROID_SDK_ROOT/tools:$ANDROID_SDK_ROOT/platform-tools

$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager "platforms;android-34"
$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager "build-tools;34.0.0"
# optional
$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager "platform-tools" "cmdline-tools;latest" "extras;google;m2repository" "extras;android;m2repository"

$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager --licenses
```
