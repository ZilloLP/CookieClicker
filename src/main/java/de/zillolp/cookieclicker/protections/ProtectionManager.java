package de.zillolp.cookieclicker.protections;

import de.zillolp.cookieclicker.CookieClicker;

import java.util.HashMap;
import java.util.UUID;

public class ProtectionManager {
    private final CookieClicker plugin;

    private final HashMap<UUID, Long> lastMoves;
    private final HashMap<UUID, Integer> playerCPS;

    public ProtectionManager(CookieClicker plugin) {
        this.plugin = plugin;
        this.lastMoves = new HashMap<>();
        this.playerCPS = new HashMap<>();
        new AntiAutoClickerProtection(plugin);
    }

    public boolean isAFK(UUID uuid) {
        return lastMoves.containsKey(uuid) && lastMoves.get(uuid) < System.currentTimeMillis();
    }

    private long getAFKTime() {
        return plugin.getPluginConfig().getFileConfiguration().getLong("AFK cooldown seconds") * 1000;
    }

    public void addAFK(UUID uuid) {
        lastMoves.put(uuid, System.currentTimeMillis() + getAFKTime());
    }

    public boolean isOverCPS(UUID uuid) {
        return playerCPS.containsKey(uuid) && playerCPS.get(uuid) > getMaxCPS();
    }

    public int getMaxCPS() {
        return plugin.getPluginConfig().getFileConfiguration().getInt("Maximum CPS");
    }

    public void addCPS(UUID uuid) {
        if (playerCPS.containsKey(uuid)) {
            playerCPS.replace(uuid, playerCPS.get(uuid) + 1);
            return;
        }
        playerCPS.put(uuid, 1);
    }

    public HashMap<UUID, Long> getLastMoves() {
        return lastMoves;
    }

    public HashMap<UUID, Integer> getPlayerCPS() {
        return playerCPS;
    }
}
