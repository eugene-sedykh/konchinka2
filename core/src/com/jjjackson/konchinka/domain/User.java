package com.jjjackson.konchinka.domain;

import com.badlogic.gdx.scenes.scene2d.Stage;

import java.util.ArrayList;
import java.util.List;

public class User extends CardHolder {

    private UserAvatar userAvatar;

    public UserType userType;

    public List<Card> boardCards = new ArrayList<>();
    public List<Card> tricks = new ArrayList<>();

    public User(UserType userType, CardPosition cardPosition, Stage stage) {
        this.userType = userType;
        this.cardPosition = cardPosition;
        this.userAvatar = new UserAvatar(cardPosition);
        stage.addActor(this.userAvatar);
    }

    public void activate() {
        this.userAvatar.activate();
    }

    public void deactivate() {
        this.userAvatar.deactivate();
    }
}
