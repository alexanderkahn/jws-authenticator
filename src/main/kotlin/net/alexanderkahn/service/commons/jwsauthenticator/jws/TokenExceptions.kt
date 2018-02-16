package net.alexanderkahn.service.commons.jwsauthenticator.jws

class InvalidJwsTokenException(message: String) : Exception(message)

class UnableToVerifyJwsTokenException(message: String) : Exception(message)