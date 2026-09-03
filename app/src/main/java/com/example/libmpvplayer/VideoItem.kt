package com.example.libmpvplayer

import android.net.Uri

data class VideoItem(
    val id: Long,
    val title: String,
    val contentUri: Uri,
    val sizeBytes: Long,
    val durationMs: Long,
    val dateAddedSec: Long
)
