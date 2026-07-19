package com.pebloop.mizzle.data

import java.io.Serializable
import java.util.UUID

class DropletData: Serializable {
    // Meta
    val id: String = UUID.randomUUID().toString()
    var name: String = "My droplet"
    var isPublic: Boolean = false

    // content
    var entities: Array<EntityData> = Array(0) { i: Int -> EntityData() }

    fun addEntity(): EntityData {
        val entity: EntityData = EntityData()
        entities = entities.plus(entity)
        return entity
    }

}
