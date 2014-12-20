package com.jjjackson.konchinka.domain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.jjjackson.konchinka.GameConstants;
import com.jjjackson.konchinka.util.PositionCalculator;


public class UserAvatar extends GameObject {

    private ShapeRenderer shapeRenderer = new ShapeRenderer();
    private int avatarX;
    private int avatarY;
    private int width = GameConstants.CARD_WIDTH;
    private int height = GameConstants.CARD_HEIGHT;
    private float alpha = 0.0f;

    public UserAvatar(CardPosition cardPosition, int opponentsNumber) {
        Point point = PositionCalculator.calcTrick(cardPosition, opponentsNumber);
        setX(point.x);
        setY(point.y);
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        return null;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.end();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        this.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        this.shapeRenderer.setColor(Color.GREEN.r, Color.GREEN.g, Color.GREEN.b, this.alpha);
        this.shapeRenderer.rect(this.getX(), this.getY(), this.width, this.height);
        this.shapeRenderer.end();
        this.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        this.shapeRenderer.setColor(Color.RED.r, Color.RED.g, Color.RED.b, 1);
        this.shapeRenderer.rect(this.getX() + 1, this.getY(), this.width - 2, this.height);
        this.shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        batch.begin();
    }

    public void activate() {
        this.alpha = 0.4f;
        toFront();
    }

    public void deactivate() {
        this.alpha = 0.0f;
    }
}
