package com.plugin.customrig.model;

import java.util.List;

/**
 * Representa um arquivo de modelo inteiro (ex: modelos/dragao.json).
 */
public class ModelDefinition {

    private String name;
    private List<BonePart> parts;

    public String getName() { return name; }
    public List<BonePart> getParts() { return parts; }
}
