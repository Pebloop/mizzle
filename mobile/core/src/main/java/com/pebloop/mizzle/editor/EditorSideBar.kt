package com.pebloop.mizzle.editor

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener

class EditorSideBar(val skin: EditorSkin, actions: EditorActions): VerticalGroup() {

    val editEntityButton: ImageButton
    var disabled: Boolean = true

    init {
        this.zIndex = 9999

        editEntityButton = ImageButton(skin.newDrawable("edit"))
        this.pad(32f)
        this.left()
        this.setPosition(5f,190 + 128f)

        this.addActor(editEntityButton)

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
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        if (!disabled) {
            batch?.draw(skin.get("black", Texture::class.java), 5f, 190f, 128f, 128f)
            super.draw(batch, parentAlpha)
        }
    }

}
