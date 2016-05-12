package com.jjjackson.konchinka.objectmover;

import aurelienribon.tweenengine.TweenCallback;
import com.jjjackson.konchinka.GameConstants;

public class TweenInfo {
    public float x;
    public float y;
    public float angle;
    public float speed = GameConstants.CARD_SPEED;
    public TweenCallback tweenCallback;
    public float delay;
    public Object userData;
}
