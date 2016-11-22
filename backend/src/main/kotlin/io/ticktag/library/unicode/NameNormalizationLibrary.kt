package io.ticktag.library.unicode

interface NameNormalizationLibrary {
    fun normalize(name: String): String
}
