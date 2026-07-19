package com.pebloop.mizzle.data

import com.pebloop.mizzle.data.components.Components
import com.pebloop.mizzle.data.components.RectComponent
import java.io.Serializable

class EntityData: Serializable {

    companion object {
        var id_count: Int = 0
    }

    val id: Int = id_count
    var components: Array<Component> = arrayOf()
    var name: String = "My entity"
    var transform: Transform = Transform()

    init {
        id_count += 1
        components = components.plus(Component(Components.list[0]))
    }
}
