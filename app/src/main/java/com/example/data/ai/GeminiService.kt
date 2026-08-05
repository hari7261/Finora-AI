package com.example.data.ai

import com.example.BuildConfig
import com.example.data.local.AiMessageEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class AiAdvisorResponse(
    val summary: String,
    val reason: String,
    val suggestion: String,
    val confidencePercent: Int,
    val actionText: String
)

object GeminiService {
    private const val MODEL_GENERAL = "gemini-3.5-flash"
    private const val MODEL_COMPLEX = "gemini-3.1-pro-preview"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun queryFinoraAi(
        userPrompt: String,
        financialContextSummary: String,
        history: List<AiMessageEntity> = emptyList()
    ): AiAdvisorResponse = withContext(Dispatchers.IO) {
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Exception) { "" }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext generateDynamicSmartResponse(userPrompt, financialContextSummary)
        }

        try {
            val modelToUse = if (userPrompt.lowercase().contains("predict") || 
                                 userPrompt.lowercase().contains("forecast") || 
                                 userPrompt.lowercase().contains("complex") || 
                                 userPrompt.lowercase().contains("calculate")) {
                MODEL_COMPLEX
            } else {
                MODEL_GENERAL
            }

            val baseUrl = "https://generativelanguage.googleapis.com/v1beta/models/$modelToUse:generateContent"

            val systemInstructionText = """
                You are Finora AI, an elite personal CFO and financial advisor built into the Finora AI app.
                Provide actionable, trustworthy, and empathetic financial guidance based strictly on the user's real financial profile and context provided below.
                All monetary amounts MUST be formatted in Indian Rupees (₹).
                Always analyze the user's actual income, expenses, transactions, goals, debts, budgets, and investments provided in the context.
                If the user has 0 income or 0 transactions recorded, guide them kindly to add their first transaction, goal, or income in the app.
                
                Always respond in strict valid JSON format with this exact structure:
                {
                  "summary": "Clear, direct 1-sentence answer addressing user's query directly.",
                  "reason": "Data-backed explanation using the user's actual numbers (₹).",
                  "suggestion": "Specific, actionable next step with clear rupee targets (₹).",
                  "confidencePercent": 95,
                  "actionText": "Short 2-3 word CTA button text"
                }
                Do not include markdown code fence formatting like ```json or ```. Return plain JSON only.
            """.trimIndent()

            val contentsArray = JSONArray()

            // Pass multi-turn conversation history
            val recentHistory = history.takeLast(6)
            for (msg in recentHistory) {
                val role = if (msg.isUser) "user" else "model"
                val textContent = if (msg.isUser) msg.prompt else "${msg.summary} ${msg.reason} ${msg.suggestion}".trim()
                if (textContent.isNotBlank()) {
                    contentsArray.put(JSONObject().apply {
                        put("role", role)
                        put("parts", JSONArray().apply {
                            put(JSONObject().put("text", textContent))
                        })
                    })
                }
            }

            // Current turn prompt with user profile context
            val currentTurnPrompt = """
                [USER FINANCIAL DATA CONTEXT]
                $financialContextSummary

                [CURRENT USER QUESTION]
                $userPrompt
            """.trimIndent()

            contentsArray.put(JSONObject().apply {
                put("role", "user")
                put("parts", JSONArray().apply {
                    put(JSONObject().put("text", currentTurnPrompt))
                })
            })

            val jsonPayload = JSONObject().apply {
                put("contents", contentsArray)
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", systemInstructionText))
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.2)
                })
            }

            val request = Request.Builder()
                .url("$baseUrl?key=$apiKey")
                .post(jsonPayload.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseBodyString = response.body?.string() ?: ""

            if (response.isSuccessful && responseBodyString.isNotBlank()) {
                val parsedText = extractTextFromGeminiResponse(responseBodyString)
                val parsedJson = parseJsonResponse(parsedText)
                if (parsedJson != null) {
                    return@withContext parsedJson
                }
            }

            return@withContext generateDynamicSmartResponse(userPrompt, financialContextSummary)
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext generateDynamicSmartResponse(userPrompt, financialContextSummary)
        }
    }

    private fun extractTextFromGeminiResponse(jsonString: String): String {
        return try {
            val root = JSONObject(jsonString)
            val candidates = root.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val firstPart = parts?.optJSONObject(0)
            firstPart?.optString("text") ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    private fun parseJsonResponse(rawText: String): AiAdvisorResponse? {
        return try {
            val cleanText = rawText.replace("```json", "").replace("```", "").trim()
            val json = JSONObject(cleanText)
            AiAdvisorResponse(
                summary = json.optString("summary", "Analysis completed."),
                reason = json.optString("reason", "Based on your recorded transactions."),
                suggestion = json.optString("suggestion", "Review your budget priorities."),
                confidencePercent = json.optInt("confidencePercent", 90),
                actionText = json.optString("actionText", "View Plan")
            )
        } catch (e: Exception) {
            null
        }
    }

    fun generateDynamicSmartResponse(prompt: String, context: String): AiAdvisorResponse {
        val lower = prompt.lowercase()

        var income = 0.0
        var expenses = 0.0

        context.lines().forEach { line ->
            if (line.contains("Recorded Income:")) {
                income = line.substringAfter("Recorded Income: ₹").trim().toDoubleOrNull() ?: 0.0
            }
            if (line.contains("Recorded Expenses:")) {
                expenses = line.substringAfter("Recorded Expenses: ₹").trim().toDoubleOrNull() ?: 0.0
            }
        }

        val netCashFlow = income - expenses

        return when {
            income == 0.0 && expenses == 0.0 -> AiAdvisorResponse(
                summary = "You currently have no income or transactions recorded in your Finora account.",
                reason = "Your recorded income and expenses stand at ₹0.",
                suggestion = "Tap '+ Add' on the Home screen to add your income or first expense to start receiving live AI financial analysis.",
                confidencePercent = 99,
                actionText = "Add Transaction"
            )
            lower.contains("save") || lower.contains("saving") -> AiAdvisorResponse(
                summary = if (netCashFlow > 0) "Your current net monthly balance is ₹${netCashFlow.toInt()}." else "Your expenses match or exceed your income.",
                reason = "Recorded income is ₹${income.toInt()} against expenses of ₹${expenses.toInt()}.",
                suggestion = if (netCashFlow > 0) "Consider routing ₹${(netCashFlow * 0.5).toInt()} into your Emergency Reserve Goal." else "Review discretionary categories to build positive savings.",
                confidencePercent = 95,
                actionText = "Review Budget"
            )
            else -> AiAdvisorResponse(
                summary = "Based on your real records: Income is ₹${income.toInt()} and Expenses are ₹${expenses.toInt()}.",
                reason = "Your net monthly cash flow is ₹${netCashFlow.toInt()}.",
                suggestion = "Keep adding transactions and setting goals to get deeper AI projections.",
                confidencePercent = 92,
                actionText = "View Summary"
            )
        }
    }
}

