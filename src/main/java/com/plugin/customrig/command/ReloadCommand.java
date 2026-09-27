package com.plugin.customrig.command;

import com.plugin.customrig.emote.EmoteManager;
import com.plugin.customrig.model.ModelManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {

    private final ModelManager modelManager;
    private final EmoteManager emoteManager;

    public ReloadCommand(ModelManager modelManager, EmoteManager emoteManager) {
        this.modelManager = modelManager;
        this.emoteManager = emoteManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("customrig.reload")) {
            sender.sendMessage(ChatColor.RED + "Sem permissão.");
            return true;
        }
        modelManager.reload();
        emoteManager.reload();
        sender.sendMessage(ChatColor.GREEN + "Modelos e emotes recarregados.");
        return true;
    }
}
