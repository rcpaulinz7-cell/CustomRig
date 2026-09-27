package com.plugin.customrig;

import com.plugin.customrig.command.CustomModelCommand;
import com.plugin.customrig.command.EmotesCommand;
import com.plugin.customrig.command.ReloadCommand;
import com.plugin.customrig.emote.EmoteManager;
import com.plugin.customrig.listener.PlayerCleanupListener;
import com.plugin.customrig.model.ModelManager;
import com.plugin.customrig.rig.RigManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomRigPlugin extends JavaPlugin {

    private ModelManager modelManager;
    private EmoteManager emoteManager;
    private RigManager rigManager;

    @Override
    public void onEnable() {
        // garante que a pasta de dados e as subpastas modelos/ e emotes/ existam
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        modelManager = new ModelManager(this);
        emoteManager = new EmoteManager(this);
        rigManager = new RigManager(this);

        modelManager.reload();
        emoteManager.reload();

        getCommand("custommodel").setExecutor(new CustomModelCommand(modelManager, rigManager));
        getCommand("emotes").setExecutor(new EmotesCommand(emoteManager, rigManager));
        getCommand("customrigreload").setExecutor(new ReloadCommand(modelManager, emoteManager));

        getServer().getPluginManager().registerEvents(new PlayerCleanupListener(rigManager), this);

        getLogger().info("CustomRig ativado. Modelos: " + modelManager.listNames().size()
                + " | Emotes: " + emoteManager.listNames().size());
    }

    @Override
    public void onDisable() {
        if (rigManager != null) {
            rigManager.removeAll();
        }
    }
}
