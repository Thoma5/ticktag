package io.ticktag.library.unicode.impl

import io.ticktag.TicktagLibrary
import io.ticktag.library.unicode.NameNormalizationLibrary
import java.text.Normalizer
import java.util.*

@TicktagLibrary
class NameNormalizationLibraryImpl : NameNormalizationLibrary {
    override fun normalize(name: String): String {
        // See also http://www.unicode.org/reports/tr15/
        var result = Normalizer.normalize(name, Normalizer.Form.NFKD)
        result = result.toLowerCase(Locale.ROOT)
        result = result.filter { ('a' <= it && it <= 'z') || ('0' <= it && it <= '9') || (it == '_') }
        return result
    }
}
