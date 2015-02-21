package com.jjjackson.konchinka.domain;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.jjjackson.konchinka.GameConstants;

import java.util.ArrayList;
import java.util.List;

public class Pack extends GameObject {
    public List<Card> cards = new ArrayList<>();

    private PackTexture packTexture = PackTexture.PACK_4_4;

    private Point showPosition = new Point();
    private Point hidePosition = new Point();

    public Pack(Skin skin, String drawableName) {
        super(skin, drawableName);
    }

    public Point getHidePosition() {
        return hidePosition;
    }

    public Point getShowPosition() {
        return showPosition;
    }

    public void initPositions(CardPosition dealerPosition) {
        switch (dealerPosition) {
            case LEFT:
                this.showPosition.x = 150;
                this.showPosition.y = 350;
                this.hidePosition.x = -100;
                this.hidePosition.y = 350;
                break;
            case TOP:
                this.showPosition.x = 225;
                this.showPosition.y = 650;
                this.hidePosition.x = 225;
                this.hidePosition.y = 900;
                break;
            case RIGHT:
                this.showPosition.x = 330;
                this.showPosition.y = 350;
                this.hidePosition.x = 490;
                this.hidePosition.y = 350;
                break;
            case BOTTOM:
                this.showPosition.x = 225;
                this.showPosition.y = 225;
                this.hidePosition.x = 225;
                this.hidePosition.y = -120;
                break;
        }
    }

    public void hideOnDealerSide() {
        setX(this.hidePosition.x);
        setY(this.hidePosition.y);
    }

    public void refreshTexture() {
        PackTexture preferredTexture = PackTexture.getTexture(this.cards.size());
        if (this.packTexture != preferredTexture) {
            this.packTexture = preferredTexture;
            if (preferredTexture == PackTexture.PACK_0) {
                setVisible(false);
            } else {
                setVisible(true);
                setDrawable(this.skin, preferredTexture.getTextureName());
                pack();
            }
        }
    }

    public int getTopCardY() {
        return (int) (getY() + getHeight() - GameConstants.CARD_HEIGHT);
    }

    public int getTopCardX() {
        return (int) (getX() + getWidth() - GameConstants.CARD_WIDTH);
    }
}
