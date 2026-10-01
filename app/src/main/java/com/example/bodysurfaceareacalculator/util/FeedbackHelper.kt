package com.example.bodysurfaceareacalculator.util

import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class FeedbackHelper(private val context: Context) {

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    private val audioManager: AudioManager? by lazy {
        context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    }

    fun triggerLightHaptic() {
        try {
            vibrator?.let { v ->
                if (v.hasVibrator()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        v.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
                    } else {
                        @Suppress("DEPRECATION")
                        v.vibrate(20)
                    }
                }
            }
        } catch (_: Exception) {
            // Silently ignore if permission or device restriction
        }
    }

    fun triggerWarningHaptic() {
        try {
            vibrator?.let { v ->
                if (v.hasVibrator()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        v.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK))
                    } else {
                        @Suppress("DEPRECATION")
                        v.vibrate(longArrayOf(0, 40, 60, 40), -1)
                    }
                }
            }
        } catch (_: Exception) {
            // Silently ignore
        }
    }

    fun playSubtleSound() {
        try {
            audioManager?.playSoundEffect(AudioManager.FX_KEY_CLICK, 0.4f)
        } catch (_: Exception) {
            // Silently ignore
        }
    }
}
