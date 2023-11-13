package com.atssystem.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ClauseList(
    @Json(name = "risky_clauses")
    val riskyClauses:List<Clause>
)

@JsonClass(generateAdapter = true)
data class Clause(
    @Json(name = "summary")
    val summary: String,
    @Json(name = "clause")
    val clause: String,
    @Json(name = "description")
    val description: String
)