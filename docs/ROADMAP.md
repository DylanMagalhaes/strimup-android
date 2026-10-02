# Roadmap — Production & Portfolio

> Objectif : amener Strimup Android à un état **publiable (test fermé Play Store)** et **présentable en entretien**.

**Légende effort :** 🟢 rapide (<1j) · 🟡 moyen (1-3j) · 🔴 gros morceau (1 sem+)

---

## État au 2 octobre 2026

- **Code prêt pour le store** : toutes les phases bloquantes (0, 1, 2, 3, 5, 6 et le code de la 7) sont terminées.
- **Reste pour publier** : uniquement de l'administratif et le site web (voir 7.4 à 7.6).
- **Reste pour l'image du projet** : tests instrumentés DAO / Compose et couverture Kover (Phase 4).

---

## Phase 0 — Hygiène du dépôt · ✅ terminée

### 0.1 Branches
- [x] Lister l'état réel de chaque branche
- [x] Merger dans `main` ce qui est terminé, supprimer le reste (local + `git push origin --delete`)
- [x] Supprimer `refactor-user`, `origin/refactor/clean-architecture-restructure`, `origin/refactor/standardize-uistates`, `origin/video`
- [x] Merger `favorite` → `main` pour que `main` soit l'état de référence

### 0.2 Finir le refactor `favorite`
- [x] Un seul emplacement : `core/favorite/`
- [x] Suppression des use cases dupliqués de `feature/favorite/domain/usecase/`
- [x] `FavoriteStreamersViewModel` migré vers les use cases de `core/favorite`
- [x] `AddStreamerToFavoritesUseCase`, `DeleteStreamerFromFavoritesUseCase`, `GetFavoriteStreamersUseCase`
- [x] Une seule interface `FavoriteStreamerRepository`, un seul `FavoriteModule`

### 0.3 Convention de nommage globale
- [x] Renommer tous les `*Usecase` → `*UseCase`
- [x] Un seul verbe (Delete) pour la suppression des favoris

### 0.4 Supprimer la Navigation v1
- [x] Supprimer la v1 de `StrimupNavDisplay` et de `Destination`
- [x] Renommer `Destination2` → `Destination` et `StrimupNavDisplay2` → `StrimupNavDisplay`
- [x] Supprimer le hack `Modifier.boldOnSelection`

### 0.5 Fichiers parasites
- [x] Ne plus suivre `.idea`
- [x] `.gitignore` : `/.idea`, `*.iml`, `/build`, `local.properties`, `keystore.properties`, `*.jks`, `*.keystore`

### 0.6 Cohérence DI
- [x] Tous les modules en `SingletonComponent`
- [x] `javax.inject.*` partout

---

## Phase 1 — Durcissement build & sécurité · ✅ terminée

### 1.1 Signing release
- [x] Keystore d'upload généré (hors repo)
- [x] `keystore.properties` (gitignoré) ou variables d'environnement `STRIMUP_RELEASE_*`, modèle dans `keystore.properties.example`
- [x] `signingConfigs.release` dans `app/build.gradle.kts`

### 1.2 Minification / shrink
- [x] `isMinifyEnabled = true` + `isShrinkResources = true`
- [x] `proguard-rules.pro` : numéros de ligne conservés, règle `-keep` des interfaces `*ApiService` (Retrofit)
- [x] APK release installé et testé sur device (login, images, JSON, Room)

### 1.3 Logs
- [x] `HttpLoggingInterceptor` en `BODY` uniquement si `BuildConfig.DEBUG`, `NONE` en release, données sensibles masquées

### 1.4 Base de données
- [x] `fallbackToDestructiveMigration` uniquement en debug
- [x] `exportSchema = true` + `app/schemas/` commité
- [x] `MIGRATION_1_2` écrite et testée en instrumenté (`StrimupDatabaseMigrationTest`)

### 1.5 Stockage des tokens
- [x] Chiffrement AES-GCM avec une clé de l'Android Keystore (`KeystoreSecretCipher`)
- [x] `AuthPreferencesDataSource` garde la même API, migration transparente des anciens tokens en clair

### 1.6 Manifest / backup
- [x] `android:allowBackup="false"` + règles de backup qui excluent les préférences
- [x] `android:usesCleartextTraffic="false"`
- [x] Icône adaptative custom (avec version monochrome)

### 1.7 Interceptors
- [ ] (Optionnel) Cache mémoire du token (`StateFlow`) pour supprimer le `runBlocking` de `AuthInterceptor` / `AuthAuthenticator`

---

## Phase 2 — Gestion des erreurs · ✅ terminée

- [x] `DomainError` : `Network`, `Timeout`, `Unauthorized`, `Server(code, message)`, `Serialization`, `Unknown`
- [x] `Throwable.toDomainError()` + `Result.toDomainResult()` au bord de chaque repository
- [x] `UserRole.fromApi(role)` avec fallback `VIEWER`
- [x] Plus aucun `onFailure { }` vide, état d'erreur + **Réessayer** sur chaque écran
- [x] `DomainError.toUiText()` : message du backend pour les 4xx, message localisé sinon
- [x] Ouverture des liens externes sécurisée (`rememberExternalLinkOpener`) avec snackbar en cas d'échec

---

## Phase 3 — i18n & finition · ✅ terminée

- [x] Toutes les chaînes UI dans `res/values/strings.xml`, avec `plurals`
- [x] Fautes corrigées (accents, « Créer un compte »…)
- [x] Revue des `contentDescription` (réels sur l'interactif, `null` sur le décoratif)
- [x] `./gradlew lintDebug` : plus aucun warning hors mises à jour de versions
- [x] `BannerType` en enum au lieu de la magic string `"FEATURED_STREAMER"`
- [x] Import mort `retrofit2.http.Url` supprimé
- [x] Mappers utilisateur dupliqués factorisés
- [ ] (Optionnel) `res/values-en/strings.xml`

---

## Phase 4 — Tests · 🟡 en grande partie faite

**533 tests unitaires** à ce jour.

| Ce que tu vois dans le code | Tests que ça génère |
| :--- | :--- |
| `?: ""` / `?: false` / `?: emptyList()` (fallback) | 1 test où le fallback s'active (input null) + 1 où il ne s'active pas |
| `if` / `when` / `&&` / `||` (branche) | 1 test par branche |
| `?.` (appel null-safe) | 1 test avec la chose à `null`, 1 avec une valeur |
| `requireNotNull` / `valueOf` / `!!` (peut planter) | 1 test qui déclenche le crash |
| `.map` / `.filter` / `.takeIf` / `listOfNotNull` (transformation) | 1 test qui vérifie la transfo + le cas vide |
| copie directe `champ = this.autreChamp` | **1 test** qui vérifie que tous les champs atterrissent au bon endroit |
| la fonction **ne set pas** un paramètre → valeur par défaut du data class | 1 test qui vérifie cette valeur par défaut |

### 4.1 Mise en place
- [x] Packages `com.example` supprimés, tests dans `com.strimup`
- [x] `kotlinx-coroutines-test`, Turbine, Truth ; **fakes** plutôt que mocks
- [x] `MainDispatcherRule`

### 4.2 Mappers
- [x] `UserMeMapper` / `UserLoggedMapper` : nominal + `role` inconnu → `VIEWER`
- [x] `StreamerMapper`, `BannerMapper` : champs null, valeurs par défaut
- [x] `Throwable.toDomainError()` (`ErrorMapperTest`)

### 4.3 ViewModels
- [x] `LoginViewModel`, `RegisterViewModel`, `HomeViewModel`, `FavoriteStreamersViewModel`
- [x] `StreamerDetailViewModel` (favori optimiste + rollback + `followersCount`)
- [x] `CreateFilterViewModel`, `MainViewModel`
- [x] `DeleteAccountViewModel`, `AccountViewModel`, `ReportStreamerViewModel`

### 4.4 Use cases & repositories
- [x] `DefaultGetStreamerUseCase` et les use cases métier (suppression de compte, signalement…)
- [x] Repositories avec fakes d'API (`DefaultAccountRepository`, `DefaultReportRepository`…)

### 4.5 Tests instrumentés — Room (`src/androidTest`)
- [x] Migrations de schéma (`StrimupDatabaseMigrationTest`)
- [ ] `FavoriteDao` : insert / `getAllFavoritesOnce` / delete (base in-memory)
- [ ] `UserDao` : insert (REPLACE) / `getUserFlow` / `deleteAllUsers`
- [ ] `HomeDao` / `FilterDao`

### 4.6 Tests Compose (`src/androidTest`)
- [ ] `LoginScreen` : saisie active le bouton ; état loading
- [ ] `FavoriteStreamerScreen` : état vide, rendu liste, recherche filtre
- [ ] `HomeScreen` : loading → contenu
- [ ] `ReportStreamerSheet` : bouton désactivé tant que le formulaire est invalide

### 4.7 Couverture
- [ ] Ajouter Kover ; générer un rapport
- [ ] Cible : **40-50 %** sur `domain` + `presentation`
- [ ] Badge de couverture dans le README

**✅ Critère de sortie :** `./gradlew testDebugUnitTest connectedDebugAndroidTest` vert, couverture > 40 % sur les couches métier.

---

## Phase 5 — Inscription & authentification · ✅ terminée

- [x] `RegisterUiState` + `RegisterViewModel` avec validation (email, mot de passe, confirmation, âge minimum)
- [x] `RegisterRequest` + `AuthApiService.register(...)` + `RegisterUseCase`
- [x] Dropdowns « Sexe » et « Je suis un(e) » fonctionnels
- [x] Navigation Login ↔ Register ↔ Home
- [x] Erreurs backend affichées (email déjà pris, 400 / 422)
- [x] Connexion / inscription **Twitch (OAuth + PKCE)** (`AuthOAuthSection`)
- [x] Acceptation des CGU et de la politique de confidentialité à l'inscription
- [x] « Mot de passe oublié » → page web `strimup.com/forgot-password`

---

## Phase 6 — CI & qualité · ✅ terminée

- [x] `.github/workflows/ci.yml` sur PR + push `main` : `assembleDebug`, `lintDebug`, `testDebugUnitTest`, `detekt`
- [x] detekt (+ formatting) avec baseline
- [x] Protection de branche sur `main` (PR obligatoire, CI verte)
- [x] Badge CI dans le README

---

## Phase 7 — Prêt pour la prod (Play Store) · 🟡 code fait, publication à faire

### 7.1 Observabilité
- [x] Firebase Crashlytics, actif en release uniquement, mapping R8 envoyé automatiquement
- [ ] (Optionnel) Analytics events clés

### 7.2 Offline & réseau
- [x] Cache Room de l'accueil (bannière + premiers streamers, jamais les infos live)
- [x] Pull-to-refresh sur l'accueil
- [ ] `core/network/NetworkMonitor` : `ConnectivityManager.NetworkCallback` → `Flow<Boolean>`
- [ ] Bandeau « Hors ligne » global dans le `Scaffold` racine

### 7.3 Compte & notifications
- [x] Écran **Mon compte** : déconnexion, notifications, suppression de compte
- [x] Suppression de compte in-app (exigée par Play)
- [x] Notifications push FCM + centre de notifications

### 7.4 Conformité Play
- [x] Politique de confidentialité et CGU en ligne (strimup.com)
- [x] Signalement de profil + modération côté backend (exigence « contenu généré par les utilisateurs »)
- [ ] Page web de suppression de compte (lien exigé par Play en plus de l'in-app)
- [ ] CGU : mentionner le signalement et la modération des profils
- [ ] Compte de démo pour les testeurs Google (« Accès à l'app »)
- [ ] Processus de traitement des signalements (routes admin via Postman / curl au début)

### 7.5 Fiche store
- [x] Icône 512 px (`docs/store/play-store-icon-512.png`)
- [ ] Image de présentation 1024×500
- [ ] Au moins 2 captures d'écran téléphone
- [ ] Description courte (80 caractères) et description longue

### 7.6 Publication
- [ ] Compte développeur Play Console (25 $)
- [ ] Créer l'app + activer la signature d'app par Google Play (la clé `.jks` devient la clé d'importation)
- [ ] Formulaires : Sécurité des données, classification du contenu, public cible (16+), publicités (non), accès à l'app
- [ ] `./gradlew bundleRelease` → envoi de l'AAB
- [ ] Test fermé : 12 testeurs pendant 14 jours (compte développeur personnel)
- [ ] Demande d'accès à la production

### 7.7 Bonus (fort impact entretien)
- [ ] Modularisation Gradle `:core:*` / `:feature:*`
- [ ] Baseline Profile + module macrobenchmark
- [ ] `fastlane` ou Gradle Play Publisher pour l'upload automatisé

**✅ Critère de sortie :** appli installable depuis la piste de test fermé, avec politique de confidentialité et crash reporting.

---

## Améliorations hors roadmap (backlog)

- [ ] Revenir sur l'écran d'origine après connexion (aujourd'hui retour à l'accueil)
- [ ] Masquer un streamer de ses propres listes
- [ ] Mises à jour des dépendances (warnings lint `NewerVersionAvailable`, `GradleDependency`, AGP) sur une branche dédiée

---

## README

- [x] Affirmations alignées sur la réalité (500+ tests, fonctionnalités, sécurité)
- [x] Section « Tests »
- [x] Badge CI
- [ ] Badge de couverture (après Kover)
- [ ] Ajouter de nouvelles captures (fiche streamer, notifications, signalement)
