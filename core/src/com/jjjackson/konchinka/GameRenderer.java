package com.jjjackson.konchinka;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.jjjackson.konchinka.domain.Card;
import com.jjjackson.konchinka.domain.Fog;
import com.jjjackson.konchinka.domain.GameModel;
import com.jjjackson.konchinka.domain.User;

public class GameRenderer {

    private GameModel model;
    private Stage stage;
    private Skin skin;

    public GameRenderer(GameModel model, Stage stage, Skin skin) {
        this.model = model;
        this.stage = stage;
        this.skin = skin;
        this.model.font = new BitmapFont(Gdx.files.internal("font/default.fnt"), Gdx.files.internal("font/default.png"), false);

        Group firstGroup = new Group();
        firstGroup.setName(GameConstants.BOTTOM_LAYER_NAME);
        Group secondGroup = new Group();
        secondGroup.setName(GameConstants.TOP_LAYER_NAME);
        Group resultGroup = new Group();
        resultGroup.setName(GameConstants.RESULT_LAYER_NAME);
        secondGroup.addActor(resultGroup);

        this.stage.addActor(firstGroup);
        this.stage.addActor(secondGroup);

        firstGroup.addActor(this.model.pack);

        for (Card card : this.model.pack.cards) {
            firstGroup.addActor(card);
        }

        initGameButtons(firstGroup, model.font);
        initSortFog(secondGroup);
        initAvatars(secondGroup);
    }

    private void initAvatars(Group group) {
        for (User user : this.model.opponents) {
            group.addActor(user.avatar);
        }
        group.addActor(this.model.player.avatar);
    }

    private void initSortFog(Group stage) {
        Fog fog = new Fog();
        fog.setVisible(false);
        this.model.fog = fog;
        stage.addActor(fog);
    }

    private void initGameButtons(Group stage, BitmapFont font) {
        TextButton.TextButtonStyle textButtonStyle = createButtonStyle(font);
        TextButton sortButton = createButton("Sort", textButtonStyle, 100, 150);
        TextButton endSortButton = createButton("Done", textButtonStyle, 100, 150);
        TextButton trickButton = createButton("Trick", textButtonStyle, 380, 150);
        TextButton endButton = createButton("End", textButtonStyle, 380, 150);
        stage.addActor(sortButton);
        stage.addActor(endSortButton);
        stage.addActor(trickButton);
        stage.addActor(endButton);
        this.model.buttons.sortButton = sortButton;
        this.model.buttons.endSortButton = endSortButton;
        this.model.buttons.trickButton = trickButton;
        this.model.buttons.endButton = endButton;
    }

    private TextButton.TextButtonStyle createButtonStyle(BitmapFont font) {
        TextureAtlas buttonAtlas = new TextureAtlas(Gdx.files.internal("buttons/menu_button.pack"));
        this.skin.addRegions(buttonAtlas);
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.up = this.skin.getDrawable("menu_button_normal");
        textButtonStyle.down = this.skin.getDrawable("menu_button_pressed");
        return textButtonStyle;
    }

    private TextButton createButton(String text, TextButton.TextButtonStyle textButtonStyle, int x, int y) {
        TextButton sortButton = new TextButton(text, textButtonStyle);
        sortButton.setPosition(x, y);
        sortButton.setVisible(false);
        return sortButton;
    }

    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor((float)56/255, (float)178/255, (float)51/255, 1);
//        Gdx.gl.glClearColor((float)176/255, (float)245/255, (float)103/255, 1);
//        Gdx.gl.glClearColor((float)205/255, (float)247/255, (float)203/255, 1);

        this.stage.act(delta);
        this.stage.draw();
    }

    public void show() {

    }
}
