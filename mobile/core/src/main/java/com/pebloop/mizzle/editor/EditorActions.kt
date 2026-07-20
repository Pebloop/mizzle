package com.pebloop.mizzle.editor

import com.pebloop.mizzle.data.EntityData

class EditorActions(
    val spawnEntity: Function0<Unit>,
    val selectEntity: Function1<EntityData?, Unit>,
    val getSelectedEntity: Function0<EntityData?>,
    val openEntityEditor: Function2<EntityData, EditorActions, Unit>,
    val updateEntity: Function1<EntityData, Unit>,
    val exitEditor: Function0<Unit>,
    val upload: Function0<Unit>,
    val deleteEntity: Function1<EntityData, Unit>,
    val openDropletSettings: Function0<Unit>,
    val openResources: Function0<Unit>,
    val getInteractionMode: Function0<EditorScreen.InteractionMode>,
    val setInteractionMode: Function1<EditorScreen.InteractionMode, Unit>
)
