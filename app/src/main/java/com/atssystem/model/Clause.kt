package com.atssystem.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GptResponse(
    @Json(name = "choices")
    val list: List<Choice>
)

@JsonClass(generateAdapter = true)
data class Choice(
    @Json(name = "message")
    val message: Message?
)
@JsonClass(generateAdapter = true)
data class Message(
    @Json(name = "content")
    val content: String?
)

@JsonClass(generateAdapter = true)
data class ClauseList(
    @Json(name = "risky_clauses")
    val riskyClauses:List<Clause>?
)

@JsonClass(generateAdapter = true)
data class SingleClause(
    @Json(name = "risky_clauses")
    val riskyClause: Clause?
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