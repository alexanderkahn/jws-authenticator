package net.alexanderkahn.service.commons.jwsauthenticator.jws.filter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import javax.servlet.http.HttpServletRequest

internal class RequestHeaderUtilsTest {

    @Test
    fun getBearerToken() {
        val expectedHeader = "Authorization"
        val expectedPrefix = "Bearer "
        val token = "arglebargle"
        val subject = mock(HttpServletRequest::class.java)
        `when`(subject.getHeader(expectedHeader)).thenReturn(expectedPrefix + token)
        assertEquals(token, subject.getBearerToken())
    }
}