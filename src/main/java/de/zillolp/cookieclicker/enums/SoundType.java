package de.zillolp.cookieclicker.enums;

import org.bukkit.Sound;

public enum SoundType {
    CLICK_ALLOW,
    CLICK_DENY,
    OPEN_INVENTORY,
    SHOP,
    DESIGN,
    SELECT_DESIGN,
    PREMIUM_ALLOW,
    PREMIUM_DENY,
    NEXT,
    BACK,
    BUY_ALLOW,
    BUY_DENY,
    STATS_INFO;

    private Sound sound;

    public Sound getSound() {
        return sound;
    }

    public void setSound(String soundName) {
        try {
            this.sound = Sound.valueOf(soundName);
        } catch (IllegalArgumentException exception) {
            this.sound = Sound.ENTITY_VILLAGER_NO;
            System.out.println("[CookieClicker] Sound: " + soundName + " doesn't exist!");
        }
    }
}
