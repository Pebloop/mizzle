package com.pebloop.mizzle;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.pebloop.mizzle.data.DropletData;
import com.pebloop.mizzle.data.components.Event;
import com.pebloop.mizzle.editor.EditorActionsExtern;
import com.pebloop.mizzle.editor.EditorScreen;
import com.pebloop.mizzle.event_builder.EventBuilderScreen;
import com.pebloop.mizzle.player.GamePlayerScreen;
import com.pebloop.mizzle.util.Graphics;

import java.util.HashMap;
import java.util.Map;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {

    private final Launcher mode;
    DropletData droplet = new DropletData();
    EditorActionsExtern actionsExtern = null;
    Event event = null;

    private Texture whitePixel;
    private Texture circle;
    private BitmapFont font;

    private Map<String, TextureRegion> userTextures = new HashMap<>();
    private Map<String, Texture> textureCache = new HashMap<>();

    public static Main getInstance() {
        return (Main) Gdx.app.getApplicationListener();
    }

    public Texture getWhitePixel() {
        return whitePixel;
    }

    public Texture getCircle() {
        return circle;
    }

    public BitmapFont getFont() {
        return font;
    }

    public DropletData getDroplet() {
        return droplet;
    }

    public void setUserTexture(String name, TextureRegion region) {
        userTextures.put(name, region);
    }

    public void addCachedTexture(String path, Texture texture) {
        if (textureCache.containsKey(path)) {
            textureCache.get(path).dispose();
        }
        textureCache.put(path, texture);
    }

    public Texture getCachedTexture(String path) {
        return textureCache.get(path);
    }

    public TextureRegion getUserTexture(String name) {
        return userTextures.get(name);
    }

    public void clearCache() {
        for (Texture texture : textureCache.values()) {
            if (texture != null) texture.dispose();
        }
        textureCache.clear();
        userTextures.clear();
    }

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        whitePixel = Graphics.INSTANCE.createWhitePixelTexture();
        circle = Graphics.INSTANCE.createCircleTexture();
        font = new BitmapFont(Gdx.files.internal("roses.fnt"), Gdx.files.internal("roses.png"), false);

        switch (mode) {
            case EDITOR:
                setScreen(new EditorScreen(droplet, actionsExtern));
                break;
            case GAME:
                setScreen(new GamePlayerScreen(droplet, actionsExtern));
                break;
            case EVENT_BUILDER:
                setScreen(new EventBuilderScreen(event, actionsExtern));
                break;
        }
    }

    public enum Launcher {
        EDITOR,
        GAME,
        EVENT_BUILDER
    }

    public Main(Launcher mode, DropletData droplet, EditorActionsExtern actions) {
        this(mode, droplet, actions, null);
    }

    public Main(Launcher mode, DropletData droplet, EditorActionsExtern actions, Event event) {
        this.mode = mode;
        if (droplet != null) {
            this.droplet = droplet;
        }
        this.actionsExtern = actions;
        this.event = event;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (whitePixel != null) whitePixel.dispose();
        if (circle != null) circle.dispose();
        if (font != null) font.dispose();
        for (Texture texture : textureCache.values()) {
            if (texture != null) texture.dispose();
        }
        textureCache.clear();
        userTextures.clear();
    }

}
