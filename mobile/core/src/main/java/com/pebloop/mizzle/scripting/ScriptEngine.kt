package com.pebloop.mizzle.scripting

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.physics.box2d.Body
import com.pebloop.mizzle.player.GameEntityInstance
import org.luaj.vm2.Globals
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.jse.JsePlatform

class ScriptEngine {
    private val globals: Globals = JsePlatform.standardGlobals()

    init {
        // Basic log function
        globals.set("print", object : org.luaj.vm2.lib.OneArgFunction() {
            override fun call(arg: LuaValue): LuaValue {
                Gdx.app.log("LUA", arg.toString())
                return LuaValue.NIL
            }
        })
    }

    fun execute(code: String, thisEntity: GameEntityInstance?, body: Body?) {
        try {
            Gdx.app.debug("LUA", "Compiling: $code")
            val chunk = globals.load(code)

            // Set 'this' context if available
            if (thisEntity != null) {
                globals.set("this", LuaEntity(thisEntity, body))
            } else {
                globals.set("this", LuaValue.NIL)
            }

            chunk.call()
        } catch (e: Exception) {
            Gdx.app.error("LUA", "Runtime Error: ${e.message}")
            e.printStackTrace()
        }
    }

    class LuaEntity(val instance: GameEntityInstance, val body: Body?) : LuaValue() {
        override fun type(): Int = TUSERDATA
        override fun typename(): String = "userdata"

        override fun get(key: LuaValue): LuaValue {
            val name = key.toString()
            return when (name) {
                "applyForce" -> object : org.luaj.vm2.lib.TwoArgFunction() {
                    override fun call(x: LuaValue, y: LuaValue): LuaValue {
                        body?.applyForceToCenter(x.tofloat(), y.tofloat(), true)
                        return LuaValue.NIL
                    }
                }
                "setLinearVelocity" -> object : org.luaj.vm2.lib.TwoArgFunction() {
                    override fun call(x: LuaValue, y: LuaValue): LuaValue {
                        body?.setLinearVelocity(x.tofloat(), y.tofloat())
                        return LuaValue.NIL
                    }
                }
                "x" -> valueOf((body?.position?.x ?: instance.x).toDouble())
                "y" -> valueOf((body?.position?.y ?: instance.y).toDouble())
                "name" -> valueOf(instance.entity.name)
                else -> super.get(key)
            }
        }

        override fun set(key: LuaValue, value: LuaValue) {
            val name = key.toString()
            when (name) {
                "x" -> {
                    val newX = value.tofloat()
                    body?.let { it.setTransform(newX, it.position.y, it.angle) }
                        ?: run { instance.x = newX }
                }
                "y" -> {
                    val newY = value.tofloat()
                    body?.let { it.setTransform(it.position.x, newY, it.angle) }
                        ?: run { instance.y = newY }
                }
                else -> super.set(key, value)
            }
        }
    }
}
