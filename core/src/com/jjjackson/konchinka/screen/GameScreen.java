package com.jjjackson.konchinka.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.jjjackson.konchinka.GameConstants;
import com.jjjackson.konchinka.GameController;
import com.jjjackson.konchinka.GameRenderer;
import com.jjjackson.konchinka.KonchinkaGame;
import com.jjjackson.konchinka.domain.*;
import com.jjjackson.konchinka.util.PlayerUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GameScreen implements Screen {

    private KonchinkaGame konchinkaGame;
    private GameController gameController;
    private GameRenderer gameRenderer;
    private Stage stage;
    private Skin skin;

    private List<UserAvatar> cpuAvatars = new ArrayList<>();
    private UserAvatar userAvatar;

    public GameScreen(KonchinkaGame konchinkaGame) {
        this.konchinkaGame = konchinkaGame;
        this.stage = new Stage();
        this.skin = new Skin(Gdx.files.internal("uiskin.json"));
        this.skin.addRegions(new TextureAtlas(Gdx.files.internal("img/cards.pack")));
        this.skin.addRegions(new TextureAtlas(Gdx.files.internal("img/avatars/avatars.pack")));
        GameModel gameModel = initModel();
        gameModel.skin = this.skin;
        gameModel.stage = this.stage;
        this.gameRenderer = new GameRenderer(gameModel, this.stage, this.skin);
        this.gameController = new GameController(gameModel);
    }

    private GameModel initModel() {
        GameModel gameModel = new GameModel();
        gameModel.states = new States();
        initPack(gameModel);
        initCards(gameModel);
        gameModel.dealerPosition = CardPosition.RIGHT;
        initAvatars();
        initPlayers(gameModel, 4, gameModel.dealerPosition);
        return gameModel;
    }

    private void initPack(GameModel gameModel) {
        gameModel.pack = new Pack(this.skin, "full_pack_diagonal");
        gameModel.pack.setX(GameConstants.PACK_X);
        gameModel.pack.setY(GameConstants.PACK_BOTTOM_HIDE_Y);
    }

    private void initCards(GameModel gameModel) {
        for (int i = 1; i < 14; i++) {
            gameModel.pack.cards.add(new Card(CardSuit.CLUBS, i, this.skin));
            gameModel.pack.cards.add(new Card(CardSuit.DIAMONDS, i, this.skin));
            gameModel.pack.cards.add(new Card(CardSuit.HEARTS, i, this.skin));
            gameModel.pack.cards.add(new Card(CardSuit.SPADES, i, this.skin));
        }
        Collections.shuffle(gameModel.pack.cards);
    }

    private void initAvatars() {
        this.cpuAvatars.add(new UserAvatar(this.skin, "axakov"));
        this.cpuAvatars.add(new UserAvatar(this.skin, "chehov"));
        this.cpuAvatars.add(new UserAvatar(this.skin, "krylov"));
        this.cpuAvatars.add(new UserAvatar(this.skin, "lermontov"));
        this.cpuAvatars.add(new UserAvatar(this.skin, "pushkin"));
        this.cpuAvatars.add(new UserAvatar(this.skin, "ushinskiy"));

        this.userAvatar = new UserAvatar(this.skin, "user");
    }

    private void initPlayers(GameModel model, int playersNumber, final CardPosition dealerPosition) {
        model.table = new Table();

        switch (playersNumber) {
            case 2:
                model.opponents.add(new User(UserType.COMPUTER, CardPosition.TOP, 1, getCpuAvatar()));
                break;
            case 3:
                model.opponents.add(new User(UserType.COMPUTER, CardPosition.LEFT, 2, getCpuAvatar()));
                model.opponents.add(new User(UserType.COMPUTER, CardPosition.RIGHT, 2, getCpuAvatar()));
                break;
            case 4:
                model.opponents.add(new User(UserType.COMPUTER, CardPosition.LEFT, 3, getCpuAvatar()));
                model.opponents.add(new User(UserType.COMPUTER, CardPosition.TOP, 3, getCpuAvatar()));
                model.opponents.add(new User(UserType.COMPUTER, CardPosition.RIGHT, 3, getCpuAvatar()));
        }
        model.player = new User(UserType.PLAYER, CardPosition.BOTTOM, model.opponents.size, this.userAvatar);

        model.cardHolders = new Array<>();
        model.cardHolders.addAll(model.opponents);
        model.cardHolders.add(model.player);

        PlayerUtil.prepareCardHoldersForDealing(model, dealerPosition);
    }

    private UserAvatar getCpuAvatar() {
        Random random = new Random();
        int avatarIndex = random.nextInt(this.cpuAvatars.size());
        return this.cpuAvatars.remove(avatarIndex);
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
        this.stage.dispose();
        this.skin.dispose();
        this.gameRenderer.dispose();
    }
}
