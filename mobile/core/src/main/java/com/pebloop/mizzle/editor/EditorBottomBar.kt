package com.pebloop.mizzle.editor

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener

class EditorBottomBar(val skin: EditorSkin, actions: EditorActions): HorizontalGroup() {

    val addObjectButton: ImageButton

    init {
        this.zIndex = 9999

        addObjectButton = ImageButton(skin.newDrawable("plus"))
        this.pad(32f)
        this.bottom()

        this.addActor(addObjectButton)

        addObjectButton.addListener(object: ChangeListener() {
            override fun changed(
                event: ChangeEvent?,
                actor: Actor?
            ) {
                actions.spawnEntity()
            }

        })
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        batch?.draw(skin.get("black", Texture::class.java), 0f,0f,Gdx.graphics.width.toFloat(), 128f)
        super.draw(batch, parentAlpha)
    }


}
