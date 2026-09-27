package com.plugin.customrig.emote;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public class EmoteManager {

    private final JavaPlugin plugin;
    private final File folder;
    private final Gson gson = new GsonBuilder().create();
    private final Map<String, EmoteDefinition> emotes = new HashMap<>();

    public EmoteManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.folder = new File(plugin.getDataFolder(), "emotes");
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    public void reload() {
        emotes.clear();
        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));
        if (files == null) return;

        for (File file : files) {
            try (FileReader reader = new FileReader(file)) {
                EmoteDefinition def = gson.fromJson(reader, EmoteDefinition.class);
                if (def == null || def.getKeyframes() == null || def.getKeyframes().isEmpty()) {
                    plugin.getLogger().warning("Emote inválido ou vazio: " + file.getName());
                    continue;
                }
                String key = stripExtension(file.getName());
                emotes.put(key.toLowerCase(), def);
                plugin.getLogger().info("Emote carregado: " + key);
            } catch (IOException e) {
                plugin.getLogger().log(Level.WARNING, "Erro ao ler emote " + file.getName(), e);
            }
        }
    }

    public EmoteDefinition get(String fileNameWithoutExtension) {
        return emotes.get(fileNameWithoutExtension.toLowerCase());
    }

    public Set<String> listNames() {
        return emotes.keySet();
    }

    private String stripExtension(String fileName) {
        int idx = fileName.lastIndexOf('.');
        return idx > 0 ? fileName.substring(0, idx) : fileName;
    }
}
