package com.plugin.customrig.emote;

import java.util.List;

public class EmoteDefinition {

    private String name;
    private int durationTicks;
    private List<Keyframe> keyframes;

    public String getName() { return name; }
    public int getDurationTicks() { return durationTicks; }
    public List<Keyframe> getKeyframes() { return keyframes; }
}
