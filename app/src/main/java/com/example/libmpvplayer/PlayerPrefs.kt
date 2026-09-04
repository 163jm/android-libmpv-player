package com.example.libmpvplayer

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ActivityInfo

/**
 * 播放相关偏好设置。
 */
object PlayerPrefs {
    private const val NAME = "player_settings"

    const val KEY_HWDEC = "hwdec"
    const val KEY_ORIENTATION = "orientation"
    const val KEY_KEEP_OPEN = "keep_open"
    const val KEY_LOOP = "loop"
    const val KEY_SEEK_EXACT = "seek_exact"
    const val KEY_DEINTERLACE = "deinterlace"
    const val KEY_CACHE = "cache"
    const val KEY_VOLUME = "volume"

    const val HWDEC_AUTO = "auto"
    const val HWDEC_YES = "yes"
    const val HWDEC_NO = "no"
    const val HWDEC_MEDIACODEC = "mediacodec"
    const val HWDEC_MEDIACODEC_COPY = "mediacodec-copy"

    const val ORIENT_SENSOR = "sensor"
    const val ORIENT_LANDSCAPE = "landscape"
    const val ORIENT_PORTRAIT = "portrait"
    const val ORIENT_REVERSE_LANDSCAPE = "reverse_landscape"

    private fun prefs(ctx: Context): SharedPreferences =
        ctx.getSharedPreferences(NAME, Context.MODE_PRIVATE)

    fun hwdec(ctx: Context): String =
        prefs(ctx).getString(KEY_HWDEC, HWDEC_AUTO) ?: HWDEC_AUTO

    fun setHwdec(ctx: Context, v: String) =
        prefs(ctx).edit().putString(KEY_HWDEC, v).apply()

    fun orientation(ctx: Context): String =
        prefs(ctx).getString(KEY_ORIENTATION, ORIENT_SENSOR) ?: ORIENT_SENSOR

    fun setOrientation(ctx: Context, v: String) =
        prefs(ctx).edit().putString(KEY_ORIENTATION, v).apply()

    fun activityOrientation(ctx: Context): Int = when (orientation(ctx)) {
        ORIENT_LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        ORIENT_PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        ORIENT_REVERSE_LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
        else -> ActivityInfo.SCREEN_ORIENTATION_SENSOR
    }

    fun keepOpen(ctx: Context): Boolean = prefs(ctx).getBoolean(KEY_KEEP_OPEN, true)
    fun setKeepOpen(ctx: Context, v: Boolean) =
        prefs(ctx).edit().putBoolean(KEY_KEEP_OPEN, v).apply()

    fun loop(ctx: Context): Boolean = prefs(ctx).getBoolean(KEY_LOOP, false)
    fun setLoop(ctx: Context, v: Boolean) =
        prefs(ctx).edit().putBoolean(KEY_LOOP, v).apply()

    fun seekExact(ctx: Context): Boolean = prefs(ctx).getBoolean(KEY_SEEK_EXACT, false)
    fun setSeekExact(ctx: Context, v: Boolean) =
        prefs(ctx).edit().putBoolean(KEY_SEEK_EXACT, v).apply()

    fun deinterlace(ctx: Context): Boolean = prefs(ctx).getBoolean(KEY_DEINTERLACE, false)
    fun setDeinterlace(ctx: Context, v: Boolean) =
        prefs(ctx).edit().putBoolean(KEY_DEINTERLACE, v).apply()

    fun cache(ctx: Context): Boolean = prefs(ctx).getBoolean(KEY_CACHE, true)
    fun setCache(ctx: Context, v: Boolean) =
        prefs(ctx).edit().putBoolean(KEY_CACHE, v).apply()

    fun volume(ctx: Context): Int = prefs(ctx).getInt(KEY_VOLUME, 100)
    fun setVolume(ctx: Context, v: Int) =
        prefs(ctx).edit().putInt(KEY_VOLUME, v.coerceIn(0, 150)).apply()
}
