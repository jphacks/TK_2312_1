package com.atssystem

fun getCurrentUnixTime(): Long {
    return System.currentTimeMillis() / 1000L
}