# Cardinal

> **Navigate smarter, drive freely.**

Cardinal is an open-source, map-based navigation app for Android phones with full Android Auto support. It is built entirely on open-source map and routing infrastructure (OpenStreetMap data via OpenFreeMap tiles, Valhalla routing).

## Features

- **Driven-road memory** — roads driven in the last 30 days are tinted cyan on the map
- **Speed-limit-aware live speedometer** — background shifts green → amber → red
- **Animated grade indicator** — shows current road gradient
- **Glanceable POI rail** — gas, rest areas, shopping, weather
- **Compass HUD** with cardinal-direction heading
- **Turn-by-turn navigation** with lane guidance
- **Android Auto** optimized car interface

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose (phone), AndroidX Car App Library (car)
- **Maps:** MapLibre Native + OpenFreeMap tiles
- **Routing:** Valhalla
- **Search:** Photon / Overpass API
- **Weather:** Open-Meteo
- **DI:** Hilt
- **Local DB:** Room

## Project Structure

```
cardinal/
├── app/              # Phone app (Compose UI, settings)
├── car/              # Android Auto Car App Service
├── core/             # Domain models, repository interfaces, common DI
├── feature/          # Feature modules (map, routing, navigation, poi, weather, traffic)
└── data/             # Local (Room) and remote (Retrofit) data layers
```

## Build

1. Open in Android Studio Hedgehog or newer.
2. Sync Gradle. Min JDK 17.
3. Run `./gradlew :app:installDebug` to deploy to phone.

## Android Auto Testing

1. Install **Desktop Head Unit (DHU)** from Android SDK Manager → SDK Tools.
2. On phone: enable Developer Mode in Android Auto app (tap version 10× in About).
3. `adb forward tcp:5277 tcp:5277` then run DHU.

## License

This project is licensed under the Apache-2.0 License. See [LICENSE](LICENSE) for details.

## Attributions

- © OpenStreetMap contributors (ODbL)
- © OpenFreeMap (CC-BY)
- Valhalla © Mapzen / contributors
- MapLibre Native (BSD-2-Clause)
- Photon (Apache-2.0)
- Open-Meteo (Attribution-NonCommercial 4.0)
