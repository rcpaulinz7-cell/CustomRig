package com.plugin.customrig.rig;

import com.plugin.customrig.model.ModelDefinition;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RigManager {

    private final JavaPlugin plugin;
    private final Map<UUID, PlayerRig> activeRigs = new HashMap<>();

    public RigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /** Aplica (ou substitui) o modelo customizado de um jogador. */
    public void apply(Player player, ModelDefinition model) {
        reset(player); // remove rig anterior, se houver

        PlayerRig rig = new PlayerRig(plugin, player, model);
        rig.spawn();
        activeRigs.put(player.getUniqueId(), rig);
    }

    /** Remove o rig e volta a mostrar o jogador normal. */
    public void reset(Player player) {
        PlayerRig rig = activeRigs.remove(player.getUniqueId());
        if (rig != null) {
            rig.remove();
        }
    }

    public PlayerRig get(UUID uuid) {
        return activeRigs.get(uuid);
    }

    public boolean hasRig(UUID uuid) {
        return activeRigs.containsKey(uuid);
    }

    /** Chamado no onDisable do plugin, para não deixar entidades órfãs no mundo. */
    public void removeAll() {
        for (PlayerRig rig : activeRigs.values()) {
            rig.remove();
        }
        activeRigs.clear();
    }
}
