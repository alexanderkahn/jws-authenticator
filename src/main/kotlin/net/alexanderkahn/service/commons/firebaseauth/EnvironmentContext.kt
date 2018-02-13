package net.alexanderkahn.service.commons.firebaseauth

import org.springframework.core.env.Environment

fun Environment.isBypassTokenProfileActive(): Boolean {
    return activeProfiles.contains("bypassToken")
}