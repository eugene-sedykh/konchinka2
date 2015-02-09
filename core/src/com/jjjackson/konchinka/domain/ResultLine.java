package com.jjjackson.konchinka.domain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;


public class ResultLine extends Actor {

    private static final int WIDTH = 350;
    private static final int HEIGHT = 3;

    private ShapeRenderer shapeRenderer = new ShapeRenderer();
    private Point startPoint;

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
        this.shapeRenderer.setColor(Color.BLACK);
        this.shapeRenderer.rect(this.startPoint.x, this.startPoint.y, WIDTH, HEIGHT);
        this.shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        batch.begin();
    }

    public void setStartPoint(Point startPoint) {
        this.startPoint = startPoint;
    }
}
