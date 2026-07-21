package com.hazerfen.sifahane

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppPreferencesTest {
    @Test fun acceptsThreeDistinctDurations() = assertTrue(validSnoozeMinutes(listOf(5, 10, 15)))
    @Test fun rejectsDuplicateDurations() = assertFalse(validSnoozeMinutes(listOf(5, 5, 15)))
    @Test fun rejectsOutOfRangeDurations() = assertFalse(validSnoozeMinutes(listOf(0, 10, 181)))
}
