package com.plugin.customrig.command;

import com.plugin.customrig.emote.EmoteDefinition;
import com.plugin.customrig.emote.EmoteManager;
import com.plugin.customrig.rig.PlayerRig;
import com.plugin.customrig.rig.RigManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class EmotesCommand implements CommandExecutor, TabCompleter {

    private final EmoteManager emoteManager;
    private final RigManager rigManager;

    public EmotesCommand(EmoteManager emoteManager, RigManager rigManager) {
        this.emoteManager = emoteManager;
        this.rigManager = rigManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Apenas jogadores podem usar este comando.");
            return true;
        }

        if (!player.hasPermission("customrig.emote")) {
            player.sendMessage(ChatColor.RED + "Você não tem permissão para usar emotes.");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(ChatColor.YELLOW + "Uso: /emotes <nome|list>");
            return true;
        }

        if (args[0].equalsIgnoreCase("list")) {
            player.sendMessage(ChatColor.AQUA + "Emotes disponíveis: " + String.join(", ", emoteManager.listNames()));
            return true;
        }

        PlayerRig rig = rigManager.get(player.getUniqueId());
        if (rig == null) {
            player.sendMessage(ChatColor.RED + "Você precisa ter um modelo aplicado (/custommodel) para usar emotes.");
            return true;
        }

        EmoteDefinition emote = emoteManager.get(args[0]);
        if (emote == null) {
            player.sendMessage(ChatColor.RED + "Emote \"" + args[0] + "\" não encontrado na pasta emotes/.");
            return true;
        }

        rig.playEmote(emote);
        player.sendMessage(ChatColor.GREEN + "Tocando emote: " + args[0]);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> options = new ArrayList<>(emoteManager.listNames());
            options.add("list");
            List<String> result = new ArrayList<>();
            for (String opt : options) {
                if (opt.toLowerCase().startsWith(args[0].toLowerCase())) {
                    result.add(opt);
                }
            }
            return result;
        }
        return new ArrayList<>();
    }
}
