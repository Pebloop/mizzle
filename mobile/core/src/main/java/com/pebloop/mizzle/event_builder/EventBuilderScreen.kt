package com.pebloop.mizzle.event_builder

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.ScreenUtils
import com.pebloop.mizzle.data.components.Event
import com.pebloop.mizzle.editor.EditorActionsExtern
import com.pebloop.mizzle.editor.EditorSkin

class EventBuilderScreen(val event: Event?, val actions: EditorActionsExtern?) : Screen {
    private val stage = Stage()
    private val skin = EditorSkin()
    private val workspace = Workspace(skin)
    private val puzzleBox = PuzzleBox(skin) { piece ->
        workspace.addPiece(piece)
    }
    private var closeButton: ImageButton? = null
    private val puzzleBoxHeight = 250f

    override fun show() {
        Gdx.input.inputProcessor = stage

        val button = ImageButton(skin.newDrawable("back"))
        val closeWidth = button.width * 1.5f
        val closeHeight = button.height * 1.5f
        button.setSize(closeWidth, closeHeight)
        button.imageCell.size(closeWidth, closeHeight)
        button.setPosition(stage.width - closeWidth - 20f, stage.height - closeHeight - 10f)
        button.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                saveAndExit()
            }
        })
        this.closeButton = button

        puzzleBox.setSize(stage.width, puzzleBoxHeight)
        puzzleBox.setPosition(0f, button.y - puzzleBoxHeight - 10f)
        stage.addActor(puzzleBox)

        workspace.setSize(stage.width, stage.height)
        workspace.touchable = Touchable.childrenOnly
        stage.addActor(workspace)

        // Load existing pieces
        event?.let {
            workspace.importState(it.pieces)
        }

        stage.addActor(button)
    }

    override fun render(delta: Float) {
        ScreenUtils.clear(Color.DARK_GRAY)
        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
        workspace.setSize(width.toFloat(), height.toFloat())

        closeButton?.let {
            it.setPosition(width - it.width - 20f, height - it.height - 10f)
            puzzleBox.setSize(width.toFloat(), puzzleBoxHeight)
            puzzleBox.setPosition(0f, it.y - puzzleBoxHeight - 10f)
        }
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun hide() {
    }

    override fun dispose() {
        stage.dispose()
    }

    private fun saveAndExit() {
        this@EventBuilderScreen.event?.let {
            it.code = workspace.generateFullCode()
            it.pieces = workspace.exportState()
        }
        actions?.exitEditor()
    }
}
