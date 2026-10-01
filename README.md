# Jiggie 🐾

**Shared health tracking for your whole pack.** One family, every dog, on the same page.

Jiggie is a small web app for households that share the care of one or more dogs. Everyone in the family logs walks, meals, water, medicine and symptoms in one place, so nobody has to ask *"did anyone feed her yet?"* again.

👉 **Try it:** [lulssam.github.io/jiggie](https://lulssam.github.io/jiggie/). It installs as a PWA from the browser's "Add to Home Screen".

---

## Features

- **Families**: create a family or join one with an invite code. Codes expire and renew on their own.
- **Several dogs per family**, all visible to every member.
- **Quick logging** from a single menu:
  - 🚶 Walks (with pee/poop tags)
  - 🍖 Food
  - 💧 Water
  - 💊 Medicine (scheduled meds and each dose given)
  - 🤒 Symptoms
- **Today's summary** on the home screen: what has been done and what is still due.
- **History**: every event, grouped by day and paginated.
- **Profile**: your account and family details.
- 🚧 *Report*: work in progress.

## Tech stack

| Layer      | Tech |
|------------|------|
| UI         | [Compose Multiplatform](https://www.jetbrains.com/compose-multiplatform/) + Material 3 |
| Language   | Kotlin 2.4 (Kotlin Multiplatform) |
| Web target | Kotlin/Wasm (`wasmJs`) |
| Navigation | Type-safe Navigation Compose |
| State      | ViewModel + `StateFlow` (one `UiState` per screen) |
| Backend    | [Supabase](https://supabase.com): Postgres, Auth, Realtime, via [supabase-kt](https://github.com/supabase-community/supabase-kt) |
| Hosting    | GitHub Pages, deployed by GitHub Actions |

## Project structure

```
.
├── shared/                 # All app code: UI, ViewModels, Supabase calls
│   └── src/
│       ├── commonMain/     # Shared code
│       │   └── kotlin/.../jiggie/
│       │       ├── features/   # One folder per screen (Screen, UiState, ViewModel)
│       │       └── ui/         # Shared components and theme
│       ├── commonTest/     # Unit tests
│       └── jvmMain/        # Desktop entry point (dev only)
├── webApp/                 # Web entry point, index.html, PWA manifest
├── landing/                # Static landing page served at the site root
├── supabase/
│   ├── migrations/         # Full database schema (tables, RLS, functions)
│   ├── DROP.sql            # Wipes the schema
│   └── testes_jiggie.sh    # End-to-end tests for the schema (curl + Supabase API)
└── .github/workflows/      # GitHub Pages deploy
```

Each feature follows the same pattern: a `@Composable` screen that only reads state, a `UiState` data class, and a `ViewModel` that talks to Supabase and updates the state.

## Getting started

### Requirements

- JDK 21
- A modern browser with WebAssembly GC support (recent Chrome, Firefox or Safari)

### Run the web app

```bash
./gradlew :webApp:wasmJsBrowserDevelopmentRun
```

### Run on desktop (handy for development)

Opens a phone-sized window with the same app:

```bash
./gradlew :shared:run
```

### Run the tests

```bash
./gradlew :shared:jvmTest
```

## Backend

The app points to a hosted Supabase project (see [`Supabase.kt`](shared/src/commonMain/kotlin/com/luisamsampaio/jiggie/Supabase.kt)). The key in there is the publishable key; access to data is enforced by Row Level Security in the database.

To use your own Supabase project:

1. Create a project at [supabase.com](https://supabase.com).
2. Run the SQL in [`supabase/migrations/`](supabase/migrations) in the SQL Editor.
3. Replace the URL and key in `Supabase.kt` with your project's.

### Schema test script

[`supabase/testes_jiggie.sh`](supabase/testes_jiggie.sh) checks the schema end to end (sign-up, families, invite codes, RLS). It needs "Confirm email" turned off in the test project.

```bash
export SUPA_URL="https://xxxxx.supabase.co"
```

```bash
export SUPA_KEY="<anon key>"
```

```bash
./supabase/testes_jiggie.sh
```

## Deployment

Every push to `main` runs [`pages.yaml`](.github/workflows/pages.yaml), which:

1. builds the Wasm production bundle (`:webApp:wasmJsBrowserDistribution`);
2. puts the landing page at the site root and the app under `/app/`;
3. publishes everything to GitHub Pages.

---

Made with ❤[U+FE0F] by [Luísa Sampaio](https://github.com/lulssam) for Jiggie.
