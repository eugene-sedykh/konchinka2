package com.jjjackson.konchinka.handler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jjjackson.konchinka.GameConstants;
import com.jjjackson.konchinka.domain.GameModel;
import com.jjjackson.konchinka.domain.state.PauseState;
import com.jjjackson.konchinka.objectmover.ObjectMover;
import com.jjjackson.konchinka.util.ActorHelper;

import java.util.ArrayList;
import java.util.List;

public class PauseHandler extends GameObjectHandler {

    private List<Actor> enabledActors = new ArrayList<>();

    public PauseHandler(final GameModel model, ObjectMover objectMover) {
        super(model, objectMover);
        model.buttons.pauseContinue.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                model.states.pause = PauseState.RESUME;
            }
        });
        model.buttons.pauseExit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        model.buttons.pauseMainMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                model.game.setScreen(model.game.mainMenuScreen);
            }
        });
    }

    @Override
    public void handle() {
        switch (model.states.pause) {
            case INIT:
                initPause();
                return;
            case WAIT:
                return;
            case RESUME:
                resumeGame();
                return;
        }
    }

    private void initPause() {
        model.pauseFog.setVisible(true);
        model.pauseFog.toFront();
        model.states.pause = PauseState.WAIT;
        addPauseMenu();
        objectMover.getTweenManager().pause();
        initEnabledActors();
        setTouchable(enabledActors, Touchable.disabled);
    }

    private void addPauseMenu() {
        Group fogLayer = ActorHelper.getLayerByName(model.stage.getActors(), GameConstants.TOP_LAYER_NAME);
        fogLayer.addActor(model.buttons.pauseContinue);
        fogLayer.addActor(model.buttons.pauseMainMenu);
        fogLayer.addActor(model.buttons.pauseExit);
    }

    private void initEnabledActors() {
        enabledActors.clear();
        for (Actor card : model.actors) {
            if (card.getTouchable().equals(Touchable.enabled)) {
                enabledActors.add(card);
            }
        }
        addButtonIfEnabled(model.buttons.endButton, enabledActors);
        addButtonIfEnabled(model.buttons.endSortButton, enabledActors);
        addButtonIfEnabled(model.buttons.mainMenuButton, enabledActors);
        addButtonIfEnabled(model.buttons.newGameButton, enabledActors);
        addButtonIfEnabled(model.buttons.sortButton, enabledActors);
        addButtonIfEnabled(model.buttons.trickButton, enabledActors);
    }

    private void addButtonIfEnabled(TextButton button, List<Actor> enabledActors) {
        if (button != null) {
            if (button.getTouchable().equals(Touchable.enabled)) {
                enabledActors.add(button);
            }
        }
    }

    private void setTouchable(List<Actor> actors, Touchable touchable) {
        for (Actor actor : actors) {
            if (actor != null) {
                actor.setTouchable(touchable);
            }
        }
    }

    private void resumeGame() {
        model.states.game = model.states.previousState;
        model.pauseFog.setVisible(false);
        model.pauseFog.toBack();
        objectMover.getTweenManager().resume();
        setTouchable(enabledActors, Touchable.enabled);
        removeMenu();
    }

    private void removeMenu() {
        Group fogLayer = ActorHelper.getLayerByName(model.stage.getActors(), GameConstants.TOP_LAYER_NAME);
        fogLayer.removeActor(model.buttons.pauseContinue);
        fogLayer.removeActor(model.buttons.pauseMainMenu);
        fogLayer.removeActor(model.buttons.pauseExit);
    }
}
