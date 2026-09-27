package com.plugin.customrig.listener;

import com.plugin.customrig.rig.RigManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerCleanupListener implements Listener {

    private final RigManager rigManager;

    public PlayerCleanupListener(RigManager rigManager) {
        this.rigManager = rigManager;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        rigManager.reset(event.getPlayer());
    }
}
