package com.pebloop.mizzle.event_builder

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox
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
    var bodyPiece: PuzzlePiece? = null

    val inlineSockets = mutableListOf<InlineSocket>()
    val textFields = mutableMapOf<Int, TextField>()
    val selectBoxes = mutableMapOf<Int, SelectBox<String>>()
    val customValues = mutableMapOf<Int, String>()

    private val topTable = Table()
    private val grabOffset = Vector2()
    private val spineWidth = 40f
    private val bottomBarHeight = 40f

    init {
        touchable = Touchable.enabled
        setTransform(true)

        topTable.setFillParent(false)
        addActor(topTable)

        val parts = pieceType.displayName.split(" ")
        for (part in parts) {
            if (part.startsWith("$")) {
                // Socket: $TYPE0
                val typeStr = part.substring(1, part.length - 1)
                val type = try { PuzzleValueType.valueOf(typeStr) } catch (e: Exception) { PuzzleValueType.ANY }
                val index = part.takeLast(1).toIntOrNull() ?: 0

                val socket = InlineSocket(index, type, skin)
                inlineSockets.add(socket)
                topTable.add(socket).pad(5f).padLeft(5f + notchRadius.toFloat())
            } else if (part.startsWith("#")) {
                // Input: #TYPE0
                val typeStr = part.substring(1, part.length - 1)
                val type = try { PuzzleValueType.valueOf(typeStr) } catch (e: Exception) { PuzzleValueType.STRING }
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

                topTable.add(field).width(80f).height(60f).pad(5f)
            } else if (part.startsWith("%")) {
                // SelectBox: %OP0 or %BOOL0
                val index = part.takeLast(1).toIntOrNull() ?: 0
                val selectBox = SelectBox<String>(skin)
                if (part.contains("OP")) {
                    selectBox.setItems("==", "~=", ">", "<", ">=", "<=")
                } else if (part.contains("BOOL")) {
                    selectBox.setItems("true", "false")
                }
                selectBoxes[index] = selectBox
                customValues[index] = selectBox.selected

                selectBox.addListener(object : ChangeListener() {
                    override fun changed(event: ChangeEvent?, actor: com.badlogic.gdx.scenes.scene2d.Actor?) {
                        customValues[index] = selectBox.selected
                        refreshLayout()
                    }
                })

                topTable.add(selectBox).minWidth(80f).pad(5f)
            } else {
                val label = Label(part, skin.get("default", Label.LabelStyle::class.java))
                label.setFontScale(0.4f)
                topTable.add(label).pad(5f)
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
                    grabOffset.set(x, y)
                    val p = parent
                    if (p is PuzzlePiece) {
                        if (p.nextPiece == this@PuzzlePiece) {
                            p.nextPiece = null
                        } else if (p.bodyPiece == this@PuzzlePiece) {
                            p.bodyPiece = null
                        }
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
                    val stagePos = localToStageCoordinates(Vector2(x, y))
                    setPosition(stagePos.x - grabOffset.x, stagePos.y - grabOffset.y)
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
        topTable.layout()
        val paddingWidth = 20f
        val paddingHeight = 60f
        val topPartHeight = topTable.prefHeight + paddingHeight

        if (pieceType.category == PuzzlePieceCategory.CONTAINER) {
            val bodyHeight = getChainHeight(bodyPiece) ?: 80f
            val totalHeight = topPartHeight + bodyHeight + bottomBarHeight

            setSize(topTable.prefWidth + paddingWidth * 2 + spineWidth, totalHeight)
            topTable.setSize(width - spineWidth, topPartHeight)
            topTable.setPosition(spineWidth, totalHeight - topPartHeight)

            texture?.dispose()
            texture = Graphics.createContainerTexture(
                width.toInt(), height.toInt(), topPartHeight.toInt(), bottomBarHeight.toInt(),
                spineWidth.toInt(), cornerRadius, notchRadius, pieceColor
            )

            bodyPiece?.let {
                it.setPosition(spineWidth, totalHeight - topPartHeight - it.height)
            }
        } else {
            setSize(topTable.prefWidth + paddingWidth * 2, topPartHeight)
            topTable.setSize(width, topPartHeight)
            topTable.setPosition(0f, 0f)

            texture?.dispose()
            texture = Graphics.createPuzzlePieceTexture(
                width.toInt(), height.toInt(), cornerRadius, notchRadius, pieceColor,
                hasTopNotch = pieceType.category == PuzzlePieceCategory.ACTION,
                hasBottomBump = pieceType.category == PuzzlePieceCategory.ACTION,
                hasLeftBump = pieceType.category == PuzzlePieceCategory.VALUE
            )
        }

        val p = parent
        if (p is InlineSocket) {
            findParentPiece(p)?.refreshLayout()
        } else if (p is PuzzlePiece && p.bodyPiece == this) {
            p.refreshLayout()
        }

        nextPiece?.let {
            it.setPosition(0f, -it.height)
        }
    }

    private fun getChainHeight(start: PuzzlePiece?): Float? {
        if (start == null) return null
        var current: PuzzlePiece? = start
        var total = 0f
        while (current != null) {
            total += current.height
            current = current.nextPiece
        }
        return total
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
                    bodyPiece?.remove()
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
        val allPieces = workspace.getAllPieces()

        if (pieceType.category == PuzzlePieceCategory.ACTION || pieceType.category == PuzzlePieceCategory.CONTAINER) {
            val myTopLeft = localToStageCoordinates(Vector2(0f, height))
            for (actor in allPieces) {
                if (actor != this && !actor.isDescendantOf(this)) {
                    if (actor.pieceType.category != PuzzlePieceCategory.VALUE && actor.nextPiece == null) {
                        val otherBottomLeft = actor.localToStageCoordinates(Vector2(0f, 0f))
                        if (myTopLeft.dst(otherBottomLeft) < 30f) {
                            snapTo(actor)
                            return
                        }
                    }

                    if (actor.pieceType.category == PuzzlePieceCategory.CONTAINER && actor.bodyPiece == null) {
                        val topPartHeight = actor.topTable.prefHeight + 60f
                        val internalTop = actor.localToStageCoordinates(Vector2(spineWidth, actor.height - topPartHeight))
                        if (myTopLeft.dst(internalTop) < 30f) {
                            snapToBody(actor)
                            return
                        }
                    }
                }
            }
        } else if (pieceType.category == PuzzlePieceCategory.VALUE) {
            val myCenter = localToStageCoordinates(Vector2(width / 2f, height / 2f))
            for (actor in allPieces) {
                if (actor != this && !actor.isDescendantOf(this)) {
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
        parentPiece.refreshLayout()
    }

    private fun snapToBody(container: PuzzlePiece) {
        container.bodyPiece = this
        container.addActor(this)
        container.refreshLayout()
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
        bodyPiece?.let { data.body = it.toData() }
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
            val offsetY = -notchRadius.toFloat()
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
        // Replace SelectBoxes
        for ((index, selectBox) in selectBoxes) {
            code = code.replace("%" + index, selectBox.selected)
        }

        if (pieceType.category == PuzzlePieceCategory.CONTAINER) {
            val bodyCode = bodyPiece?.generateCode() ?: ""
            val indentedBody = bodyCode.split("\n").joinToString("\n") { "    " + it }
            code = code.replace("\$BODY", indentedBody)
        }

        if (pieceType.category != PuzzlePieceCategory.VALUE && nextPiece != null) {
            code += "\n" + nextPiece!!.generateCode()
        }
        return code
    }

    private fun getDefaultValueForPlaceholder(type: PuzzleValueType): String {
        return when (type) {
            PuzzleValueType.STRING -> "\"\""
            PuzzleValueType.BOOLEAN -> "true"
            else -> "0"
        }
    }
}
