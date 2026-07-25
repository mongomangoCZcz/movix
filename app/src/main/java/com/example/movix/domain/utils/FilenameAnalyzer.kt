package com.example.movix.domain.utils

import java.util.Locale

data class FileMetadata(
    val quality: String,
    val codec: String,
    val languages: String
)

object FilenameAnalyzer {

    fun analyze(filename: String): FileMetadata {
        val text = filename.lowercase(Locale.ROOT)
        
        val quality = when {
            text.contains("4k") || text.contains("2160p") || text.contains("uhd") -> "4K UHD"
            text.contains("1080p") || text.contains("fhd") -> "FHD 1080"
            text.contains("720p") || text.contains("hd") -> "HD 720"
            else -> "SD"
        }

        val codec = when {
            text.contains("hevc") || text.contains("x265") || text.contains("h265") -> "HEVC"
            text.contains("x264") || text.contains("h264") -> "H264"
            text.contains("avi") || text.contains("xvid") -> "XviD"
            text.contains("av1") -> "AV1"
            else -> "H264"
        }

        val langs = mutableListOf<String>()
        if (text.contains("cz") || text.contains("cesky") || text.contains("dabing")) {
            langs.add("CZ")
        }
        if (text.contains("en") || text.contains("eng")) {
            langs.add("EN")
        }
        if (text.contains("tit") || text.contains("titulky") || text.contains("subs")) {
            langs.add("Tit")
        }

        val langStr = if (langs.isNotEmpty()) langs.joinToString("+") else "CZ/EN"
        
        return FileMetadata(quality, codec, langStr)
    }
}
