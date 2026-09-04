package com.example.libmpvplayer

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.libmpvplayer.databinding.ActivitySettingsBinding
import com.google.android.material.slider.Slider

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    private val hwdecValues = arrayOf(
        PlayerPrefs.HWDEC_AUTO,
        PlayerPrefs.HWDEC_YES,
        PlayerPrefs.HWDEC_MEDIACODEC,
        PlayerPrefs.HWDEC_MEDIACODEC_COPY,
        PlayerPrefs.HWDEC_NO
    )
    private val hwdecLabels by lazy {
        arrayOf(
            getString(R.string.hwdec_auto),
            getString(R.string.hwdec_yes),
            getString(R.string.hwdec_mediacodec),
            getString(R.string.hwdec_mediacodec_copy),
            getString(R.string.hwdec_no)
        )
    }

    private val orientValues = arrayOf(
        PlayerPrefs.ORIENT_SENSOR,
        PlayerPrefs.ORIENT_LANDSCAPE,
        PlayerPrefs.ORIENT_PORTRAIT,
        PlayerPrefs.ORIENT_REVERSE_LANDSCAPE
    )
    private val orientLabels by lazy {
        arrayOf(
            getString(R.string.orient_sensor),
            getString(R.string.orient_landscape),
            getString(R.string.orient_portrait),
            getString(R.string.orient_reverse_landscape)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.spinnerHwdec.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, hwdecLabels
        )
        val hw = PlayerPrefs.hwdec(this)
        binding.spinnerHwdec.setSelection(hwdecValues.indexOf(hw).coerceAtLeast(0))

        binding.spinnerOrientation.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, orientLabels
        )
        val or = PlayerPrefs.orientation(this)
        binding.spinnerOrientation.setSelection(orientValues.indexOf(or).coerceAtLeast(0))

        binding.switchKeepOpen.isChecked = PlayerPrefs.keepOpen(this)
        binding.switchLoop.isChecked = PlayerPrefs.loop(this)
        binding.switchSeekExact.isChecked = PlayerPrefs.seekExact(this)
        binding.switchDeinterlace.isChecked = PlayerPrefs.deinterlace(this)
        binding.switchCache.isChecked = PlayerPrefs.cache(this)

        val vol = PlayerPrefs.volume(this).toFloat()
        binding.sliderVolume.value = vol
        updateVolumeLabel(vol.toInt())

        binding.spinnerHwdec.onItemSelectedListener = simpleSelect {
            PlayerPrefs.setHwdec(this, hwdecValues[binding.spinnerHwdec.selectedItemPosition])
        }
        binding.spinnerOrientation.onItemSelectedListener = simpleSelect {
            PlayerPrefs.setOrientation(
                this, orientValues[binding.spinnerOrientation.selectedItemPosition]
            )
        }
        binding.switchKeepOpen.setOnCheckedChangeListener { _, c ->
            PlayerPrefs.setKeepOpen(this, c)
        }
        binding.switchLoop.setOnCheckedChangeListener { _, c ->
            PlayerPrefs.setLoop(this, c)
        }
        binding.switchSeekExact.setOnCheckedChangeListener { _, c ->
            PlayerPrefs.setSeekExact(this, c)
        }
        binding.switchDeinterlace.setOnCheckedChangeListener { _, c ->
            PlayerPrefs.setDeinterlace(this, c)
        }
        binding.switchCache.setOnCheckedChangeListener { _, c ->
            PlayerPrefs.setCache(this, c)
        }
        binding.sliderVolume.addOnChangeListener { _: Slider, value: Float, fromUser: Boolean ->
            if (fromUser) {
                val v = value.toInt()
                PlayerPrefs.setVolume(this, v)
                updateVolumeLabel(v)
            }
        }
    }

    private fun updateVolumeLabel(v: Int) {
        binding.volumeLabel.text = getString(R.string.volume_value, v)
    }

    private fun simpleSelect(onSelect: () -> Unit) =
        object : android.widget.AdapterView.OnItemSelectedListener {
            private var ready = false
            override fun onItemSelected(
                parent: android.widget.AdapterView<*>?,
                view: android.view.View?,
                position: Int,
                id: Long
            ) {
                if (!ready) {
                    ready = true
                    return
                }
                onSelect()
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }
}
