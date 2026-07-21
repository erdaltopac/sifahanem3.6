package com.hazerfen.sifahane.security

import com.hazerfen.sifahane.data.UserProfile
import java.security.MessageDigest

object UserRoles {
    const val ADMIN = "ADMIN"
    const val STANDARD = "STANDARD"
    const val ADMIN_PERMISSIONS = "ALL"
    const val STANDARD_PERMISSIONS =
        "OWN_DATA,MEDICATIONS,MEASUREMENTS,REPORTS,EXPORT_BACKUP"

    fun isAdmin(profile: UserProfile?): Boolean =
        profile?.accountEnabled == true && profile.role == ADMIN

    fun can(profile: UserProfile?, permission: String): Boolean {
        if (profile?.accountEnabled != true) return false
        if (profile.role == ADMIN || profile.permissionsCsv == ADMIN_PERMISSIONS) return true
        return profile.permissionsCsv.split(',').map(String::trim)
            .any { it.equals(permission, ignoreCase = true) }
    }
}

object AdminCredentialHasher {
    fun hash(profileId: Long, pin: String): String =
        SecureCredentialKdf.hash(pin, "ADMIN_PROFILE:$profileId")

    fun verify(profile: UserProfile, pin: String): Boolean {
        val stored = profile.adminPinHash ?: return false
        return if (SecureCredentialKdf.isModern(stored)) {
            SecureCredentialKdf.verify(stored, pin, "ADMIN_PROFILE:${profile.id}")
        } else {
            MessageDigest.isEqual(stored.toByteArray(), legacyHash(profile.id, pin).toByteArray())
        }
    }

    fun needsUpgrade(profile: UserProfile): Boolean =
        profile.adminPinHash?.let { !SecureCredentialKdf.isModern(it) } == true

    private fun legacyHash(profileId: Long, pin: String): String {
        val raw = "SIFAHANE|ADMIN_PROFILE|$profileId|$pin"
        return MessageDigest.getInstance("SHA-256")
            .digest(raw.toByteArray())
            .joinToString("") { "%02x".format(it) }
    }
}
