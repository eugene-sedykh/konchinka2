package com.jjjackson.konchinka.inputadapter;

import com.badlogic.gdx.InputAdapter;
import com.jjjackson.konchinka.domain.GameModel;
import com.jjjackson.konchinka.domain.state.GameState;
import com.jjjackson.konchinka.domain.state.PauseState;

import static com.badlogic.gdx.Input.Keys;

public class PauseInputProcessor extends InputAdapter {
    private GameModel model;

    public PauseInputProcessor(GameModel gameModel) {
        model = gameModel;
    }

    @Override
    public boolean keyDown(int keycode) {
        if ((keycode == Keys.ESCAPE) || (keycode == Keys.BACK) )
            if (model.states.game == GameState.PAUSE) {
                model.states.pause = PauseState.RESUME;
            } else {
                model.states.previousState = model.states.game;
                model.states.game = GameState.PAUSE;
                model.states.pause = PauseState.INIT;
            }
        return false;
    }
}
