package com.atssystem.http

import android.util.Log
import com.atssystem.BuildConfig
import com.atssystem.model.Clause
import com.atssystem.model.ClauseList
import com.atssystem.model.SingleClause
import com.fasterxml.jackson.databind.ObjectMapper
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit


class AnalyzeToPRepository() {

    val prefixPrompt = "サービスを利用する際には多くの場合利用規約に同意することが求められますが、人々は利用規約をよく読まずに同意しています。その行為には、利用規約に書かれた利用者にとって不利な条項にも同意してしまうという潜在的な危険性をはらんでいます。あなたは人々をそのような危険から守る有能なアシスタントです。利用規約から危険性を孕む条項を抜き出し、risky_clausesというkeyのvalueとして、それが何条何項かをclause、その危険性の要約をsummary、その危険性の詳細をdescriptionというkeyにして列挙して、JSON形式で私に教えて下さい。もし一つしか危険を孕む条項がなくても、JSONは配列形式を保ってください。もちろんそのような危険のない利用規約もありますので、その場合にはrisky_clausesは使わずに、noneというkeyだけを作って、そこに”null”と入れてください。本当に危険だと思うものだけ教えて下さい。"

    suspend fun analyzeToP(packageName: String): Result<RiskyClausesResponse> {
        when(val privacyLinkResponse = makeGetToPRequest(packageName)) {
            is Result.Success<ToPLinkResponse> -> {
                return when(val rawTextResponse =  parsePrivacySite(privacyLinkResponse.data.url)) {
                    is Result.Success<RawToPResponse> -> {
                        getRiskyClause(rawTextResponse.data.rawText)
                    }

                    else -> {
                        Result.Error(Exception("No privacy policy"))
                    }
                }
            }
            else -> {
                return Result.Error(Exception("Couldn't find terms of policy link in Google Play Store"))
            }
        }
    }

    private suspend fun parsePrivacySite(url: String): Result<RawToPResponse> {
        return withContext(Dispatchers.IO) {
            val document = Jsoup.connect(url).get()
            var target: Elements = document.select("body main")

            // main要素が見つからない場合、div要素内でテキストに「。」を含む要素を検索
            if (target.text().isBlank()) {
                target = document.select("body > div:has(*:contains(。))")
            }

            // さらに見つからない場合、articleまたはpre要素を検索
            if (target.text().isBlank()) {
                target = document.select("article, pre")
            }

            if (target != null) {
                return@withContext Result.Success<RawToPResponse>(RawToPResponse(target.text()))
            } else {
                return@withContext Result.Error(Exception("privacy policy is not found."))
            }
        }
    }

    private suspend fun getRiskyClause(rawText: String): Result<RiskyClausesResponse> {
        val bearerToken: String = BuildConfig.OPENAI_APIKEY
        val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
        val client = OkHttpClient.Builder()
            .addInterceptor(Interceptor { chain ->
                val original: Request = chain.request()
                val requestBuilder: Request.Builder = original.newBuilder()
                    .header("Authorization", "Bearer $bearerToken")
                val request: Request = requestBuilder.build()
                chain.proceed(request)
            }
        )
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openai.com/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .build()

        val apiService = retrofit.create(ChatGptService::class.java)
        return withContext(Dispatchers.IO) {
            val rawTextList = splitJapaneseText(rawText, 1500)
            val jobList = rawTextList.map {
                async { askGpt(it, apiService, moshi) }
            }
            val results = jobList.awaitAll()
            val joinedList = results.flatten()
            return@withContext Result.Success(RiskyClausesResponse(joinedList))
        }
    }

    private suspend fun askGpt(
        text: String,
        apiService: ChatGptService,
        moshi: Moshi): List<Clause> {
        val rawJson = """
            {
          "model": "gpt-3.5-turbo-1106",
          "messages": [ 
            {
              "role": "system",
              "content": "$prefixPrompt"
            },
            {
              "role": "user",
              "content": "JSON形式で返答してください。以下が利用規約です。" 
            },
            {
              "role": "user",
              "content": "$text"
            }
          ],
          "temperature": 0.1,
          "max_tokens": 4096,
          "response_format": {"type": "json_object"}
        }
        """
        val requestBody = RequestBody.create(MediaType.get("application/json; charset=utf-8"), rawJson)
        val response = apiService.getRiskyClauses(requestBody)
        if(response.isSuccessful) {
            val content = response.body()?.list?.get(0)?.message?.content

            val objectMapper = ObjectMapper()
            val jsonNode = objectMapper.readTree(content)

            val riskyClauseNode = jsonNode["risky_clauses"]
            if (riskyClauseNode.isArray) {
                val adapter = moshi.adapter(ClauseList::class.java)
                val clauseList = adapter.fromJson(content) ?: ClauseList(listOf())
                Log.d("response", content ?: "nothing")
                return clauseList.riskyClauses ?: listOf()
            } else {
                val adapter = moshi.adapter(SingleClause::class.java)
                val clause = adapter.fromJson(content) ?: Clause("","","")
                return listOf(clause as Clause)
            }

        } else {
            return listOf()
        }
    }

    private suspend fun makeGetToPRequest(packageName: String): Result<ToPLinkResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val document = Jsoup
                    .connect("https://play.google.com/store/apps/details")
                    .data("id", packageName)
                    .data("hl", "en")
                    .data("gl", "US")
                    .get()
                    .html()

                val soup = Jsoup.parse(document)
                val targetElements = soup.select(".Si6A0c.RrSxVb")

                for (element in targetElements) {
                    if ("Privacy Policy" in element.attr("aria-label")) {
                        val link = element.attr("href")
                        return@withContext Result.Success(ToPLinkResponse(link))
                    }
                }

                return@withContext Result.Error(Exception("Privacy Policy link not found"))
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    private fun splitJapaneseText(text: String, maxLength: Int): List<String> {
        val regex = Regex("""第(\d+)条""")
        val matches = regex.findAll(text)
        val indices = matches.map { it.range.first }.toList() + listOf(text.length)

        val result = mutableListOf<String>()
        var currentChunk = StringBuilder()

        for ((start, end) in indices.windowed(2)) {
            val articleText = text.substring(start, end).trim()
            if (currentChunk.length + articleText.length <= maxLength) {
                currentChunk.append(articleText)
            } else {
                result.add(currentChunk.toString())
                currentChunk = StringBuilder(articleText)
            }
        }

        if (currentChunk.isNotEmpty()) {
            result.add(currentChunk.toString())
        }

        return result
    }
}


data class ToPLinkResponse(
    val url: String
)

data class RawToPResponse(
    val rawText: String
)

data class RiskyClausesResponse(
    val clauseList: List<Clause>
)

