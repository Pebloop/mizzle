package com.pebloop.mizzle.data.components

import java.io.Serializable

data class TimelineAnimationData(
    var name: String = "new_animation",
    var duration: Float = 1.0f,
    var looping: Boolean = true,
    var tracks: MutableList<TimelineTrackData> = mutableListOf()
) : Serializable

data class TimelineTrackData(
    var componentIndex: Int = 0,
    var propertyName: String = "",
    var keyframes: MutableList<TimelineKeyframeData> = mutableListOf()
) : Serializable

data class TimelineKeyframeData(
    var time: Float = 0f,
    var value: Any? = null
) : Serializable
