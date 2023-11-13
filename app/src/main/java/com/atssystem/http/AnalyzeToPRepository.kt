package com.atssystem.http

import android.util.Log
import androidx.compose.ui.Modifier
import com.atssystem.model.Clause
import com.atssystem.model.ClauseList
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup


class AnalyzeToPRepository() {

    suspend fun analyzeToP(packageName: String): Result<RiskyClausesResponse> {
        val privacyLinkResponse = makeGetToPRequest(packageName)
        when(privacyLinkResponse) {
            is Result.Success<ToPLinkResponse> -> {
                val rawTextResponse =  parsePrivacySite(privacyLinkResponse.data.url)
                if(rawTextResponse is Result.Error) {
                    return rawTextResponse
                }

                return getRiskyClause()
            }
            else -> {
                return Result.Error(Exception("Couldn't find terms of policy link in Google Play Store"))
            }
        }
    }

    fun parsePrivacySite(url: String): Result<RawToPResponse> {
        val rawText = "this is a fake one"
        val response = RawToPResponse(rawText = rawText)
        return Result.Success(response)
    }

    suspend fun getRiskyClause(): Result<RiskyClausesResponse> {
        val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
        val jsonAdapter:JsonAdapter<ClauseList> = moshi.adapter(ClauseList::class.java)
        val exampleJson = """
            {
              "risky_clauses": [
                {
                  "clause": "第4条 本規約の変更、終了および解除",
                  "summary": "利用規約の変更に同意しない場合、サービスの使用停止や規約の解除が可能",
                  "description": "利用者が利用規約の変更に同意しない場合、サービスの使用停止や規約の解除が可能とされており、利用者にとって不利な変更が行われた場合でも、それに同意しなければならないという点で危険性がある。また、利用者が規定に違反した場合にも、事前の通知なしにサービスの一部または全部の使用停止や規約の解除が行われる可能性がある。"
                },
                {
                  "clause": "第5条 免責および責任の制限",
                  "summary": "サービスの利用に関連する損害に対する責任を限定し、免責する内容が含まれている",
                  "description": "利用者が本システムや本システム上のデータ等へのアクセス、使用、不正使用、もしくは使用できないこと、または本規約が終了もしくは解除されたことに関連して生じる損害に対して、任天堂の責任を限定し、免責する内容が含まれている。また、利用者が実際に支払った金額を限度として、任天堂の損害賠償責任が制限されている。"
                }
              ]
            }

        """.trimIndent()

        val list: List<Clause> = jsonAdapter.fromJson(exampleJson)?.riskyClauses ?: listOf()
        return Result.Success(RiskyClausesResponse(list))
    }

    suspend fun makeGetToPRequest(packageName: String): Result<ToPLinkResponse> {
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

