package com.jjjackson.konchinka.domain;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.ArrayList;
import java.util.List;

public class Pack extends GameObject {
    List<Card> cards = new ArrayList<>();

    public Pack(Skin skin, String drawableName) {
        super(skin, drawableName);
    }
}
