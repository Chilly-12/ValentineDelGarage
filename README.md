# Valentine's Garage — Android App

A native Android application built for **Valentine's Garage** to manage truck
check-in, collaborative repair work, owner-level reporting, and day-to-day
team workflow. Submitted for **MAP711S — Mobile Application Development**,
NUST Faculty of Computing and Informatics.

---

## 1. What's in the box

A polished, production-style workshop assistant with:

- **Real authentication** — username + password sign-in, salted SHA-256 hashing
  with iteration stretching, persistent session via DataStore. Users stay
  signed in across launches until they explicitly sign out.
- **Splash screen** with brand animation and the "Crafted by Aurevarg"
  attribution.
- **Dashboard** with live stats: trucks in shop, completed jobs, check-ins
  today, pending tasks, completion rate, your personal contributions, and
  the latest 5 check-ins.
- **Truck check-in** capturing odometer reading + vehicle condition + notes
  (the assignment's anti-misuse safeguard).
- **Collaborative repair work** — every mechanic can tick off tasks and add
  notes. Each task records who completed it and when so the team never argues
  over "I thought you did it".
- **Search** across plate / make / model / customer.
- **Owner-only reports** — employee activity, condition breakdown, per-truck
  arrival snapshots.
- **Self-service profile** — edit your name / email / phone, change password.
- **Settings** — sign-out, security info, "About", Aurevarg attribution.
- **Role-based gating** — owners get the Reports tab + workshop-wide stats;
  mechanics get personal stats + the same workflow tools.
- **Seeded demo data** — four ready-to-use accounts and three example trucks
  so the app is never empty on first launch.

---

## 2. Demo accounts

| Role     | Username     | Password    |
| -------- | ------------ | ----------- |
| Owner    | `valentine`  | `Garage123` |
| Mechanic | `john`       | `Mechanic1` |
| Mechanic | `peter`      | `Mechanic1` |
| Mechanic | `mary`       | `Mechanic1` |

You can also create a new mechanic account from the **Create an account**
link on the sign-in screen.

---

## 3. Functional coverage (assignment requirements)

| Requirement                                                          | Where it lives in the app                                                                 |
| -------------------------------------------------------------------- | ----------------------------------------------------------------------------------------- |
| Truck check-in for repairs                                           | `ui/checkin/CheckInScreen.kt` + `domain/usecase/CheckInTruckUseCase.kt`                   |
| Capture **condition** and **kilometres** to prevent misuse           | `CheckInScreen` form (`condition`, `odometerKm`, `conditionNotes`)                        |
| Mechanics collaboratively **tick off** repair tasks                  | `ui/trucks/TruckDetailScreen.kt` checkboxes, `ToggleTaskUseCase`                          |
| Mechanics **write notes** on what they worked on                     | `AddTaskNoteUseCase` + per-task note dialog in `TruckDetailScreen`                        |
| Prevent tasks going undone (visibility of who did what, when)        | Each task shows "Completed by X • timestamp"; notes show author + timestamp               |
| Valentine sees **reports on what each employee did**                 | `ui/reports/ReportsScreen.kt` "Employee activity" section                                 |
| Valentine sees **condition of vehicles when checked in**             | `ReportsScreen` "Conditions on arrival" + per-truck snapshots                             |
| Real user accounts with passwords                                    | `util/PasswordHasher.kt`, `EmployeeRepository.authenticate`, `RegisterUseCase`            |
| Persistent session across app launches                               | `data/local/SessionStore.kt` (DataStore) + `SessionViewModel`                             |
| Live dashboard counters                                              | `domain/usecase/GetDashboardStatsUseCase.kt` (combines five reactive Flows)               |

---

## 4. App architecture

A **layered MVVM + Clean Architecture** structure with one Android module so
the project stays easy to grade, but with clear package boundaries that act
as logical modules:

```
com.valentinesgarage.app
├── data
│   ├── local            (Room: AppDatabase, DAOs, entities, SessionStore, DatabaseSeeder)
│   └── repository       (Repositories that map entities ↔ domain)
├── domain
│   ├── model            (Pure Kotlin: Truck, Employee, RepairTask, DashboardStats…)
│   └── usecase          (Login, Register, ChangePassword, CheckInTruck, ToggleTask,
│                         AddTaskNote, GetDashboardStats, GetReports)
├── di                   (AppContainer — manual DI)
├── ui
│   ├── splash           (Animated brand splash)
│   ├── login            (Auth)
│   ├── register         (New mechanic accounts)
│   ├── dashboard        (Home / KPIs / recent activity)
│   ├── trucks           (List + search, detail with collaborative tasks/notes)
│   ├── checkin          (Truck check-in form)
│   ├── reports          (Owner-only)
│   ├── profile          (Edit profile + change password)
│   ├── settings         (About, sign-out, security)
│   ├── components       (StatCard, GarageBottomBar, AurevargFooter)
│   ├── session          (SessionViewModel — persistent auth state)
│   ├── navigation       (Routes registry)
│   └── theme            (Material 3 garage palette + typography)
└── util                 (PasswordHasher, Validators, Time)
```

Highlights:

- **ViewModels** never see Room entities; repositories map them into domain models.
- **Manual DI** (`di/AppContainer.kt`) keeps the dependency graph trivially
  auditable — perfect for the assignment presentation.
- **DataStore-backed session** — opens on the dashboard if you've signed in before.
- **Reactive dashboard** — every stat is a `Flow` derived directly from Room.

---

## 5. Code quality

- Kotlin 2.0, Java 17 toolchain, AGP 8.7, compileSdk 34.
- Strict separation of concerns. **No business logic inside Composables.**
- Every public class carries a KDoc explaining its role.
- Validation lives in `util/Validators.kt` and the use cases — testable and reusable.
- Time is injected via a `TimeProvider` interface so tests are deterministic.
- Passwords are **never** stored in plaintext: `SHA-256 + per-user salt + 12 000 iterations`.
- No `print` / `Log.d` calls in production paths.

### Unit tests

Located in `app/src/test/java/com/valentinesgarage/app/`:

- `PasswordHasherTest` — verifies hashing, salting, determinism and rejection.
- `ValidatorsTest` — username / password / name / email / phone rules.
- `CheckInTruckUseCaseTest` — validation, plate normalisation, persistence call.
- `ToggleTaskUseCaseTest` — collaboration guard (must be signed in).
- `AddTaskNoteUseCaseTest` — note authorship guard.
- `VehicleConditionTest` — defensive enum parsing.

Run them with:

```
./gradlew :app:testDebugUnitTest
```

(Or use the green ▶ in Android Studio.)

---

## 6. UI & navigation

- **Jetpack Compose + Material 3** with a custom red/amber/cream "garage" palette.
- **Splash** → **Login / Register** → **Bottom-tab home** (Dashboard, Trucks,
  Reports*, Me) where ` *Reports` is owner-only.
- **Search** is wired to a Room query so results are reactive and instant.
- **Hand-off-aware UI** — every task shows who completed it and when, and every
  note shows author + timestamp. This is the visual cue that prevents the
  "I thought you did it" problem the brief calls out.
- **Empty states** and **inline success/error feedback** for friendly UX.
- **Branding**: "Crafted by Aurevarg" appears on the splash and in Settings.

Platform features leveraged:

- Room (persistence), DataStore (session persistence), Coroutines + Flow
  (reactive data), Lifecycle-aware Compose state collection
  (`collectAsStateWithLifecycle`), Navigation Compose, Material Icons Extended,
  edge-to-edge insets.

---

## 7. Presentation — clear separation of tasks done

Suggested split for a 4-person team (matches the package structure so each
member can demo their own files):

| Member | Owns                                                                           |
| ------ | ------------------------------------------------------------------------------ |
| #1     | Domain layer + auth (`domain/`, `util/PasswordHasher`, `util/Validators`)      |
| #2     | Data layer (`data/local`, `data/repository`, `SessionStore`, `DatabaseSeeder`) |
| #3     | Auth UI + dashboard (`ui/splash`, `ui/login`, `ui/register`, `ui/dashboard`)   |
| #4     | Workflow UI (`ui/checkin`, `ui/trucks`, `ui/reports`, `ui/profile`, `ui/settings`, theme) |

---

## 8. Building & running

You need **Android Studio Hedgehog** (or newer) with the Android SDK 34
platform installed. The Gradle wrapper jar is generated automatically the
first time you open the project (or you can run
`gradle wrapper --gradle-version 8.10.2` once from the project root).

1. Open `ValentineDelGarage` in Android Studio.
2. Let it sync Gradle (downloads Compose, Room, Navigation, DataStore, etc.).
3. Run the `app` configuration on an emulator or device (min SDK 24 — Android 7.0).
4. Sign in with one of the demo accounts above, or tap **Create an account**.

---

## 9. Tech summary

| Concern              | Choice                                                |
| -------------------- | ----------------------------------------------------- |
| Language             | Kotlin 2.0                                            |
| UI                   | Jetpack Compose + Material 3                          |
| Navigation           | Navigation-Compose 2.8                                |
| Persistence          | Room 2.6 (KSP)                                        |
| Session storage      | Jetpack DataStore Preferences 1.1                     |
| Reactive layer       | Kotlin Coroutines + Flow                              |
| DI                   | Manual `AppContainer` — no annotation magic to grade  |
| Testing              | JUnit4, MockK, Turbine, kotlinx-coroutines-test       |
| Min / Target / Compile SDK | 24 / 34 / 34                                    |
| Password hashing     | SHA-256 + per-user salt + 12 000 iterations           |

---

_Crafted by **Aurevarg**._
