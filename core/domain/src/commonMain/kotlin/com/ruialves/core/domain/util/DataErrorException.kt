package com.ruialves.core.domain.util

class DataErrorException(
    val error: DataError
): Exception()
