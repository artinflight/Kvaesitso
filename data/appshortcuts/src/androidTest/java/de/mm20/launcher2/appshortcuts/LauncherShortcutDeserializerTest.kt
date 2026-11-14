package de.mm20.launcher2.appshortcuts

import android.content.pm.LauncherApps
import android.content.pm.ShortcutInfo
import android.os.UserHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LauncherShortcutDeserializerTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val serialized = """{"packagename":"android","id":"retained-site","user":-1}"""

    @Test
    fun missingHostPermissionRetainsShortcutWithoutQuerying() = runBlocking {
        assertRetained(access(permission = false) { error("Must not query without permission") })
    }

    @Test
    fun shortcutPinnedOnlyByAnotherLauncherIsRetained() = runBlocking {
        assertRetained(access { emptyList() })
    }

    @Test
    fun nullQueryResultRetainsShortcut() = runBlocking {
        assertRetained(access { null })
    }

    @Test
    fun permissionRevokedDuringLookupRetainsShortcut() = runBlocking {
        assertRetained(access { throw SecurityException("Permission changed") })
    }

    @Test
    fun temporarilyLockedUserRetainsShortcut() = runBlocking {
        assertRetained(access { throw IllegalStateException("User locked") })
    }

    @Test
    fun removedSourceAppCanStillBeCleanedUp() = runBlocking {
        val data = JSONObject(serialized)
            .put("packagename", context.packageName + ".absent.fixture").toString()
        val deserializer = LauncherShortcutDeserializer(context, access { emptyList() })
        assertNull(deserializer.deserialize(data))
    }

    private suspend fun assertRetained(api: ShortcutAccess) {
        val shortcut = LauncherShortcutDeserializer(context, api).deserialize(serialized)!!
        assertTrue(shortcut is UnavailableShortcut)
        assertEquals("shortcut://android/retained-site", shortcut.key)
        val saved = JSONObject(shortcut.getSerializer().serialize(shortcut)!!)
        assertEquals("android", saved.getString("packagename"))
        assertEquals("retained-site", saved.getString("id"))
        assertEquals(-1L, saved.getLong("user"))
    }

    private fun access(
        permission: Boolean = true,
        query: () -> List<ShortcutInfo>?,
    ) = object : ShortcutAccess {
        override fun hasHostPermission() = permission
        override fun getShortcuts(querySpec: LauncherApps.ShortcutQuery, user: UserHandle) = query()
    }
}
