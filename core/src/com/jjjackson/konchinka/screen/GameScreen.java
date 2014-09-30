package com.jjjackson.konchinka.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.jjjackson.konchinka.GameConstants;
import com.jjjackson.konchinka.GameController;
import com.jjjackson.konchinka.GameRenderer;
import com.jjjackson.konchinka.KonchinkaGame;
import com.jjjackson.konchinka.domain.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameScreen implements Screen {

    private KonchinkaGame konchinkaGame;
    private GameController gameController;
    private GameRenderer gameRenderer;
    private Stage stage;
    private Skin skin;

    public GameScreen(KonchinkaGame konchinkaGame) {
        this.konchinkaGame = konchinkaGame;
        this.stage = new Stage();
        this.skin = new Skin();
        this.skin.addRegions(new TextureAtlas(Gdx.files.internal("img/cards.pack")));
        GameModel gameModel = initModel();
        this.gameController = new GameController(gameModel);
        this.gameRenderer = new GameRenderer(this.gameController.model, this.stage, this.skin);
    }

    private GameModel initModel() {
        GameModel gameModel = new GameModel();
        gameModel.states = new States();
        initPack(gameModel);
        initCards(gameModel);
        gameModel.dealerPosition = CardPosition.RIGHT;
        initPlayers(gameModel, 4, gameModel.dealerPosition);
        return gameModel;
    }

    private void initPack(GameModel gameModel) {
        gameModel.pack = new Pack(this.skin, "b1fv");
        gameModel.pack.setX(GameConstants.PACK_X);
        gameModel.pack.setY(GameConstants.PACK_BOTTOM_HIDE_Y);
    }

    private void initCards(GameModel gameModel) {
        for (int i = 1; i < 14; i++) {
            gameModel.cards.add(new Card(CardSuit.CLUBS, i, this.skin));
            gameModel.cards.add(new Card(CardSuit.DIAMONDS, i, this.skin));
            gameModel.cards.add(new Card(CardSuit.HEARTS, i, this.skin));
            gameModel.cards.add(new Card(CardSuit.SPADES, i, this.skin));
        }
        Collections.shuffle(gameModel.cards);
    }

    private void initPlayers(GameModel model, int playersNumber, final CardPosition dealerPosition) {
        model.table = new Table();
        switch (playersNumber) {
            case 2:
                model.opponents.add(new User(UserType.COMPUTER, CardPosition.TOP));
                break;
            case 3:
                model.opponents.add(new User(UserType.COMPUTER, CardPosition.LEFT));
                model.opponents.add(new User(UserType.COMPUTER, CardPosition.RIGHT));
                break;
            case 4:
                model.opponents.add(new User(UserType.COMPUTER, CardPosition.LEFT));
                model.opponents.add(new User(UserType.COMPUTER, CardPosition.TOP));
                model.opponents.add(new User(UserType.COMPUTER, CardPosition.RIGHT));
        }
        model.player = new User(UserType.PLAYER, CardPosition.BOTTOM);

        model.cardHolders = new ArrayList<>();
        model.cardHolders.addAll(model.opponents);
        model.cardHolders.add(model.player);

        sort(model.cardHolders, dealerPosition);

        model.cardHolders.add(model.cardHolders.size() - 1, model.table);

        model.cardHolders.get(0).isCurrent = true;
    }

    private void sort(List<CardHolder> cardHolders, CardPosition dealerPosition) {
        int dealerIndex = getDealerIndex(cardHolders, dealerPosition);
        Collections.rotate(cardHolders, cardHolders.size() - dealerIndex - 1);
    }

    private int getDealerIndex(List<CardHolder> cardHolders, CardPosition dealerPosition) {
        for (int i = 0; i < cardHolders.size(); i++) {
            if (cardHolders.get(i).cardPosition == dealerPosition) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public void render(float delta) {
        this.gameController.update(delta);
        this.gameRenderer.render(delta);
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this.stage);
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }
}
