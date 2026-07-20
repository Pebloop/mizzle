package com.pebloop.mizzle.data.components.data

import java.io.Serializable


@Suppress("UNCHECKED_CAST")
class DataComponent<T>(var data: T, val displayId: String = ""): Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
