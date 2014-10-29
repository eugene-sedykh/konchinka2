package com.jjjackson.konchinka.domain;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.jjjackson.konchinka.GameConstants;

public class Card extends GameObject {

    public static final String BACK = "b1fv";
    public static final float MARK_OPACITY = 0.8f;
    public final CardSuit cardSuit;
    public int value;
    public int endX;
    public int endY;
    public String face;
    private boolean isMarked;

    public Card(CardSuit cardSuit, int value, Skin skin) {
        super(skin, BACK);
        this.face = cardSuit.getPrefix() + value;
        this.cardSuit = cardSuit;
        this.value = value;
        setY(-GameConstants.CARD_HEIGHT);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    public void showFace() {
        setDrawable(this.skin, this.face);
    }

    public void showBack() {
        setDrawable(this.skin, BACK);
    }

    public void mark() {
        this.isMarked = true;
        setColor(Color.GRAY.r, Color.GRAY.g, Color.GRAY.b, MARK_OPACITY);
    }

    public void unmark() {
        this.isMarked = false;
        setColor(Color.WHITE);
    }

    public boolean isMarked() {
        return this.isMarked;
    }
}
