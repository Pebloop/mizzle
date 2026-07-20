package com.pebloop.mizzle.editor

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.List
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextField

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
        this.add("resources", Texture(Gdx.files.internal("resources_button.png")))
        this.add("move", Texture(Gdx.files.internal("move_button.png")))
        this.add("rotate", Texture(Gdx.files.internal("rotate_button.png")))
        this.add("scale", Texture(Gdx.files.internal("scale_button.png")))
        this.add("delete", Texture(Gdx.files.internal("delete_button.png")))
        this.add("font", BitmapFont(Gdx.files.internal("roses.fnt"), Gdx.files.internal("roses.png"), false))

        val style: Label.LabelStyle = Label.LabelStyle()
        style.fontColor = Color.WHITE
        style.font = this.get("font", BitmapFont::class.java)
        this.add("default", style)

        val textFieldStyle = TextField.TextFieldStyle()
        textFieldStyle.font = this.get("font", BitmapFont::class.java)
        textFieldStyle.fontColor = Color.WHITE
        textFieldStyle.background = this.newDrawable("white", Color(0f, 0f, 0f, 0.3f))
        textFieldStyle.cursor = this.newDrawable("white", Color.WHITE)
        textFieldStyle.selection = this.newDrawable("white", Color(1f, 1f, 1f, 0.5f))
        this.add("default", textFieldStyle)

        val smallTextFieldStyle = TextField.TextFieldStyle(textFieldStyle)
        val smallFont = BitmapFont(Gdx.files.internal("roses.fnt"), Gdx.files.internal("roses.png"), false)
        smallFont.data.setScale(0.3f)
        smallTextFieldStyle.font = smallFont
        this.add("small", smallTextFieldStyle)

        val mediumFont = BitmapFont(Gdx.files.internal("roses.fnt"), Gdx.files.internal("roses.png"), false)
        mediumFont.data.setScale(0.5f)
        this.add("medium", mediumFont)

        val listStyle = List.ListStyle()
        listStyle.font = mediumFont
        listStyle.fontColorSelected = Color.BLACK
        listStyle.fontColorUnselected = Color.WHITE
        listStyle.selection = this.newDrawable("white", Color.WHITE)
        this.add("default", listStyle)

        val scrollPaneStyle = ScrollPane.ScrollPaneStyle()
        this.add("default", scrollPaneStyle)

        val selectBoxStyle = SelectBox.SelectBoxStyle()
        selectBoxStyle.font = mediumFont
        selectBoxStyle.fontColor = Color.WHITE
        selectBoxStyle.background = this.newDrawable("white", Color(0f, 0f, 0f, 0.3f))
        selectBoxStyle.scrollStyle = scrollPaneStyle
        selectBoxStyle.listStyle = listStyle
        this.add("default", selectBoxStyle)
    }

}
