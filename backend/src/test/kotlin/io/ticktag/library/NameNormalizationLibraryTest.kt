package io.ticktag.library

import io.ticktag.library.unicode.impl.NameNormalizationLibraryImpl
import org.junit.Test
import org.junit.Assert.*

class NameNormalizationLibraryTest {

    private val nn = NameNormalizationLibraryImpl()

    @Test
    fun `normalize should remove bad characters`() {
        assertEquals("", nn.normalize("#+~'\"`´!§$%%&/()=?^°|<> -\\"))
    }

    @Test
    fun `normalize should keep allowed characters`() {
        assertEquals("1234567890abcdefghijklmnopqrstuvwxyz_", nn.normalize("1234567890abcdefghijklmnopqrstuvwxyz_"))
    }

    @Test
    fun `normalize should normalize casing`() {
        assertEquals("abcd", nn.normalize("ABCD"))
        assertEquals("aou", nn.normalize("ÄÖÜ"))
    }

    @Test
    fun `normalize should remove accents and similar stuff`() {
        assertEquals("aaaaaaaaa", nn.normalize("aáàâäÀÁÂÄ"))
        assertEquals("iiii", nn.normalize("íìîi"))
        assertEquals("tiengviet", nn.normalize("Tiếng Việt"))  // Note the nested modifiers.
    }

    @Test
    fun `normalize should convert letter-variants to their ASCII equivalent`() {
        assertEquals("aa", nn.normalize("ÅÅ"))  // Note: These are different Unicode symbols!
    }

    @Test
    fun `normalize should remove foreign characters`() {
        // TODO: This could become an issue once the application will be internationalized.
        // In this case it must be possible to manually override the internal name of tags.
        assertEquals("", nn.normalize("русский язык"))
        assertEquals("", nn.normalize("汉语"))
        assertEquals("", nn.normalize("한국어"))
        assertEquals("", nn.normalize("عَرَبِيّ"))
    }
}