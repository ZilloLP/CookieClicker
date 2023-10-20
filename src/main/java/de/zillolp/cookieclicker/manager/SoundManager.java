package de.zillolp.cookieclicker.manager;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.enums.SoundType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class SoundManager {
    private final CookieClicker plugin;
    private FileConfiguration fileConfiguration;
    private boolean useSounds;

    public SoundManager(CookieClicker plugin) {
        this.plugin = plugin;
        this.load();
    }

    public void load() {
        this.fileConfiguration = plugin.getPluginConfig().getFileConfiguration();
        this.useSounds = fileConfiguration.getBoolean("Sounds");
        for (SoundType soundType : SoundType.values()) {
            String soundName = fileConfiguration.getString(soundType.name());
            if (soundName == null) {
                continue;
            }
            soundType.setSound(soundName);
        }
    }

    public void playSound(Player player, SoundType soundType) {
        if (!(useSounds)) {
            return;
        }
        player.playSound(player, soundType.getSound(), fileConfiguration.getInt("Sound volume"), fileConfiguration.getInt("Sound pitch"));
    }
}
