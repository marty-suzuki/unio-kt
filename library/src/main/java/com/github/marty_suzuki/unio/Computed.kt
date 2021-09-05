package com.github.marty_suzuki.unio

class Computed<Element>(
    internal val getter: () -> Element
) {
    val value: Element
        get() = getter()

    constructor(element: Element) : this({ element })

    constructor(computed: Computed<Element>) : this(computed.getter)
}