package com.jjjackson.konchinka;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.jjjackson.konchinka.domain.Card;
import com.jjjackson.konchinka.domain.DealState;
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
            card.setDrawable(this.skin, card.face);
            card.pack();
        }
    }

    public void show() {

    }
}
