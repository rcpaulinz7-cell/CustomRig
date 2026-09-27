package com.plugin.customrig.emote;

/**
 * Um "quadro-chave" de animação: em determinado tick, uma parte (boneId)
 * deve estar com essa rotação/translação extra (somada à pose base do modelo).
 */
public class Keyframe {

    private int tick;          // momento da animação em que este keyframe ocorre
    private String boneId;     // qual parte do modelo é afetada (deve bater com BonePart.id)

    private double rotX;
    private double rotY;
    private double rotZ;

    private double transX;
    private double transY;
    private double transZ;

    public int getTick() { return tick; }
    public String getBoneId() { return boneId; }
    public double getRotX() { return rotX; }
    public double getRotY() { return rotY; }
    public double getRotZ() { return rotZ; }
    public double getTransX() { return transX; }
    public double getTransY() { return transY; }
    public double getTransZ() { return transZ; }
}
