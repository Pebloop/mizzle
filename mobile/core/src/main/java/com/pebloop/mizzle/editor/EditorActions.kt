package com.pebloop.mizzle.editor

import com.pebloop.mizzle.data.EntityData

class EditorActions(
    val spawnEntity: Function0<Unit>,
    val selectEntity: Function1<EntityData?, Unit>,
    val getSelectedEntity: Function0<EntityData?>,
    val openEntityEditor: Function2<EntityData, EditorActions, Unit>,
    val updateEntity: Function1<EntityData, Unit>
)
