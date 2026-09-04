package com.example.libmpvplayer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.libmpvplayer.databinding.ActivityMainBinding

/**
 * 本机视频浏览：申请媒体权限 → 扫描 MediaStore 持久化存储 → 列表展示 → 点击播放。
 * 无文件选择器。
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: VideoAdapter

    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        if (results.values.any { it }) {
            scanVideos()
        } else {
            binding.swipeRefresh.isRefreshing = false
            binding.progressBar.visibility = View.GONE
            binding.emptyText.visibility = View.VISIBLE
            binding.emptyText.setText(R.string.permission_required)
            Toast.makeText(this, R.string.permission_required, Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.inflateMenu(R.menu.menu_main)
        binding.toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_settings) {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            } else false
        }

        adapter = VideoAdapter { item ->
            playUri(item.contentUri)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        binding.btnRefresh.setOnClickListener { ensurePermissionAndScan() }
        binding.swipeRefresh.setOnRefreshListener { ensurePermissionAndScan() }

        binding.btnOpenUrl.setOnClickListener {
            val visible = binding.urlPanel.visibility == View.VISIBLE
            binding.urlPanel.visibility = if (visible) View.GONE else View.VISIBLE
        }
        binding.btnPlayUrl.setOnClickListener {
            val url = binding.urlEditText.text?.toString()?.trim()
            if (!url.isNullOrEmpty()) {
                playUri(Uri.parse(url))
            } else {
                Toast.makeText(this, R.string.enter_url, Toast.LENGTH_SHORT).show()
            }
        }

        intent?.data?.let { playUri(it) }

        if (hasStoragePermission()) {
            scanVideos()
        } else {
            binding.emptyText.visibility = View.VISIBLE
            binding.emptyText.setText(R.string.permission_hint)
            requestStoragePermission()
        }
    }

    private fun ensurePermissionAndScan() {
        if (hasStoragePermission()) {
            scanVideos()
        } else {
            requestStoragePermission()
        }
    }

    private fun scanVideos() {
        binding.progressBar.visibility = View.VISIBLE
        binding.swipeRefresh.isRefreshing = true
        binding.emptyText.visibility = View.GONE
        binding.btnRefresh.isEnabled = false
        binding.statusText.setText(R.string.scanning)

        Thread {
            val list = VideoScanner.scan(this)
            runOnUiThread {
                binding.progressBar.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
                binding.btnRefresh.isEnabled = true
                adapter.submit(list)
                binding.statusText.text = getString(R.string.video_count, list.size)
                if (list.isEmpty()) {
                    binding.emptyText.visibility = View.VISIBLE
                    binding.emptyText.setText(R.string.no_videos)
                } else {
                    binding.emptyText.visibility = View.GONE
                }
            }
        }.start()
    }

    private fun hasStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) ==
                PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestStoragePermission() {
        val perms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_VIDEO)
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        requestPermission.launch(perms)
    }

    private fun playUri(uri: Uri) {
        val intent = Intent(this, PlayerActivity::class.java).apply {
            data = uri
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        if (uri.scheme == "content") {
            try {
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: SecurityException) {
            }
        }
        startActivity(intent)
    }
}
