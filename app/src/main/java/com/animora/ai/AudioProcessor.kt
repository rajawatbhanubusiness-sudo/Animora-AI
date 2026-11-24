package com.animora.ai

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import kotlin.math.abs

data class AudioAnalysis(
    val durationMs: Long,
    val averageEnergy: Double,
    val peakEnergy: Double,
    val beatTimestampsMs: List<Long>
)

object AudioProcessor {

    fun getAudioDurationMs(
        context: Context,
        audioUri: Uri
    ): Long? {
        val retriever = MediaMetadataRetriever()

        return try {
            retriever.setDataSource(context, audioUri)

            retriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_DURATION
            )?.toLongOrNull()?.takeIf { it > 0L }

        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            retriever.release()
        }
    }

    fun analyzeAudioEnergy(
        samples: ShortArray,
        sampleRate: Int,
        durationMs: Long
    ): AudioAnalysis {

        if (samples.isEmpty() || sampleRate <= 0 || durationMs <= 0) {
            return AudioAnalysis(durationMs, 0.0, 0.0, emptyList())
        }

        val windowSize = (sampleRate / 10).coerceAtLeast(1)
        val energies = mutableListOf<Double>()

        var index = 0

        while (index < samples.size) {
            val end = minOf(index + windowSize, samples.size)
            var total = 0.0

            for (i in index until end) {
                val normalized = samples[i].toDouble() / 32768.0
                total += normalized * normalized
            }

            val energy = kotlin.math.sqrt(total / (end - index))
            energies.add(energy)

            index = end
        }

        val average = energies.average()
        val peak = energies.maxOrNull() ?: 0.0

        val beats = mutableListOf<Long>()

        for (i in 1 until energies.size - 1) {
            if (
                energies[i] > energies[i - 1] * 1.5 &&
                energies[i] > energies[i + 1] &&
                energies[i] > average * 1.3
            ) {
                val timestamp = i * 100L

                if (
                    beats.isEmpty() ||
                    timestamp - beats.last() >= 250L
                ) {
                    beats.add(timestamp)
                }
            }
        }

        return AudioAnalysis(
            durationMs = durationMs,
            averageEnergy = average,
            peakEnergy = peak,
            beatTimestampsMs = beats
        )
    }
}
