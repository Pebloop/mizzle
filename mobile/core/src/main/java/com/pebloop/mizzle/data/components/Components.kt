package com.pebloop.mizzle.data.components

import com.pebloop.mizzle.data.ComponentData

object Components {
    val list: Array<ComponentData> = arrayOf(
        RectComponent(),
        CircleComponent(),
        RigidBodyComponent(),
        BoxColliderComponent(),
        CircleColliderComponent(),
        EventComponent()
    )
}
