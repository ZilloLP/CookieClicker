package de.zillolp.cookieclicker.hologram.holograms;

import de.zillolp.cookieclicker.utils.ReflectionUtil;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R2.CraftWorld;
import org.bukkit.entity.Player;

import java.util.Collections;

public class LineHologram extends Hologram {
    private final String line;
    private ArmorStand armorStand;

    public LineHologram(String line) {
        this.line = line;
    }

    @Override
    public void spawn(Player player, Location location) {
        armorStand = new ArmorStand(((CraftWorld) location.getWorld()).getHandle(), location.getX() + 0.5, location.getY(), location.getZ() + 0.5);

        armorStand.setInvisible(true);
        armorStand.setCustomNameVisible(true);
        armorStand.setNoGravity(true);
        armorStand.setNoBasePlate(true);
        armorStand.setSmall(true);
        armorStand.setSilent(true);
        armorStand.setMarker(true);

        ReflectionUtil.sendPacket(new ClientboundAddEntityPacket(armorStand), player);
        changeLine(player, line);
    }

    @Override
    public void destroy(Player player) {
        if (armorStand == null) {
            return;
        }
        ReflectionUtil.sendPacket(new ClientboundRemoveEntitiesPacket(armorStand.getId()), player);
    }

    public void changeLine(Player player, String line) {
        if (armorStand == null) {
            return;
        }
        armorStand.setCustomName(ComponentUtils.formatList(Collections.singletonList(line)));
        ReflectionUtil.sendPacket(new ClientboundSetEntityDataPacket(armorStand.getId(), armorStand.getEntityData().getNonDefaultValues()), player);
    }
}
