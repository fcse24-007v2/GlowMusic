package com.example.glowmusic.domain.usecase

import android.graphics.Bitmap
import androidx.palette.graphics.Palette
import com.example.glowmusic.domain.model.ColorPalette

class ResolveNowPlayingColorsUseCase {
    operator fun invoke(bitmap: Bitmap?): ColorPalette {
        if (bitmap == null) return ColorPalette()

        val palette = Palette.from(bitmap).generate()

        val dominant = palette.getDominantColor(0xFF1E1B2E.toInt()).toLong() and 0xFFFFFFFFL
        val vibrant = palette.getVibrantColor(
            palette.getLightVibrantColor(palette.getMutedColor(0xFF6366F1.toInt()))
        ).toLong() and 0xFFFFFFFFL
        val muted = palette.getMutedColor(0xFF3B3B4F.toInt()).toLong() and 0xFFFFFFFFL

        return ColorPalette(
            dominantColor = dominant,
            vibrantColor = vibrant,
            mutedColor = muted,
            textPrimary = 0xFFFFFFFF,
            textSecondary = 0xFFB0B0C0,
            isDark = true
        )
    }
}
