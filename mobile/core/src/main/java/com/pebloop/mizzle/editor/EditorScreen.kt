package com.pebloop.mizzle.editor

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.ScreenUtils
import com.pebloop.mizzle.data.DropletData
import com.pebloop.mizzle.data.EntityData

class EditorScreen(val droplet: DropletData, val externAction: EditorActionsExtern): Screen {
    enum class InteractionMode { MOVE, ROTATE, SCALE }

    val stage: Stage = Stage()
    val skin: EditorSkin = EditorSkin()
    val bottomBar: EditorBottomBar
    val sideBar: EditorSideBar
    val actions: EditorActions
    var selectedEntity: EntityData? = null
    var entities: Array<EditorEntityInstance> = arrayOf()
    val camera: OrthographicCamera = OrthographicCamera()
    val selectedName: Label
    val closeButton: ImageButton
    val uploadButton: ImageButton
    var interactionMode: InteractionMode = InteractionMode.MOVE


    init {
        actions = EditorActions(
            ::spawnEntity,
            ::selectEntity,
            ::requestSelectedEntity,
            externAction.openEntityEditor,
            ::updateEntity,
            externAction.exitEditor,
            externAction.upload,
            { externAction.openDropletSettings(droplet) },
            { interactionMode },
            { mode -> interactionMode = mode }
        )
        bottomBar = EditorBottomBar(skin, actions)
        sideBar = EditorSideBar(skin, actions)
        stage.addActor(bottomBar)
        stage.addActor(sideBar)
        stage.viewport.camera = camera

        selectedName = Label("", skin.get("default", Label.LabelStyle::class.java))
        selectedName.setPosition(10f, 150f)
        selectedName.setFontScale(0.7f)
        stage.addActor(selectedName)

        closeButton = ImageButton(skin.newDrawable("back"))
        val closeWidth = closeButton.width * 1.5f
        val closeHeight = closeButton.height * 1.5f
        closeButton.setSize(closeWidth, closeHeight)
        closeButton.imageCell.size(closeWidth, closeHeight)
        closeButton.setPosition(30f, Gdx.graphics.height - 30f - closeHeight)
        closeButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                actions.exitEditor()
            }
        })
        stage.addActor(closeButton)

        uploadButton = ImageButton(skin.newDrawable("upload"))
        val uploadWidth = uploadButton.width * 1.5f
        val uploadHeight = uploadButton.height * 1.5f
        uploadButton.setSize(uploadWidth, uploadHeight)
        uploadButton.imageCell.size(uploadWidth, uploadHeight)
        uploadButton.setPosition(Gdx.graphics.width - 30f - uploadWidth, Gdx.graphics.height - 30f - uploadHeight)
        uploadButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                actions.upload()
            }
        })
        stage.addActor(uploadButton)


        displayEntities()
    }

    fun updateEntity(newEntity: EntityData) {
        Gdx.app.debug("test", newEntity.name)
        for (entity in droplet.entities) {
            if (entity.id == newEntity.id) {
                Gdx.app.debug("test", newEntity.name)
                entity.name = newEntity.name
                selectedName.setText(entity.name)
                entity.transform = newEntity.transform
                entity.components = newEntity.components

                for (instance in entities) {
                    if (instance.entity == entity) {
                        instance.forceUpdate()
                    }
                }

            }
        }
    }

    fun requestSelectedEntity(): EntityData? {
        return selectedEntity
    }

    fun selectEntity(entity: EntityData?) {
        selectedEntity = entity
        if (entity == null) {
            selectedName.setText("")
            sideBar.disabled = true
        } else {
            selectedName.setText(entity.name)
            sideBar.disabled = false
        }
    }

    fun spawnEntity() {
        var entity = droplet.addEntity()
        entity.transform.position = Vector2(Gdx.graphics.width / 2f, Gdx.graphics.height / 2f)
        addEntity(entity)
    }

    fun addEntity(entity: EntityData) {
        val instance: EditorEntityInstance = EditorEntityInstance(entity, actions)
        entities = entities.plus(instance)
        stage.addActor(instance)
    }

    fun displayEntities() {
        for (entity in droplet.entities) {
            addEntity(entity)
        }
    }

    override fun show() {
        stage.viewport.update(Gdx.graphics.width, Gdx.graphics.height)
        Gdx.input.inputProcessor = stage
    }

    override fun render(delta: Float) {
        if (Gdx.input.inputProcessor != stage) {
            Gdx.input.inputProcessor = stage
        }
        ScreenUtils.clear(Color.SKY)
        stage.act(Gdx.graphics.getDeltaTime())
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
        closeButton.setPosition(30f, height - 30f - closeButton.height)
        uploadButton.setPosition(width - 30f - uploadButton.width, height - 30f - uploadButton.height)
    }

    override fun pause() {

    }

    override fun resume() {
        Gdx.app.postRunnable {
            Gdx.input.inputProcessor = stage
            stage.viewport.update(Gdx.graphics.width, Gdx.graphics.height, true)
        }
    }

    override fun hide() {

    }

    override fun dispose() {
        stage.dispose()
    }
}
