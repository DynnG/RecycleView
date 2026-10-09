# RecyclerView Person Manager

An Android learning project written in Kotlin. It displays person records in a RecyclerView and uses a local database helper for storage and searching.

## Features

- Display person records in a scrollable list.
- Add and edit records through `AddPersonActivity`.
- Delete records through list actions or swipe gestures.
- Search records with a SearchView.
- Drag records to reorder the displayed list.

## Run locally

1. Open the repository root in Android Studio.
2. Allow Gradle sync to finish and install Android SDK 36 if prompted.
3. Use the Gradle JDK required by the project's Android Gradle plugin. The source compatibility and Kotlin JVM target are 11.
4. Select an emulator or device running Android 7.0 / API 24 or newer.
5. Run the `app` configuration.

Optional debug build from Windows PowerShell:

```powershell
.\gradlew.bat assembleDebug
```

On macOS/Linux, use `./gradlew assembleDebug`.

## Source guide

The Kotlin files are in `app/src/main/java/com/example/recycleview/`:

- `MainActivity.kt`: list, search, and gesture handling.
- `AddPersonActivity.kt`: add/edit screen.
- `ItemAdapter.kt`: RecyclerView adapter.
- `DatabaseHelper.kt`: local storage.
- `Person.kt`: person model.

Layouts and other resources are in `app/src/main/res/`. Drag reordering changes the displayed list; do not assume it persists across app restarts.
