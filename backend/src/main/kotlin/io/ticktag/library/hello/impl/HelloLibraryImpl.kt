package io.ticktag.library.hello.impl

import io.ticktag.TicktagLibrary
import io.ticktag.library.hello.HelloLibrary

@TicktagLibrary
class HelloLibraryImpl : HelloLibrary {
    override fun hello(name: String): String {
        return "Hello $name!"
    }
}
