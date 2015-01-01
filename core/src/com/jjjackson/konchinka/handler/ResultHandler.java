package com.jjjackson.konchinka.handler;

import aurelienribon.tweenengine.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.jjjackson.konchinka.GameConstants;
import com.jjjackson.konchinka.domain.*;
import com.jjjackson.konchinka.util.PositionCalculator;
import com.jjjackson.konchinka.util.ResultCalculator;

import java.util.*;

public class ResultHandler extends GameObjectHandler {

    private ResultCalculator resultCalculator = new ResultCalculator();

    public ResultHandler(GameModel model, TweenManager tweenManager) {
        super(model, tweenManager);
    }

    @Override
    public void handle() {
        switch (this.model.states.result) {
            case NONE:
                List<User> users = new ArrayList<>();
                users.add(this.model.player);
                users.addAll(this.model.opponents);
                this.resultCalculator.calculate(users);
                this.model.states.result = ResultState.RENDER;
                break;
            case RENDER:
                hideBottomLayer();
                moveAvatarsToCenter();
                this.model.states.result = ResultState.WAIT;
            case WAIT:
                break;
        }

    }

    private void hideBottomLayer() {
        Group bottomLayer = getLayerByName(this.model.stage, GameConstants.BOTTOM_LAYER_NAME);
        bottomLayer.setVisible(false);
    }

    private Group getLayerByName(Stage stage, String layerName) {
        for (Actor actor : stage.getActors()) {
            if (actor.getName().equals(layerName)) {
                return (Group) actor;
            }
        }

        return null;
    }

    private void moveAvatarsToCenter() {
        List<UserAvatar> avatars = getAvatars();

        Timeline timeline = Timeline.createParallel();
        for (int avatarIndex = 0; avatarIndex < avatars.size(); avatarIndex++) {
            Point destination = PositionCalculator.calcAvatarCenter(avatarIndex, avatars.size());
            timeline.push(createAvatarTween(avatars.get(avatarIndex), destination));
        }
        timeline.setCallbackTriggers(TweenCallback.COMPLETE)
                .setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int i, BaseTween<?> baseTween) {
                        List<User> users = new ArrayList<>(model.opponents);
                        users.add(model.player);

                        Group topLayer = getLayerByName(model.stage, GameConstants.TOP_LAYER_NAME);
                        Group resultLayer = getLayerByName(topLayer, GameConstants.RESULT_LAYER_NAME);

                        renderResultLabels((model.player.avatar.getY() - model.player.avatar.getHeight()), topLayer);
                        for (User user : users) {
                            renderResults(user, resultLayer);
                        }

                    }
                })
                .start(this.tweenManager);

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
        Image image = new Image(this.model.skin, textureName);
        image.setX(imageX);
        image.setY(imageY);
        return image;
    }

    private Group getLayerByName(Group group, String layerName) {
        return group.findActor(layerName);
    }

    private void renderResults(User user, Group topLayer) {
        float labelX = user.avatar.getX() + GameConstants.RESULT_LABEL_X_SHIFT;
        float labelY = user.avatar.getY()- GameConstants.RESULT_LABEL_Y_SHIFT;

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

        labelY -= GameConstants.RESULT_LABEL_Y_SHIFT * 2;
        topLayer.addActor(createLabel(user.gameResult.total, labelX, labelY));
    }

    private Label createLabel(int value, float x, float y) {
        Label label = new Label(String.valueOf(value), model.skin);
        label.setX(x - 25);
        label.setY(y + 20);
        label.setFontScale(3f);
        return label;
    }

    private List<UserAvatar> getAvatars() {
        List<UserAvatar> avatars = new ArrayList<>();

        avatars.add(this.model.player.avatar);
        for (User opponent : this.model.opponents) {
            avatars.add(opponent.avatar);
        }

        return avatars;
    }

    private Tween createAvatarTween(UserAvatar avatar, Point destination) {
        return Tween.to(avatar, GameObject.POSITION_XY, GameConstants.AVATAR_SPEED).
                target(destination.x, destination.y).
                start(this.tweenManager);
    }
}
