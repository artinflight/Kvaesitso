package de.mm20.launcher2.ui.theme

import androidx.core.content.res.ResourcesCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import de.mm20.launcher2.themes.ThemeJson
import de.mm20.launcher2.themes.typography.FontFamily
import de.mm20.launcher2.themes.typography.FontManager
import de.mm20.launcher2.themes.typography.Typography
import de.mm20.launcher2.ui.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OutfitCompatibilityTest {
    @Test
    fun outfitSelectionSurvivesBackupSerialization() {
        val original = Typography(
            name = "Original launcher font",
            fonts = mapOf("brand" to FontFamily.Outfit, "plain" to null),
        )
        val restored = ThemeJson.decodeFromString<Typography>(ThemeJson.encodeToString(original))
        assertEquals(original, restored)
    }

    @Test
    fun bundledWeightsLoadAndBothFontsRemainSelectable() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        for (resource in listOf(
            R.font.outfit_100, R.font.outfit_200, R.font.outfit_300,
            R.font.outfit_400, R.font.outfit_500, R.font.outfit_600,
            R.font.outfit_700, R.font.outfit_800, R.font.outfit_900,
        )) {
            assertNotNull(ResourcesCompat.getFont(context, resource))
        }
        val fonts = FontManager(context).getInstalledFonts().builtIn
        assertTrue(fonts.contains(FontFamily.Outfit))
        assertTrue(fonts.any { it is FontFamily.LauncherDefault })
    }
}
