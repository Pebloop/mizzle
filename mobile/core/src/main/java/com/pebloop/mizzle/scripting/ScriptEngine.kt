package com.pebloop.mizzle.scripting

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.physics.box2d.Body
import com.pebloop.mizzle.player.GameEntityInstance
import org.luaj.vm2.*
import org.luaj.vm2.lib.jse.JsePlatform

class ScriptEngine {
    private val globals: Globals = JsePlatform.standardGlobals()
    private val activeScripts = mutableListOf<ActiveScript>()

    private data class ActiveScript(val thread: LuaThread, var waitTimer: Float)

    init {
        // Basic log function
        globals.set("print", object : org.luaj.vm2.lib.OneArgFunction() {
            override fun call(arg: LuaValue): LuaValue {
                Gdx.app.log("LUA", arg.toString())
                return NIL
            }
        })

        // Wait function that yields execution
        globals.set("wait", object : org.luaj.vm2.lib.OneArgFunction() {
            override fun call(arg: LuaValue): LuaValue {
                // Use the coroutine.yield function from the standard library
                return globals.get("coroutine").get("yield").call(arg)
            }
        })
    }

    fun execute(code: String, thisEntity: GameEntityInstance?, body: Body?, onDestroy: () -> Unit) {
        try {
            Gdx.app.debug("LUA", "Executing script:\n$code")

            // Wrap the code in a function to pass 'this' as an argument
            val wrappedCode = "local this = ...\n$code"
            val chunk = globals.load(wrappedCode)

            // Create a Lua thread (coroutine) from the chunk
            val thread = LuaThread(globals, chunk)

            val luaEntity = if (thisEntity != null) LuaEntity(thisEntity, body, onDestroy) else LuaValue.NIL
            resumeScript(thread, luaEntity)
        } catch (e: Exception) {
            Gdx.app.error("LUA", "Compilation Error: ${e.message}")
        }
    }

    private fun resumeScript(thread: LuaThread, args: LuaValue) {
        try {
            val result = thread.resume(args)

            if (result.arg1().toboolean()) {
                // Check if the thread is still alive (yielded)
                // In Luaj 3.0, LuaThread has a public 'state' object with a 'status' int.
                // Alternatively, we can check the status via the coroutine library.
                val status = globals.get("coroutine").get("status").call(thread).toString()

                if (status != "dead") {
                    // Script yielded, presumably calling wait(n)
                    // The second return value of resume is the value passed to yield()
                    val waitTime = result.arg(2).tofloat()
                    activeScripts.add(ActiveScript(thread, waitTime))
                }
            } else {
                Gdx.app.error("LUA", "Runtime Error: ${result.arg(2)}")
            }
        } catch (e: Exception) {
            Gdx.app.error("LUA", "Execution Error: ${e.message}")
            e.printStackTrace()
        }
    }

    fun update(delta: Float) {
        val toRemove = mutableListOf<ActiveScript>()
        val toResume = mutableListOf<ActiveScript>()

        for (script in activeScripts) {
            script.waitTimer -= delta
            if (script.waitTimer <= 0) {
                toResume.add(script)
                toRemove.add(script)
            }
        }

        activeScripts.removeAll(toRemove)

        for (script in toResume) {
            resumeScript(script.thread, LuaValue.NIL)
        }
    }

    class LuaEntity(val instance: GameEntityInstance, val body: Body?, val onDestroy: () -> Unit) : LuaValue() {
        override fun type(): Int = TUSERDATA
        override fun typename(): String = "userdata"

        override fun get(key: LuaValue): LuaValue {
            return when (val name = key.toString()) {
                "applyForce" -> object : org.luaj.vm2.lib.VarArgFunction() {
                    override fun invoke(args: Varargs): Varargs {
                        // args.arg(1) is self, args.arg(2) is x, args.arg(3) is y
                        val x = args.arg(2).tofloat()
                        val y = args.arg(3).tofloat()
                        body?.applyForceToCenter(x, y, true)
                        return LuaValue.NIL
                    }
                }
                "setLinearVelocity" -> object : org.luaj.vm2.lib.VarArgFunction() {
                    override fun invoke(args: Varargs): Varargs {
                        val x = args.arg(2).tofloat()
                        val y = args.arg(3).tofloat()
                        body?.setLinearVelocity(x, y)
                        return LuaValue.NIL
                    }
                }
                "destroy" -> object : org.luaj.vm2.lib.OneArgFunction() {
                    override fun call(arg: LuaValue): LuaValue {
                        onDestroy()
                        return NIL
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
