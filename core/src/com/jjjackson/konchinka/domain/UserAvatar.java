package com.jjjackson.konchinka.domain;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.jjjackson.konchinka.GameConstants;
import com.jjjackson.konchinka.util.PositionCalculator;


public class UserAvatar extends GameObject {

    private static final String STROKE_SUFFIX = "_stroke";

    private int avatarX;
    private int avatarY;

    public int getAvatarX() {
        return avatarX;
    }

    public int getAvatarY() {
        return avatarY;
    }

    private TextureRegion avatar;
    private TextureRegion avatarStroke;
    private boolean isUserActive;

    public UserAvatar(Skin skin, String name) {
        this.avatar = skin.getRegion(name);
        this.avatarStroke = skin.getRegion(name + STROKE_SUFFIX);
    }

    public void initPosition(CardPosition cardPosition, int opponentsNumber) {
        Point point = PositionCalculator.calcTrick(cardPosition, opponentsNumber);
        this.avatarX = point.x + ((GameConstants.CARD_WIDTH - this.avatar.getRegionWidth()) / 2);
        this.avatarY = point.y + ((GameConstants.CARD_HEIGHT - this.avatar.getRegionHeight()) / 2);
        setX(this.avatarX);
        setY(this.avatarY);
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        return null;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(this.avatar, getX(), getY());
        if (this.isUserActive) {
            batch.draw(this.avatarStroke, getX(), getY());
        }
    }

    public void activate() {
        this.isUserActive = true;
    }

    public void deactivate() {
        this.isUserActive = false;
    }
}
