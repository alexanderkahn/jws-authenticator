package net.alexanderkahn.service.commons.firebaseauth.jws

class InvalidJwsTokenException(message: String) : Exception(message)

class UnableToVerifyJwsTokenException(message: String) : Exception(message)