package com.example.worldnews

import com.example.worldnews.data.mapper.toEntity
import com.example.worldnews.data.model.Article
import com.example.worldnews.data.model.Source
import junit.framework.TestCase.assertEquals
import org.junit.Test

class ArticleMapperTest {


    @Test
    fun `toEntity maps source name correctly`() {

        val article = Article(
            source = Source(id = "2131", name = "BBC NEWS"),
            title = "sdad",
            description = "asdad",
            author = "adsasdasd",
            publishedAt = "1231231",
            content = "asdasda",
            url = "dsadadas"
        )

        val entity = article.toEntity()

        assertEquals("BBC NEWS", entity.source)



    }

}