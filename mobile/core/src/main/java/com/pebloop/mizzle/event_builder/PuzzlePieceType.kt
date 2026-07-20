package com.pebloop.mizzle.event_builder

enum class PuzzlePieceType(
    val id: String,
    val displayName: String,
    val luaTemplate: String,
    val category: PuzzlePieceCategory,
    val semanticCategory: PuzzleSemanticCategory,
    val returnType: PuzzleValueType = PuzzleValueType.ANY
) {
    // Actions
    APPLY_FORCE("APPLY_FORCE", "apply force \$FLOAT0 , \$FLOAT1", "this:applyForce(\$0, \$1)", PuzzlePieceCategory.ACTION, PuzzleSemanticCategory.ENTITY),
    SET_VELOCITY("SET_VELOCITY", "set velocity \$FLOAT0 , \$FLOAT1", "this:setLinearVelocity(\$0, \$1)", PuzzlePieceCategory.ACTION, PuzzleSemanticCategory.ENTITY),
    PRINT_LOG("PRINT_LOG", "print \$STRING0", "print(\$0)", PuzzlePieceCategory.ACTION, PuzzleSemanticCategory.TEXT),
    SET_X("SET_X", "set x to \$FLOAT0", "this.x = \$0", PuzzlePieceCategory.ACTION, PuzzleSemanticCategory.ENTITY),
    SET_Y("SET_Y", "set y to \$FLOAT0", "this.y = \$0", PuzzlePieceCategory.ACTION, PuzzleSemanticCategory.ENTITY),
    WAIT("WAIT", "wait \$FLOAT0 seconds", "wait(\$0)", PuzzlePieceCategory.ACTION, PuzzleSemanticCategory.LOGIC),
    DESTROY("DESTROY", "destroy entity", "this:destroy()", PuzzlePieceCategory.ACTION, PuzzleSemanticCategory.ENTITY),

    // Values
    INT_VALUE("INT_VALUE", "int #INT0", "\$0", PuzzlePieceCategory.VALUE, PuzzleSemanticCategory.NUMBERS, PuzzleValueType.INT),
    FLOAT_VALUE("FLOAT_VALUE", "float #FLOAT0", "\$0", PuzzlePieceCategory.VALUE, PuzzleSemanticCategory.NUMBERS, PuzzleValueType.FLOAT),
    STRING_VALUE("STRING_VALUE", "string #STRING0", "\$0", PuzzlePieceCategory.VALUE, PuzzleSemanticCategory.TEXT, PuzzleValueType.STRING),
    ROUND_VALUE("ROUND_VALUE", "rounded \$FLOAT0", "math.round(\$0)", PuzzlePieceCategory.VALUE, PuzzleSemanticCategory.NUMBERS, PuzzleValueType.INT);

    companion object {
        fun getById(id: String): PuzzlePieceType? {
            return entries.find { it.id == id }
        }
    }
}
