package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.StarterDuaData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Noor — Islamic Duas", appName)
    }

    @Test
    fun `verify starter categories and duas`() {
        assertEquals(20, StarterDuaData.categories.size)
        assertTrue(StarterDuaData.verifiedDuas.isNotEmpty())
        // Ensure every dua has authentic arabic text and reference
        StarterDuaData.verifiedDuas.forEach { dua ->
            assertTrue(dua.arabicText.isNotBlank())
            assertTrue(dua.reference.isNotBlank())
            assertTrue(dua.meaningBn.isNotBlank())
            assertEquals("VERIFIED", dua.verificationStatus)
        }
    }
}
