package com.plugin.customrig.model;

/**
 * Representa uma "parte" do modelo (ex: cabeça, tronco, braço).
 * Cada parte vira um ItemDisplay no mundo, posicionado relativo ao jogador.
 *
 * material + customModelData -> aponta para o item/textura definido no
 * resource pack (o resource pack precisa ser distribuído separadamente,
 * não faz parte deste plugin).
 */
public class BonePart {

    private String id;                // ex: "head", "body", "left_arm"
    private String material;          // material vanilla base, ex: "PLAYER_HEAD", "LEATHER_HORSE_ARMOR"
    private int customModelData;      // valor usado no resource pack para trocar a textura/modelo

    // offset relativo ao "centro" do jogador (pés), em blocos
    private double offsetX;
    private double offsetY;
    private double offsetZ;

    private float scaleX = 1f;
    private float scaleY = 1f;
    private float scaleZ = 1f;

    // se true, essa parte acompanha o pitch da cabeça do jogador (olhar para cima/baixo)
    private boolean followsHeadPitch = false;

    public String getId() { return id; }
    public String getMaterial() { return material; }
    public int getCustomModelData() { return customModelData; }
    public double getOffsetX() { return offsetX; }
    public double getOffsetY() { return offsetY; }
    public double getOffsetZ() { return offsetZ; }
    public float getScaleX() { return scaleX; }
    public float getScaleY() { return scaleY; }
    public float getScaleZ() { return scaleZ; }
    public boolean isFollowsHeadPitch() { return followsHeadPitch; }
}
