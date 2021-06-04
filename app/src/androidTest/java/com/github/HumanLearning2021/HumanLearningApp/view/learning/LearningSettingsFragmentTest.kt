package com.github.HumanLearning2021.HumanLearningApp.view.learning

import android.content.Context
import android.content.Intent
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.android.architecture.blueprints.todoapp.launchFragmentInHiltContainer
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.TestUtils
import com.github.HumanLearning2021.HumanLearningApp.TestUtils.waitFor
import com.github.HumanLearning2021.HumanLearningApp.hilt.DatabaseNameModule
import com.github.HumanLearning2021.HumanLearningApp.hilt.ProductionDatabaseName
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.Id
import com.github.HumanLearning2021.HumanLearningApp.model.UniqueDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.view.MainActivity
import com.github.HumanLearning2021.HumanLearningApp.view.dataset_list_fragment.DatasetListRecyclerViewAdapter
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import javax.inject.Inject

@UninstallModules(DatabaseNameModule::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class LearningSettingsFragmentTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var globalDatabaseManagement: UniqueDatabaseManagement

    @BindValue
    @ProductionDatabaseName
    val dbName = "dummy"

    lateinit var dbMgt: DatabaseManagement

    private lateinit var datasetId: Id

    val navController: NavController = mock(NavController::class.java)

    @get:Rule
    val activityScenarioRule: ActivityScenarioRule<MainActivity> = ActivityScenarioRule(
        Intent(
            ApplicationProvider.getApplicationContext(),
            MainActivity::class.java
        )
    )

    @Before
    fun setup() {
        hiltRule.inject()
        Intents.init()
        dbMgt = globalDatabaseManagement.accessDatabase(dbName)
        datasetId = TestUtils.getFirstDataset(dbMgt).id
    }

    @After
    fun cleanUp() {
        Intents.release()
        activityScenarioRule.scenario.close()
    }

    @Test
    fun pressingPresentationButtonLaunchesLearningActivity() {
        launchFragment()
        onView(withId(R.id.learningSettings_btChoosePresentation)).perform(click())


        verify(navController).navigate(
            LearningSettingsFragmentDirections.actionLearningSettingsFragmentToLearningFragment(
                datasetId,
                LearningMode.PRESENTATION
            )
        )
    }

    @Test
    fun pressingRepresentationButtonLaunchesLearningActivity() {
        launchFragment()
        onView(withId(R.id.learningSettings_btChooseRepresentation)).perform(click())
        verify(navController).navigate(
            LearningSettingsFragmentDirections.actionLearningSettingsFragmentToLearningFragment(
                datasetId,
                LearningMode.REPRESENTATION
            )
        )
    }

    @Test
    fun pressingEvaluationButtonLaunchesLearningActivity() {
        launchFragment()
        onView(withId(R.id.learningSettings_btChooseEvaluation)).perform(click())
        verify(navController).navigate(
            LearningSettingsFragmentDirections.actionLearningSettingsFragmentToLearningFragment(
                datasetId,
                LearningMode.EVALUATION
            )
        )
    }

    @Test
    fun titleTestAndButtonsDisplayed() {
        launchFragment()
        assertDisplayed(R.id.learningSettings_btChoosePresentation)
        assertDisplayed(R.id.learningSettings_btChooseRepresentation)
        assertDisplayed(R.id.learningSettings_btChooseEvaluation)
        assertDisplayed(R.id.learningSettings_tvMode)
    }

    @Test
    fun clickOnInfoButtonWorks() {
        onView(withId(R.id.startLearningButton)).perform(click())
        onView(withId(R.id.DatasetList_list))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<DatasetListRecyclerViewAdapter.ListItemViewHolder>(
                    0,
                    click()
                )
            )
        onView(withId(R.id.learning_settings_menu_info)).perform(click())
        onView(
            withText(
                ApplicationProvider.getApplicationContext<Context>()
                    .getString(R.string.LearningSetting_displaysInfo)
            )
        ).check(
            matches(isDisplayed())
        )
        pressBack()
        waitFor(10)
        onView(withId(R.id.learningSettings_btChooseEvaluation)).check(matches(isDisplayed()))
    }

    private fun launchFragment() {
        val args = bundleOf("datasetId" to datasetId)
        launchFragmentInHiltContainer<LearningSettingsFragment>(fragmentArgs = args) {
            Navigation.setViewNavController(requireView(), navController)
        }
    }
}