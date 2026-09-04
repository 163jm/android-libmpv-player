package com.example.libmpvplayer

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * 通过 MediaStore 扫描设备上的视频（持久化存储中的媒体库条目）。
 */
object VideoScanner {

    private val VIDEO_EXTENSIONS = setOf(
        "mp4", "mkv", "webm", "avi", "mov", "flv", "ts", "m2ts", "m4v", "3gp", "wmv", "mpg", "mpeg"
    )

    fun scan(context: Context): List<VideoItem> {
        val list = ArrayList<VideoItem>()
        val collection: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.DATE_MODIFIED,
            MediaStore.Video.Media.DATA
        )

        val sortOrder = "${MediaStore.Video.Media.DATE_MODIFIED} DESC"

        try {
            context.contentResolver.query(
                collection,
                projection,
                null,
                null,
                sortOrder
            )?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
                val durCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                val dataCol = cursor.getColumnIndex(MediaStore.Video.Media.DATA)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idCol)
                    var name = cursor.getString(nameCol) ?: "video_$id"
                    val size = cursor.getLong(sizeCol)
                    val duration = cursor.getLong(durCol)
                    val path = if (dataCol >= 0) cursor.getString(dataCol) else null

                    val ext = name.substringAfterLast('.', "").lowercase(Locale.US)
                    if (ext.isNotEmpty() && ext !in VIDEO_EXTENSIONS) {
                        if (path != null) {
                            val pathExt = path.substringAfterLast('.', "").lowercase(Locale.US)
                            if (pathExt !in VIDEO_EXTENSIONS) continue
                        }
                    }

                    val contentUri = ContentUris.withAppendedId(collection, id)
                    list.add(
                        VideoItem(
                            id = id,
                            title = name,
                            contentUri = contentUri,
                            sizeBytes = size,
                            durationMs = duration
                        )
                    )
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun formatSize(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        var v = bytes.toDouble()
        var i = 0
        while (v >= 1024 && i < units.lastIndex) {
            v /= 1024
            i++
        }
        return String.format(Locale.US, "%.1f %s", v, units[i])
    }

    fun formatDuration(ms: Long): String {
        if (ms <= 0) return "--:--"
        val totalSec = TimeUnit.MILLISECONDS.toSeconds(ms)
        val h = totalSec / 3600
        val m = (totalSec % 3600) / 60
        val s = totalSec % 60
        return if (h > 0) {
            String.format(Locale.US, "%d:%02d:%02d", h, m, s)
        } else {
            String.format(Locale.US, "%02d:%02d", m, s)
        }
    }
}
