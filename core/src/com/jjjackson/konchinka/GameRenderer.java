package com.jjjackson.konchinka;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.jjjackson.konchinka.domain.Card;
import com.jjjackson.konchinka.domain.state.DealState;
import com.jjjackson.konchinka.domain.Fog;
import com.jjjackson.konchinka.domain.GameModel;

import java.util.List;

public class GameRenderer {

    public static final String BACK_DRAWABLE_NAME = "b1fv";
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

        showCardsFace(this.model.player.playCards);
        showCardsFace(this.model.table.playCards);

        if (this.model.states.deal == DealState.JACK_OUT) {
            showJacksBack(this.model.cards);
        }

        this.stage.act(delta);
        this.stage.draw();
    }

    private void showJacksBack(List<Card> cards) {
        for (Card card : cards) {
            if (card.value == GameConstants.JACK_VALUE) {
                card.setDrawable(this.skin, BACK_DRAWABLE_NAME);
                card.pack();
            }
        }
    }

    private void showCardsFace(List<Card> cards) {
        for (Card card : cards) {
            if (this.model.player.tricks.contains(card)) return;
            card.setDrawable(this.skin, card.face);
            if (card.isMarked) {
                card.setColor(Color.GRAY.r, Color.GRAY.g, Color.GRAY.b, 0.8f);
            } else {
                card.setColor(Color.WHITE);
            }
            card.pack();
        }
    }

    public void show() {

    }
}
