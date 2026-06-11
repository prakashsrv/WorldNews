package com.example.worldnews.ui.news

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class temp {


    @Composable
    fun CounterScreen() {

        var counter = remember { mutableStateOf(0) }

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {

            Text(counter.toString())
            Button(onClick = {
                counter.value++
            }) { Text("Increment") }

        }


    }

    @Preview(showBackground = true)
    @Composable
    fun CounterScreenPreview() {
        CounterScreen()
    }
}

