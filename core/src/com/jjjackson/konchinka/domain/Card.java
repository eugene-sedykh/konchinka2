package com.jjjackson.konchinka.domain;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.jjjackson.konchinka.GameConstants;

public class Card extends GameObject {

    public final CardSuit cardSuit;
    public final int value;
    public int endX;
    public int endY;

    public Card(Skin skin, CardSuit cardSuit, int value) {
        super(skin, cardSuit.getPrefix() + value);
        this.cardSuit = cardSuit;
        this.value = value;
        setY(-GameConstants.CARD_HEIGHT);
    }

}
