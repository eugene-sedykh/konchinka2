package com.jjjackson.konchinka.domain;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.jjjackson.konchinka.GameConstants;

public class Card extends GameObject {

    public final CardSuit cardSuit;
    public final int value;
    public int endX;
    public int endY;
    public boolean showFace = true;
    public String face;

    public Card(CardSuit cardSuit, int value, Skin skin) {
        super(skin, "b1fv");
        this.face = cardSuit.getPrefix() + value;
        this.cardSuit = cardSuit;
        this.value = value;
        setY(-GameConstants.CARD_HEIGHT);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }
}
