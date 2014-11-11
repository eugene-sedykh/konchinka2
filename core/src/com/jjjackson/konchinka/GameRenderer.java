package com.jjjackson.konchinka;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.jjjackson.konchinka.domain.Card;
import com.jjjackson.konchinka.domain.Fog;
import com.jjjackson.konchinka.domain.GameModel;

public class GameRenderer {

    private GameModel model;
    private Stage stage;
    private Skin skin;

    public GameRenderer(GameModel model, Stage stage, Skin skin) {
        this.model = model;
        this.stage = stage;
        this.skin = skin;

        this.stage.addActor(this.model.pack);

        for (Card card : this.model.cards) {
            this.stage.addActor(card);
        }

        initGameButtons(this.stage);
        initSortFog(this.stage);
    }

    private void initSortFog(Stage stage) {
        Fog fog = new Fog();
        fog.setVisible(false);
        this.model.fog = fog;
        stage.addActor(fog);
    }

    private void initGameButtons(Stage stage) {
        TextButton.TextButtonStyle textButtonStyle = createButtonStyle();
        TextButton sortButton = createButton("Sort", textButtonStyle, 100, 150);
        TextButton endSortButton = createButton("Done", textButtonStyle, 100, 150);
        TextButton trickButton = createButton("Trick", textButtonStyle, 380, 150);
        TextButton endButton = createButton("End", textButtonStyle, 380, 150);
        stage.addActor(sortButton);
        stage.addActor(endSortButton);
        stage.addActor(trickButton);
        stage.addActor(endButton);
        this.model.buttons.sortButton = sortButton;
        this.model.buttons.endSortButton = endSortButton;
        this.model.buttons.trickButton = trickButton;
        this.model.buttons.endButton = endButton;
    }

    private TextButton.TextButtonStyle createButtonStyle() {
        BitmapFont font = new BitmapFont(Gdx.files.internal("font/calibri.fnt"), Gdx.files.internal("font/calibri.png"), false);
        TextureAtlas buttonAtlas = new TextureAtlas(Gdx.files.internal("buttons/menu_button.pack"));
        this.skin.addRegions(buttonAtlas);
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.up = this.skin.getDrawable("menu_button_normal");
        textButtonStyle.down = this.skin.getDrawable("menu_button_pressed");
        return textButtonStyle;
    }

    private TextButton createButton(String text, TextButton.TextButtonStyle textButtonStyle, int x, int y) {
        TextButton sortButton = new TextButton(text, textButtonStyle);
        sortButton.setPosition(x, y);
        sortButton.setVisible(false);
        return sortButton;
    }

    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        this.stage.act(delta);
        this.stage.draw();
    }

    public void show() {

    }
}
