<img width="129" height="150" alt="cardinal-logo" src="https://github.com/user-attachments/assets/8f348211-b450-43eb-9912-a399997e22d8" />![Uploa<svg viewBox="0 0 480 560" xmlns="http://www.w3.org/2000/svg" role="img" aria-label="Cardinal logo">
  <defs>
    <radialGradient id="bezel" cx="50%" cy="40%" r="60%">
      <stop offset="0%" stop-color="#1a2942"/>
      <stop offset="100%" stop-color="#0a1322"/>
    </radialGradient>
    <linearGradient id="bird" x1="0%" y1="0%" x2="0%" y2="100%">
      <stop offset="0%" stop-color="#E63946"/>
      <stop offset="60%" stop-color="#C8102E"/>
      <stop offset="100%" stop-color="#8B0A1F"/>
    </linearGradient>
    <filter id="soft" x="-20%" y="-20%" width="140%" height="140%">
      <feGaussianBlur in="SourceAlpha" stdDeviation="2"/>
      <feOffset dx="0" dy="2"/>
      <feComponentTransfer><feFuncA type="linear" slope="0.35"/></feComponentTransfer>
      <feMerge><feMergeNode/><feMergeNode in="SourceGraphic"/></feMerge>
    </filter>
  </defs>

  <!-- Outer compass bezel (suggests a dashboard gauge) -->
  <circle cx="240" cy="220" r="190" fill="url(#bezel)" stroke="#FFFFFF" stroke-width="3"/>
  <circle cx="240" cy="220" r="172" fill="none" stroke="#FFFFFF" stroke-width="1" stroke-opacity="0.25"/>

  <!-- Tick marks: 4 major + 4 minor -->
  <g stroke="#FFFFFF" stroke-linecap="round">
    <line x1="240" y1="42"  x2="240" y2="68"  stroke-width="3"/>
    <line x1="418" y1="220" x2="392" y2="220" stroke-width="3"/>
    <line x1="240" y1="398" x2="240" y2="372" stroke-width="3"/>
    <line x1="62"  y1="220" x2="88"  y2="220" stroke-width="3"/>
    <g stroke-width="1.5" stroke-opacity="0.55">
      <line x1="366" y1="94"  x2="354" y2="106"/>
      <line x1="366" y1="346" x2="354" y2="334"/>
      <line x1="114" y1="346" x2="126" y2="334"/>
      <line x1="114" y1="94"  x2="126" y2="106"/>
    </g>
  </g>

  <!-- Cardinal direction labels -->
  <g font-family="system-ui, -apple-system, Segoe UI, sans-serif" font-weight="700" font-size="22" fill="#FFFFFF" text-anchor="middle">
    <text x="240" y="35">N</text>
    <text x="450" y="228">E</text>
    <text x="240" y="430">S</text>
    <text x="30"  y="228">W</text>
  </g>

  <!-- Cardinal bird as the compass needle, body oriented up = north -->
  <g transform="translate(240,220)" filter="url(#soft)">
    <!-- Tail (south) -->
    <path d="M -10 80 Q 0 130 10 80 L 4 60 L -4 60 Z"
          fill="#A30D24" stroke="#0a1322" stroke-width="1.5"/>

    <!-- Body (red cardinal silhouette) -->
    <path d="M -32 40
             C -42 10, -40 -30, -22 -55
             C -10 -70, 10 -72, 22 -58
             C 38 -38, 40 -10, 32 25
             C 26 50, 14 65, 0 65
             C -14 65, -26 55, -32 40 Z"
          fill="url(#bird)" stroke="#0a1322" stroke-width="2"/>

    <!-- Iconic crest -->
    <path d="M -16 -62
             L -10 -88
             L -4 -72
             L  2 -94
             L  8 -74
             L 16 -88
             L 18 -60 Z"
          fill="#E63946" stroke="#0a1322" stroke-width="1.5"/>

    <!-- Black mask -->
    <path d="M -18 -42 Q -10 -52 6 -50 Q 12 -42 6 -34 Q -8 -30 -18 -36 Z"
          fill="#0a1322"/>

    <!-- Beak (orange) -->
    <path d="M -14 -38 L -28 -34 L -14 -32 Z" fill="#F5A623" stroke="#0a1322" stroke-width="1"/>

    <!-- Eye highlight -->
    <circle cx="-6" cy="-44" r="1.8" fill="#FFFFFF"/>

    <!-- Wing accent -->
    <path d="M 6 -10 Q 26 5 26 35 Q 14 45 4 30 Z"
          fill="#7A0B1C" opacity="0.85"/>
  </g>

  <!-- Center pivot -->
  <circle cx="240" cy="220" r="4" fill="#FFFFFF"/>

  <!-- Wordmark -->
  <text x="240" y="488" font-family="system-ui, -apple-system, Segoe UI, sans-serif"
        font-weight="800" font-size="56" fill="#0a1322" text-anchor="middle" letter-spacing="4">CARDINAL</text>

  <!-- Motto -->
  <text x="240" y="522" font-family="system-ui, -apple-system, Segoe UI, sans-serif"
        font-weight="500" font-size="15" fill="#0a1322" text-anchor="middle"
        letter-spacing="3.5" opacity="0.78">NAVIGATE SMARTER, DRIVE FREELY</text>
</svg>
ding cardinal-logo.svg…]()

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
