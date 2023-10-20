package de.zillolp.cookieclicker.events;

import net.minecraft.core.BlockPos;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ClickerInteractEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final Location location;
    private final InteractType interactType;

    public ClickerInteractEvent(Player player, BlockPos blockPos, InteractType interactType) {
        this.player = player;
        this.location = new Location(player.getWorld(), blockPos.getX(), blockPos.getY(), blockPos.getZ());
        this.interactType = interactType;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public Location getLocation() {
        return location;
    }

    public InteractType getInteractType() {
        return interactType;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public enum InteractType {
        ATTACK,
        ADVENTURE_ATTACK,
        INTERACT
    }
}
