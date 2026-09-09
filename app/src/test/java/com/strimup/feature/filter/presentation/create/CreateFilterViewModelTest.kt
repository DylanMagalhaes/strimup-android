package com.strimup.feature.filter.presentation.create

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.strimup.core.tag.domain.entity.TagEntity
import com.strimup.core.tag.domain.usecase.GetTagsUseCase
import com.strimup.feature.filter.domain.entity.FilterCriteria
import com.strimup.feature.filter.domain.entity.FilterEntity
import com.strimup.feature.filter.domain.entity.FilterOptionsEntity
import com.strimup.feature.filter.domain.usecase.CreateFilterUseCase
import com.strimup.feature.filter.domain.usecase.GetFilterOptionsUseCase
import com.strimup.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CreateFilterViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fakeTags = listOf(
        TagEntity(id = 1, name = "FPS", category = "Jeux"),
        TagEntity(id = 2, name = "RPG", category = "Jeux"),
        TagEntity(id = 3, name = "Chill", category = "Ambiance"),
    )

    private val fakeOptions = FilterOptionsEntity(
        averageViewers = listOf("0-10", "10-50"),
        languages = listOf("FR", "EN"),
        personalities = listOf("Drôle", "Posé"),
        streamFrequencies = listOf("Tous les jours"),
    )

    private val fakeFilter = FilterEntity(
        id = "1",
        name = "Mon filtre",
        criteria = FilterCriteria(),
        userId = "u1",
    )

    private fun buildViewModel(
        createFilter: CreateFilterUseCase = CreateFilterUseCase { _, _ -> Result.success(fakeFilter) },
        getFilterOptions: GetFilterOptionsUseCase = GetFilterOptionsUseCase { Result.success(fakeOptions) },
        getTags: GetTagsUseCase = GetTagsUseCase { Result.success(fakeTags) },
    ) = CreateFilterViewModel(
        createFilter = createFilter,
        getFilterOptions = getFilterOptions,
        getTags = getTags,
    )

    // region init

    @Test
    fun `init when options and tags succeed should emit Content with options categories and default tags`() = runTest {
        // GIVEN
        val viewModel = buildViewModel()

        // WHEN
        advanceUntilIdle()

        // THEN
        val state = viewModel.state.value as CreateFilterUiState.Content
        assertThat(state.availableOptions).isEqualTo(fakeOptions)
        assertThat(state.availableCategories).isEqualTo(listOf(fakeTags[0], fakeTags[2]))
        assertThat(state.selectedCategory).isEqualTo(fakeTags[0])
        assertThat(state.availableTags).isEqualTo(listOf(fakeTags[0], fakeTags[1]))
    }

    @Test
    fun `init when options fail should emit ShowSnackBar with error message`() = runTest {
        // GIVEN
        val errorMessage = "Impossible de joindre le serveur"
        val viewModel = buildViewModel(
            getFilterOptions = GetFilterOptionsUseCase { Result.failure(Exception(errorMessage)) },
        )

        // WHEN & THEN
        viewModel.events.test {
            advanceUntilIdle()

            val event = awaitItem()
            assertThat(event).isInstanceOf(CreateFilterUiEvent.ShowSnackBar::class.java)
            assertThat((event as CreateFilterUiEvent.ShowSnackBar).text).isEqualTo(errorMessage)
        }
    }

    @Test
    fun `init when tags fail should emit ShowSnackBar with error message`() = runTest {
        // GIVEN
        val errorMessage = "Erreur de chargement des tags"
        val viewModel = buildViewModel(
            getTags = GetTagsUseCase { Result.failure(Exception(errorMessage)) },
        )

        // WHEN & THEN
        viewModel.events.test {
            advanceUntilIdle()

            val event = awaitItem()
            assertThat(event).isInstanceOf(CreateFilterUiEvent.ShowSnackBar::class.java)
            assertThat((event as CreateFilterUiEvent.ShowSnackBar).text).isEqualTo(errorMessage)
        }
    }

    @Test
    fun `saveFilter when name is blank should set nameError and not call createFilter`() = runTest {
        // GIVEN
        var createCalled = false
        val viewModel = buildViewModel(
            createFilter = CreateFilterUseCase { _, _ -> createCalled = true; Result.success(fakeFilter) },
        )
        advanceUntilIdle()

        // WHEN
        viewModel.saveFilter()
        advanceUntilIdle()

        // THEN
        val state = viewModel.state.value as CreateFilterUiState.Content
        assertThat(state.nameError).isEqualTo("Le nom du filtre ne peut pas être vide")
        assertThat(createCalled).isFalse()
    }

    @Test
    fun `saveFilter when valid should call createFilter with trimmed name and emit FilterCreated`() = runTest {
        // GIVEN
        var capturedName: String? = null
        var capturedCriteria: FilterCriteria? = null
        val viewModel = buildViewModel(
            createFilter = CreateFilterUseCase { name, criteria ->
                capturedName = name
                capturedCriteria = criteria
                Result.success(fakeFilter)
            },
        )
        advanceUntilIdle()
        viewModel.onFilterNameChange("  Mon filtre  ")
        viewModel.onRangeSelected(20..30)

        // WHEN & THEN
        viewModel.events.test {
            viewModel.saveFilter()
            advanceUntilIdle()

            assertThat(awaitItem()).isEqualTo(CreateFilterUiEvent.FilterCreated)
        }
        assertThat(capturedName).isEqualTo("Mon filtre")
        assertThat(capturedCriteria?.ageRange).isEqualTo(20..30)

        val state = viewModel.state.value as CreateFilterUiState.Content
        assertThat(state.isSubmitting).isFalse()
    }

    @Test
    fun `saveFilter when createFilter fails should emit ShowSnackBar and reset isSubmitting`() = runTest {
        // GIVEN
        val errorMessage = "Le filtre existe déjà"
        val viewModel = buildViewModel(
            createFilter = CreateFilterUseCase { _, _ -> Result.failure(Exception(errorMessage)) },
        )
        advanceUntilIdle()
        viewModel.onFilterNameChange("Mon filtre")

        // WHEN & THEN
        viewModel.events.test {
            viewModel.saveFilter()
            advanceUntilIdle()

            val event = awaitItem()
            assertThat(event).isInstanceOf(CreateFilterUiEvent.ShowSnackBar::class.java)
            assertThat((event as CreateFilterUiEvent.ShowSnackBar).text).isEqualTo(errorMessage)
        }

        val state = viewModel.state.value as CreateFilterUiState.Content
        assertThat(state.isSubmitting).isFalse()
    }

    @Test
    fun `onFilterNameChange should update filterName and clear existing nameError`() = runTest {
        // GIVEN
        val viewModel = buildViewModel()
        advanceUntilIdle()
        viewModel.saveFilter() // triggers nameError because name is blank
        advanceUntilIdle()

        // WHEN
        viewModel.onFilterNameChange("Test")

        // THEN
        val state = viewModel.state.value as CreateFilterUiState.Content
        assertThat(state.filterName).isEqualTo("Test")
        assertThat(state.nameError).isNull()
    }

    @Test
    fun `onTagSelected should add the tag then remove it when selected again`() = runTest {
        // GIVEN
        val viewModel = buildViewModel()
        advanceUntilIdle()
        val tag = fakeTags[0]

        // WHEN
        viewModel.onTagSelected(tag)

        // THEN
        var state = viewModel.state.value as CreateFilterUiState.Content
        assertThat(state.criteria.tags).containsExactly(tag)

        // WHEN
        viewModel.onTagSelected(tag)

        // THEN
        state = viewModel.state.value as CreateFilterUiState.Content
        assertThat(state.criteria.tags).isEmpty()
    }

    @Test
    fun `onTagSelected should not allow more than 5 tags`() = runTest {
        // GIVEN
        val viewModel = buildViewModel()
        advanceUntilIdle()
        val tags = (1..6).map { TagEntity(id = it, name = "tag$it", category = "Jeux") }

        // WHEN
        tags.forEach(viewModel::onTagSelected)

        // THEN
        val state = viewModel.state.value as CreateFilterUiState.Content
        assertThat(state.criteria.tags).hasSize(5)
        assertThat(state.criteria.tags).doesNotContain(tags[5])
    }

    @Test
    fun `onRangeSelected should update criteria ageRange`() = runTest {
        // GIVEN
        val viewModel = buildViewModel()
        advanceUntilIdle()

        // WHEN
        viewModel.onRangeSelected(25..40)

        // THEN
        val state = viewModel.state.value as CreateFilterUiState.Content
        assertThat(state.criteria.ageRange).isEqualTo(25..40)
    }

    @Test
    fun `onCategorySelected should update selectedCategory and filter availableTags`() = runTest {
        // GIVEN
        val viewModel = buildViewModel()
        advanceUntilIdle()

        // WHEN
        viewModel.onCategorySelected(fakeTags[2]) // category "Ambiance"

        // THEN
        val state = viewModel.state.value as CreateFilterUiState.Content
        assertThat(state.selectedCategory).isEqualTo(fakeTags[2])
        assertThat(state.availableTags).containsExactly(fakeTags[2])
    }

    @Test
    fun `onPersonalitySelected should toggle the personality`() = runTest {
        // GIVEN
        val viewModel = buildViewModel()
        advanceUntilIdle()

        // WHEN / THEN
        viewModel.onPersonalitySelected("Drôle")
        assertThat((viewModel.state.value as CreateFilterUiState.Content).criteria.personalities)
            .containsExactly("Drôle")

        viewModel.onPersonalitySelected("Drôle")
        assertThat((viewModel.state.value as CreateFilterUiState.Content).criteria.personalities)
            .isEmpty()
    }

    @Test
    fun `onLanguagesSelected should toggle the language`() = runTest {
        // GIVEN
        val viewModel = buildViewModel()
        advanceUntilIdle()

        // WHEN / THEN
        viewModel.onLanguagesSelected("FR")
        assertThat((viewModel.state.value as CreateFilterUiState.Content).criteria.languages)
            .containsExactly("FR")

        viewModel.onLanguagesSelected("FR")
        assertThat((viewModel.state.value as CreateFilterUiState.Content).criteria.languages)
            .isEmpty()
    }

    @Test
    fun `onPlatformSelected should toggle the platform`() = runTest {
        // GIVEN
        val viewModel = buildViewModel()
        advanceUntilIdle()

        // WHEN / THEN
        viewModel.onPlatformSelected("Twitch")
        assertThat((viewModel.state.value as CreateFilterUiState.Content).criteria.platforms)
            .containsExactly("Twitch")

        viewModel.onPlatformSelected("Twitch")
        assertThat((viewModel.state.value as CreateFilterUiState.Content).criteria.platforms)
            .isEmpty()
    }

    @Test
    fun `onAverageViewersSelected should set the averageViewers criteria`() = runTest {
        // GIVEN
        val viewModel = buildViewModel()
        advanceUntilIdle()

        // WHEN
        viewModel.onAverageViewersSelected("10-50")

        // THEN
        assertThat((viewModel.state.value as CreateFilterUiState.Content).criteria.averageViewers)
            .isEqualTo("10-50")
    }

    @Test
    fun `onStreamFrequencySelected should set the streamFrequency criteria`() = runTest {
        // GIVEN
        val viewModel = buildViewModel()
        advanceUntilIdle()

        // WHEN
        viewModel.onStreamFrequencySelected("Tous les jours")

        // THEN
        assertThat((viewModel.state.value as CreateFilterUiState.Content).criteria.streamFrequency)
            .isEqualTo("Tous les jours")
    }

    @Test
    fun `openEdit then dismissEdit should update activeEdit`() = runTest {
        // GIVEN
        val viewModel = buildViewModel()
        advanceUntilIdle()

        // WHEN
        viewModel.openEdit(ActiveEditType.FilterName)

        // THEN
        assertThat((viewModel.state.value as CreateFilterUiState.Content).activeEdit)
            .isEqualTo(ActiveEditType.FilterName)

        // WHEN
        viewModel.dismissEdit()

        // THEN
        assertThat((viewModel.state.value as CreateFilterUiState.Content).activeEdit).isNull()
    }

}
