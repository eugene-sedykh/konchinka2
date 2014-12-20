package com.jjjackson.konchinka.domain;

public enum PackTexture {
    PACK_4_4("pack_diagonal_4", 40, 52),
    PACK_3_4("pack_diagonal_3", 27, 39),
    PACK_2_4("pack_diagonal_2", 14, 26),
    PACK_1_4("pack_diagonal_1", 1, 13),
    PACK_0("", 0, 0);

    private String name;
    private int minCardsNumber;
    private int maxCardsNumber;

    PackTexture(String textureName, int minCardsNumber, int maxCardsNumber) {
        this.name = textureName;
        this.minCardsNumber = minCardsNumber;
        this.maxCardsNumber = maxCardsNumber;
    }

    public static PackTexture getTexture(int cardsNumber) {
        for (PackTexture packTexture : values()) {
            if (packTexture.minCardsNumber <= cardsNumber && packTexture.maxCardsNumber >= cardsNumber) {
                return packTexture;
            }
        }

        return null;
    }

    public String getTextureName() {
        return this.name;
    }
}
