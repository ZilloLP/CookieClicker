package de.zillolp.cookieclicker.manager;

import de.zillolp.cookieclicker.config.ConfigTools;
import de.zillolp.cookieclicker.xclasses.XSound;
import org.bukkit.entity.Player;

public class SoundManager {
    private final Player player;
    private final boolean sounds;

    public SoundManager(Player player) {
        this.player = player;
        sounds = ConfigTools.isSound();
    }

    public void playSound(XSound xSound) {
        if (sounds) {
            XSound.play(player, xSound.name());
        }
    }
}
