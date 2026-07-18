package com.pebloop.mizzle;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.pebloop.mizzle.data.DropletData;
import com.pebloop.mizzle.editor.EditorActionsExtern;
import com.pebloop.mizzle.editor.EditorScreen;
import com.pebloop.mizzle.event_builder.EventBuilderScreen;
import com.pebloop.mizzle.player.GamePlayerScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {

    private final Launcher mode;
    DropletData droplet = null;
    EditorActionsExtern actionsExtern = null;

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        switch (mode) {
            case EDITOR:
                setScreen(new EditorScreen(droplet, actionsExtern));
                break;
            case GAME:
                setScreen(new GamePlayerScreen());
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
        this.droplet = droplet;
        this.actionsExtern = actions;
    }

}
