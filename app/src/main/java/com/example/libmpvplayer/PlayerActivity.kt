package com.example.libmpvplayer

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.SurfaceHolder
import android.view.View
import android.view.WindowManager
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.example.libmpvplayer.databinding.ActivityPlayerBinding
import dev.jdtech.mpv.MPVLib
import java.util.concurrent.TimeUnit

/**
 * Fullscreen player activity powered by libmpv.
 */
class PlayerActivity : AppCompatActivity(), SurfaceHolder.Callback, MPVLib.EventObserver {

    private lateinit var binding: ActivityPlayerBinding
    private var mpv: MPVLib? = null
    private var isPlaying = false
    private var duration = 0.0
    private var userSeeking = false

    private val handler = Handler(Looper.getMainLooper())
    private val updateProgress = object : Runnable {
        override fun run() {
            if (!userSeeking) {
                updateTimeDisplay()
            }
            handler.postDelayed(this, 500)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.surfaceView.holder.addCallback(this)

        setupControls()

        // Create libmpv instance
        mpv = MPVLib.create(this) ?: run {
            finish()
            return
        }

        // Basic options before init
        mpv?.apply {
            setOptionString("vo", "gpu")
            setOptionString("gpu-context", "android")
            setOptionString("hwdec", "auto")
            setOptionString("ao", "audiotrack,opensles")
            setOptionString("force-window", "no")
            setOptionString("idle", "once")
            setOptionString("keep-open", "yes")
            init()
            addObserver(this@PlayerActivity)
            // Observe useful properties
            observeProperty("time-pos", MPVLib.MpvFormat.MPV_FORMAT_DOUBLE)
            observeProperty("duration", MPVLib.MpvFormat.MPV_FORMAT_DOUBLE)
            observeProperty("pause", MPVLib.MpvFormat.MPV_FORMAT_FLAG)
        }
    }

    private fun setupControls() {
        binding.btnPlayPause.setOnClickListener {
            togglePlayPause()
        }
        binding.btnStop.setOnClickListener {
            finish()
        }
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser && duration > 0) {
                    val pos = progress / 1000.0 * duration
                    binding.timeText.text = "${formatTime(pos)} / ${formatTime(duration)}"
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                userSeeking = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                userSeeking = false
                val progress = seekBar?.progress ?: 0
                if (duration > 0) {
                    val pos = progress / 1000.0 * duration
                    mpv?.command(arrayOf("seek", pos.toString(), "absolute"))
                }
            }
        })

        // Tap surface to toggle controls
        binding.surfaceView.setOnClickListener {
            binding.controls.visibility =
                if (binding.controls.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }
    }

    private fun togglePlayPause() {
        val paused = mpv?.getPropertyBoolean("pause") ?: true
        mpv?.setPropertyBoolean("pause", !paused)
        isPlaying = paused
        updatePlayPauseIcon()
    }

    private fun updatePlayPauseIcon() {
        binding.btnPlayPause.setImageResource(
            if (isPlaying) android.R.drawable.ic_media_pause
            else android.R.drawable.ic_media_play
        )
    }

    private fun updateTimeDisplay() {
        val pos = mpv?.getPropertyDouble("time-pos") ?: 0.0
        if (duration <= 0) {
            duration = mpv?.getPropertyDouble("duration") ?: 0.0
        }
        if (duration > 0) {
            binding.seekBar.progress = ((pos / duration) * 1000).toInt().coerceIn(0, 1000)
        }
        binding.timeText.text = "${formatTime(pos)} / ${formatTime(duration)}"
    }

    private fun formatTime(seconds: Double): String {
        val total = seconds.toLong().coerceAtLeast(0)
        val h = TimeUnit.SECONDS.toHours(total)
        val m = TimeUnit.SECONDS.toMinutes(total) % 60
        val s = total % 60
        return if (h > 0) String.format("%d:%02d:%02d", h, m, s)
        else String.format("%02d:%02d", m, s)
    }

    private fun loadMedia(uri: Uri) {
        val path = when (uri.scheme) {
            "content" -> {
                uri.toString()
            }
            "file" -> uri.path ?: uri.toString()
            else -> uri.toString()
        }
        mpv?.command(arrayOf("loadfile", path))
        isPlaying = true
        updatePlayPauseIcon()
        handler.post(updateProgress)
    }

    // ---- SurfaceHolder.Callback ----
    override fun surfaceCreated(holder: SurfaceHolder) {
        mpv?.attachSurface(holder.surface)
        // Load media after surface is ready
        intent?.data?.let { loadMedia(it) }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        mpv?.setPropertyInt("android-surface-width", width)
        mpv?.setPropertyInt("android-surface-height", height)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        mpv?.detachSurface()
    }

    // ---- MPVLib.EventObserver ----
    override fun eventProperty(property: String) {}
    override fun eventProperty(property: String, value: Long) {}
    override fun eventProperty(property: String, value: Double) {
        when (property) {
            "time-pos" -> { /* handled by progress runnable */ }
            "duration" -> duration = value
        }
    }

    override fun eventProperty(property: String, value: Boolean) {
        if (property == "pause") {
            isPlaying = !value
            runOnUiThread { updatePlayPauseIcon() }
        }
    }

    override fun eventProperty(property: String, value: String) {}
    override fun event(eventId: Int) {
        when (eventId) {
            MPVLib.MpvEvent.MPV_EVENT_END_FILE -> {
                runOnUiThread {
                    isPlaying = false
                    updatePlayPauseIcon()
                }
            }
            MPVLib.MpvEvent.MPV_EVENT_FILE_LOADED -> {
                runOnUiThread {
                    duration = mpv?.getPropertyDouble("duration") ?: 0.0
                    updateTimeDisplay()
                }
            }
        }
    }

    override fun onDestroy() {
        handler.removeCallbacks(updateProgress)
        mpv?.removeObserver(this)
        mpv?.destroy()
        mpv = null
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
    }
}
