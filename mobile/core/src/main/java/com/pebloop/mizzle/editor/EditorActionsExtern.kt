package com.pebloop.mizzle.editor

import com.pebloop.mizzle.data.DropletData
import com.pebloop.mizzle.data.EntityData

class EditorActionsExtern(
    val openEntityEditor: Function2<EntityData, EditorActions, Unit>,
    val exitEditor: Function0<Unit>,
    val upload: Function0<Unit>,
    val openDropletSettings: Function1<DropletData, Unit>,
    val openResources: Function1<DropletData, Unit>,
    val showUpload: Boolean = true
) {
}
