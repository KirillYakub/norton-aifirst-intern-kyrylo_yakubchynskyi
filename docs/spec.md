# App name: Security Health Dashboard

## Project Goal
Build a modern, clean Android prototype that is inspired by the **Smart Scan** experience from the Norton 360 mobile app.

This is a **prototype only**:
- No real backend or actual security scanning
- All data and delays are mocked
- Architecture should be structured as if this could evolve into a production feature

---

## Technical Stack
- **Language:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Architecture:** MVVM
- **State Management:** Kotlin `StateFlow`, `Channel` for events
- **DI:** Koin
- **Navigation:** Jetpack Compose Navigation
- **Project Type:** Single-module

---

## Core App Flow

### 1. Home Screen (Dashboard)
- Title: **Health Dashboard**
- Subtitle: **"Scan device for security risks"**
- Prominent **"Scan Now"** button with yellow accent
- Full Pull-to-Refresh support
- Both button tap and pull-to-refresh navigate to Smart Scan screen

### 2. Smart Scan Screen
- Top bar: **"Smart Scan"** title with back button
- Scan simulation **automatically starts** when the screen opens

#### Scanning State
- Animated circular progress indicator (yellow accent)
- Overall Security Score (0–100) with smooth counting animation
- Sequential security category checks:
  1. OS Version
  2. App Threats
  3. Wi-Fi Safety
  4. Password Strength
- Each item displays: icon, title, and status badge (Success / Warning / Danger)
- Include error states to simulate possible scan/API failures

#### Result State
- Final Security Score with circular indicator
- HorizontalPager with 3 pages:
  1. **Possible Risks**
  2. **Recommendations**
  3. **Protection Upgrades**
- Each page contains multiple `RecommendationCard` components
- Cards include: icon, title, description, and "Fix Now" / "Set Up" button

---

## Special Behaviors
- **During active scan:** Back button shows confirmation dialog  
  - Title: "Do you want to leave Smart Scan?"  
  - Actions: Yes / No
- **After scan completes:** Back button returns to Home screen
- Smooth animations for progress, score counting, and transitions
- Proper error handling states

---

## Reusable Components
- `CircularScoreIndicator`
- `SecurityCheckItem`
- `RecommendationCard`
- `PrimaryActionButton`
- `StatusBadge`

---

## Data & Architecture
- Repository pattern (`ScanRepository` interface + `ScanRepositoryImpl`)
- Realistic mock data with coroutine-based delayed simulation
- Clean and maintainable code structure

---

## Testing Requirements
Implement at least 3 unit tests covering:
- ViewModel state transitions
- Score calculation logic
- Cancellation / back button behavior

---

## Design Notes
- Use white background with yellow/gold accent color inspired by modern mobile security apps
- Prioritize code quality, smooth UX, and maintainability