package com.atssystem.http

import com.atssystem.model.Clause
import com.atssystem.model.ClauseList
import com.atssystem.model.GptResponse
import okhttp3.Call
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatGptService {
    @POST("v1/chat/completions")
    suspend fun getRiskyClauses(
        @Body requestBody: RequestBody
    ): Response<GptResponse>
}
