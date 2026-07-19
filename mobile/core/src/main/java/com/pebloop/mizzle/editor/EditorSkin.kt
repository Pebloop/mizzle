package com.pebloop.mizzle.editor

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin

class EditorSkin: Skin() {

    init {
        val pixmap = Pixmap(1,1, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.WHITE)
        pixmap.fill()
        this.add("white", Texture(pixmap))
        pixmap.setColor(Color.BLACK)
        pixmap.fill()
        this.add("black", Texture(pixmap))
        this.add("plus", Texture(Gdx.files.internal("add_button.png")))
        this.add("edit", Texture(Gdx.files.internal("edit_button.png")))
        this.add("back", Texture(Gdx.files.internal("back_button.png")))
        this.add("upload", Texture(Gdx.files.internal("upload_button.png")))
        this.add("settings", Texture(Gdx.files.internal("settings_button.png")))
        this.add("move", Texture(Gdx.files.internal("move_button.png")))
        this.add("rotate", Texture(Gdx.files.internal("rotate_button.png")))
        this.add("scale", Texture(Gdx.files.internal("scale_button.png")))
        this.add("font", BitmapFont(Gdx.files.internal("roses.fnt"), Gdx.files.internal("roses.png"), false))

        val style: Label.LabelStyle = Label.LabelStyle()
        style.fontColor = Color.WHITE
        style.font = this.get("font", BitmapFont::class.java)
        this.add("default", style)

    }

}
