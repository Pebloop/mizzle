package com.pebloop.mizzle;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.pebloop.mizzle.editor.EditorScreen;
import com.pebloop.mizzle.event_builder.EventBuilderScreen;
import com.pebloop.mizzle.player.GamePlayerScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {

    private final Launcher mode;

    @Override
    public void create() {
        switch (mode) {
            case EDITOR:
                setScreen(new EditorScreen());
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

    public Main(Launcher mode) {
        this.mode = mode;
    }

}
