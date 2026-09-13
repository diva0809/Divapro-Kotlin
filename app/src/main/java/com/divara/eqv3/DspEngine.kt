package com.divara.eqv3

import android.content.Context
import android.media.audiofx.Equalizer
import android.media.audiofx.BassBoost
import android.media.audiofx.Virtualizer

class DspEngine {
        var equalizer: Equalizer? = null
            private var bassBoost: BassBoost? = null
                private var virtualizer: Virtualizer? = null
                    
                        val presetList = mutableListOf<String>()

                            fun initEqualizer(context: Context, audioSessionId: Int) {
                                        release()
                                                try {
                                                                equalizer = Equalizer(0, audioSessionId).apply { enabled = true }
                                                                            bassBoost = BassBoost(0, audioSessionId).apply { enabled = true }
                                                                                        virtualizer = Virtualizer(0, audioSessionId).apply { enabled = true }
                                                                                                    
                                                                                                                presetList.clear()
                                                                                                                            equalizer?.let { eq ->
                                                                                                                                            for (i in 0 until eq.numberOfPresets) {
                                                                                                                                                                    presetList.add(eq.getPresetName(i.toShort()))
                                                                                                                                            }
                                                                                                                            }
                                                } catch (e: Exception) {
                                                                e.printStackTrace()
                                                }
                            }

                                fun getNumberOfBands(): Short = equalizer?.numberOfBands ?: 0
                                    fun getBandLevelRange(): ShortArray = equalizer?.bandLevelRange ?: shortArrayOf(-1500, 1500)
                                        fun getCenterFreq(band: Short): Int = equalizer?.getCenterFreq(band) ?: 0
                                            fun getBandLevel(band: Short): Short = equalizer?.getBandLevel(band) ?: 0

                                                fun setBandLevel(band: Short, level: Short) {
                                                            try { equalizer?.setBandLevel(band, level) } catch (e: Exception) { e.printStackTrace() }
                                                }

                                                    fun usePreset(presetIndex: Short) {
                                                                try { equalizer?.usePreset(presetIndex) } catch (e: Exception) { e.printStackTrace() }
                                                    }

                                                        fun setBassBoostStrength(strength: Short) {
                                                                    try {
                                                                                    if (bassBoost?.strengthSupported == true) {
                                                                                                        bassBoost?.setStrength(strength)
                                                                                    }
                                                                    } catch (e: Exception) { e.printStackTrace() }
                                                        }

                                                            fun setVirtualizerStrength(strength: Short) {
                                                                        try {
                                                                                        if (virtualizer?.strengthSupported == true) {
                                                                                                            virtualizer?.setStrength(strength)
                                                                                        }
                                                                        } catch (e: Exception) { e.printStackTrace() }
                                                            }

                                                                fun release() {
                                                                            equalizer?.release(); equalizer = null
                                                                                    bassBoost?.release(); bassBoost = null
                                                                                            virtualizer?.release(); virtualizer = null
                                                                }
}package
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
                                                                                                                                            }}
                                                }
                            }
}