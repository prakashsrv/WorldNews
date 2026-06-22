package com.example.worldnews.data.summarization
//Common contract for every on-device summarization engine.
interface ArticleSummarizer {

    val provider: Provider


    suspend fun summarize(
        text: String,
        options: SummaryOptions = SummaryOptions(),
    ): SummaryResult
}


