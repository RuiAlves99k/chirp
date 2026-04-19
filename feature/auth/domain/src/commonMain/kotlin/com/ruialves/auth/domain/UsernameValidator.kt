package com.ruialves.auth.domain

object UsernameValidator {

    fun validate(username: String): Boolean {
        return username.length in 3..20 && username.none { it.isWhitespace() }
    }
}
