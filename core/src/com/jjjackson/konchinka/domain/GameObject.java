package com.jjjackson.konchinka.domain;

import aurelienribon.tweenengine.TweenAccessor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public abstract class GameObject extends Image implements TweenAccessor<GameObject> {

    public static final int POSITION_XY = 1;

    public GameObject(Skin skin, String drawableName) {
        super(skin, drawableName);
    }

    public GameObject() {
        super();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    @Override
    public int getValues(GameObject gameObject, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case POSITION_XY:
                returnValues[0] = gameObject.getX();
                returnValues[1] = gameObject.getY();
                return 2;
            default:
                assert false; return -1;
        }
    }

    @Override
    public void setValues(GameObject gameObject, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case POSITION_XY:
                gameObject.setX(returnValues[0]);
                gameObject.setY(returnValues[1]);
                break;
            default:
                assert false; break;
        }
    }
}
