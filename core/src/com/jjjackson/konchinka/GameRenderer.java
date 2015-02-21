package com.jjjackson.konchinka;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.jjjackson.konchinka.domain.Card;
import com.jjjackson.konchinka.domain.Fog;
import com.jjjackson.konchinka.domain.GameModel;
import com.jjjackson.konchinka.domain.User;

public class GameRenderer {

    private final BitmapFont buttonFont;
    private GameModel model;
    private Stage stage;
    private Skin skin;

    public GameRenderer(GameModel model, Stage stage, Skin skin) {
        this.model = model;
        this.stage = stage;
        this.skin = skin;
        this.model.font = new BitmapFont(Gdx.files.internal("font/default.fnt"), Gdx.files.internal("font/default.png"), false);
//        this.model.font = new BitmapFont(Gdx.files.internal("font/torhok.fnt"), Gdx.files.internal("font/torhok.png"), false);
        this.stage.getBatch().enableBlending();

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

        BitmapFont font = loadFont();
        this.buttonFont = font;

        initGameButtons(firstGroup, font);
        initSortFog(secondGroup);
        initAvatars(secondGroup);
    }

    private BitmapFont loadFont() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font/42759.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.characters = "НоваяИгрГленМюСтиьВзкКцхд";
        parameter.size = 20;
        BitmapFont font = generator.generateFont(parameter);

        font.setColor(Color.RED);

        generator.dispose();
        Gdx.app.log("Color", font.getColor().toString());
        return font;
    }

    private void initAvatars(Group group) {
        for (User user : this.model.opponents) {
            group.addActor(user.avatar);
        }
        group.addActor(this.model.player.avatar);
    }

    private void initSortFog(Group fogLayer) {
        Fog fog = new Fog();
        fog.setVisible(false);
        this.model.fog = fog;
        fogLayer.addActor(fog);
    }

    private void initGameButtons(Group stage, BitmapFont font) {
        TextButton.TextButtonStyle textButtonStyle = createButtonStyle(font);
        TextButton sortButton = createButton("Сортировать", textButtonStyle, 100, 150);
        TextButton endSortButton = createButton("Готово", textButtonStyle, 100, 150);
        TextButton trickButton = createButton("Взятка", textButtonStyle, 380, 150);
        TextButton endButton = createButton("Конец хода", textButtonStyle, 380, 150);
        TextButton mainMenuButton = createButton("Главное Меню", textButtonStyle, 80, 50);
        TextButton newGameButton = createButton("Новая Игра", textButtonStyle, 240, 50);
        stage.addActor(sortButton);
        stage.addActor(endSortButton);
        stage.addActor(trickButton);
        stage.addActor(endButton);
        this.model.buttons.sortButton = sortButton;
        this.model.buttons.endSortButton = endSortButton;
        this.model.buttons.trickButton = trickButton;
        this.model.buttons.endButton = endButton;
        this.model.buttons.mainMenuButton = mainMenuButton;
        this.model.buttons.newGameButton = newGameButton;
        mainMenuButton.setVisible(true);
        newGameButton.setVisible(true);
    }

    private TextButton.TextButtonStyle createButtonStyle(BitmapFont font) {
        TextureAtlas buttonAtlas = new TextureAtlas(Gdx.files.internal("buttons/menu_button.pack"));
        this.skin.addRegions(buttonAtlas);
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.downFontColor = Color.RED;
        textButtonStyle.fontColor = Color.BLUE;
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
        Gdx.gl.glClearColor((float) 56 / 255, (float) 178 / 255, (float) 51 / 255, 1);
//        Gdx.gl.glClearColor((float)176/255, (float)245/255, (float)103/255, 1);
//        Gdx.gl.glClearColor((float)205/255, (float)247/255, (float)203/255, 1);

        this.stage.act(delta);
        this.stage.draw();
    }

    public void show() {

    }

    public void dispose () {
        this.buttonFont.dispose();
    }
}
