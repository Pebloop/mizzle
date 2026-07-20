package com.pebloop.mizzle.data

import java.io.Serializable
import java.util.UUID

class DropletData: Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }

    // Meta
    var id: String = UUID.randomUUID().toString()
    var name: String = "My droplet"
    var isPublic: Boolean = false

    // content
    var entities: Array<EntityData> = Array(0) { i: Int -> EntityData() }
    var textures: Array<ResourceData> = arrayOf()
    var audios: Array<ResourceData> = arrayOf()

    fun addEntity(): EntityData {
        val entity: EntityData = EntityData()
        entities = entities.plus(entity)
        return entity
    }

    fun updateFrom(other: DropletData) {
        this.name = other.name
        this.isPublic = other.isPublic
        this.entities = other.entities
        this.textures = other.textures
        this.audios = other.audios
    }

}
