package net.alexanderkahn.service.commons.firebaseauth.jws.filter.config

data class UnauthenticatedPath(private val url: String) {
    val pattern = Regex(url)
}