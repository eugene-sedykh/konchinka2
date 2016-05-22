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
        Gdx.input.setCatchBackKey(true);
        mainMenuScreen = new MainMenuScreen(this);
        gameScreen = new GameScreen(this);
        setScreen(mainMenuScreen);
    }

    @Override
    public void dispose() {
        gameScreen.dispose();
        Gdx.app.exit();
    }
}
