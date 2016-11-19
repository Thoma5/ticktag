package io.ticktag.util

import java.io.Closeable

public inline fun <T : AutoCloseable, R> T.tryw(block: (T) -> R): R {
    return Closeable { this.close() }.use({ block.invoke(this) })
}
