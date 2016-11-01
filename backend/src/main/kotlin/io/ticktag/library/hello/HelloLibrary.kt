package io.ticktag.library.hello

import io.ticktag.TicktagLibrary

@TicktagLibrary
open class HelloLibrary {
    open fun hello(name: String): String {
        return "Hello $name!"
    }
}
