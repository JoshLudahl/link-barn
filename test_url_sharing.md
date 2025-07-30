# URL Sharing Feature Test Guide

## Overview
The URL sharing feature has been successfully implemented. When users share a URL from another application, Link Barn will appear in the share menu and automatically prompt the user to create a new link item.

## Implementation Summary

### 1. AndroidManifest.xml Changes
- Added intent filter for `ACTION_SEND` with `text/plain` MIME type
- This allows Link Barn to appear in the share menu when sharing text/URLs

### 2. MainActivity.kt Changes
- Added `handleIntent()` method to process shared URLs
- Added `onNewIntent()` method to handle intents when app is already running
- Stores shared URL in companion object for access by UI components

### 3. MainViewModel.kt Changes
- Added `sharedUrl` StateFlow to track shared URLs
- Added `setSharedUrl()` and `clearSharedUrl()` methods
- Integrated shared URL handling into the existing architecture

### 4. MainScreen.kt Changes
- Added LaunchedEffect to detect shared URLs and open the add URL dialog
- Modified ModalBottomSheetAddUrl to pre-fill URL field with shared URL
- Added proper cleanup of shared URL state when dialog is dismissed

## How to Test

### Manual Testing Steps:
1. Install the app on a device or emulator
2. Open any app that allows sharing URLs (e.g., Chrome, Twitter, etc.)
3. Find a URL you want to share
4. Tap the share button
5. Look for "Link Barn" in the share menu
6. Tap on "Link Barn"
7. The app should open with the add URL dialog pre-filled with the shared URL
8. Add a name and categories as desired
9. Tap "Add Link" to save

### Expected Behavior:
- Link Barn appears in the share menu when sharing text/URLs
- The add URL dialog opens automatically with the shared URL pre-filled
- User can add a name and select categories before saving
- The shared URL is cleared after saving or canceling

### Unit Tests:
All unit tests are passing (10/10), including new tests for:
- `setSharedUrl()` functionality
- `clearSharedUrl()` functionality  
- Initial state verification

## Technical Details

### Intent Filter Configuration:
```xml
<intent-filter>
    <action android:name="android.intent.action.SEND" />
    <category android:name="android.intent.category.DEFAULT" />
    <data android:mimeType="text/plain" />
</intent-filter>
```

### Key Components:
- **MainActivity**: Handles incoming share intents
- **MainViewModel**: Manages shared URL state
- **MainScreen**: Displays UI and handles user interaction
- **ModalBottomSheetAddUrl**: Pre-fills URL field with shared content

The implementation follows the existing MVVM architecture and integrates seamlessly with the current codebase.
