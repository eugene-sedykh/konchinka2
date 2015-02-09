package com.jjjackson.konchinka.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.jjjackson.konchinka.KonchinkaGame;

public class MainMenuScreen implements Screen {

    private final KonchinkaGame konchinkaGame;

    private Stage stage;
    TextButton button;
    TextButton.TextButtonStyle textButtonStyle;
    BitmapFont font;
    Skin skin;
    TextureAtlas buttonAtlas;

    public MainMenuScreen(final KonchinkaGame konchinkaGame) {
        this.konchinkaGame = konchinkaGame;
        this.stage = new Stage();

//        this.font = new BitmapFont(Gdx.files.internal("font/torhok.fnt"), Gdx.files.internal("font/torhok.png"), false);
        this.font = new BitmapFont(Gdx.files.internal("font/default.fnt"), Gdx.files.internal("font/default.png"), false);
        this.skin = new Skin();
        this.buttonAtlas = new TextureAtlas(Gdx.files.internal("buttons/menu_button.pack"));
        this.skin.addRegions(buttonAtlas);
        this.textButtonStyle = new TextButton.TextButtonStyle();
        this.textButtonStyle.font = this.font;
        this.textButtonStyle.up = this.skin.getDrawable("menu_button_normal");
        this.textButtonStyle.down = this.skin.getDrawable("menu_button_pressed");
        this.button = new TextButton("Новая игра", this.textButtonStyle);
        this.button.setPosition(200, 500);
        this.stage.addActor(button);

        this.button.addListener(new ChangeListener() {
            public void changed (ChangeListener.ChangeEvent event, Actor actor) {
                konchinkaGame.setScreen(konchinkaGame.gameScreen);
            }
        });
    }

    @Override
    public void render(float delta) {
        this.stage.draw();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this.stage);
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }
}
