package com.jjjackson.konchinka.listener;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jjjackson.konchinka.domain.GameModel;

public class SortButtonListener extends ClickListener {
    private GameModel model;

    public SortButtonListener(GameModel model) {
        this.model = model;
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        this.model.sortFog.setVisible(true);
        this.model.sortFog.validate();
        this.model.sortFog.pack();

        this.model.sortFog.toFront();
    }
}
