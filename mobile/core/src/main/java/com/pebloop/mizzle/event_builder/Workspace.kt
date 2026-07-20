package com.pebloop.mizzle.event_builder

import com.badlogic.gdx.scenes.scene2d.Group
import com.pebloop.mizzle.editor.EditorSkin

class Workspace(val skin: EditorSkin) : Group() {

    fun addPiece(piece: PuzzlePiece) {
        addActor(piece)
    }

    fun getAllPieces(): List<PuzzlePiece> {
        val pieces = mutableListOf<PuzzlePiece>()
        collectPieces(this, pieces)
        return pieces
    }

    private fun collectPieces(group: Group, list: MutableList<PuzzlePiece>) {
        for (actor in group.children) {
            if (actor is PuzzlePiece) {
                list.add(actor)
                collectPieces(actor, list)
            } else if (actor is Group) {
                collectPieces(actor, list)
            }
        }
    }

    fun generateFullCode(): String {
        val codes = mutableListOf<String>()
        for (actor in children) {
            if (actor is PuzzlePiece) {
                codes.add(actor.generateCode())
            }
        }
        return if (codes.isEmpty()) "none" else codes.joinToString("\n")
    }

    fun exportState(): List<PuzzlePieceData> {
        val pieces = mutableListOf<PuzzlePieceData>()
        for (actor in children) {
            if (actor is PuzzlePiece) {
                pieces.add(actor.toData())
            }
        }
        return pieces
    }

    fun importState(data: List<PuzzlePieceData>) {
        clearChildren()
        for (pieceData in data) {
            val piece = createPieceFromData(pieceData)
            addActor(piece)
            piece.refreshLayout()
        }
    }

    private fun createPieceFromData(data: PuzzlePieceData): PuzzlePiece {
        val type = PuzzlePieceType.getById(data.type) ?: PuzzlePieceType.PRINT_LOG
        val piece = PuzzlePiece(type, skin)
        piece.setPosition(data.x, data.y)

        // Restore custom values (text fields and select boxes)
        for ((index, value) in data.customValues) {
            piece.customValues[index] = value
            piece.textFields[index]?.text = value
            piece.selectBoxes[index]?.selected = value
        }

        // Load internal argument values
        for ((index, valueData) in data.internalValues) {
            if (valueData != null) {
                val valPiece = createPieceFromData(valueData)
                val socket = piece.inlineSockets.find { it.index == index }
                if (socket != null) {
                    socket.attachedPiece = valPiece
                    valPiece.setPosition(0f, 0f)
                    socket.addActor(valPiece)
                }
            }
        }

        // Load body piece for containers
        data.body?.let {
            val bodyPiece = createPieceFromData(it)
            piece.bodyPiece = bodyPiece
            piece.addActor(bodyPiece)
        }

        // Load next piece in chain
        data.next?.let {
            val nextPiece = createPieceFromData(it)
            piece.nextPiece = nextPiece
            piece.addActor(nextPiece)
        }

        piece.refreshLayout()

        return piece
    }
}
