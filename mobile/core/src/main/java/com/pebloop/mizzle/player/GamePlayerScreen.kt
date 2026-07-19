package com.pebloop.mizzle.player

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
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

    init {
        for (entity in droplet.entities) {
            val instance = GameEntityInstance(entity)
            stage.addActor(instance)
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
    }
}
