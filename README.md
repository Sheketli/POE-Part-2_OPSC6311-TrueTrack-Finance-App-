# POE Part 2_OPSC6311 (TrueTrack Finance App)
TrueTrack Finance App.

**Smart budgeting. Real results.**

TrueTrackFinance is a fully offline, privacy-first personal budget tracker for Android, built in Kotlin with Material Design 3. All data is stored exclusively on-device in an encrypted Room (SQLite) database — nothing is ever sent to a server.

---

## Features

| Category | Feature |
|---|---|
| Auth | User registration + login, bcrypt password hashing, 3-attempt lockout (60 s cooldown), BiometricPrompt / 4–6 digit PIN gate, EncryptedSharedPreferences session |
| Dashboard | Circular budget ring, daily allowance indicator, category progress bars (amber at 90%, red at 100%), streak counter, top spenders, recent transactions, unallocated-funds banner |
| Expenses | Add / edit / delete expenses, ZAR amount input, date picker, category chip selector, swipe-to-delete with Snackbar undo, long-press context menu, description search, date range + category + amount filters |
| Receipts | CameraX capture or gallery picker, JPEG compression to under 1 MB, private internal storage, full-screen zoomable viewer, auto-delete on expense deletion |
| Categories | Create, rename, reorder (drag-and-drop), delete (with reassignment dialog), 16-colour presets, emoji icon, per-category budget limit |
| Reports | MPAndroidChart doughnut (spending by category) + stacked bar (daily spending), category variance table, period presets: This Month / Last Month / Last 3 Months / This Year / Custom |
| Savings Goals | Named goals with target amount and deadline, animated progress bars, auto-calculated monthly contribution, milestone push notifications at 25/50/75/100% |
| Annual Envelopes | Large irregular annual expenses (e.g. school fees), auto-calculated monthly set-aside |
| Gamification | 7 badge types (First Log, 7-Day Streak, 30-Day Streak, Budget Hero, Category Master, Saver, Consistent Planner), Lottie confetti on unlock, streak counter |
| Zero-Based Budget | Monthly income allocation across categories until remaining balance is zero |
| Recurring Transactions | Toggle per expense, Daily/Weekly/Fortnightly/Monthly/Annually frequency, WorkManager scheduling, 24 h pre-log notification |
| Settings | Biometric/PIN toggle, push notification toggles, dark mode, currency selector, monthly budget quick-set, Export to CSV |

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| Architecture | MVVM + Repository |
| DI | Hilt |
| Database | Room 2.8 + SQLCipher (encrypted) |
| Async | Kotlin Coroutines + StateFlow / LiveData |
| Charts | MPAndroidChart |
| Camera | CameraX |
| Animations | Lottie |
| Security | EncryptedSharedPreferences (AES-256), bcrypt (jBCrypt), BiometricPrompt |
| Background | WorkManager |
| Navigation | Jetpack Navigation Component |
| UI | Material Design 3 (Material3 theme) |
| Tests | JUnit 4, MockK, Kotlin Coroutines Test, Espresso |
| CI | GitHub Actions |
| Min SDK | Android 7.0 (API 25) |
| Target SDK | Android 16 (API 36) |

---

## Project Structure

```
TrueTrackFinance/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/truetrackfinance/
│   │   │   │   ├── TrueTrackFinanceApp.kt        # Hilt Application class
│   │   │   │   ├── data/
│   │   │   │   │   ├── db/
│   │   │   │   │   │   ├── entity/Entities.kt    # Room entities (User, Category, Expense, …)
│   │   │   │   │   │   ├── dao/                  # UserDao, ExpenseDao, CategoryDao, …
│   │   │   │   │   │   └── AppDatabase.kt        # Room database + SQLCipher factory
│   │   │   │   │   ├── model/Models.kt           # Projection data classes
│   │   │   │   │   └── repository/               # UserRepository, ExpenseRepository, …
│   │   │   │   ├── di/AppModule.kt               # Hilt bindings
│   │   │   │   ├── ui/
│   │   │   │   │   ├── SplashActivity.kt
│   │   │   │   │   ├── AuthActivity.kt / AuthPagerAdapter.kt
│   │   │   │   │   ├── MainActivity.kt
│   │   │   │   │   ├── auth/                     # LoginFragment, RegisterFragment
│   │   │   │   │   ├── dashboard/                # DashboardFragment + adapters
│   │   │   │   │   ├── expenses/                 # ExpensesFragment, AddExpenseFragment, adapters
│   │   │   │   │   ├── reports/                  # ReportsFragment + adapters
│   │   │   │   │   ├── savings/                  # SavingsFragment + adapters + bottom sheets
│   │   │   │   │   ├── categories/               # CategoriesFragment + adapter + bottom sheet
│   │   │   │   │   ├── achievements/             # AchievementsFragment + BadgesAdapter
│   │   │   │   │   ├── profile/                  # ProfileFragment
│   │   │   │   │   └── settings/                 # SettingsFragment
│   │   │   │   ├── viewmodel/                    # AuthViewModel, DashboardViewModel, …
│   │   │   │   ├── worker/                       # BudgetNotificationWorker, RecurringExpenseWorker, …
│   │   │   │   └── util/                         # SessionManager, DateUtil, CurrencyUtil, …
│   │   │   ├── res/
│   │   │   │   ├── layout/                       # All XML layout files
│   │   │   │   ├── drawable/                     # Vector icons, backgrounds
│   │   │   │   ├── menu/bottom_nav_menu.xml
│   │   │   │   ├── navigation/nav_graph.xml
│   │   │   │   ├── raw/confetti.json             # Lottie animation (replace with real file)
│   │   │   │   ├── values/colors.xml, strings.xml, themes.xml, dimens.xml
│   │   │   │   └── xml/file_provider_paths.xml
│   │   │   └── AndroidManifest.xml
│   │   ├── test/                                 # JVM unit tests (MockK + coroutines-test)
│   │   └── androidTest/                          # Espresso instrumented tests
│   └── build.gradle.kts
├── .github/workflows/build.yml                   # CI: build APK + run tests
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```
