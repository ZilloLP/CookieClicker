package de.zillolp.cookieclicker.holograms;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.interfaces.Hologram;
import de.zillolp.cookieclicker.utils.ReflectionUtil;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

public class Hologram_v26_R2 implements Hologram {
    private final UUID uuid;
    private final ReflectionUtil reflectionUtil;
    private String line;
    private ArmorStand armorStand;
    private Location spawnLocation;

    public Hologram_v26_R2(CookieClicker plugin, String line) {
        uuid = UUID.randomUUID();
        reflectionUtil = plugin.getReflectionUtil();
        this.line = line;
    }

    @Override
    public void spawn(Player player, Location location) {
        spawnLocation = location;
        World world = location.getWorld();
        if (world == null) {
            world = Bukkit.getWorlds().getFirst();
        }
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        armorStand = new ArmorStand((ServerLevel) reflectionUtil.getServerObject(world, reflectionUtil.getCraftObjectClass(reflectionUtil.isSpigot(), "CraftWorld")), x, y, z);
        armorStand.setInvisible(true);
        armorStand.setCustomNameVisible(true);
        armorStand.setNoGravity(true);
        armorStand.setNoBasePlate(true);
        armorStand.setSmall(true);
        armorStand.setSilent(true);
        armorStand.setMarker(true);

        reflectionUtil.sendPacket(new ClientboundAddEntityPacket(armorStand.getId(), armorStand.getUUID(), x, y, z, 0, 0, armorStand.getType(), 0, new Vec3(0, 0, 0), armorStand.getYHeadRot()), player);
        changeLine(player, line);
    }

    @Override
    public void destroy(Player player) {
        if (armorStand == null) {
            return;
        }
        reflectionUtil.sendPacket(new ClientboundRemoveEntitiesPacket(armorStand.getId()), player);
    }

    @Override
    public void changeLine(Player player, String line) {
        if (armorStand == null) {
            return;
        }
        this.line = line;
        armorStand.setCustomName(ComponentUtils.formatList(Collections.singletonList(line)));
        reflectionUtil.sendPacket(new ClientboundSetEntityDataPacket(armorStand.getId(), armorStand.getEntityData().getNonDefaultValues()), player);
    }

    @Override
    public void moveHologram(Player player, Location location) {
        if (armorStand == null) {
            return;
        }
        armorStand.teleportTo(location.getX(), location.getY(), location.getZ());
        PositionMoveRotation positionMoveRotation = new PositionMoveRotation(armorStand.position(), armorStand.getDeltaMovement(), armorStand.getYRot(), armorStand.getXRot());
        reflectionUtil.sendPacket(new ClientboundTeleportEntityPacket(armorStand.getId(), positionMoveRotation, new HashSet<>(), armorStand.onGround()), player);
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public Location getSpawnLocation() {
        return spawnLocation;
    }

    @Override
    public Location getCurrentLocation() {
        return armorStand.getBukkitEntity().getLocation();
    }
}
