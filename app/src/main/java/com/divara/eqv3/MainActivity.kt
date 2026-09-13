package com.divara.eqv3

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.media.audiofx.AudioEffect
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

        private val dspEngine = DspEngine()
            private val seekBars = mutableListOf<SeekBar>()
                private var isUpdatingUiFromPreset = false
                    
                        private lateinit var sharedPrefs: SharedPreferences
                            private val customPresetsMap = mutableMapOf<String, String>()

                                private val audioSessionReceiver = object : BroadcastReceiver() {
                                            override fun onReceive(context: Context, intent: Intent) {
                                                            val sessionId = intent.getIntExtra(AudioEffect.EXTRA_AUDIO_SESSION, -1)
                                                                        if (sessionId != -1 && intent.action == AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION) {
                                                                                            dspEngine.initEqualizer(applicationContext, sessionId)
                                                                                                            refreshFullUi()
                                                                        }
                                            }
                                }

                                    override fun onCreate(savedInstanceState: Bundle?) {
                                                super.onCreate(savedInstanceState)
                                                        setContentView(R.layout.activity_main)
                                                                
                                                                        sharedPrefs = getSharedPreferences("EqualizerCustomPresets", Context.MODE_PRIVATE)
                                                                                loadSavedCustomPresets()

                                                                                        val filter = IntentFilter(AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION)
                                                                                                registerReceiver(audioSessionReceiver, filter)

                                                                                                        dspEngine.initEqualizer(this, 0)
                                                                                                                refreshFullUi()
                                                                                                                        setupExtraEffects()
                                                                                                                                setupSaveButton()
                                    }

                                        private fun refreshFullUi() {
                                                    setupPresetSpinner()
                                                            setupEqualizerSliders()
                                        }

                                            private fun setupExtraEffects() {
                                                        findViewById<SeekBar>(R.id.bassSeekBar).setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                                                                        override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                                                                                            if (fromUser) dspEngine.setBassBoostStrength(progress.toShort())
                                                                        }
                                                                                    override fun onStartTrackingTouch(sb: SeekBar?) {}
                                                                                                override fun onStopTrackingTouch(sb: SeekBar?) {}
                                                        })

                                                                findViewById<SeekBar>(R.id.virtualizerSeekBar).setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                                                                                override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                                                                                                    if (fromUser) dspEngine.setVirtualizerStrength(progress.toShort())
                                                                                }
                                                                                            override fun onStartTrackingTouch(sb: SeekBar?) {}
                                                                                                        override fun onStopTrackingTouch(sb: SeekBar?) {}
                                                                })
                                            }

                                                private fun setupPresetSpinner() {
                                                            val spinner = findViewById<Spinner>(R.id.presetSpinner)
                                                                    val fullList = mutableListOf<String>()
                                                                            fullList.addAll(dspEngine.presetList)
                                                                                    fullList.add("Custom Manual")
                                                                                            fullList.addAll(customPresetsMap.keys)

                                                                                                    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, fullList)
                                                                                                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                                                                                                    spinner.adapter = adapter

                                                                                                                            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                                                                                                                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                                                                                                                                                val selectedName = fullList[position]
                                                                                                                                                                                isUpdatingUiFromPreset = true

                                                                                                                                                                                                if (position < dspEngine.presetList.size) {
                                                                                                                                                                                                                        dspEngine.usePreset(position.toShort())
                                                                                                                                                                                                                                            updateSlidersVisual()
                                                                                                                                                                                                } else if (customPresetsMap.containsKey(selectedName)) {
                                                                                                                                                                                                                        applyCustomPresetData(customPresetsMap[selectedName])
                                                                                                                                                                                                }
                                                                                                                                                                                                                isUpdatingUiFromPreset = false
                                                                                                                                            }
                                                                                                                                                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                                                                                                                            }
                                                }

                                                    private fun setupEqualizerSliders() {
                                                                val container = findViewById<LinearLayout>(R.id.eqSliderContainer)
                                                                        container.removeAllViews()
                                                                                seekBars.clear()

                                                                                        val bands = dspEngine.getNumberOfBands()
                                                                                                val range = dspEngine.getBandLevelRange()
                                                                                                        if (bands <= 0) return

                                                                                                                val minLevel = range[0]
                                                                                                                        val maxProgress = (range[1] - range[0]).toInt()

                                                                                                                                for (i in 0 until bands) {
                                                                                                                                                val bandIndex = i.toShort()
                                                                                                                                                            val freqTextView = TextView(this).apply {
                                                                                                                                                                                val centerFreqHz = dspEngine.getCenterFreq(bandIndex) / 1000
                                                                                                                                                                                                text = if (centerFreqHz >= 1000) "${centerFreqHz / 1000} kHz" else "$centerFreqHz Hz"
                                                                                                                                                                                                                setPadding(0, 10, 0, 2)
                                                                                                                                                            }
                                                                                                                                                                        container.addView(freqTextView)

                                                                                                                                                                                    val seekBar = SeekBar(this).apply {
                                                                                                                                                                                                        max = maxProgress
                                                                                                                                                                                                                        progress = (dspEngine.getBandLevel(bandIndex) - minLevel).toInt()
                                                                                                                                                                                                                                        tag = bandIndex

                                                                                                                                                                                                                                                        setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                                                                                                                                                                                                                                                                                override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                                                                                                                                                                                                                                                                                                            if (fromUser) {
                                                                                                                                                                                                                                                                                                                                            val currentBand = sb?.tag as Short
                                                                                                                                                                                                                                                                                                                                                                        val newLevel = (progress + minLevel).toShort()
                                                                                                                                                                                                                                                                                                                                                                                                    dspEngine.setBandLevel(currentBand, newLevel)

                                                                                                                                                                                                                                                                                                                                                                                                                                if (!isUpdatingUiFromPreset) {
                                                                                                                                                                                                                                                                                                                                                                                                                                                                    changeSpinnerToCustomManual()
                                                                                                                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                    override fun onStartTrackingTouch(sb: SeekBar?) {}
                                                                                                                                                                                                                                                                                                                        override fun onStopTrackingTouch(sb: SeekBar?) {}
                                                                                                                                                                                                                                                        })
                                                                                                                                                                                    }
                                                                                                                                                                                                container.addView(seekBar)
                                                                                                                                                                                                            seekBars.add(seekBar)
                                                                                                                                }
                                                    }

                                                        private fun updateSlidersVisual() {
                                                                    val minLevel = dspEngine.getBandLevelRange()[0]
                                                                            for (seekBar in seekBars) {
                                                                                            val bandIndex = seekBar.tag as Short
                                                                                                        seekBar.progress = (dspEngine.getBandLevel(bandIndex) - minLevel).toInt()
                                                                            }
                                                        }

                                                            private fun changeSpinnerToCustomManual() {
                                                                        val spinner = findViewById<Spinner>(R.id.presetSpinner)
                                                                                for (i in 0 until spinner.adapter.count) {
                                                                                                if (spinner.adapter.getItem(i).toString() == "Custom Manual") {
                                                                                                                    spinner.setSelection(i)
                                                                                                                                    break
                                                                                                }
                                                                                }
                                                            }

                                                                private fun setupSaveButton() {
                                                                            val input = findViewById<EditText>(R.id.customPresetNameInput)
                                                                                    val button = findViewById<Button>(R.id.savePresetButton)

                                                                                            button.setOnClickListener {
                                                                                                            val name = input.text.toString().trim()
                                                                                                                        if (name.isEmpty() || name == "Custom Manual") {
                                                                                                                                            Toast.makeText(this, "Nama preset tidak valid!", Toast.LENGTH_SHORT).show()
                                                                                                                                                            return@setOnClickListener
                                                                                                                        }

                                                                                                                                    val dataBuilder = StringBuilder()
                                                                                                                                                val bands = dspEngine.getNumberOfBands()
                                                                                                                                                            for (i in 0 until bands) {
                                                                                                                                                                                dataBuilder.append(dspEngine.getBandLevel(i.toShort())).append(",")
                                                                                                                                                            }

                                                                                                                                                                        sharedPrefs.edit().putString(name, dataBuilder.toString()).apply()
                                                                                                                                                                                    customPresetsMap[name] = dataBuilder.toString()
                                                                                                                                                                                                
                                                                                                                                                                                                            input.text.clear()
                                                                                                                                                                                                                        Toast.makeText(this, "Preset '$name' disimpan!", Toast.LENGTH_SHORT).show()
                                                                                                                                                                                                                                    setupPresetSpinner()
                                                                                            }
                                                                }

                                                                    private fun loadSavedCustomPresets() {
                                                                                customPresetsMap.clear()
                                                                                        val allEntries = sharedPrefs.all
                                                                                                for ((key, value) in allEntries) {
                                                                                                                if (value is String) customPresetsMap[key] = value
                                                                                                }
                                                                    }

                                                                        private fun applyCustomPresetData(dataStr: String?) {
                                                                                    if (dataStr.isNullOrEmpty()) return
                                                                                            try {
                                                                                                            val levels = dataStr.split(",").filter { it.isNotEmpty() }
                                                                                                                        for (i in levels.indices) {
                                                                                                                                            val bandIndex = i.toShort()
                                                                                                                                                            if (bandIndex < dspEngine.getNumberOfBands()) {
                                                                                                                                                                                    dspEngine.setBandLevel(bandIndex, levels[i].toShort())
                                                                                                                                                            }
                                                                                                                        }
                                                                                                                                    updateSlidersVisual()
                                                                                            } catch (e: Exception) { e.printStackTrace() }
                                                                        }

                                                                            override fun onDestroy() {
                                                                                        super.onDestroy()
                                                                                                try { unregisterReceiver(audioSessionReceiver) } catch (e: Exception) {}
                                                                                                        dspEngine.release()
                                                                            }
}
                                                                            }
                                                                                                                                                            }
                                                                                                                        }
                                                                                            }
                                                                        }
                                                                                                }
                                                                    }
                                                                                                                                                            }
                                                                                                                        }
                                                                                            }
                                                                }
                                                                                                }
                                                                                }
                                                            }
                                                                            }
                                                        }
                                                                                                                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                        })
                                                                                                                                                                                    }
                                                                                                                                                            }
                                                                                                                                }
                                                    }
                                                                                                                                                                                                }
                                                                                                                                                                                                }
                                                                                                                                            }
                                                                                                                            }
                                                }
                                                                                }
                                                                })
                                                                        }
                                                        })
                                            }
                                        }
                                    }
                                                                        }
                                            }
                                }
}