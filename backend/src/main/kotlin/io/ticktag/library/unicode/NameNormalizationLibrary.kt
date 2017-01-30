package io.ticktag.library.unicode

interface NameNormalizationLibrary {
    /**
     * normalize names so user input can be compared
     * @param name any name which has to be normalized
     * @return normalized name
     */
    fun normalize(name: String): String
}
