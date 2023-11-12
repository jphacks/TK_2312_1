package com.atssystem.http

import android.util.Log
import androidx.compose.ui.Modifier
import com.atssystem.model.Clause
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup


class AnalyzeToPRepository {

//    suspend fun analyzeToP(packageName: String): Result<RiskyClausesResponse> {
//        val privacyLinkResponse = makeGetToPRequest(packageName)
//        when(privacyLinkResponse) {
//            is Result.Success<ToPLinkResponse> -> {
//                val rawTextResponse =  parsePrivacySite(privacyLinkResponse.data.url)
//                if(rawTextResponse is Result.Error) {
//                    return rawTextResponse
//                }
//
//                return getRiskyClause()
//            }
//            else -> {
//                return Result.Error(Exception("Couldn't find terms of policy link in Google Play Store"))
//            }
//        }
//    }
//
//    fun parsePrivacySite(url: String): Result<RawToPResponse> {
//
//    }
//
//    suspend fun getRiskyClause(): Result<RiskyClausesResponse> {
//
//    }

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

