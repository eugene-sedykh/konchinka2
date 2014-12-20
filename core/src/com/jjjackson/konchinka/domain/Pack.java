package com.jjjackson.konchinka.domain;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.ArrayList;
import java.util.List;

public class Pack extends GameObject {
    public List<Card> cards = new ArrayList<>();

    private PackTexture packTexture = PackTexture.PACK_4_4;

    public Pack(Skin skin, String drawableName) {
        super(skin, drawableName);
    }

    public void refreshTexture() {
        PackTexture preferredTexture = PackTexture.getTexture(this.cards.size());
        if (this.packTexture != preferredTexture) {
            this.packTexture = preferredTexture;
            if (preferredTexture == PackTexture.PACK_0) {
                setVisible(false);
            } else {
                setVisible(true);
                setDrawable(this.skin, preferredTexture.getTextureName());
                pack();
            }
        }
    }
}
