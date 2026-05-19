package com.software.financetracker.core.error

sealed interface DataError {
    enum class Local : DataError { DISK_FULL, NOT_FOUND, UNKNOWN }
}
