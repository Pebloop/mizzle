package com.pebloop.mizzle.event_builder

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.DragListener
import com.pebloop.mizzle.editor.EditorSkin
import com.pebloop.mizzle.util.Graphics

class PuzzlePiece(
    val pieceType: PuzzlePieceType,
    val skin: EditorSkin
) : Group() {

    private val pieceColor = pieceType.semanticCategory.color
    private val cornerRadius = 15
    private val notchRadius = 20
    private var texture: Texture? = null

    var isStatic: Boolean = false
    var onDragStart: ((PuzzlePiece, Float, Float) -> Unit)? = null
    var onDrag: ((Float, Float) -> Unit)? = null
    var onDragStop: (() -> Unit)? = null

    var nextPiece: PuzzlePiece? = null
    val inlineSockets = mutableListOf<InlineSocket>()
    val textFields = mutableMapOf<Int, TextField>()
    val customValues = mutableMapOf<Int, String>()

    private val mainTable = Table()

    init {
        touchable = Touchable.enabled

        mainTable.setFillParent(true)
        addActor(mainTable)

        val parts = pieceType.displayName.split(" ")
        for (part in parts) {
            if (part.startsWith("$")) {
                // Socket: $TYPE0
                val typeStr = part.substring(1, part.length - 1)
                val type = try { PuzzleValueType.valueOf(typeStr) } catch(e: Exception) { PuzzleValueType.ANY }
                val index = part.takeLast(1).toIntOrNull() ?: 0

                val socket = InlineSocket(index, type, skin)
                inlineSockets.add(socket)
                mainTable.add(socket).pad(5f).padLeft(5f + notchRadius.toFloat())
            } else if (part.startsWith("#")) {
                // Input: #TYPE0
                val typeStr = part.substring(1, part.length - 1)
                val type = try { PuzzleValueType.valueOf(typeStr) } catch(e: Exception) { PuzzleValueType.STRING }
                val index = part.takeLast(1).toIntOrNull() ?: 0

                val field = TextField("0", skin, "small")
                if (type == PuzzleValueType.INT || type == PuzzleValueType.FLOAT) {
                    field.textFieldFilter = TextField.TextFieldFilter.DigitsOnlyFilter()
                }
                textFields[index] = field

                field.addListener(object : ChangeListener() {
                    override fun changed(event: ChangeEvent?, actor: com.badlogic.gdx.scenes.scene2d.Actor?) {
                        customValues[index] = field.text
                        refreshLayout()
                    }
                })

                mainTable.add(field).width(80f).height(60f).pad(5f)
            } else {
                val label = Label(part, skin.get("default", Label.LabelStyle::class.java))
                label.setFontScale(0.4f)
                mainTable.add(label).pad(5f)
            }
        }

        refreshLayout()

        addListener(object : DragListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                val handled = super.touchDown(event, x, y, pointer, button)
                if (handled) {
                    event?.stop()
                }
                return handled
            }

            override fun dragStart(event: InputEvent?, x: Float, y: Float, pointer: Int) {
                if (isStatic) {
                    val stagePos = localToStageCoordinates(Vector2(x, y))
                    onDragStart?.invoke(this@PuzzlePiece, stagePos.x, stagePos.y)
                } else {
                    // Detach from parent if needed
                    val p = parent
                    if (p is PuzzlePiece) {
                        p.nextPiece = null
                        val stagePos = localToStageCoordinates(Vector2(0f, 0f))
                        findWorkspace()?.addPiece(this@PuzzlePiece)
                        setPosition(stagePos.x, stagePos.y)
                        p.refreshLayout()
                    } else if (p is InlineSocket) {
                        p.attachedPiece = null
                        val stagePos = localToStageCoordinates(Vector2(0f, 0f))
                        findWorkspace()?.addPiece(this@PuzzlePiece)
                        setPosition(stagePos.x, stagePos.y)
                        findParentPiece(p)?.refreshLayout()
                    }
                    toFront()
                }
                event?.stop()
            }

            override fun drag(event: InputEvent?, x: Float, y: Float, pointer: Int) {
                if (isStatic) {
                    val stagePos = localToStageCoordinates(Vector2(x, y))
                    onDrag?.invoke(stagePos.x, stagePos.y)
                } else {
                    moveBy(deltaX, deltaY)
                }
                event?.stop()
            }

            override fun dragStop(event: InputEvent?, x: Float, y: Float, pointer: Int) {
                if (isStatic) {
                    onDragStop?.invoke()
                } else {
                    if (!checkDeletion()) {
                        checkSnapping()
                    }
                }
                event?.stop()
            }
        })
    }

    private fun findParentPiece(socket: InlineSocket): PuzzlePiece? {
        var p = socket.parent
        while (p != null) {
            if (p is PuzzlePiece) return p
            p = p.parent
        }
        return null
    }

    fun refreshLayout() {
        mainTable.layout()
        val paddingWidth = 20f
        val paddingHeight = 40f
        setSize(mainTable.prefWidth + paddingWidth * 2, mainTable.prefHeight + paddingHeight)

        texture?.dispose()
        texture = Graphics.createPuzzlePieceTexture(
            width.toInt(),
            height.toInt(),
            cornerRadius,
            notchRadius,
            pieceColor,
            hasTopNotch = pieceType.category == PuzzlePieceCategory.ACTION,
            hasBottomBump = pieceType.category == PuzzlePieceCategory.ACTION,
            hasLeftBump = pieceType.category == PuzzlePieceCategory.VALUE
        )

        val p = parent
        if (p is InlineSocket) {
            findParentPiece(p)?.refreshLayout()
        }

        nextPiece?.let {
            it.setPosition(0f, -it.height)
        }
    }

    private fun checkDeletion(): Boolean {
        val stage = stage ?: return false
        val myPos = localToStageCoordinates(Vector2(width / 2f, height / 2f))
        for (actor in stage.actors) {
            if (actor is PuzzleBox) {
                val boxPos = actor.localToStageCoordinates(Vector2(0f, 0f))
                if (myPos.x >= boxPos.x && myPos.x <= boxPos.x + actor.width &&
                    myPos.y >= boxPos.y && myPos.y <= boxPos.y + actor.height
                ) {
                    nextPiece?.remove()
                    inlineSockets.forEach { it.attachedPiece?.remove() }
                    remove()
                    return true
                }
            }
        }
        return false
    }

    private fun findWorkspace(): Workspace? {
        var p = parent
        while (p != null) {
            if (p is Workspace) return p
            p = p.parent
        }
        return null
    }

    private fun checkSnapping() {
        val workspace = findWorkspace() ?: return

        if (pieceType.category == PuzzlePieceCategory.ACTION) {
            val myTopLeft = localToStageCoordinates(Vector2(0f, height))
            for (actor in workspace.children) {
                if (actor is PuzzlePiece && actor != this && actor.pieceType.category == PuzzlePieceCategory.ACTION) {
                    val otherBottomLeft = actor.localToStageCoordinates(Vector2(0f, 0f))
                    if (myTopLeft.dst(otherBottomLeft) < 30f) {
                        snapTo(actor)
                        return
                    }
                }
            }
        } else if (pieceType.category == PuzzlePieceCategory.VALUE) {
            val myCenter = localToStageCoordinates(Vector2(width / 2f, height / 2f))
            for (actor in workspace.children) {
                if (actor is PuzzlePiece && actor.pieceType.category == PuzzlePieceCategory.ACTION) {
                    for (socket in actor.inlineSockets) {
                        if (socket.attachedPiece == null && isTypeCompatible(pieceType.returnType, socket.expectedType)) {
                            val socketCenter = socket.localToStageCoordinates(Vector2(socket.width / 2f, socket.height / 2f))
                            if (myCenter.dst(socketCenter) < 40f) {
                                snapToSocket(socket)
                                return
                            }
                        }
                    }
                }
            }
        }
    }

    private fun isTypeCompatible(offered: PuzzleValueType, expected: PuzzleValueType): Boolean {
        if (expected == PuzzleValueType.ANY) return true
        if (offered == expected) return true
        if (expected == PuzzleValueType.FLOAT && offered == PuzzleValueType.INT) return true
        return false
    }

    private fun snapTo(parentPiece: PuzzlePiece) {
        parentPiece.nextPiece = this
        setPosition(0f, -height)
        parentPiece.addActor(this)
    }

    private fun snapToSocket(socket: InlineSocket) {
        socket.attachedPiece = this
        setPosition(0f, 0f)
        socket.addActor(this)
        refreshLayout()
    }

    fun toData(): PuzzlePieceData {
        val data = PuzzlePieceData(pieceType.id, x, y)
        nextPiece?.let { data.next = it.toData() }
        for (socket in inlineSockets) {
            socket.attachedPiece?.let {
                data.internalValues[socket.index] = it.toData()
            }
        }
        for ((index, value) in customValues) {
            data.customValues[index] = value
        }
        return data
    }

    fun clone(): PuzzlePiece {
        return PuzzlePiece(pieceType, skin)
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        texture?.let {
            val offsetX = if (pieceType.category == PuzzlePieceCategory.VALUE) -notchRadius.toFloat() else 0f
            val offsetY = if (pieceType.category == PuzzlePieceCategory.ACTION) -notchRadius.toFloat() else 0f
            batch?.draw(it, x + offsetX, y + offsetY, it.width.toFloat(), it.height.toFloat())
        }
        super.draw(batch, parentAlpha)
    }

    override fun remove(): Boolean {
        texture?.dispose()
        texture = null
        return super.remove()
    }

    fun generateCode(): String {
        var code = pieceType.luaTemplate
        // Replace sockets
        for (socket in inlineSockets) {
            val valCode = socket.attachedPiece?.generateCode() ?: getDefaultValueForPlaceholder(socket.expectedType)
            code = code.replace("$" + socket.index, valCode)
        }
        // Replace inputs
        for ((index, field) in textFields) {
            var valStr = field.text
            if (pieceType.returnType == PuzzleValueType.STRING) {
                valStr = "\"" + valStr.replace("\"", "\\\"") + "\""
            }
            code = code.replace("\$$index", valStr)
        }

        if (pieceType.category == PuzzlePieceCategory.ACTION && nextPiece != null) {
            code += "\n" + nextPiece!!.generateCode()
        }
        return code
    }

    private fun getDefaultValueForPlaceholder(type: PuzzleValueType): String {
        return when(type) {
            PuzzleValueType.STRING -> "\"\""
            else -> "0"
        }
    }
}
