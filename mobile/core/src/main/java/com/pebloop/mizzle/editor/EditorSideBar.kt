package com.pebloop.mizzle.editor

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener

class EditorSideBar(val skin: EditorSkin, actions: EditorActions): VerticalGroup() {

    val editEntityButton: ImageButton
    val moveButton: ImageButton
    val rotateButton: ImageButton
    val scaleButton: ImageButton
    val deleteButton: ImageButton
    var disabled: Boolean = true

    init {
        this.zIndex = 9999

        editEntityButton = ImageButton(skin.newDrawable("edit"))
        moveButton = ImageButton(skin.newDrawable("move"))
        rotateButton = ImageButton(skin.newDrawable("rotate"))
        scaleButton = ImageButton(skin.newDrawable("scale"))
        deleteButton = ImageButton(skin.newDrawable("delete"))

        this.pad(32f)
        this.left()
        this.setPosition(5f,190 + (64f * 6))

        this.addActor(editEntityButton)
        this.addActor(moveButton)
        this.addActor(rotateButton)
        this.addActor(scaleButton)
        this.addActor(deleteButton)

        editEntityButton.addListener(object: ChangeListener() {
            override fun changed(
                event: ChangeEvent?,
                actor: Actor?
            ) {
                if (!disabled) {
                    val entity = actions.getSelectedEntity()
                    if (entity != null) {
                        actions.openEntityEditor(entity, actions)
                    }
                }
            }

        })

        moveButton.addListener(object: ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                actions.setInteractionMode(EditorScreen.InteractionMode.MOVE)
            }
        })

        rotateButton.addListener(object: ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                actions.setInteractionMode(EditorScreen.InteractionMode.ROTATE)
            }
        })

        scaleButton.addListener(object: ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                actions.setInteractionMode(EditorScreen.InteractionMode.SCALE)
            }
        })

        deleteButton.addListener(object: ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                if (!disabled) {
                    val entity = actions.getSelectedEntity()
                    if (entity != null) {
                        actions.deleteEntity(entity)
                    }
                }
            }
        })
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        if (!disabled) {
            batch?.draw(skin.get("black", Texture::class.java), 5f, 190f, 128f, 64f * 6)
            super.draw(batch, parentAlpha)
        }
    }

}
