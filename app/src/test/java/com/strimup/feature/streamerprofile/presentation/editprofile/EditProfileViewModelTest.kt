package com.strimup.feature.streamerprofile.presentation.editprofile

import com.google.common.truth.Truth.assertThat
import com.strimup.core.streamer.data.request.StreamerMatchRequest
import com.strimup.core.streamer.domain.entity.Social
import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.core.streamer.domain.entity.StreamerMatchResult
import com.strimup.core.streamer.domain.entity.StreamerOptions
import com.strimup.core.streamer.domain.repository.StreamerRepository
import com.strimup.core.tag.domain.entity.TagEntity
import com.strimup.core.tag.domain.usecase.GetTagsUseCase
import com.strimup.core.user.domain.entity.UserEntity
import com.strimup.core.user.domain.entity.UserRole
import com.strimup.core.user.domain.usecase.GetUserFlowUseCase
import com.strimup.feature.streamerprofile.domain.usecase.DefaultUpdateAvatarUseCase
import com.strimup.feature.streamerprofile.domain.usecase.DefaultUpdateProfileUseCase
import com.strimup.feature.streamerprofile.domain.usecase.GetStreamerOptionsUseCase
import com.strimup.feature.streamerprofile.domain.usecase.GetStreamerUseCase
import com.strimup.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EditProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fakeUser = UserEntity(
        id = "1",
        userName = "inox",
        email = "inox@mail.com",
        role = UserRole.STREAMER,
        avatarUrl = "",
    )

    private val fakeTags = listOf(
        TagEntity(id = 1, name = "FPS", category = "Gaming"),
        TagEntity(id = 2, name = "RPG", category = "Gaming"),
        TagEntity(id = 3, name = "Chill", category = "Ambiance"),
    )

    private val fakeOptions = StreamerOptions(
        averageViewers = listOf("0-50"),
        languages = listOf("FR", "EN"),
        personalities = listOf("Chill", "Tryhard"),
        streamFrequencies = listOf("DAILY"),
    )

    private val fakeStreamer = Streamer(
        id = "1",
        userName = "inox",
        imageUrl = "https://example.com/avatar.png",
        bio = "Ma bio",
        dailyStatus = "En stream",
        languages = listOf("FR"),
        tags = listOf(fakeTags[0]),
        socials = listOf(Social(url = "https://twitch.tv/inox", type = Social.Type.Twitch)),
        personality = "Chill",
        personalitySecondary = "Tryhard",
        streamFrequency = "DAILY",
        averageViewers = "0-50",
    )

    private fun getStreamerUseCase(fn: suspend (String) -> Result<Streamer>) =
        object : GetStreamerUseCase {
            override suspend fun invoke(id: String): Result<Streamer> = fn(id)
        }

    private fun buildViewModel(
        getStreamer: GetStreamerUseCase = getStreamerUseCase { Result.success(fakeStreamer) },
        getUser: GetUserFlowUseCase = GetUserFlowUseCase { flowOf(fakeUser) },
        getTags: GetTagsUseCase = GetTagsUseCase { Result.success(fakeTags) },
        repository: FakeStreamerRepository = FakeStreamerRepository(),
    ) = EditProfileViewModel(
        getStreamer = getStreamer,
        updateProfile = DefaultUpdateProfileUseCase(repository),
        updateAvatar = DefaultUpdateAvatarUseCase(repository),
        getUser = getUser,
        getOptions = GetStreamerOptionsUseCase(repository),
        getTags = getTags,
    )

    // region init / loading

    @Test
    fun `init should load options and populate availableOptions on success`() = runTest {
        // GIVEN
        val viewModel = buildViewModel(repository = FakeStreamerRepository(optionsResult = Result.success(fakeOptions)))

        // WHEN
        advanceUntilIdle()

        // THEN
        assertThat(viewModel.state.value.availableOptions).isEqualTo(fakeOptions)
    }

    @Test
    fun `init should load tags and populate availableCategories and availableTags on success`() = runTest {
        // GIVEN
        val viewModel = buildViewModel()

        // WHEN
        advanceUntilIdle()

        // THEN
        val state = viewModel.state.value
        assertThat(state.availableTags).isEqualTo(fakeTags)
        assertThat(state.availableCategories).isEqualTo(listOf(fakeTags[0], fakeTags[2]))
    }

    @Test
    fun `init when user is logged in should load the streamer and populate the editable fields`() = runTest {
        // GIVEN
        val viewModel = buildViewModel()

        // WHEN
        advanceUntilIdle()

        // THEN
        val state = viewModel.state.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.originalProfile).isEqualTo(fakeStreamer)
        assertThat(state.bio).isEqualTo("Ma bio")
        assertThat(state.dailyStatus).isEqualTo("En stream")
        assertThat(state.selectedLanguages).isEqualTo(listOf("FR"))
        assertThat(state.selectedTags).isEqualTo(listOf(fakeTags[0]))
        assertThat(state.socials).isEqualTo(fakeStreamer.socials)
        assertThat(state.personality).isEqualTo("Chill")
        assertThat(state.personalitySecondary).isEqualTo("Tryhard")
        assertThat(state.streamFrequency).isEqualTo("DAILY")
        assertThat(state.averageViewers).isEqualTo("0-50")
        assertThat(state.imageUrl).isEqualTo("https://example.com/avatar.png")
    }

    @Test
    fun `init when getStreamer fails should set the fixed error message and isLoading false`() = runTest {
        // GIVEN
        val viewModel = buildViewModel(
            getStreamer = getStreamerUseCase { Result.failure(Exception("Peu importe")) },
        )

        // WHEN
        advanceUntilIdle()

        // THEN
        val state = viewModel.state.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.errorMessage).isEqualTo("Erreur pendant la récupération du profil")
        assertThat(state.originalProfile).isNull()
    }

    @Test
    fun `init when user is null should never call getStreamer and stay in the default loading state`() = runTest {
        // GIVEN
        var getStreamerCallCount = 0
        val viewModel = buildViewModel(
            getUser = GetUserFlowUseCase { flowOf(null) },
            getStreamer = getStreamerUseCase { getStreamerCallCount++; Result.success(fakeStreamer) },
        )

        // WHEN
        advanceUntilIdle()

        // THEN
        assertThat(getStreamerCallCount).isEqualTo(0)
        assertThat(viewModel.state.value.isLoading).isTrue()
    }

    @Test
    fun `init when getOptions fails should silently fall back to empty options with no error shown`() = runTest {
        // TODO: voir le TODO dans EditProfileViewModel.loadOptions() — aucun retour utilisateur
        //  en cas d'échec. Pire : loadStreamer() retombe ensuite sur un StreamerOptions vide
        //  (fetchedOptions ?: StreamerOptions(...)), donc l'écran a l'air d'avoir chargé des
        //  options valides alors qu'elles sont vides. Ce test documente ce comportement actuel.
        // GIVEN
        val viewModel = buildViewModel(
            repository = FakeStreamerRepository(optionsResult = Result.failure(Exception("Erreur serveur"))),
        )

        // WHEN
        advanceUntilIdle()

        // THEN
        assertThat(viewModel.state.value.availableOptions).isEqualTo(
            StreamerOptions(emptyList(), emptyList(), emptyList(), emptyList())
        )
        assertThat(viewModel.state.value.errorMessage).isNull()
    }

    @Test
    fun `init when getTags fails should silently leave availableTags and availableCategories empty`() = runTest {
        // TODO: voir le TODO dans EditProfileViewModel.loadTags() — aucun retour utilisateur
        //  en cas d'échec, ce test documente le comportement actuel (silencieux).
        // GIVEN
        val viewModel = buildViewModel(
            getTags = GetTagsUseCase { Result.failure(Exception("Erreur serveur")) },
        )

        // WHEN
        advanceUntilIdle()

        // THEN
        assertThat(viewModel.state.value.availableTags).isEmpty()
        assertThat(viewModel.state.value.availableCategories).isEmpty()
        assertThat(viewModel.state.value.errorMessage).isNull()
    }

    // endregion

    // region simple field updaters

    @Test
    fun `onImageSelected should update imageUrl`() = runTest {
        val viewModel = buildViewModel()
        viewModel.onImageSelected("content://media/42")
        assertThat(viewModel.state.value.imageUrl).isEqualTo("content://media/42")
    }

    @Test
    fun `onBioChanged should update bio`() = runTest {
        val viewModel = buildViewModel()
        viewModel.onBioChanged("Nouvelle bio")
        assertThat(viewModel.state.value.bio).isEqualTo("Nouvelle bio")
    }

    @Test
    fun `onDailyStatusChanged should update dailyStatus`() = runTest {
        val viewModel = buildViewModel()
        viewModel.onDailyStatusChanged("Chill session")
        assertThat(viewModel.state.value.dailyStatus).isEqualTo("Chill session")
    }

    @Test
    fun `onCategorySelected should update selectedCategory and filter availableTags from the fetched tags`() = runTest {
        // GIVEN
        val viewModel = buildViewModel()
        advanceUntilIdle() // fetchedTags populated

        // WHEN
        viewModel.onCategorySelected(fakeTags[2]) // "Ambiance"

        // THEN
        val state = viewModel.state.value
        assertThat(state.selectedCategory).isEqualTo(fakeTags[2])
        assertThat(state.availableTags).containsExactly(fakeTags[2])
    }

    @Test
    fun `onTagSelected should add a tag not yet selected`() = runTest {
        val viewModel = buildViewModel()
        viewModel.onTagSelected(fakeTags[0])
        assertThat(viewModel.state.value.selectedTags).containsExactly(fakeTags[0])
    }

    @Test
    fun `onTagSelected called again on the same tag should remove it`() = runTest {
        val viewModel = buildViewModel()
        viewModel.onTagSelected(fakeTags[0])
        viewModel.onTagSelected(fakeTags[0])
        assertThat(viewModel.state.value.selectedTags).isEmpty()
    }

    @Test
    fun `onTagSelected should not allow selecting more than 4 tags`() = runTest {
        // GIVEN
        val viewModel = buildViewModel()
        val fiveTags = (1..5).map { TagEntity(id = it, name = "tag$it", category = "Gaming") }

        // WHEN
        fiveTags.forEach(viewModel::onTagSelected)

        // THEN
        val state = viewModel.state.value
        assertThat(state.selectedTags).hasSize(4)
        assertThat(state.selectedTags).doesNotContain(fiveTags[4])
    }

    @Test
    fun `onLanguageSelected should add a language not yet selected`() = runTest {
        val viewModel = buildViewModel()
        viewModel.onLanguageSelected("FR")
        assertThat(viewModel.state.value.selectedLanguages).containsExactly("FR")
    }

    @Test
    fun `onLanguageSelected called again on the same language should remove it`() = runTest {
        val viewModel = buildViewModel()
        viewModel.onLanguageSelected("FR")
        viewModel.onLanguageSelected("FR")
        assertThat(viewModel.state.value.selectedLanguages).isEmpty()
    }

    @Test
    fun `onPrimaryPersonalityChanged should set personality and clear secondary if it matches`() = runTest {
        // GIVEN
        val viewModel = buildViewModel()
        viewModel.onSecondaryPersonalityChanged("Chill")

        // WHEN
        viewModel.onPrimaryPersonalityChanged("Chill")

        // THEN
        val state = viewModel.state.value
        assertThat(state.personality).isEqualTo("Chill")
        assertThat(state.personalitySecondary).isNull()
    }

    @Test
    fun `onPrimaryPersonalityChanged should keep secondary if it does not match`() = runTest {
        // GIVEN
        val viewModel = buildViewModel()
        viewModel.onSecondaryPersonalityChanged("Tryhard")

        // WHEN
        viewModel.onPrimaryPersonalityChanged("Chill")

        // THEN
        val state = viewModel.state.value
        assertThat(state.personality).isEqualTo("Chill")
        assertThat(state.personalitySecondary).isEqualTo("Tryhard")
    }

    @Test
    fun `onSecondaryPersonalityChanged should set secondary and clear primary if it matches`() = runTest {
        // GIVEN
        val viewModel = buildViewModel()
        viewModel.onPrimaryPersonalityChanged("Chill")

        // WHEN
        viewModel.onSecondaryPersonalityChanged("Chill")

        // THEN
        val state = viewModel.state.value
        assertThat(state.personalitySecondary).isEqualTo("Chill")
        assertThat(state.personality).isNull()
    }

    @Test
    fun `onSecondaryPersonalityChanged should keep primary if it does not match`() = runTest {
        // GIVEN
        val viewModel = buildViewModel()
        viewModel.onPrimaryPersonalityChanged("Tryhard")

        // WHEN
        viewModel.onSecondaryPersonalityChanged("Chill")

        // THEN
        val state = viewModel.state.value
        assertThat(state.personality).isEqualTo("Tryhard")
        assertThat(state.personalitySecondary).isEqualTo("Chill")
    }

    @Test
    fun `onAverageViewersChanged should update averageViewers`() = runTest {
        val viewModel = buildViewModel()
        viewModel.onAverageViewersChanged("50-100")
        assertThat(viewModel.state.value.averageViewers).isEqualTo("50-100")
    }

    @Test
    fun `onStreamFrequencyChanged should update streamFrequency`() = runTest {
        val viewModel = buildViewModel()
        viewModel.onStreamFrequencyChanged("WEEKLY")
        assertThat(viewModel.state.value.streamFrequency).isEqualTo("WEEKLY")
    }

    @Test
    fun `onSocialUrlChanged should add a new social when it does not exist yet and the url is not blank`() = runTest {
        val viewModel = buildViewModel()
        viewModel.onSocialUrlChanged("https://youtube.com/inox", Social.Type.Youtube)
        assertThat(viewModel.state.value.socials).containsExactly(
            Social(url = "https://youtube.com/inox", type = Social.Type.Youtube)
        )
    }

    @Test
    fun `onSocialUrlChanged should update an existing social url`() = runTest {
        // GIVEN
        val viewModel = buildViewModel()
        viewModel.onSocialUrlChanged("https://twitch.tv/old", Social.Type.Twitch)

        // WHEN
        viewModel.onSocialUrlChanged("https://twitch.tv/new", Social.Type.Twitch)

        // THEN
        assertThat(viewModel.state.value.socials).containsExactly(
            Social(url = "https://twitch.tv/new", type = Social.Type.Twitch)
        )
    }

    @Test
    fun `onSocialUrlChanged should remove an existing social when the new url is blank`() = runTest {
        // GIVEN
        val viewModel = buildViewModel()
        viewModel.onSocialUrlChanged("https://twitch.tv/inox", Social.Type.Twitch)

        // WHEN
        viewModel.onSocialUrlChanged("   ", Social.Type.Twitch)

        // THEN
        assertThat(viewModel.state.value.socials).isEmpty()
    }

    @Test
    fun `onSocialUrlChanged should do nothing when the social does not exist and the url is blank`() = runTest {
        val viewModel = buildViewModel()
        viewModel.onSocialUrlChanged("   ", Social.Type.Kick)
        assertThat(viewModel.state.value.socials).isEmpty()
    }

    @Test
    fun `openEdit then dismissEdit should update activeEdit`() = runTest {
        // GIVEN
        val viewModel = buildViewModel()

        // WHEN
        viewModel.openEdit(ActiveEditType.Bio)

        // THEN
        assertThat(viewModel.state.value.activeEdit).isEqualTo(ActiveEditType.Bio)

        // WHEN
        viewModel.dismissEdit()

        // THEN
        assertThat(viewModel.state.value.activeEdit).isNull()
    }

    // endregion

    // region saveProfile

    @Test
    fun `saveProfile when originalProfile is not loaded yet should do nothing`() = runTest {
        // GIVEN
        val repository = FakeStreamerRepository()
        val viewModel = buildViewModel(
            getUser = GetUserFlowUseCase { flowOf(null) }, // originalProfile never gets set
            repository = repository,
        )
        advanceUntilIdle()

        // WHEN
        viewModel.saveProfile()
        advanceUntilIdle()

        // THEN
        assertThat(repository.updateProfileCallCount).isEqualTo(0)
        assertThat(viewModel.state.value.isSaving).isFalse()
    }

    @Test
    fun `saveProfile when the image is not a local uri should call updateProfile without uploading an avatar`() = runTest {
        // GIVEN
        val repository = FakeStreamerRepository()
        val viewModel = buildViewModel(repository = repository)
        advanceUntilIdle()

        // WHEN
        viewModel.saveProfile()
        advanceUntilIdle()

        // THEN
        assertThat(repository.updateAvatarCallCount).isEqualTo(0)
        assertThat(repository.updateProfileCallCount).isEqualTo(1)
        assertThat(repository.lastUpdateProfileArg?.imageUrl).isEqualTo(fakeStreamer.imageUrl)
    }

    @Test
    fun `saveProfile when the image is a local content uri should upload it first then save the uploaded url`() = runTest {
        // GIVEN
        val repository = FakeStreamerRepository(
            avatarResult = Result.success("https://example.com/uploaded.png"),
        )
        val viewModel = buildViewModel(repository = repository)
        advanceUntilIdle()
        viewModel.onImageSelected("content://media/42")

        // WHEN
        viewModel.saveProfile()
        advanceUntilIdle()

        // THEN
        assertThat(repository.updateAvatarCallCount).isEqualTo(1)
        assertThat(repository.lastUpdateAvatarUri).isEqualTo("content://media/42")
        assertThat(repository.lastUpdateProfileArg?.imageUrl).isEqualTo("https://example.com/uploaded.png")
        assertThat(viewModel.state.value.imageUrl).isEqualTo("https://example.com/uploaded.png")
    }

    @Test
    fun `saveProfile when the avatar upload fails should set errorMessage and never call updateProfile`() = runTest {
        // GIVEN
        val errorMessage = "Fichier trop volumineux"
        val repository = FakeStreamerRepository(
            avatarResult = Result.failure(Exception(errorMessage)),
        )
        val viewModel = buildViewModel(repository = repository)
        advanceUntilIdle()
        viewModel.onImageSelected("content://media/42")

        // WHEN
        viewModel.saveProfile()
        advanceUntilIdle()

        // THEN
        assertThat(repository.updateProfileCallCount).isEqualTo(0)
        val state = viewModel.state.value
        assertThat(state.isSaving).isFalse()
        assertThat(state.isSaveSuccess).isFalse()
        assertThat(state.errorMessage).isEqualTo(errorMessage)
    }

    @Test
    fun `saveProfile when the avatar upload fails without a message should use the default error message`() = runTest {
        // GIVEN
        val repository = FakeStreamerRepository(avatarResult = Result.failure(Exception()))
        val viewModel = buildViewModel(repository = repository)
        advanceUntilIdle()
        viewModel.onImageSelected("file://tmp/photo.jpg")

        // WHEN
        viewModel.saveProfile()
        advanceUntilIdle()

        // THEN
        assertThat(viewModel.state.value.errorMessage).isEqualTo("Erreur lors de l'envoi de la photo de profil")
    }

    @Test
    fun `saveProfile when updateProfile succeeds should mark isSaveSuccess and refresh originalProfile`() = runTest {
        // GIVEN
        val updatedStreamer = fakeStreamer.copy(bio = "Bio mise à jour")
        val repository = FakeStreamerRepository(
            updateProfileResult = { Result.success(updatedStreamer) },
        )
        val viewModel = buildViewModel(repository = repository)
        advanceUntilIdle()
        viewModel.onBioChanged("Bio mise à jour")

        // WHEN
        viewModel.saveProfile()
        advanceUntilIdle()

        // THEN
        val state = viewModel.state.value
        assertThat(state.isSaving).isFalse()
        assertThat(state.isSaveSuccess).isTrue()
        assertThat(state.originalProfile).isEqualTo(updatedStreamer)
        assertThat(repository.lastUpdateProfileArg?.bio).isEqualTo("Bio mise à jour")
    }

    @Test
    fun `saveProfile when updateProfile fails should set errorMessage without isSaveSuccess`() = runTest {
        // GIVEN
        val errorMessage = "Conflit serveur"
        val repository = FakeStreamerRepository(
            updateProfileResult = { Result.failure(Exception(errorMessage)) },
        )
        val viewModel = buildViewModel(repository = repository)
        advanceUntilIdle()

        // WHEN
        viewModel.saveProfile()
        advanceUntilIdle()

        // THEN
        val state = viewModel.state.value
        assertThat(state.isSaving).isFalse()
        assertThat(state.isSaveSuccess).isFalse()
        assertThat(state.errorMessage).isEqualTo(errorMessage)
    }

    @Test
    fun `saveProfile when updateProfile fails without a message should use the default error message`() = runTest {
        // GIVEN
        val repository = FakeStreamerRepository(
            updateProfileResult = { Result.failure(Exception()) },
        )
        val viewModel = buildViewModel(repository = repository)
        advanceUntilIdle()

        // WHEN
        viewModel.saveProfile()
        advanceUntilIdle()

        // THEN
        assertThat(viewModel.state.value.errorMessage).isEqualTo("Impossible de sauvegarder les modifications")
    }

    @Test
    fun `saveProfile should send a null bio and dailyStatus to updateProfile when they are blank`() = runTest {
        // GIVEN
        val repository = FakeStreamerRepository()
        val viewModel = buildViewModel(repository = repository)
        advanceUntilIdle()
        viewModel.onBioChanged("   ")
        viewModel.onDailyStatusChanged("")

        // WHEN
        viewModel.saveProfile()
        advanceUntilIdle()

        // THEN
        assertThat(repository.lastUpdateProfileArg?.bio).isNull()
        assertThat(repository.lastUpdateProfileArg?.dailyStatus).isNull()
    }

    // endregion

    private class FakeStreamerRepository(
        private val updateProfileResult: (Streamer) -> Result<Streamer> = { Result.success(it) },
        private val avatarResult: Result<String> = Result.success("https://example.com/avatar.png"),
        private val optionsResult: Result<StreamerOptions> = Result.success(
            StreamerOptions(emptyList(), emptyList(), emptyList(), emptyList())
        ),
    ) : StreamerRepository {

        var updateProfileCallCount = 0
        var lastUpdateProfileArg: Streamer? = null
        var updateAvatarCallCount = 0
        var lastUpdateAvatarUri: String? = null

        override suspend fun getRandomStreamers(favoriteStreamerIds: List<String>): Result<List<Streamer>> = TODO()

        override suspend fun getLiveStreamers(favoriteStreamerIds: List<String>): Result<List<Streamer>> = TODO()

        override suspend fun searchStreamers(userName: String): Result<List<Streamer>> = TODO()

        override suspend fun getStreamerById(id: String): Result<Streamer> = TODO()

        override suspend fun updateProfile(streamer: Streamer): Result<Streamer> {
            updateProfileCallCount++
            lastUpdateProfileArg = streamer
            return updateProfileResult(streamer)
        }

        override suspend fun updateAvatar(uri: String): Result<String> {
            updateAvatarCallCount++
            lastUpdateAvatarUri = uri
            return avatarResult
        }

        override suspend fun getStreamerOptions(): Result<StreamerOptions> = optionsResult

        override suspend fun getStreamersByFilter(request: StreamerMatchRequest): Result<StreamerMatchResult> = TODO()
    }
}
