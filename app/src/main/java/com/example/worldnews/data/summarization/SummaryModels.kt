package com.example.worldnews.data.summarization

data class SummaryOptions(
    val bulletCount: Int = 3,
    val maxBulletLength: Int = 25,
)

enum class Provider {
    GEMINI_NANO,
    GEMMA_3_1B,
}


data class SummaryResult(
    val summary: String,
    val provider: Provider,
    val latencyMs: Long,
)

/** Thrown by an [ArticleSummarizer] when it cannot produce a summary. */
class SummarizationException(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause)
