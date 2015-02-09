package com.jjjackson.konchinka.domain;

import java.util.ArrayList;
import java.util.List;

public class User extends CardHolder {

    public UserAvatar avatar;

    public UserType userType;

    public List<Card> boardCards = new ArrayList<>();
    public List<Card> tricks = new ArrayList<>();

    public GameResult gameResult = new GameResult();

    public User(UserType userType, CardPosition cardPosition, int opponentsNumber, UserAvatar userAvatar) {
        this.userType = userType;
        this.cardPosition = cardPosition;
        this.avatar = userAvatar;
        this.avatar.initPosition(cardPosition, opponentsNumber);
    }

    public void activate() {
        this.avatar.activate();
    }

    public void deactivate() {
        this.avatar.deactivate();
    }
}
