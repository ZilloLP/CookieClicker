package de.zillolp.cookieclicker.clickerevents;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.enums.ClickerEventType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class CookieExplosionEvent extends ClickerEvent {
    private final CookieClicker plugin;
    private final ArrayList<Item> items = new ArrayList<>();

    public CookieExplosionEvent(CookieClicker plugin, Player player, ClickerEventType clickerEventType, double time) {
        super(player, clickerEventType, time);
        this.plugin = plugin;
    }

    public void addGlowing(Item item) {
        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            plugin.getNmsBridge().sendGlowPacket(player, item.getUniqueId());
            item.setGlowing(true);
        }, 20);
    }

    public ArrayList<Item> getItems() {
        return items;
    }
}
