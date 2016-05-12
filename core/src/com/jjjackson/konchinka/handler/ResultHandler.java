package com.jjjackson.konchinka.handler;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.jjjackson.konchinka.GameConstants;
import com.jjjackson.konchinka.domain.*;
import com.jjjackson.konchinka.domain.state.GameState;
import com.jjjackson.konchinka.domain.state.ResultState;
import com.jjjackson.konchinka.objectmover.ObjectMover;
import com.jjjackson.konchinka.objectmover.TweenInfo;
import com.jjjackson.konchinka.util.ActorHelper;
import com.jjjackson.konchinka.util.PositionCalculator;
import com.jjjackson.konchinka.util.ResultCalculator;

import java.util.List;

public class ResultHandler extends GameObjectHandler {

    private ResultCalculator resultCalculator = new ResultCalculator();

    public ResultHandler(GameModel model, ObjectMover tweenManager) {
        super(model, tweenManager);
    }

    @Override
    public void handle() {
        switch (model.states.result) {
            case INIT:
                model.buttons.newGameButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        model.states.game = GameState.NEW_GAME;
                        model.states.result = ResultState.CALCULATE;
                    }
                });
                model.buttons.mainMenuButton.addListener(new ClickListener());
                model.states.result = ResultState.CALCULATE;
                break;
            case CALCULATE:
                Array<User> users = new Array<>();
                users.add(model.player);
                users.addAll(model.opponents);
                resultCalculator.calculate(users);
                model.states.result = ResultState.RENDER;
                break;
            case RENDER:
                hideBottomLayer();
                moveAvatarsToCenter();
                model.states.result = ResultState.WAIT;
            case WAIT:
                break;
        }

    }

    private void hideBottomLayer() {
        Group bottomLayer = ActorHelper.getLayerByName(model.stage.getActors(), GameConstants.BOTTOM_LAYER_NAME);
        bottomLayer.setVisible(false);
    }

    private void moveAvatarsToCenter() {
        List<UserAvatar> avatars = getAvatars();

        for (int avatarIndex = 0; avatarIndex < avatars.size(); avatarIndex++) {
            Point destination = PositionCalculator.calcAvatarCenter(avatarIndex, avatars.size());
            TweenInfo tweenInfo = new TweenInfo();
            tweenInfo.x = destination.x;
            tweenInfo.y = destination.y;
            avatars.get(avatarIndex).tweenInfo = tweenInfo;
        }

        objectMover.move(avatars, false, new TweenCallback() {
            @Override
            public void onEvent(int i, BaseTween<?> baseTween) {
                Array<User> users = new Array(model.opponents);
                users.add(model.player);

                Group resultLayer = ActorHelper.getLayerByName(model.stage.getActors(), GameConstants.RESULT_LAYER_NAME);

                renderResultLabels((model.player.avatar.getY() - model.player.avatar.getHeight()), resultLayer);
                for (User user : users) {
                    renderResults(user, resultLayer);
                }

                resultLayer.addActor(createResultLine());
                resultLayer.addActor(model.buttons.mainMenuButton);
                resultLayer.addActor(model.buttons.newGameButton);

            }
        });
    }

    private ResultLine createResultLine() {
        List<UserAvatar> avatars = getAvatars();

        ResultLine resultLine = new ResultLine();
        Point startPoint = new Point();
        startPoint.x = (int) avatars.get(0).getX();
        startPoint.y = GameConstants.RESULT_LINE_Y;
        resultLine.setStartPoint(startPoint);
        return resultLine;
    }

    private void renderResultLabels(float y0, Group topLayer) {
        float imageX = GameConstants.RESULT_IMAGE_X;
        float imageY = y0 - GameConstants.RESULT_LABEL_Y_SHIFT;

        topLayer.addActor(createImage("result_a", imageX, imageY));

        imageY -= GameConstants.RESULT_LABEL_Y_SHIFT;
        topLayer.addActor(createImage("result_c2", imageX, imageY));

        imageY -= GameConstants.RESULT_LABEL_Y_SHIFT;
        topLayer.addActor(createImage("result_d10", imageX, imageY));

        imageY -= GameConstants.RESULT_LABEL_Y_SHIFT;
        topLayer.addActor(createImage("result_clubs", imageX, imageY));

        imageY -= GameConstants.RESULT_LABEL_Y_SHIFT;
        topLayer.addActor(createImage("result_pack", imageX, imageY));

        imageY -= GameConstants.RESULT_LABEL_Y_SHIFT;
        topLayer.addActor(createImage("result_trick", imageX, imageY));

    }

    private Actor createImage(String textureName, float imageX, float imageY) {
        Image image = new Image(model.skin, textureName);
        image.setX(imageX);
        image.setY(imageY);
        return image;
    }

    private void renderResults(User user, Group topLayer) {
        float labelX = user.avatar.getX() + GameConstants.RESULT_LABEL_X_SHIFT;
        float labelY = user.avatar.getY() - GameConstants.RESULT_LABEL_Y_SHIFT - GameConstants.RESULT_FONT_Y_SHIFT;

        topLayer.addActor(createLabel(user.gameResult.ace, labelX, labelY));

        labelY -= GameConstants.RESULT_LABEL_Y_SHIFT;
        topLayer.addActor(createLabel(user.gameResult.clubsTwo, labelX, labelY));

        labelY -= GameConstants.RESULT_LABEL_Y_SHIFT;
        topLayer.addActor(createLabel(user.gameResult.diamondsTen, labelX, labelY));

        labelY -= GameConstants.RESULT_LABEL_Y_SHIFT;
        topLayer.addActor(createLabel(user.gameResult.clubs, labelX, labelY));

        labelY -= GameConstants.RESULT_LABEL_Y_SHIFT;
        topLayer.addActor(createLabel(user.gameResult.cards, labelX, labelY));

        labelY -= GameConstants.RESULT_LABEL_Y_SHIFT;
        topLayer.addActor(createLabel(user.gameResult.tricks, labelX, labelY));

        labelY -= GameConstants.RESULT_LABEL_Y_SHIFT * 1.5;
        topLayer.addActor(createLabel(user.gameResult.total, labelX, labelY));
    }

    private Label createLabel(int value, float x, float y) {
        Label label = new Label(String.valueOf(value), model.skin);
        label.setX(x - 25);
        label.setY(y + 20);
        return label;
    }

}
