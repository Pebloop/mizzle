package com.pebloop.mizzle.android.components.displays

object ComponentDisplays {
    val list: Array<ComponentDisplay<*>> = arrayOf(
        Vector2Display(),
        IntDisplay(),
        ColorDisplay(),
        FloatDisplay(),
        BooleanDisplay()
    )

    fun getById(id: String): ComponentDisplay<*>? {
        for (item in list) {
            if (item.getId() == id) {
                return item
            }
        }
        return null
    }
}
