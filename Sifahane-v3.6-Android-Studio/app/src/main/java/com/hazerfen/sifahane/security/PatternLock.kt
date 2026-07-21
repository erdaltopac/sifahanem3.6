package com.hazerfen.sifahane.security

import android.content.Context
import java.security.MessageDigest

object PatternStore {
    private const val PREF = "sifahane_profile_security"

    private fun patternKey(profileId: Long) = "pattern_hash_$profileId"
    private fun failureKey(profileId: Long) = "failures_$profileId"
    private fun lockoutKey(profileId: Long) = "lockout_until_epoch_$profileId"
    private fun context(profileId: Long) = "PROFILE_PATTERN:$profileId"

    fun hasPattern(context: Context, profileId: Long): Boolean =
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .contains(patternKey(profileId))

    fun save(context: Context, profileId: Long, pattern: List<Int>) {
        require(pattern.size >= 4)
        val encoded = pattern.joinToString("-")
        val saved = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit()
            .putString(patternKey(profileId), SecureCredentialKdf.hash(encoded, context(profileId)))
            .remove(failureKey(profileId))
            .remove(lockoutKey(profileId))
            .commit()
        check(saved) { "Kullanıcı deseni kaydedilemedi." }
    }

    fun matchesStoredPattern(context: Context, profileId: Long, pattern: List<Int>): Boolean {
        val prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        val stored = prefs.getString(patternKey(profileId), null) ?: return false
        val encoded = pattern.joinToString("-")
        val valid = if (SecureCredentialKdf.isModern(stored)) {
            SecureCredentialKdf.verify(stored, encoded, context(profileId))
        } else {
            MessageDigest.isEqual(stored.toByteArray(), legacyHash(profileId, pattern).toByteArray())
        }
        if (valid && !SecureCredentialKdf.isModern(stored)) {
            prefs.edit().putString(
                patternKey(profileId),
                SecureCredentialKdf.hash(encoded, context(profileId))
            ).apply()
        }
        return valid
    }

    fun verify(context: Context, profileId: Long, pattern: List<Int>): Boolean {
        if (remainingLockoutMillis(context, profileId) > 0) return false

        val prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        val stored = prefs.getString(patternKey(profileId), null) ?: return false
        val encoded = pattern.joinToString("-")
        val correct = if (SecureCredentialKdf.isModern(stored)) {
            SecureCredentialKdf.verify(stored, encoded, context(profileId))
        } else {
            MessageDigest.isEqual(stored.toByteArray(), legacyHash(profileId, pattern).toByteArray())
        }

        if (correct) {
            val editor = prefs.edit()
                .remove(failureKey(profileId))
                .remove(lockoutKey(profileId))
            if (!SecureCredentialKdf.isModern(stored)) {
                editor.putString(patternKey(profileId), SecureCredentialKdf.hash(encoded, context(profileId)))
            }
            editor.apply()
            return true
        }

        recordFailure(prefs, failureKey(profileId), lockoutKey(profileId))
        return false
    }

    fun remainingLockoutMillis(context: Context, profileId: Long): Long {
        val until = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getLong(lockoutKey(profileId), 0L)
        return (until - System.currentTimeMillis()).coerceAtLeast(0L)
    }

    fun clear(context: Context, profileId: Long) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit()
            .remove(patternKey(profileId))
            .remove(failureKey(profileId))
            .remove(lockoutKey(profileId))
            .apply()
    }

    private fun legacyHash(profileId: Long, pattern: List<Int>): String {
        val raw = "SIFAHANE|PROFILE|$profileId|" + pattern.joinToString("-")
        return MessageDigest.getInstance("SHA-256")
            .digest(raw.toByteArray())
            .joinToString("") { "%02x".format(it) }
    }
}

object AdminPinStore {
    private const val PREF = "sifahane_admin_security"
    private const val PIN_HASH = "admin_pin_hash"
    private const val INITIALIZED = "admin_pin_initialized"
    private const val LAST_VERSION_CODE = "last_admin_version_code"
    private const val FAILURES = "admin_pin_failures"
    private const val LOCKOUT_UNTIL = "admin_pin_lockout_until_epoch"
    private const val KDF_CONTEXT = "GLOBAL_ADMIN_PIN"

    fun prepareForVersion(context: Context, versionCode: Int) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit()
            .putInt(LAST_VERSION_CODE, versionCode)
            .apply()
    }

    fun requiresInitialSetup(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        return !prefs.getBoolean(INITIALIZED, false) || prefs.getString(PIN_HASH, null).isNullOrBlank()
    }

    fun verify(context: Context, pin: String): Boolean {
        if (remainingLockoutMillis(context) > 0) return false
        val prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        if (!prefs.getBoolean(INITIALIZED, false)) return false
        val stored = prefs.getString(PIN_HASH, null) ?: return false
        val correct = if (SecureCredentialKdf.isModern(stored)) {
            SecureCredentialKdf.verify(stored, pin, KDF_CONTEXT)
        } else {
            MessageDigest.isEqual(stored.toByteArray(), legacyHash(pin).toByteArray())
        }

        if (correct) {
            val editor = prefs.edit().remove(FAILURES).remove(LOCKOUT_UNTIL)
            if (!SecureCredentialKdf.isModern(stored)) {
                editor.putString(PIN_HASH, SecureCredentialKdf.hash(pin, KDF_CONTEXT))
            }
            editor.apply()
            return true
        }

        recordFailure(prefs, FAILURES, LOCKOUT_UNTIL)
        return false
    }

    fun remainingLockoutMillis(context: Context): Long {
        val until = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getLong(LOCKOUT_UNTIL, 0L)
        return (until - System.currentTimeMillis()).coerceAtLeast(0L)
    }

    fun saveNewPin(context: Context, pin: String) {
        require(pin.length in 4..12 && pin.all(Char::isDigit))
        val saved = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit()
            .putString(PIN_HASH, SecureCredentialKdf.hash(pin, KDF_CONTEXT))
            .putBoolean(INITIALIZED, true)
            .remove(FAILURES)
            .remove(LOCKOUT_UNTIL)
            .commit()
        check(saved) { "Yeni yönetici şifresi kaydedilemedi." }
    }

    private fun legacyHash(pin: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(("SIFAHANE|ADMIN|" + pin).toByteArray())
            .joinToString("") { "%02x".format(it) }
}

private fun recordFailure(
    prefs: android.content.SharedPreferences,
    failureKey: String,
    lockoutKey: String
) {
    val failures = prefs.getInt(failureKey, 0) + 1
    val delay = PersistentLockoutPolicy.delayMillis(failures)
    val editor = prefs.edit().putInt(failureKey, failures)
    if (delay > 0L) editor.putLong(lockoutKey, System.currentTimeMillis() + delay)
    editor.apply()
}
