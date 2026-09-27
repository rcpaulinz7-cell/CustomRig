package com.plugin.customrig.command;

import com.plugin.customrig.model.ModelDefinition;
import com.plugin.customrig.model.ModelManager;
import com.plugin.customrig.rig.RigManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomModelCommand implements CommandExecutor, TabCompleter {

    private final ModelManager modelManager;
    private final RigManager rigManager;

    public CustomModelCommand(ModelManager modelManager, RigManager rigManager) {
        this.modelManager = modelManager;
        this.rigManager = rigManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("customrig.model")) {
            sender.sendMessage(ChatColor.RED + "Você não tem permissão para usar este comando.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.YELLOW + "Uso: /custommodel <jogador> <arquivo|reset>");
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Jogador não encontrado ou offline: " + args[0]);
            return true;
        }

        String arquivo = args[1];

        if (arquivo.equalsIgnoreCase("reset")) {
            rigManager.reset(target);
            sender.sendMessage(ChatColor.GREEN + "Modelo removido de " + target.getName() + ".");
            return true;
        }

        ModelDefinition model = modelManager.get(arquivo);
        if (model == null) {
            sender.sendMessage(ChatColor.RED + "Modelo \"" + arquivo + "\" não encontrado na pasta modelos/.");
            return true;
        }

        rigManager.apply(target, model);
        sender.sendMessage(ChatColor.GREEN + "Modelo \"" + arquivo + "\" aplicado em " + target.getName() + ".");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> names = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) {
                names.add(p.getName());
            }
            return filter(names, args[0]);
        }
        if (args.length == 2) {
            List<String> options = new ArrayList<>(modelManager.listNames());
            options.add("reset");
            return filter(options, args[1]);
        }
        return new ArrayList<>();
    }

    private List<String> filter(List<String> options, String prefix) {
        List<String> result = new ArrayList<>();
        for (String opt : options) {
            if (opt.toLowerCase().startsWith(prefix.toLowerCase())) {
                result.add(opt);
            }
        }
        return result;
    }
}
