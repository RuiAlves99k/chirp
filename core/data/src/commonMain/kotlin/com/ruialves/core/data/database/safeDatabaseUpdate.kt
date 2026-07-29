package com.ruialves.core.data.database

import androidx.sqlite.SQLiteException
import com.ruialves.core.domain.util.DataError
import com.ruialves.core.domain.util.Result

suspend inline fun <T> safeDatabaseUpdate(update: () -> T): Result<T, DataError.Local> {
    return try {
        Result.Success(update())
    } catch (_: SQLiteException){
        Result.Failure(DataError.Local.DISK_FULL)
    }
}
