package com.jjjackson.konchinka.domain;

import aurelienribon.tweenengine.TweenAccessor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public abstract class GameObject extends Image implements TweenAccessor<GameObject> {

    public static final int POSITION_XY = 1;
    public static final int ROTATION_XY = 2;
    public static final int COLOR = 3;
    protected Skin skin;

    public GameObject(Skin skin, String drawableName) {
        super(skin, drawableName);
        this.skin = skin;
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
            case ROTATION_XY:
                returnValues[0] = gameObject.getX();
                returnValues[1] = gameObject.getY();
                returnValues[2] = gameObject.getRotation();
                return 3;
            case COLOR:
                returnValues[0] = gameObject.getColor().r;
                returnValues[1] = gameObject.getColor().g;
                returnValues[2] = gameObject.getColor().b;
//                returnValues[3] = gameObject.getColor().a;
                return 3;
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
            case ROTATION_XY:
                gameObject.setX(returnValues[0]);
                gameObject.setY(returnValues[1]);
                gameObject.setRotation(returnValues[2]);
                break;
            case COLOR:
                gameObject.setColor(returnValues[0], returnValues[1], returnValues[2], 1);
                gameObject.pack();
                break;
            default:
                assert false; break;
        }
    }
}
