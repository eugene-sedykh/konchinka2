package com.jjjackson.konchinka;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.jjjackson.konchinka.screen.GameScreen;
import com.jjjackson.konchinka.screen.MainMenuScreen;

public class KonchinkaGame extends Game {

    public MainMenuScreen mainMenuScreen;
    public GameScreen gameScreen;

    @Override
    public void create() {
        this.mainMenuScreen = new MainMenuScreen(this);
        this.gameScreen = new GameScreen(this);
        setScreen(this.mainMenuScreen);
    }

    @Override
    public void dispose() {
        this.gameScreen.dispose();
        Gdx.app.exit();
    }
}
