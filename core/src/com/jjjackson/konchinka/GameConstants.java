package com.jjjackson.konchinka;

import java.util.Arrays;
import java.util.List;

public class GameConstants {

    public final static List<String> VALUABLE_CARDS = Arrays.asList("c1", "d1", "h1", "s1", "d10", "s2");

    public static final int SCREEN_WIDTH = 480;
    public static final int SCREEN_HEIGHT = 800;

    public static final int PLAY_CARD_X = 205;
    public static final int PLAY_CARD_Y = 225;

    public static final int TOP_BOARD_Y = 125;

    public static final int CARD_WIDTH = 71;
    public static final int CARD_HEIGHT = 96;
    public static final int TABLE_CARDS_GAP = 4;

    public static final int BUTTON_WIDTH = 100;
    public static final int BUTTON_HEIGHT = 75;

    public static final int SORT_BUTTON_X =50 ;
    public static final int SORT_BUTTON_Y = 600;
    public static final int END_BUTTON_X = 330;
    public static final int END_BUTTON_Y = 600;

    public static final int ACE_VALUE = 1;
    public static final int JACK_VALUE = 11;

    public static final int LEFT_TRICK_X = 0;
    public static final int LEFT_TRICK_Y = 110;
    public static final int TOP_TRICK_X = 400;
    public static final int TOP_TRICK_Y = 0;
    public static final int RIGHT_TRICK_X = 409;
    public static final int RIGHT_TRICK_Y = 110;
    public static final int PACK_X = 220;

    public static final int PACK_BOTTOM_HIDE_Y = -CARD_HEIGHT;
    public static final int PACK_BOTTOM_SHOW_Y = 200;

    public static final int BOARD_LEFT_X = 0;
    public static final int BOARD_LEFT_Y = 294;
    public static final int BOARD_TOP_X = 288;
    public static final int BOARD_TOP_Y = 599;
    public static final int BOARD_RIGHT_X = 409;
    public static final int BOARD_RIGHT_Y = 294;
    public static final int BOARD_BOTTOM_X = 288;
    public static final int BOARD_BOTTOM_Y = 130;

    public static final int TRICK_LEFT_X = 0;
    public static final int TRICK_LEFT_Y = 400;
    public static final int TRICK_TOP_X = 399;
    public static final int TRICK_TOP_Y = 704;
    public static final int TRICK_RIGHT_X = 409;
    public static final int TRICK_RIGHT_Y = 400;
    public static final int TRICK_BOTTOM_X = 10;
    public static final int TRICK_BOTTOM_Y = 0;

    public static final int PLAY_CARD_RIGHT_Y = 581;
    public static final int PLAY_CARD_LEFT_Y = 581;

    public static final float CARD_SPEED = 0.2f;
    public static final float DEALING_SPEED = 0.2f;

    public static final float PLAY_CARD_TO_TABLE_DELAY = 0.5f;

    public static final String BOTTOM_LAYER_NAME = "bottom";
    public static final String TOP_LAYER_NAME = "top";
    public static final String RESULT_LAYER_NAME = "result";

    public static final int AVATARS_CENTER_Y = 700;
    public static final int AVATAR_WIDTH = 96;
    public static final int AVATAR_HEIGHT = 96;
    public static final float AVATAR_SPEED = 1;
    public static final int AVATAR_X0 = 100;

    public static final float RESULT_LABEL_X_SHIFT = 37;
    public static final float RESULT_LABEL_Y_SHIFT = 70;
    public static final float RESULT_IMAGE_X = 30;

    public static final float RESULT_FONT_Y_SHIFT = 45;

    public static final int SHIFT_Y = 85;
    public static final int RESULT_LINE_Y = 260;

    public static final float PACK_SPEED = 0.4f;

    public static final int PLAY_CARDS_NUMBER = 4;
}