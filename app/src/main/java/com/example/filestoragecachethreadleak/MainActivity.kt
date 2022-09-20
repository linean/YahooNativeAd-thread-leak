package com.example.filestoragecachethreadleak

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.yahoo.ads.support.FileStorageCache
import com.yahoo.ads.support.StorageCache
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.io.File

/**
 * Expected result: loading ads should not increase threadsCount.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val threadsCount = countThreads()

        lifecycleScope.launch {
            while (true) {
                delay(10)
                loadAd()
            }
        }

        setContent {
            val count by threadsCount.collectAsState(initial = 0)

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Text(text = "Threads count $count")
            }
        }
    }
}

/**
 * To reproduce OutOfMemory exception we need to leak a lof of threads.
 * It would take a lof of time to load many real YahooNativeAds so instead we only copied interesting part.
 * Every new YahooNativeAd creates FileStorageCache in its constructor
 * and later calls downloadAndCacheFile when there are files available.
 *
 * On production, the application can live for a long time and display many ads.
 * The longer it runs, the more threads it will leak and consume more memory.
 *
 * Note that destroying the ad deletes the file cache, but that does not release the leaked threads.
 */
private fun loadAd() {
    // decompiled YahooNativeAd.class:122
    val fileCache = FileStorageCache(StorageCache(File("cache")))
    // decompiled YahooNativeAd.class:412
    fileCache.downloadAndCacheFile("url") { _, _ -> }
}

private fun countThreads(): Flow<Int> = flow {
    while (true) {
        emit(Thread.getAllStackTraces().size)
        delay(1000)
    }
}
