package com.pebloop.mizzle.data

import java.io.Serializable

data class ResourceData(
    var name: String = "",
    var path: String = "",
    var regionX: Int = 0,
    var regionY: Int = 0,
    var regionWidth: Int = -1,
    var regionHeight: Int = -1
): Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
