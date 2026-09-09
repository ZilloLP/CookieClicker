package de.zillolp.cookieclicker.manager;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.customconfigs.PluginConfig;
import de.zillolp.cookieclicker.customparticles.*;
import de.zillolp.cookieclicker.enums.CustomParticleEffectType;
import de.zillolp.cookieclicker.enums.ShopType;
import de.zillolp.cookieclicker.profiles.ClickerStatsProfile;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.profile.PlayerProfile;

import java.util.HashMap;
import java.util.UUID;

public class DesignManager {
    private final CookieClicker plugin;
    private final PluginConfig pluginConfig;
    private final ClickerPlayerManager clickerPlayerManager;
    private final HashMap<Material, BlockData> materialBlockData = new HashMap<>();
    private final HashMap<UUID, HashMap<CustomParticleEffectType, CustomParticleEffect>> customParticleEffects = new HashMap<>();

    public DesignManager(CookieClicker plugin) {
        this.plugin = plugin;
        pluginConfig = plugin.getPluginConfig();
        clickerPlayerManager = plugin.getClickerPlayerManager();
    }

    public Material getMenuDesign(UUID uuid) {
        if (uuid == null) {
            return Material.AIR;
        }
        ClickerStatsProfile statsProfile = clickerPlayerManager.getStatsProfile(uuid);
        if (statsProfile == null) {
            return Material.AIR;
        }
        long menuDesign = statsProfile.getMenuDesign();
        if (menuDesign <= 0) {
            return Material.AIR;
        }
        return pluginConfig.getBlockType(ShopType.MENU_DESIGN, menuDesign);
    }

    public void sendClickerBlockDesign(Player player) {
        if (player == null) {
            return;
        }
        ShopType shopType = ShopType.BLOCK_DESIGN;
        ClickerStatsProfile statsProfile = clickerPlayerManager.getStatsProfile(player.getUniqueId());
        if (statsProfile == null) {
            return;
        }
        long blockDesign = statsProfile.getBlockDesign();
        if (blockDesign <= 0) {
            return;
        }
        String typeName = pluginConfig.getTypeName(shopType, blockDesign);
        if (typeName.equalsIgnoreCase("PLAYER_HEAD")) {
            String textureURL = pluginConfig.getSkullTexture(shopType, blockDesign);
            plugin.getReflectionUtil().getPlayerProfile(textureURL, player.getName()).thenAccept(textureProfile -> {
                sendSkullClickerBlockDesign(player, textureProfile);
            }).exceptionally(throwable -> {
                plugin.getLogger().log(java.util.logging.Level.SEVERE, "Failed to load PlayerProfile for URL: " + textureURL, throwable);
                return null;
            });
            return;
        } else if (typeName.equalsIgnoreCase("REAL_PLAYER_HEAD")) {
            sendRealPlayerSkullClickerBlockDesign(player);
            return;
        }
        BlockData blockData;
        Material type = pluginConfig.getBlockType(shopType, blockDesign);
        if (!(materialBlockData.containsKey(type))) {
            blockData = type.createBlockData();
            materialBlockData.put(type, blockData);
        } else {
            blockData = materialBlockData.get(type);
        }
        for (Location location : plugin.getCookieClickerManager().getClickerLocations()) {
            player.sendBlockChange(location, blockData);
        }
    }

    private void sendSkullClickerBlockDesign(Player player, PlayerProfile playerProfile) {
        BlockData blockData;
        Material type = Material.PLAYER_HEAD;
        if (!(materialBlockData.containsKey(type))) {
            blockData = type.createBlockData();
            materialBlockData.put(type, blockData);
        } else {
            blockData = materialBlockData.get(type);
        }
        for (Location location : plugin.getCookieClickerManager().getClickerLocations()) {
            plugin.getNmsBridge().sendSkullBlock(player, location, blockData, playerProfile);
        }
    }

    private void sendRealPlayerSkullClickerBlockDesign(Player player) {
        BlockData blockData;
        Material type = Material.PLAYER_HEAD;
        if (!(materialBlockData.containsKey(type))) {
            blockData = type.createBlockData();
            materialBlockData.put(type, blockData);
        } else {
            blockData = materialBlockData.get(type);
        }
        for (Location location : plugin.getCookieClickerManager().getClickerLocations()) {
            plugin.getNmsBridge().sendRealPlayerSkullBlock(player, location, blockData);
        }
    }

    public void sendParticleEffect(Player player, Location location, long particleDesign) {
        if (player == null || location == null) {
            return;
        }
        UUID uuid = player.getUniqueId();
        CustomParticleEffectType customParticleEffectType = pluginConfig.getCustomParticleEffectType(ShopType.HIT_PARTICLE_DESIGN, particleDesign);
        if (customParticleEffectType == null) {
            return;
        }
        CustomParticleEffect customParticleEffect = createNewEffect(particleDesign, customParticleEffectType, player, location);
        if (customParticleEffect == null) {
            return;
        }
        stopSpecificEffect(uuid, customParticleEffectType);
        synchronized (customParticleEffects) {
            if (!customParticleEffects.containsKey(uuid)) {
                customParticleEffects.put(uuid, new HashMap<>());
            }
            customParticleEffects.get(uuid).put(customParticleEffectType, customParticleEffect);
        }
        customParticleEffect.start();
    }

    private void stopSpecificEffect(UUID uuid, CustomParticleEffectType customParticleEffectType) {
        if (uuid == null || customParticleEffectType == null) {
            return;
        }
        synchronized (customParticleEffects) {
            if (!customParticleEffects.containsKey(uuid)) {
                return;
            }
            HashMap<CustomParticleEffectType, CustomParticleEffect> effects = customParticleEffects.get(uuid);
            if (effects == null) {
                return;
            }
            CustomParticleEffect customParticleEffect = effects.get(customParticleEffectType);
            if (customParticleEffect == null) {
                return;
            }
            customParticleEffect.stop();
        }
    }

    private CustomParticleEffect createNewEffect(long particleDesign, CustomParticleEffectType customParticleEffectType, Player player, Location location) {
        ShopType shopType = ShopType.HIT_PARTICLE_DESIGN;
        boolean coloredDust = pluginConfig.hasColoredDust(shopType, particleDesign);
        Particle particle = pluginConfig.getParticle(shopType, particleDesign);
        Particle secondParticle = pluginConfig.getSecondParticle(shopType, particleDesign);
        Color color = pluginConfig.getColor(shopType, particleDesign);
        switch (customParticleEffectType) {
            case SPIRAL:
                return new SpiralEffect(plugin, player, location.clone().add(0.5, -1, 0.5), coloredDust, particle, color);
            case CIRCLE_RINGS:
                return new CircleRingsEffect(plugin, player, location.clone().add(0.5, 0.25, 0.5), coloredDust, particle, color);
            case EXPLOSION_WAVE:
                return new ExplosionWaveEffect(plugin, player, location.clone().add(0.5, -1, 0.5), coloredDust, particle, color);
            case STAR_BURST:
                return new StarBurstEffect(plugin, player, location.clone().add(0.5, -1, 0.5), coloredDust, particle, color);
            case MAGICAL_WHIRLWIND:
                return new MagicalWhirlwindEffect(plugin, player, location.clone().add(0.5, -0.5, 0.5), coloredDust, particle, secondParticle, color);
            default:
                return null;
        }
    }

    public void stopParticleEffects(UUID uuid) {
        if (uuid == null) {
            return;
        }
        synchronized (customParticleEffects) {
            if (!customParticleEffects.containsKey(uuid)) {
                return;
            }
            HashMap<CustomParticleEffectType, CustomParticleEffect> effects = customParticleEffects.get(uuid);
            if (effects != null) {
                for (CustomParticleEffect customParticleEffect : effects.values()) {
                    if (customParticleEffect != null) {
                        customParticleEffect.stop();
                    }
                }
            }
            customParticleEffects.remove(uuid);
        }
    }
}
