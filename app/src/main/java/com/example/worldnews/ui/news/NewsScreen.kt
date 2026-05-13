package com.example.worldnews.ui.news

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.keepScreenOn
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.worldnews.data.model.Article
import com.example.worldnews.ui.base.UiState

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NewsScreen(viewModel: NewsViewModel = hiltViewModel()
) {

    val state by viewModel.newsState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize()

    ) { innerPadding ->

        when(state){

            is UiState.Loading ->{

                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center,

                )
                {
                    CircularProgressIndicator()
                }

            }
            is UiState.Success<*> -> {

                val article = (state as UiState.Success).data.articles

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    items(article){
                        article -> NewsItem(article = article)

                    }
                }

            }
            is UiState.Error -> {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (state as UiState.Error).message ?: "Something went wrong",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

            }
            is UiState.Initial -> {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (state as UiState.Error).message ?: "INIT.....",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

            }

        }



    }







}


@Composable
fun NewsItem(
    article: Article
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(12.dp)
        ) {

            AsyncImage(
                model = article.urlToImage,
                contentDescription = article.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = article.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            article.description?.let {

                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

            }

            Text(
                text = article.source.name,
                style = MaterialTheme.typography.labelMedium
            )

        }

    }

}

