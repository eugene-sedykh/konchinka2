package com.jjjackson.konchinka.domain;

import java.util.ArrayList;
import java.util.List;

public class User extends CardHolder {

    public UserType userType;

    public List<Card> boardCards = new ArrayList<Card>();
    public List<Card> tricks = new ArrayList<Card>();

    public User(UserType userType, CardPosition cardPosition) {
        this.userType = userType;
        this.cardPosition = cardPosition;
    }
}
