# Strimup — Android Native

> **Découvrez et connectez-vous avec les créateurs de contenu qui vous correspondent.**
> Le portage 100% natif Kotlin/Compose de la plateforme web [strimup.com](https://www.strimup.com).

<p align="left">
  <img src="https://img.shields.io/badge/Kotlin-2.3-7F52FF?logo=kotlin&logoColor=white" alt="Kotlin" />
  <img src="https://img.shields.io/badge/Jetpack%20Compose-BOM%202026.05-4285F4?logo=jetpackcompose&logoColor=white" alt="Jetpack Compose" />
  <img src="https://img.shields.io/badge/Architecture-Clean%20Architecture-informational" alt="Clean Architecture" />
  <img src="https://img.shields.io/badge/Pattern-MVVM%20%2F%20MVI-blueviolet" alt="MVVM/MVI" />
  <img src="https://img.shields.io/badge/Async-Coroutines%20%7C%20Flow-orange" alt="Coroutines & Flow" />
  <img src="https://img.shields.io/badge/DI-Hilt-2C9C4A" alt="Hilt" />
  <img src="https://img.shields.io/badge/Min%20SDK-27-brightgreen" alt="Min SDK 27" />
  <img src="https://img.shields.io/badge/Target%20SDK-36-brightgreen" alt="Target SDK 36" />
  <img src="https://img.shields.io/badge/License-Proprietary-lightgrey" alt="License" />
</p>

---

## Sommaire

- [À propos & Genèse du projet](#a-propos)
- [Architecture & Design Patterns](#architecture)
- [Stack technique & bibliothèques](#stack-technique)
- [Fonctionnalités clés](#fonctionnalites)
- [Bonnes pratiques & Code Quality](#bonnes-pratiques)
- [Structure du projet](#structure)
- [Installation & Configuration](#installation)
- [Roadmap](#roadmap)
- [Auteur](#auteur)

---

<a id="a-propos"></a>
## À propos & Genèse du projet

**Strimup** est une plateforme de mise en relation entre **streamers** et **communautés**, pensée pour aider les créateurs de contenu à gagner en visibilité et permettre aux viewers de découvrir de nouveaux talents grâce à un système de **filtres multicritères** (catégorie, langue, plateforme, tranche d'âge, tags, statut live, etc.).

Le projet est né sous la forme d'une **application web complète** ([www.strimup.com](https://www.strimup.com)) que j'ai développée et déployée en full-stack. Face à l'usage massif du mobile et à mon envie de me spécialiser sur l'écosystème **Android natif**, j'ai entrepris un **portage complet en Kotlin / Jetpack Compose**, en repartant du même backend (API REST) mais avec une architecture pensée **from scratch** selon les standards de l'industrie mobile.

Ce dépôt n'est donc pas un simple exercice académique : c'est une **application produit réelle**, avec un backend en production, dont l'objectif ici est double :

1. Offrir une expérience mobile fluide et rapide aux utilisateurs de Strimup.
2. Démontrer ma capacité à concevoir une application Android robuste, testable et maintenable — architecture propre, séparation des responsabilités, gestion d'état stricte...

---
<a id="architecture"></a>
## Architecture & Design Patterns

L'application suit les principes de la **Clean Architecture**, découpée en trois couches indépendantes par feature, favorisant le découplage, la testabilité et l'évolutivité.

```
┌───────────────────────────────────────────────┐
│                 Presentation                   │
│   Composables · ViewModel · UiState · UiEvent  │
└───────────────────────▲───────────────────────┘
                         │ expose (StateFlow / Channel)
┌───────────────────────┴───────────────────────┐
│                     Domain                     │
│     UseCases · Entities · Repository (interface)│
└───────────────────────▲───────────────────────┘
                         │ implémente
┌───────────────────────┴───────────────────────┐
│                      Data                      │
│  Repository (impl) · Retrofit API · Room · DTO │
└─────────────────────────────────────────────────┘
```

- **`data/`** — Implémentations des repositories, services Retrofit, DAO Room, DTO et mappers (DTO → entité de domaine).
- **`domain/`** — Cœur métier, indépendant du framework Android : entités, interfaces de repository et **UseCases** (une responsabilité unique par cas d'usage : `CreateFilterUsecase`, `GetStreamersByFilterUsecase`, `GetUserFlowUseCase`…).
- **`presentation/`** — ViewModels, états d'UI et écrans Compose. Aucune logique métier n'y transite : le ViewModel orchestre les UseCases et expose un état immuable.

Le projet est découpé en modules fonctionnels au sein d'un module `app` unique, organisés en **`core/`** (briques transverses : auth, user, streamer, tag, réseau, base de données) et **`feature/`** (écrans métier : `home`, `search`, `filter`, `streamerdetail`, `streamerprofile`, `auth`), chacun exposant son propre module Hilt d'injection.

### MVVM / MVI & gestion d'état

- **State unidirectionnel (UDF)** : le `ViewModel` détient un unique `MutableStateFlow<UiState>` privé, exposé en lecture seule via `StateFlow`. La UI Compose ne fait qu'observer et rendre cet état — jamais l'inverse.
- **États modélisés en `sealed interface`** : chaque écran définit son propre contrat d'état exhaustif, ce qui permet au compilateur Kotlin de garantir qu'aucun cas (`Loading`, `Success`, `Error`) n'est oublié dans le `when` du Composable.

  ```kotlin
  sealed interface MatchedStreamersUiState {
      data object Loading : MatchedStreamersUiState

      data class Success(
          val filterName: String? = null,
          val matchedResult: StreamerMatchResult,
          val originalMatchedResult: StreamerMatchResult,
          val isLiveOnly: Boolean = false,
          val isLoadingNextPage: Boolean = false,
      ) : MatchedStreamersUiState

      data class Error(val errorMessage: String) : MatchedStreamersUiState
  }
  ```

- **Événements ponctuels (`UiEvent`)** : les effets à usage unique (afficher un `Snackbar`, naviguer, déclencher une action ponctuelle) transitent via un `sealed interface UiEvent` dédié, séparé de l'état persistant — évitant ainsi le rejeu d'événements lors d'une recomposition ou d'un changement de configuration.

  ```kotlin
  sealed interface FilterListUiEvent {
      data class ShowSnackBar(val text: String) : FilterListUiEvent
  }
  ```

### Injection de dépendances — Hilt

Chaque `feature` et chaque module `core` expose son propre **module Hilt** (`@Module @InstallIn(SingletonComponent::class)`), liant les interfaces de `domain` à leurs implémentations `data` (`FilterModule`, `HomeModule`, `AuthModule`, `StreamerCoreModule`, `TagModule`…). Les `ViewModel` sont injectés via `@HiltViewModel`, garantissant un couplage faible et une testabilité maximale (mock des dépendances par interface).

---
<a id="stack-technique"></a>
## Stack technique & bibliothèques

| Domaine | Technologies |
|---|---|
| **UI** | [Jetpack Compose](https://developer.android.com/jetpack/compose) · Material 3 · [Navigation 3](https://developer.android.com/guide/navigation/navigation-3) · Coil 3 (chargement d'images) |
| **Asynchronisme** | Kotlin Coroutines · `Flow` / `StateFlow` |
| **Réseau** | Retrofit 2 · OkHttp (+ `logging-interceptor`, `Authenticator`/`Interceptor` custom pour le refresh de token) · Kotlinx Serialization |
| **Persistance** | Room 3 · DataStore Preferences (session utilisateur) |
| **Architecture** | ViewModel & Lifecycle AndroidX · Clean Architecture · UseCases |
| **Injection de dépendances** | Hilt / Dagger |
| **Build** | Gradle Kotlin DSL · Version Catalog (`libs.versions.toml`) · KSP |

**Langage & tooling**

- **Kotlin 2.3**.
- **KSP** pour la génération de code Room et Hilt.
- **Kotlinx Serialization** pour le (dé)sérialisation JSON.

---
<a id="fonctionnalites"></a>
## Fonctionnalités clés

### Filtres multicritères avancés
Création de filtres de recherche personnalisés (catégorie, langue, plateforme, tranche d'âge via un `AgeRangePicker`, tags…), sauvegardés et gérables depuis une liste dédiée avec **suppression optimiste** (mise à jour immédiate de l'UI, rollback silencieux en cas d'échec réseau).

### Découverte & matching de streamers
Écran de résultats (`MatchedStreamersScreen`) affichant les streamers correspondant à un filtre, avec **pagination**, **filtrage en direct** (bascule "en live uniquement") et affichage du statut live en temps réel.

### Gestion & édition du profil streamer
Édition dynamique du profil (avatar, bio, réseaux sociaux, tags) via des composants modulaires (`BottomSheet`, sélecteurs de tags) et des `UseCase` dédiés (`UpdateProfileUsecase`, `UpdateAvatarUsecase`).

### Tags & catégories
Sélection de tags organisés par catégorie (`TagEntity(id, name, category)`) pour affiner à la fois les filtres de recherche et le profil d'un streamer.

### Authentification
Connexion / inscription avec gestion de session via **DataStore**, refresh automatique du token via un `Authenticator` OkHttp dédié, et connexion via réseaux sociaux.

### Accueil & recherche
Fils de découverte (favoris / sans favoris), recherche de streamers, navigation vers le détail d'un profil.

---
<a id="bonnes-pratiques"></a>
## Bonnes pratiques & Code Quality

- **Immuabilité du state** : les `UiState` sont des `data class` / `sealed interface` immuables ; toute mise à jour passe par `copy()`, jamais de mutation directe — garantissant la prévisibilité du flux de données (UDF).
- **`Result<T>` de bout en bout** : les `UseCase` et `Repository` retournent des `Result<T>` Kotlin plutôt que de lever des exceptions non contrôlées, forçant une gestion explicite du succès/échec (`onSuccess` / `onFailure`) jusque dans le ViewModel.
- **Repositories orientés interface** : chaque feature expose une interface de `Repository` dans `domain/`, implémentée dans `data/` — permettant le mock complet en tests unitaires sans dépendance à Retrofit/Room.
- **Composants Compose découplés & réutilisables** : extraction systématique en petits composants (`FilterBadge`, `FilterItemCard`, `FavoriteIconButton`, `EditProfileImageSection`…) avec **`@Preview`** pour une itération visuelle rapide, indépendante du run sur device/émulateur.
- **UI responsive** : layouts construits avec les primitives Compose (`Column`, `Row`, `LazyColumn`, `BoxWithConstraints`) sans dimensions codées en dur, pour s'adapter aux différentes tailles d'écran.
- **Séparation stricte des couches** : aucune dépendance Android (`Context`, vues) dans `domain/`.

---
<a id="structure"></a>
## Structure du projet

```
app/src/main/java/com/strimup/
├── core/                     # Briques transverses partagées
│   ├── database/              # Room : base, DAO, converters
│   ├── network/                # Configuration Retrofit / OkHttp
│   ├── user/                  # Session utilisateur (data/domain)
│   ├── streamer/              # Domaine streamer (entités, repository, mappers)
│   ├── tag/                   # Tags & catégories
│   └── ui/theme/               # Design system (couleurs, typographie, thème M3)
│
└── feature/                  # Écrans & logique métier par fonctionnalité
    ├── auth/                  # Connexion, inscription, session
    ├── home/                  # Accueil, favoris, onglets
    ├── search/                # Recherche de streamers
    ├── filter/                # Création, liste & matching de filtres
    └── streamerprofile/        # Profil streamer & édition
        └── streamerdetail/     # Détail d'un profil streamer

# Chaque feature suit le découpage : data/ · domain/ · presentation/ · injection/
```

---
<a id="installation"></a>
## Installation & Configuration

### Prérequis

- [Android Studio](https://developer.android.com/studio) (dernière version stable, Ladybug ou supérieur)
- JDK 11+
- Un appareil / émulateur avec **API 27 (Android 8.1)** minimum

### Étapes

```bash
# 1. Cloner le dépôt
git clone https://github.com/DylanMagalhaes/strimup-android.git
cd strimup-android

# 2. Ouvrir le projet dans Android Studio
#    File > Open... > sélectionner le dossier du projet
#    Laisser Gradle synchroniser les dépendances

# 3. Lancer l'application
#    Sélectionner un émulateur ou un appareil physique (API 27+)
#    Run ▶ (Shift+F10)
```

Le projet utilise un **Version Catalog** (`gradle/libs.versions.toml`) : aucune configuration manuelle des dépendances n'est nécessaire, Gradle se charge de tout résoudre à la synchronisation.

> L'application communique avec l'API REST de production Strimup. Aucune clé API n'est requise pour builder et lancer le projet en local.

---
<a id="roadmap"></a>
## Roadmap

- [ ] Externalisation des chaînes de caractères en dur vers `strings.xml`
- [ ] Couverture de tests unitaires (UseCases & ViewModels) et tests d'instrumentation Compose
- [ ] Mode hors-ligne enrichi (cache Room étendu à l'ensemble des entités)
- [ ] Modularisation Gradle multi-module (`:core:*`, `:feature:*`)
- [ ] CI/CD (build, lint, tests automatisés)

---
<a id="auteur"></a>
## Auteur

**Dylan Magalhaes** — Développeur Full-Stack (Web & Android Natif)
Développeur de la plateforme [strimup.com](https://www.strimup.com).

---

