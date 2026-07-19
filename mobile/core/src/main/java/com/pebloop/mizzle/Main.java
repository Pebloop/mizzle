package com.pebloop.mizzle;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.pebloop.mizzle.data.DropletData;
import com.pebloop.mizzle.editor.EditorActionsExtern;
import com.pebloop.mizzle.editor.EditorScreen;
import com.pebloop.mizzle.event_builder.EventBuilderScreen;
import com.pebloop.mizzle.player.GamePlayerScreen;
import com.pebloop.mizzle.util.Graphics;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {

    private final Launcher mode;
    DropletData droplet = new DropletData();
    EditorActionsExtern actionsExtern = null;

    private Texture whitePixel;
    private Texture circle;

    public static Main getInstance() {
        return (Main) Gdx.app.getApplicationListener();
    }

    public Texture getWhitePixel() {
        return whitePixel;
    }

    public Texture getCircle() {
        return circle;
    }

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        whitePixel = Graphics.INSTANCE.createWhitePixelTexture();
        circle = Graphics.INSTANCE.createCircleTexture();

        switch (mode) {
            case EDITOR:
                setScreen(new EditorScreen(droplet, actionsExtern));
                break;
            case GAME:
                setScreen(new GamePlayerScreen(droplet, actionsExtern));
                break;
            case EVENT_BUILDER:
                setScreen(new EventBuilderScreen());
                break;
        }
    }

    public enum Launcher {
        EDITOR,
        GAME,
        EVENT_BUILDER
    }

    public Main(Launcher mode, DropletData droplet, EditorActionsExtern actions) {
        this.mode = mode;
        if (droplet != null) {
            this.droplet = droplet;
        }
        this.actionsExtern = actions;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (whitePixel != null) whitePixel.dispose();
        if (circle != null) circle.dispose();
    }

}
