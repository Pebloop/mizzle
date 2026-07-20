package com.pebloop.mizzle.data.components

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.pebloop.mizzle.data.ComponentData
import com.pebloop.mizzle.data.EntityData
import com.pebloop.mizzle.data.components.data.DataComponent

class AnimationComponent : ComponentData {
    override fun getId(): String = "ANIMATION"

    override fun getDisplayName(): String = "Animation"

    override fun getDataList(): Map<String, DataComponent<*>> {
        return mapOf(
            Pair("animations", DataComponent(ArrayList<TimelineAnimationData>(), "TIMELINE_ANIMATIONS")),
            Pair("currentAnimation", DataComponent("", "STRING")),
            Pair("stateTime", DataComponent(0f, "FLOAT")),
            Pair("isPlaying", DataComponent(true, "BOOLEAN"))
        )
    }

    override fun update(delta: Float, entity: EntityData, datas: Map<String, DataComponent<*>>) {
        val isPlaying = datas["isPlaying"]?.data as? Boolean ?: false
        if (!isPlaying) return

        val animations = datas["animations"]?.data as? ArrayList<TimelineAnimationData> ?: return
        val currentAnimationName = datas["currentAnimation"]?.data as? String ?: ""
        val stateTimeData = datas["stateTime"] as? DataComponent<Float> ?: return

        var stateTime = stateTimeData.data
        val animation = animations.find { it.name == currentAnimationName } ?: animations.firstOrNull() ?: return

        stateTime += delta
        if (animation.looping) {
            stateTime %= animation.duration
        } else {
            stateTime = Math.min(stateTime, animation.duration)
        }
        stateTimeData.data = stateTime

        // Apply tracks
        for (track in animation.tracks) {
            if (track.componentIndex < 0 || track.componentIndex >= entity.components.size) continue
            val targetComponent = entity.components[track.componentIndex]
            val value = interpolate(track, stateTime)
            if (value != null) {
                targetComponent.setData(track.propertyName, value)
            }
        }
    }

    private fun interpolate(track: TimelineTrackData, time: Float): Any? {
        if (track.keyframes.isEmpty()) return null

        val sortedKeyframes = track.keyframes.sortedBy { it.time }

        if (time <= sortedKeyframes.first().time) return sortedKeyframes.first().value
        if (time >= sortedKeyframes.last().time) return sortedKeyframes.last().value

        var start = sortedKeyframes.first()
        var end = sortedKeyframes.last()

        for (i in 0 until sortedKeyframes.size - 1) {
            if (time >= sortedKeyframes[i].time && time <= sortedKeyframes[i+1].time) {
                start = sortedKeyframes[i]
                end = sortedKeyframes[i+1]
                break
            }
        }

        val t = (time - start.time) / (end.time - start.time)

        return when {
            start.value is Float && end.value is Float -> {
                start.value as Float + t * (end.value as Float - start.value as Float)
            }
            start.value is Vector2 && end.value is Vector2 -> {
                val v1 = start.value as Vector2
                val v2 = end.value as Vector2
                Vector2(v1.x + t * (v2.x - v1.x), v1.y + t * (v2.y - v1.y))
            }
            else -> start.value // No interpolation for other types yet
        }
    }

    override fun draw(
        batch: Batch?,
        parentAlpha: Float,
        x: Float,
        y: Float,
        originX: Float,
        originY: Float,
        scaleX: Float,
        scaleY: Float,
        rotation: Float,
        datas: Map<String, DataComponent<*>>
    ) {
        // Animation component is logic only
    }

    override fun getBounds(datas: Map<String, DataComponent<*>>): Vector2 {
        return Vector2(0f, 0f)
    }
}
