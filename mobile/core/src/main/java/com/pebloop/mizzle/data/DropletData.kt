package com.pebloop.mizzle.data

import java.io.Serializable

class DropletData: Serializable {
    // Meta
    var name: String = "My droplet"

    // content
    var entities: Array<EntityData> = Array(0) { i: Int -> EntityData() }

    fun addEntity(): EntityData {
        val entity: EntityData = EntityData()
        entities = entities.plus(entity)
        return entity
    }

}
