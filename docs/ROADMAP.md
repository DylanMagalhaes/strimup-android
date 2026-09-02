# Roadmap — Production & Portfolio

> Objectif : amener Strimup Android à un état **publiable (test fermé Play Store)** et **présentable en entretien**.
> Rythme retenu : **plein temps, ~7h/j concentré**. Estimation « publiable » : **~3-4 semaines** (≈ 18-22 jours ouvrés).

**Légende effort :** 🟢 rapide (<1j) · 🟡 moyen (1-3j) · 🔴 gros morceau (1 sem+)

---

## Chemin critique « je peux commencer à postuler »

Faisable en **~4-5 jours plein temps**, le reste se fait **en parallèle des candidatures** :

1. Phase 0 complète (repo propre) — jours 1-2
2. Phase 4 : vraie suite de tests (infra + mappers + ViewModels) — jours 3-8
3. Pass README : retirer les affirmations non encore vraies (tests), puis ajouter la section Testing
4. → **On commence à postuler** ; Phases 1, 2, 3, 5, 6, 7 se font ensuite

> ⚠️ Ne pas disparaître 3-4 semaines pour tout finir avant de postuler. Repo propre + suite de tests suffit à passer les premiers filtres.

---

## Phase 0 — Hygiène du dépôt · 🟡 ~2j

L'état de `main` est la première chose qu'un recruteur regarde. Il doit compiler, être sans code mort et sans refactor à moitié fait.

### 0.1 Branches
- [x] Lister l'état réel de chaque branche
- [x] Merger dans `main` ce qui est terminé, supprimer le reste (local + `git push origin --delete`)
- [ ] Reste : supprimer `refactor-user` (local) + `origin/refactor/clean-architecture-restructure`, `origin/refactor/standardize-uistates`, `origin/video`
- [ ] Merger `favorite` → `main` pour que `main` soit l'état de référence

### 0.2 Finir le refactor `favorite` — ✅ FAIT (commit `675cade`)
- [x] Un seul emplacement : `core/favorite/`
- [x] Suppression des use cases dupliqués de `feature/favorite/domain/usecase/`
- [x] `FavoriteStreamersViewModel` migré vers les use cases de `core/favorite`
- [x] `AddStreamerToFavoritesUseCase`, `DeleteStreamerFromFavoritesUseCase`, `GetFavoriteStreamersUseCase`
- [x] Une seule interface `FavoriteStreamerRepository`, un seul `FavoriteModule`
- [x] `./gradlew assembleDebug` vert

### 0.3 Convention de nommage globale
- [ ] Renommer **tous** les `*Usecase` → `*UseCase` (~30 fichiers restants : `feature/home`, `feature/filter`, `feature/auth`, `feature/streamerdetail`, `feature/streamerprofile`, `feature/search`, `core/tag`)
- [ ] Refactor via Android Studio (met à jour les imports)
- [ ] `StreamerDetailViewModel` : param `removeStreamerFromFavorites` / type `DeleteStreamerFromFavoritesUseCase` → choisir **un** verbe (Delete ou Remove) partout

### 0.4 Supprimer la Navigation v1
- [ ] Supprimer `StrimupNavDisplay.kt` (v1, non utilisée)
- [ ] Supprimer `core/navigation/Destination.kt` (v1)
- [ ] Renommer `Destination2` → `Destination` et `StrimupNavDisplay2` → `StrimupNavDisplay`
- [ ] Supprimer le hack `Modifier.boldOnSelection` (`drawContent(); drawContent()` ne fait rien) → vraie variation de style

### 0.5 Fichiers parasites
- [ ] `git rm -r --cached .idea && git commit -m "chore: untrack .idea"`
- [ ] Vérifier `.gitignore` : `/.idea`, `*.iml`, `/build`, `local.properties` (OK)
- [ ] Ajouter `keystore.properties`, `*.jks`, `*.keystore` au `.gitignore` (préparation Phase 1)

### 0.6 Cohérence DI
- [ ] `feature/auth/injection/AuthModule.kt` : `AuthDomainModule` est en `@InstallIn(ViewModelComponent::class)` alors que tout le reste est `SingletonComponent`. Le passer en `SingletonComponent`
- [ ] Uniformiser `javax.inject.Singleton` vs `jakarta.inject.Singleton` → `javax.inject.*` partout

**✅ Critère de sortie :** `git checkout main && ./gradlew assembleDebug` passe, zéro `Destination2`/`NavDisplay2`, zéro doublon favorite, `git status` propre.

---

## Phase 1 — Durcissement build & sécurité · 🟡 ~2-3j

### 1.1 Signing release
- [ ] Générer un keystore : `keytool -genkey -v -keystore strimup-release.jks -keyalg RSA -keysize 2048 -validity 10000 -alias strimup` (hors repo)
- [ ] `keystore.properties` (gitignoré) : `storeFile`, `storePassword`, `keyAlias`, `keyPassword`
- [ ] `app/build.gradle.kts` : bloc `signingConfigs { create("release") { ... } }` qui lit `keystore.properties`
- [ ] `buildTypes.release` : `signingConfig = signingConfigs.getByName("release")`

### 1.2 Minification / shrink
- [ ] `release { isMinifyEnabled = true; isShrinkResources = true }`
- [ ] `proguard-rules.pro` : règles `-keep` pour `kotlinx.serialization` (classes `@Serializable` + `$$serializer`), interfaces Retrofit, Room entities/DAO, modèles utilisés en réflexion
- [ ] `./gradlew assembleRelease` puis **installer l'APK release sur un device** et tester : login, images Coil, désérialisation JSON, Room

### 1.3 Logs
- [ ] `core/network/injection/NetworkModule.kt` : `HttpLoggingInterceptor` **uniquement si `BuildConfig.DEBUG`** (sinon `Level.NONE`). Aujourd'hui `Level.BODY` en prod = tokens + PII dans logcat

### 1.4 Base de données
- [ ] `core/database/injection/DatabaseModule.kt` : retirer `.fallbackToDestructiveMigration()` du release (garder seulement en debug, ou écrire les migrations)
- [ ] `core/database/StrimupDatabase.kt` : `exportSchema = true`
- [ ] Commiter `app/schemas/`
- [ ] Écrire une première `Migration(1, 2)` d'exemple quand le schéma bougera

### 1.5 Stockage des tokens
Aujourd'hui `access_token` + `refresh_token` en **clair** dans DataStore Preferences.
- [ ] Option A : chiffrer les valeurs avant écriture avec une clé AES de l'`AndroidKeyStore`
- [ ] Option B : `EncryptedSharedPreferences` (déprécié mais fonctionnel, acceptable si commenté)
- [ ] Cible : `AuthPreferencesDataSource` garde la même API, le chiffrement est interne

### 1.6 Manifest / backup
- [ ] `AndroidManifest.xml` : `android:allowBackup="false"` (ou `backup_rules.xml` + `data_extraction_rules.xml` qui **excluent** `strimup_prefs`)
- [ ] `android:usesCleartextTraffic="false"`
- [ ] Vérifier que l'icône est une vraie icône adaptative custom

### 1.7 Interceptors
- [ ] `AuthInterceptor` / `AuthAuthenticator` : envisager un cache mémoire du token (`StateFlow`) alimenté au login/refresh pour supprimer le `runBlocking` par requête (optionnel, bon sujet d'entretien)

**✅ Critère de sortie :** un AAB release signé, minifié, testé sur device ; aucun log réseau en release ; tokens chiffrés.

---

## Phase 2 — Gestion des erreurs · 🟡 ~2-4j

### 2.1 Modèle d'erreur
- [ ] `core/common/DomainError.kt` : `sealed class` → `Network`, `Timeout`, `Unauthorized`, `Server(code: Int)`, `Serialization`, `Unknown`
- [ ] `core/network/ErrorMapper.kt` : `fun Throwable.toDomainError(): DomainError` mappant `IOException`, `SocketTimeoutException`, `retrofit2.HttpException` (via `.code()`), `SerializationException`

### 2.2 Propagation
- [ ] Les repos renvoient toujours `Result<T>`, mais les échecs portent un `DomainException(error: DomainError)`
- [ ] `UserRole` : remplacer `UserRole.valueOf(role)` par `UserRole.fromApi(role)` avec fallback `VIEWER` (crash actuel si valeur inconnue). Uniformiser le `.uppercase()` entre `core/user/.../UserMeMapper.kt` et `feature/auth/.../UserLoggedMapper.kt`

### 2.3 Présentation
- [ ] Supprimer **tous** les `onFailure { }` vides. Priorité : `FavoriteStreamersViewModel` (échec 100 % silencieux)
- [ ] `FavoriteStreamersUiState` : ajouter un état `error`
- [ ] Chaque `UiState` d'écran : représentation d'erreur + action **Réessayer**
- [ ] Mapper `DomainError` → `@StringRes` dans la présentation, **ne plus afficher `exception.localizedMessage`** brut
- [ ] `HomeScreen` : les `catch (_: Exception) {}` autour de `uriHandler.openUri` → au moins un snackbar

**✅ Critère de sortie :** couper le wifi → chaque écran affiche un message clair + bouton réessayer. Aucun `onFailure {}` vide.

---

## Phase 3 — i18n & finition · 🟢🟡 ~1-2j

- [ ] Extraire les ~49 chaînes en dur vers `res/values/strings.xml`
- [ ] Corriger les fautes : « Creer un compte » → « Créer un compte », etc.
- [ ] (Optionnel) `res/values-en/strings.xml`
- [ ] Revue des `contentDescription` : description réelle pour l'interactif, `null` pour le décoratif
- [ ] `./gradlew lintDebug` → corriger les warnings (unused resources, `Uri.parse` déprécié, etc.)
- [ ] Remplacer les magic strings (`type == "FEATURED_STREAMER"`) par un enum sérialisé
- [ ] `CommonConverters` : `Json` en constante (companion object) au lieu d'une instance par appel
- [ ] Supprimer l'import mort `retrofit2.http.Url` dans `core/user/domain/entity/UserEntity.kt`
- [ ] Factoriser les mappers `toRoomEntity`/`toDomainEntity` dupliqués entre `core/user` et `feature/auth`

**✅ Critère de sortie :** aucune chaîne UI en dur, `lint` sans erreur.

---

## Phase 4 — Tests · 🔴 ~5-7j (plein temps)

**Le levier n°1 pour la crédibilité.** Le README vend la testabilité : il faut des tests.

### 4.1 Mise en place
- [ ] Supprimer `app/src/test/java/com/example/` et `app/src/androidTest/java/com/example/`
- [ ] Créer `app/src/test/java/com/strimup/`
- [ ] Dépendances : `kotlinx-coroutines-test`, `app.cash.turbine:turbine`, `com.google.truth:truth` (ou kotest-assertions), `io.mockk:mockk` (préférer des **fakes** pour les repos)
- [ ] `MainDispatcherRule` (JUnit rule → `StandardTestDispatcher`)

### 4.2 Tests unitaires — mappers
- [ ] `UserMeMapper` / `UserLoggedMapper` : mapping nominal + `role` inconnu → `VIEWER` (pas de crash)
- [ ] `StreamerMapper`, `BannerMapper` : champs null, valeurs par défaut
- [ ] `Throwable.toDomainError()` : `HttpException(401)` → `Unauthorized`, `IOException` → `Network`, `SocketTimeoutException` → `Timeout`

### 4.3 Tests unitaires — ViewModels (avec fakes)
- [ ] `LoginViewModel` : input vide → pas d'appel ; succès → `user` set + event `ShowHomeUi` ; échec → event `ShowSnackBar`
- [ ] `HomeViewModel` : init charge bannière + streamers ; `onTabClick` annule le job précédent ; échec → état erreur + snackbar
- [ ] `FavoriteStreamersViewModel` : succès peuple la liste ; échec → état erreur (après Phase 2) ; `onSearchQueryChange` filtre
- [ ] `StreamerDetailViewModel` : toggle favori optimiste + **rollback** sur échec + ajustement `followersCount`
- [ ] `CreateFilterViewModel` : règles de validation
- [ ] `MainViewModel` : collecte du `Flow` user → état mis à jour

### 4.4 Tests use cases
- [ ] `GetStreamersWithoutFavoriteUsecase` : `Discovery` → `getRandomStreamers`, `Live` → `getLiveStreamers`

### 4.5 Tests instrumentés — Room (`src/androidTest`)
- [ ] `FavoriteDao` : insert / `getAllFavoritesOnce` / delete (base in-memory)
- [ ] `UserDao` : insert (REPLACE) / `getUserFlow` / `deleteAllUsers`

### 4.6 Tests Compose (`src/androidTest`)
- [ ] `LoginScreen` : saisie active le bouton ; état loading affiche « Connexion en cours... »
- [ ] `FavoriteStreamerScreen` : état vide, rendu liste, recherche filtre
- [ ] `HomeScreen` : loading → contenu

### 4.7 Couverture
- [ ] Ajouter `kover` (ou jacoco) ; générer un rapport
- [ ] Cible : **40-50 %** sur `domain` + `presentation`
- [ ] Badge de couverture dans le README

**✅ Critère de sortie :** `./gradlew testDebugUnitTest connectedDebugAndroidTest` vert, couverture > 40 % sur les couches métier.

---

## Phase 5 — Finir l'inscription (Register) · 🟡 ~2-4j

`RegisterScreen.kt` est aujourd'hui stateless pur avec `onClick = {}` et un `TODO()` dans le dropdown « Sexe » (crash si atteint).

- [ ] `RegisterUiState` : champs + erreurs par champ (`emailError`, `passwordError`, `confirmError`, `dateError`) + `isSubmitEnabled` + `isLoading` + `error`
- [ ] `RegisterViewModel` : handlers + validation
  - email : regex
  - mot de passe : longueur min + critères de force
  - confirmation : égalité
  - date de naissance : âge minimum (13 ou 18 ans selon les CGU)
- [ ] `RegisterRequest` (DTO) + `AuthApiService.register(...)` + méthode repo + `RegisterUseCase`
- [ ] Implémenter le `DropdownMenu` « Sexe » (supprimer le `TODO()`)
- [ ] Câbler `RegisterScreen` au ViewModel
- [ ] Navigation : Login « S'inscrire » → Register ; succès → Home (ou retour Login) ; back
- [ ] Erreurs backend : email déjà pris, mot de passe refusé
- [ ] Nettoyer / finir `feature/auth/presentation/component/SocialLoginSection.kt` (fichier entièrement commenté)

**✅ Critère de sortie :** parcours complet inscription → connexion → Home, avec validation et messages d'erreur.

---

## Phase 6 — CI & qualité · 🟢🟡 ~1j

- [ ] `.github/workflows/ci.yml` sur PR + push `main` :
  - JDK 17 + cache Gradle
  - `./gradlew assembleDebug`
  - `./gradlew lintDebug`
  - `./gradlew testDebugUnitTest`
  - `./gradlew detekt`
  - upload des rapports en artifacts
- [ ] Ajouter **detekt** (+ règles formatting) ; baseline pour l'existant, corriger au fil de l'eau
- [ ] Protection de branche sur `main` (PR obligatoire, CI verte)
- [ ] Badges README : build CI, couverture

**✅ Critère de sortie :** une PR déclenche le workflow, tout est vert, badge visible.

---

## Phase 7 — Prêt pour la prod (Play Store) · 🟡 en continu

### 7.1 Observabilité
- [ ] Crash reporting : Sentry (plus léger sans Firebase) ou Firebase Crashlytics ; init dans `StrimupApplication`
- [ ] (Optionnel) Analytics events clés

### 7.2 Offline & réseau
- [ ] `core/network/NetworkMonitor` : `ConnectivityManager.NetworkCallback` → `Flow<Boolean>`
- [ ] Bandeau « Hors ligne » global dans le `Scaffold` racine
- [ ] (Optionnel) Étendre le cache Room au-delà des favoris

### 7.3 Compte
- [ ] Écran **Réglages** + `LogoutUseCase` (clear DataStore + `userDao.deleteAllUsers()` + retour Home) — la plomberie existe déjà (`clear()`, `deleteAllUsers()`), il manque l'UI
- [ ] Suppression de compte (souvent exigé par Play)

### 7.4 Conformité Play
- [ ] Politique de confidentialité (page GitHub Pages) — **obligatoire** : l'app collecte email, date de naissance, genre
- [ ] Play Console : créer l'app, piste **test interne**, formulaire *Data safety*, *Content rating*, fiche Store (screenshots dans `docs/screenshots/`)
- [ ] `./gradlew bundleRelease` → vérifier l'AAB avec `bundletool`
- [ ] Upload de l'AAB signé sur la piste test interne

### 7.5 Bonus (fort impact entretien)
- [ ] Modularisation Gradle `:core:*` / `:feature:*` — gros signal de maturité
- [ ] Baseline Profile + module macrobenchmark
- [ ] `fastlane` ou Gradle Play Publisher pour l'upload automatisé

**✅ Critère de sortie :** appli installable depuis la piste test interne, avec politique de confidentialité et crash reporting.

---

## README — à mettre à jour au fil des phases

- [ ] **Maintenant** : retirer / nuancer les affirmations pas encore vraies (couverture de tests) jusqu'à la Phase 4
- [ ] Après Phase 4 : section « Testing » (stratégie, outils, couverture)
- [ ] Après Phase 6 : badges CI + couverture
- [ ] Tenir la section Roadmap du README à jour

---

## Planning — plein temps ~7h/j

### Estimation par phase

| Phase | Effort 7h/j | Note |
|---|---|---|
| 0 — Hygiène dépôt | **1,5j** | Renames mécaniques, revérifier le build à chaque étape |
| 1 — Build & sécurité | **2-3j** | Le 1er build release casse toujours (ProGuard + serialization/Retrofit) → debug incompressible |
| 2 — Gestion d'erreurs | **2j** | |
| 3 — i18n & finition | **1j** | |
| 4 — Tests | **5-7j** | ⚠️ Départ de zéro test → taxe d'apprentissage (fakes, Turbine, MainDispatcherRule, Compose test). Peu compressible par les heures |
| 5 — Register | **2-3j** | Dépend de l'endpoint backend `register` prêt ou non |
| 6 — CI | **1j** | |
| 7 — Prod (essentiels) | **2-3j** | + délais externes : review Play Console (1-7j), hébergement politique de confidentialité |

**Total « publiable Play Store » : ~18-22 jours ouvrés ≈ 3-4 semaines.**

### Déroulé jour par jour (2 premières semaines)

| Jours | Contenu |
|---|---|
| 1-2 | Phase 0 — `main` propre, sans code mort |
| 3-8 | Phase 4 — infra de test + mappers + ViewModels + Room + quelques Compose |
| 8 | Pass README → **début des candidatures** |
| 9-11 | Phase 1 — build release signé + minifié + tokens chiffrés |
| 12-13 | Phase 2 — modèle d'erreur + suppression des `onFailure {}` vides |
| 14 | Phase 3 — `strings.xml` + accents + lint |

### Semaine 3

- Phase 5 (Register) → 2-3j
- Phase 6 (CI) → 1j
- Phase 7 essentiels : crash reporting, écran Réglages + Logout, politique de confidentialité, formulaire Data safety, upload AAB sur la piste test interne

### Ce qui bloque la vitesse (ne pas se battre contre)

1. **Phase 4** : composante réflexion/apprentissage — bosser plus d'heures aide peu.
2. **Phase 7** : dépendances externes (review Play Console, backend, hosting) — temps d'attente, pas de code.
3. **Fatigue solo** : 7h/j de deep work Kotlin/tests sur 3 semaines sans review → la qualité chute en semaine 2-3. Prévoir des respirations.

**Rappel :** dès la fin de la Phase 0 + une vraie suite de tests + pass README (≈ 1 semaine), le projet est présentable. Ne pas attendre la Phase 7 pour postuler.
