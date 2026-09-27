package com.plugin.customrig.rig;

import com.plugin.customrig.emote.EmoteDefinition;
import com.plugin.customrig.emote.Keyframe;
import com.plugin.customrig.model.BonePart;
import com.plugin.customrig.model.ModelDefinition;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Representa o "rig" (boneco de Display Entities) que substitui visualmente
 * um jogador. O jogador real fica escondido (hidePlayer) para todos os
 * outros viewers, e este rig aparece na posição dele, copiando seus
 * movimentos a cada tick.
 */
public class PlayerRig {

    private final JavaPlugin plugin;
    private final UUID playerUUID;
    private final ModelDefinition model;

    private final Map<String, ItemDisplay> boneEntities = new HashMap<>();
    // deslocamento extra (de emote) somado por cima da pose base, por bone
    private final Map<String, float[]> emoteOffset = new HashMap<>(); // [rotX,rotY,rotZ,transX,transY,transZ]

    private BukkitTask followTask;
    private BukkitTask emoteTask;

    public PlayerRig(JavaPlugin plugin, Player player, ModelDefinition model) {
        this.plugin = plugin;
        this.playerUUID = player.getUniqueId();
        this.model = model;
    }

    public ModelDefinition getModel() {
        return model;
    }

    /** Spawna as partes do rig e esconde o jogador real para todo mundo. */
    public void spawn() {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;

        // Esconde o jogador real de todos os outros viewers (API nativa, sem ProtocolLib)
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            if (!viewer.getUniqueId().equals(playerUUID)) {
                viewer.hidePlayer(plugin, player);
            }
        }

        Location base = player.getLocation();

        for (BonePart part : model.getParts()) {
            ItemDisplay display = player.getWorld().spawn(base, ItemDisplay.class, entity -> {
                entity.setBillboard(Display.Billboard.FIXED);
                entity.setPersistent(false);
                entity.setItemStack(buildItemStack(part));
                entity.setInterpolationDuration(2);
                entity.setInterpolationDelay(0);
                entity.setTeleportDuration(1);
            });
            boneEntities.put(part.getId(), display);
            emoteOffset.put(part.getId(), new float[6]);
        }

        startFollowTask();
    }

    /** Remove o rig e volta a mostrar o jogador real. */
    public void remove() {
        if (followTask != null) followTask.cancel();
        if (emoteTask != null) emoteTask.cancel();

        for (ItemDisplay display : boneEntities.values()) {
            if (display != null && !display.isDead()) {
                display.remove();
            }
        }
        boneEntities.clear();

        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null) {
            for (Player viewer : Bukkit.getOnlinePlayers()) {
                viewer.showPlayer(plugin, player);
            }
        }
    }

    /** Toca uma animação de emote sobre a pose base. */
    public void playEmote(EmoteDefinition emote) {
        if (emoteTask != null) {
            emoteTask.cancel();
        }

        // ordena os keyframes por tick para tocar em sequência
        List<Keyframe> keyframes = emote.getKeyframes();
        keyframes.sort((a, b) -> Integer.compare(a.getTick(), b.getTick()));

        emoteTask = new org.bukkit.scheduler.BukkitRunnable() {
            int currentTick = 0;
            int nextIndex = 0;

            @Override
            public void run() {
                if (currentTick == 0) {
                    // limpa offsets no início do emote
                    for (String boneId : emoteOffset.keySet()) {
                        emoteOffset.put(boneId, new float[6]);
                    }
                }

                // aplica todos os keyframes cujo tick chegou
                while (nextIndex < keyframes.size() && keyframes.get(nextIndex).getTick() <= currentTick) {
                    Keyframe kf = keyframes.get(nextIndex);
                    emoteOffset.put(kf.getBoneId(), new float[]{
                            (float) kf.getRotX(), (float) kf.getRotY(), (float) kf.getRotZ(),
                            (float) kf.getTransX(), (float) kf.getTransY(), (float) kf.getTransZ()
                    });
                    nextIndex++;
                }

                if (currentTick >= emote.getDurationTicks()) {
                    // fim do emote: zera offsets e cancela
                    for (String boneId : emoteOffset.keySet()) {
                        emoteOffset.put(boneId, new float[6]);
                    }
                    this.cancel();
                    return;
                }

                currentTick++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    /** Task que roda a cada tick copiando a posição/rotação do jogador para o rig. */
    private void startFollowTask() {
        followTask = new org.bukkit.scheduler.BukkitRunnable() {
            @Override
            public void run() {
                Player player = Bukkit.getPlayer(playerUUID);
                if (player == null || !player.isOnline()) {
                    this.cancel();
                    return;
                }

                Location loc = player.getLocation();
                float yawRad = (float) Math.toRadians(loc.getYaw());
                float pitchRad = (float) Math.toRadians(loc.getPitch());

                for (BonePart part : model.getParts()) {
                    ItemDisplay display = boneEntities.get(part.getId());
                    if (display == null || display.isDead()) continue;

                    // gira o offset da parte em torno do eixo Y conforme o yaw do jogador
                    double cos = Math.cos(-yawRad);
                    double sin = Math.sin(-yawRad);
                    double rotatedX = part.getOffsetX() * cos - part.getOffsetZ() * sin;
                    double rotatedZ = part.getOffsetX() * sin + part.getOffsetZ() * cos;

                    Location boneLoc = loc.clone().add(rotatedX, part.getOffsetY(), rotatedZ);
                    display.teleport(boneLoc);

                    float[] offset = emoteOffset.getOrDefault(part.getId(), new float[6]);

                    Quaternionf rotation = new Quaternionf()
                            .rotateY(-yawRad)
                            .rotateY((float) Math.toRadians(offset[1]))
                            .rotateX((float) Math.toRadians(offset[0]))
                            .rotateZ((float) Math.toRadians(offset[2]));

                    if (part.isFollowsHeadPitch()) {
                        rotation = new Quaternionf()
                                .rotateY(-yawRad)
                                .rotateX(pitchRad)
                                .rotateY((float) Math.toRadians(offset[1]))
                                .rotateX((float) Math.toRadians(offset[0]))
                                .rotateZ((float) Math.toRadians(offset[2]));
                    }

                    Vector3f translation = new Vector3f(
                            (float) offset[3],
                            (float) offset[4],
                            (float) offset[5]
                    );
                    Vector3f scale = new Vector3f(part.getScaleX(), part.getScaleY(), part.getScaleZ());

                    Transformation transformation = new Transformation(
                            translation,
                            rotation,
                            scale,
                            new Quaternionf(new AxisAngle4f(0, 0, 0, 1))
                    );
                    display.setTransformation(transformation);
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private ItemStack buildItemStack(BonePart part) {
        Material material;
        try {
            material = Material.valueOf(part.getMaterial().toUpperCase());
        } catch (IllegalArgumentException e) {
            material = Material.LEATHER_HORSE_ARMOR; // fallback seguro
        }
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(part.getCustomModelData());
            item.setItemMeta(meta);
        }
        return item;
    }
}
