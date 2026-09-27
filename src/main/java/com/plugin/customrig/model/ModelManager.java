package com.plugin.customrig.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

public class ModelManager {

    private final JavaPlugin plugin;
    private final File folder;
    private final Gson gson = new GsonBuilder().create();
    private final Map<String, ModelDefinition> models = new HashMap<>();

    public ModelManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.folder = new File(plugin.getDataFolder(), "modelos");
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    public void reload() {
        models.clear();
        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));
        if (files == null) return;

        for (File file : files) {
            try (FileReader reader = new FileReader(file)) {
                ModelDefinition def = gson.fromJson(reader, ModelDefinition.class);
                if (def == null || def.getParts() == null || def.getParts().isEmpty()) {
                    plugin.getLogger().warning("Modelo inválido ou vazio: " + file.getName());
                    continue;
                }
                String key = stripExtension(file.getName());
                models.put(key.toLowerCase(), def);
                plugin.getLogger().info("Modelo carregado: " + key + " (" + def.getParts().size() + " partes)");
            } catch (IOException e) {
                plugin.getLogger().log(Level.WARNING, "Erro ao ler modelo " + file.getName(), e);
            }
        }
    }

    public ModelDefinition get(String fileNameWithoutExtension) {
        return models.get(fileNameWithoutExtension.toLowerCase());
    }

    public java.util.Set<String> listNames() {
        return models.keySet();
    }

    private String stripExtension(String fileName) {
        int idx = fileName.lastIndexOf('.');
        return idx > 0 ? fileName.substring(0, idx) : fileName;
    }
}
