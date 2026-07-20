package com.pebloop.mizzle.data

import com.pebloop.mizzle.data.components.Components
import com.pebloop.mizzle.data.components.RectComponent
import java.io.Serializable
import java.util.UUID

class EntityData: Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }

    var id: String = UUID.randomUUID().toString()
    var components: Array<Component> = arrayOf()
    var name: String = "My entity"
    var transform: Transform = Transform()

    init {
        components = components.plus(Component(Components.list[0]))
    }
}
