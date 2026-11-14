package de.mm20.launcher2.ui.launcher.scaffold

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.mm20.launcher2.ui.launcher.scaffold.components.ScaffoldComponent
import de.mm20.launcher2.ui.launcher.scaffold.components.SearchComponent
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LauncherScaffoldStateTest {
    @Test
    fun allTapGesturesUpdateDepthProgress() {
        for (gesture in listOf(
            Gesture.HomeButton,
            Gesture.DoubleTap,
            Gesture.LongPress,
            Gesture.TapSearchBar,
        )) {
            val state = createState(gesture)
            assertEquals(1f, state.currentProgress, 0f)

            state.setProgress(0.4f)
            assertEquals(0.4f, state.currentZOffset, 0f)
            assertEquals(0.4f, state.currentProgress, 0f)
            assertEquals(Offset.Zero, state.currentOffset)

            state.setProgress(0f)
            assertEquals(0f, state.currentProgress, 0f)
        }
    }

    @Test
    fun homeButtonPredictiveBackCanBePartiallyCompleted() {
        val state = createState(Gesture.HomeButton)

        state.onPredictiveBack(0.5f)

        assertTrue(state.currentProgress > 0f)
        assertTrue(state.currentProgress < 1f)
    }

    @Test
    fun restoredAppLaunchPageResetsBeforeHomeIsHandled() = runBlocking {
        val home = TestComponent()
        val launch = TestComponent(resetOnResume = true)
        val state = createState(Gesture.HomeButton, home, launch)

        // A newly restored scaffold has no pause duration. It must still clear the splash.
        assertTrue(state.resetOnResumeIfNeeded())

        assertNull(state.currentGesture)
        assertNull(state.currentComponent)
        assertEquals(0f, state.currentProgress, 0f)
        assertEquals(0f, state.currentZOffset, 0f)
        assertFalse(state.isSettledOnSecondaryPage)
        assertFalse(state.isLocked)
        assertEquals(1, home.activations)
        assertEquals(1, launch.dismissals)
        assertEquals(0, launch.activations)

        // Repeated resume cannot relaunch the app, and swipe input works immediately.
        assertFalse(state.resetOnResumeIfNeeded())
        state.onDragStarted()
        state.onDrag(Offset(0f, -100f))
        assertEquals(Gesture.SwipeUp, state.currentGesture)
        assertTrue(state.currentProgress > 0f)
    }

    @Test
    fun swipeAppLaunchPageAlsoResetsOnResume() = runBlocking {
        val state = createState(
            Gesture.SwipeUp,
            secondary = TestComponent(resetOnResume = true),
        )

        assertTrue(state.resetOnResumeIfNeeded())
        assertNull(state.currentGesture)
        assertEquals(Offset.Zero, state.currentOffset)
        assertFalse(state.isSettledOnSecondaryPage)
    }

    @Test
    fun ordinarySecondaryPageIsPreservedByImmediateResumeCheck() = runBlocking {
        val state = createState(Gesture.HomeButton)

        assertFalse(state.resetOnResumeIfNeeded())

        assertEquals(Gesture.HomeButton, state.currentGesture)
        assertEquals(1f, state.currentProgress, 0f)
        assertTrue(state.isSettledOnSecondaryPage)
    }

    @Test
    fun homePageDoesNotConsumeHomeButtonAction() = runBlocking {
        val state = createState(null)

        assertFalse(state.resetOnResumeIfNeeded())
        assertNull(state.currentGesture)
        assertEquals(0f, state.currentProgress, 0f)
    }

    @Test
    fun swipeProgressStillUsesHorizontalOffset() {
        val state = createState(Gesture.SwipeLeft)

        state.setProgress(0.4f)

        assertEquals(Offset(-400f, 0f), state.currentOffset)
        assertEquals(0f, state.currentZOffset, 0f)
        assertEquals(0.4f, state.currentProgress, 0f)
    }

    private fun createState(
        initialGesture: Gesture?,
        home: ScaffoldComponent = TestComponent(),
        secondary: ScaffoldComponent = TestComponent(),
    ): LauncherScaffoldState {
        val tap = ScaffoldGesture(secondary, ScaffoldAnimation.ZoomIn)
        val swipe = ScaffoldGesture(secondary, ScaffoldAnimation.Push)
        return LauncherScaffoldState(
            config = ScaffoldConfiguration(
                homeComponent = home,
                searchComponent = SearchComponent(),
                homeButton = tap,
                doubleTap = tap,
                longPress = tap,
                swipeUp = swipe,
                swipeLeft = swipe,
                backgroundColor = Color.Black,
            ),
            size = Size(1000f, 2000f),
            touchSlop = 10f,
            rubberbandThreshold = 64f,
            velocityThreshold = 125f,
            maxSearchBarOffset = 128f,
            onHapticFeedback = {},
            initialGesture = initialGesture,
        )
    }

    private class TestComponent(
        override val resetOnResume: Boolean = false,
    ) : ScaffoldComponent() {
        var activations = 0
        var dismissals = 0

        @Composable
        override fun Component(
            modifier: Modifier,
            insets: PaddingValues,
            state: LauncherScaffoldState,
        ) = Unit

        override suspend fun onActivate(state: LauncherScaffoldState) {
            super.onActivate(state)
            activations++
        }

        override suspend fun onDismiss(state: LauncherScaffoldState) {
            super.onDismiss(state)
            dismissals++
        }
    }
}
