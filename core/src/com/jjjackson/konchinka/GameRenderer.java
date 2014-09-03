package com.jjjackson.konchinka;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Elastic;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.jjjackson.konchinka.domain.Card;
import com.jjjackson.konchinka.domain.GameModel;
import com.jjjackson.konchinka.domain.GameObject;
import com.jjjackson.konchinka.domain.Pack;

public class GameRenderer {

    private GameModel model;
    private Stage stage;

    public GameRenderer(GameModel model, Stage stage) {
        this.model = model;
        this.stage = stage;

        this.stage.addActor(this.model.pack);

        for (Card card : this.model.cards) {
            this.stage.addActor(card);
        }

    }

    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        this.stage.act(delta);
        this.stage.draw();
    }

    public void show() {

    }
}
