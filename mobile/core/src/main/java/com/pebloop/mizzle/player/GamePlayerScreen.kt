package com.pebloop.mizzle.player

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.ScreenUtils
import com.pebloop.mizzle.data.DropletData
import com.pebloop.mizzle.editor.EditorActionsExtern
import com.pebloop.mizzle.editor.EditorSkin

class GamePlayerScreen(val droplet: DropletData, val actionsExtern: EditorActionsExtern? = null): Screen {
    private val stage: Stage = Stage()
    private var closeButton: ImageButton? = null
    private var uploadButton: ImageButton? = null

    private val world: World = World(Vector2(0f, -9.8f), true)
    private val bodyMap = mutableMapOf<GameEntityInstance, Body>()
    private val PPM = 100f

    init {
        for (entity in droplet.entities) {
            val instance = GameEntityInstance(entity)
            stage.addActor(instance)

            // Physics initialization
            val rigidBody = entity.components.find { it.component.getId() == "RIGID_BODY" }
            if (rigidBody != null) {
                val isDynamic = rigidBody.getData("isDynamic") as? Boolean ?: true
                val bodyDef = BodyDef()
                bodyDef.type = if (isDynamic) BodyDef.BodyType.DynamicBody else BodyDef.BodyType.StaticBody

                bodyDef.position.set(
                    (instance.x + instance.width / 2f) / PPM,
                    (instance.y + instance.height / 2f) / PPM
                )
                bodyDef.angle = instance.rotation * MathUtils.degreesToRadians

                val body = world.createBody(bodyDef)

                for (comp in entity.components) {
                    when (comp.component.getId()) {
                        "BOX_COLLIDER" -> {
                            val size = comp.getData("size") as? Vector2 ?: Vector2(50f, 50f)
                            val shape = PolygonShape()
                            shape.setAsBox(size.x / 2f / PPM, size.y / 2f / PPM)

                            val fixtureDef = FixtureDef()
                            fixtureDef.shape = shape
                            fixtureDef.density = rigidBody.getData("mass") as? Float ?: 1.0f
                            fixtureDef.friction = rigidBody.getData("friction") as? Float ?: 0.2f
                            fixtureDef.restitution = rigidBody.getData("restitution") as? Float ?: 0.0f

                            body.createFixture(fixtureDef)
                            shape.dispose()
                        }
                        "CIRCLE_COLLIDER" -> {
                            val radius = comp.getData("radius") as? Float ?: 25f
                            val shape = CircleShape()
                            shape.radius = radius / PPM

                            val fixtureDef = FixtureDef()
                            fixtureDef.shape = shape
                            fixtureDef.density = rigidBody.getData("mass") as? Float ?: 1.0f
                            fixtureDef.friction = rigidBody.getData("friction") as? Float ?: 0.2f
                            fixtureDef.restitution = rigidBody.getData("restitution") as? Float ?: 0.0f

                            body.createFixture(fixtureDef)
                            shape.dispose()
                        }
                    }
                }
                bodyMap[instance] = body
            }
        }

        if (actionsExtern != null) {
            val skin = EditorSkin()
            val closeBtn = ImageButton(skin.newDrawable("back"))
            val closeWidth = closeBtn.width * 1.5f
            val closeHeight = closeBtn.height * 1.5f
            closeBtn.setSize(closeWidth, closeHeight)
            closeBtn.imageCell.size(closeWidth, closeHeight)
            closeBtn.setPosition(30f, Gdx.graphics.height - 30f - closeHeight)
            closeBtn.addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    actionsExtern.exitEditor()
                }
            })
            stage.addActor(closeBtn)
            closeButton = closeBtn

            if (actionsExtern.showUpload) {
                val uploadBtn = ImageButton(skin.newDrawable("upload"))
                val uploadWidth = uploadBtn.width * 1.5f
                val uploadHeight = uploadBtn.height * 1.5f
                uploadBtn.setSize(uploadWidth, uploadHeight)
                uploadBtn.imageCell.size(uploadWidth, uploadHeight)
                uploadBtn.setPosition(
                    Gdx.graphics.width - 30f - uploadWidth,
                    Gdx.graphics.height - 30f - uploadHeight
                )
                uploadBtn.addListener(object : ChangeListener() {
                    override fun changed(event: ChangeEvent?, actor: Actor?) {
                        actionsExtern.upload()
                    }
                })
                stage.addActor(uploadBtn)
                uploadButton = uploadBtn
            }
        }
    }

    override fun show() {
        Gdx.input.inputProcessor = stage
    }

    override fun render(delta: Float) {
        if (Gdx.input.inputProcessor != stage) {
            Gdx.input.inputProcessor = stage
        }

        world.step(delta, 6, 2)
        for ((instance, body) in bodyMap) {
            instance.setPosition(
                body.position.x * PPM - instance.width / 2f,
                body.position.y * PPM - instance.height / 2f
            )
            instance.rotation = body.angle * MathUtils.radiansToDegrees
        }

        ScreenUtils.clear(Color.SKY)
        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
        closeButton?.setPosition(30f, height - 30f - closeButton!!.height)
        uploadButton?.setPosition(width - 30f - uploadButton!!.width, height - 30f - uploadButton!!.height)
    }

    override fun pause() {
    }

    override fun resume() {
        Gdx.app.postRunnable {
            Gdx.input.inputProcessor = stage
            stage.viewport.update(Gdx.graphics.width, Gdx.graphics.height, true)
        }
    }

    override fun hide() {
    }

    override fun dispose() {
        stage.dispose()
        world.dispose()
    }
}
