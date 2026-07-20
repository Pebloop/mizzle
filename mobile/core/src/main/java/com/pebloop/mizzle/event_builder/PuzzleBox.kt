package com.pebloop.mizzle.event_builder

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.pebloop.mizzle.editor.EditorSkin

class PuzzleBox(val skin: EditorSkin, val onPieceSelected: (PuzzlePiece) -> Unit) : Group() {

    private val inventory = Table()
    private val scrollPane: ScrollPane
    private val background = skin.newDrawable("white", Color.BLACK)
    private val tabTable = Table()
    private var currentCategory = PuzzleSemanticCategory.ENTITY

    init {
        // Tab Table Setup
        addActor(tabTable)
        setupTabs()

        // ScrollPane Setup
        val scrollStyle = ScrollPane.ScrollPaneStyle()
        scrollPane = ScrollPane(inventory, scrollStyle)
        scrollPane.setScrollingDisabled(false, true)
        scrollPane.setFadeScrollBars(true)
        scrollPane.setCancelTouchFocus(false)
        addActor(scrollPane)

        setupInventory()
    }

    private fun setupTabs() {
        tabTable.clearChildren()
        for (category in PuzzleSemanticCategory.entries) {
            val buttonStyle = TextButton.TextButtonStyle()
            buttonStyle.font = skin.getFont("font")
            buttonStyle.fontColor = if (category == currentCategory) category.color else Color.GRAY

            val button = TextButton(category.displayName, buttonStyle)
            button.label.setFontScale(0.3f)

            button.addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    currentCategory = category
                    setupTabs()
                    setupInventory()
                }
            })
            tabTable.add(button).pad(10f)
        }
    }

    override fun sizeChanged() {
        super.sizeChanged()
        tabTable.setSize(width, 40f)
        tabTable.setPosition(0f, height - 40f)

        scrollPane.setSize(width, height - 40f)
        scrollPane.setPosition(0f, 0f)
    }

    private fun setupInventory() {
        inventory.clearChildren()
        for (type in PuzzlePieceType.entries) {
            if (type.semanticCategory != currentCategory) continue

            val piece = PuzzlePiece(type, skin)
            piece.isStatic = true
            var activeClone: PuzzlePiece? = null

            piece.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    val clone = piece.clone()
                    clone.setPosition(300f, 300f)
                    onPieceSelected(clone)
                }
            })

            piece.onDragStart = { _, stageX, stageY ->
                val clone = piece.clone()
                clone.setPosition(stageX - clone.width / 2f, stageY - clone.height / 2f)
                onPieceSelected(clone)
                activeClone = clone
            }

            piece.onDrag = { stageX, stageY ->
                activeClone?.let {
                    it.setPosition(stageX - it.width / 2f, stageY - it.height / 2f)
                }
            }

            piece.onDragStop = {
                activeClone = null
            }

            inventory.add(piece).pad(20f).height(80f)
        }
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        background.draw(batch, x, y, width, height)
        super.draw(batch, parentAlpha)
    }
}
