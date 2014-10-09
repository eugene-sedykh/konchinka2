package com.jjjackson.konchinka.domain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;

import javax.microedition.khronos.opengles.GL10;

public class Fog extends Actor {

    private ShapeRenderer shapeRenderer = new ShapeRenderer();

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        return null;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.end();
        Gdx.gl.glEnable(GL10.GL_BLEND);
        Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        this.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        this.shapeRenderer.setColor(Color.GRAY.r, Color.GRAY.g, Color.GRAY.b, 0.3f);
        this.shapeRenderer.rect(0, 0, Gdx.app.getGraphics().getWidth(), Gdx.app.getGraphics().getHeight());
        this.shapeRenderer.end();
        Gdx.gl.glDisable(GL10.GL_BLEND);
        batch.begin();
    }
}
