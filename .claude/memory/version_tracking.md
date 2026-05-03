---
name: Automatic Version Tracking for Cardinal Android App
description: Increment app versionCode and versionName in app/build.gradle.kts on every major update to avoid Android deployment conflicts.
type: project
---

Whenever a major update is designed or implemented for the Cardinal Android app, the version in `app/build.gradle.kts` must be automatically incremented.

**Current version:** 1.0.0 (versionCode 1)

**Why:** Android deployments (APK/AAB uploads to Play Console or sideloading) require a unique `versionCode` for each build. Failing to increment it causes deployment conflicts and installation failures.

**How to apply:**
- On any major feature addition, architecture change, or significant update: increment `versionCode` by +1 and bump `versionName` according to semantic versioning (e.g., 1.0.0 → 1.1.0 for features, 1.0.0 → 1.0.1 for fixes).
- Update this memory file to reflect the new current version.
- Update `app/src/main/res/values/strings.xml` version string if changed.
