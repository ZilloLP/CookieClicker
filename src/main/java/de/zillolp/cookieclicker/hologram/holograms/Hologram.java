package de.zillolp.cookieclicker.hologram.holograms;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class Hologram {

    public abstract void spawn(Player player, Location location);

    public abstract void destroy(Player player);
}
