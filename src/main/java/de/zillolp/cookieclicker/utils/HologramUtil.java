package de.zillolp.cookieclicker.utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class HologramUtil extends ReflectionUtil {
    private static Class<?> craftWorldClass;
    private static Class<?> entityArmorStandClass;
    private static Class<?> IChatBaseComponentClass;
    private static Class<?> chatComponentClass;
    private static Class<?> craftArmorStandClass;
    private static Class<?> spawnArmorStandClass;
    private static Class<?> entityLivingClass;
    private static Class<?> changeArmorStandClass;
    private static Class<?> dataWatcherClass;
    private static Class<?> deleteArmorStandClass;

    private static Constructor<?> entityArmorStandConstructor;

    private final Player player;
    private String line;
    private String[] lines;
    private final Location location;
    private final List<Object> holograms;
    private final List<Object> craftHolograms;

    public HologramUtil(Player player, String line, Location location) {
        this.player = player;
        this.line = line;
        this.location = location;
        this.holograms = new ArrayList<>();
        this.craftHolograms = new ArrayList<>();
    }

    public HologramUtil(Player player, String[] lines, Location location) {
        this.player = player;
        this.lines = lines;
        this.location = location;
        this.holograms = new ArrayList<>();
        this.craftHolograms = new ArrayList<>();
    }

    public static void initialize() {
        try {
            craftWorldClass = Class.forName("org.bukkit.craftbukkit." + version + ".CraftWorld");
            craftArmorStandClass = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftArmorStand");
            Class<?> worldClass;
            if (versionNumber < 17) {
                worldClass = Class.forName("net.minecraft.server." + version + ".World");
                entityArmorStandClass = Class.forName("net.minecraft.server." + version + ".EntityArmorStand");
                IChatBaseComponentClass = Class.forName("net.minecraft.server." + version + ".IChatBaseComponent");
                chatComponentClass = Class.forName("net.minecraft.server." + version + ".ChatComponentText");
                spawnArmorStandClass = Class.forName("net.minecraft.server." + version + ".PacketPlayOutSpawnEntityLiving");
                entityLivingClass = Class.forName("net.minecraft.server." + version + ".EntityLiving");
                changeArmorStandClass = Class.forName("net.minecraft.server." + version + ".PacketPlayOutEntityMetadata");
                dataWatcherClass = Class.forName("net.minecraft.server." + version + ".DataWatcher");
                deleteArmorStandClass = Class.forName("net.minecraft.server." + version + ".PacketPlayOutEntityDestroy");
            } else {
                worldClass = Class.forName("net.minecraft.world.level.World");
                entityArmorStandClass = Class.forName("net.minecraft.world.entity.decoration.EntityArmorStand");
                entityLivingClass = Class.forName("net.minecraft.world.entity.EntityLiving");
                changeArmorStandClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata");
                dataWatcherClass = Class.forName("net.minecraft.network.syncher.DataWatcher");
                deleteArmorStandClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy");
                if (versionNumber < 19) {
                    IChatBaseComponentClass = Class.forName("net.minecraft.network.chat.IChatBaseComponent");
                    chatComponentClass = Class.forName("net.minecraft.network.chat.ChatComponentText");
                    spawnArmorStandClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityLiving");
                } else {
                    spawnArmorStandClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity");
                }
            }
            entityArmorStandConstructor = entityArmorStandClass.getConstructor(worldClass, double.class, double.class, double.class);
        } catch (ClassNotFoundException | NoSuchMethodException exception) {
            exception.printStackTrace();
            System.out.println("[CookieClicker] Error on initializing the NMS classes from the hologram");
        }
    }

    public void spawn() {
        double holo_height = 0;
        if (lines == null || lines.length <= 0) {
            if (versionNumber <= 8) {
                holo_height = -0.75;
            }
            createHologram(location.getX(), location.getY() + holo_height, location.getZ());
            return;
        }
        holo_height = 0.3 * lines.length + 0.75;
        if (versionNumber <= 8) {
            holo_height = 0.3 * lines.length - 0.25;
        }
        for (String line : lines) {
            this.line = line;
            if (line.equalsIgnoreCase("%empty%") || line.equalsIgnoreCase("")) {
                holo_height = holo_height - 0.3;
                continue;
            }
            createHologram(location.getX(), location.getY() + holo_height, location.getZ());
            holo_height = holo_height - 0.3;
        }
    }

    private void createHologram(double x, double y, double z) {
        try {
            Object craftWorldObject = craftWorldClass.cast(location.getWorld());
            Method getCraftWorldHandleMethod = craftWorldClass.getMethod("getHandle");
            Object entityArmorStand = entityArmorStandConstructor.newInstance(getCraftWorldHandleMethod.invoke(craftWorldObject), x + 0.5, y, z + 0.5);
            Object armorStand = entityArmorStandClass.getMethod("getBukkitEntity").invoke(entityArmorStand);

            if (versionNumber < 18) {
                entityArmorStandClass.getMethod("setInvisible", boolean.class).invoke(entityArmorStand, true);
                entityArmorStandClass.getMethod("setCustomNameVisible", boolean.class).invoke(entityArmorStand, true);
                if (versionNumber < 13) {
                    entityArmorStandClass.getMethod("setCustomName", String.class).invoke(entityArmorStand, line);
                } else {
                    entityArmorStandClass.getMethod("setCustomName", IChatBaseComponentClass).invoke(entityArmorStand, chatComponentClass.getConstructor(String.class).newInstance(line));
                }
                if (versionNumber < 10) {
                    entityArmorStandClass.getMethod("setGravity", boolean.class).invoke(entityArmorStand, false);
                } else {
                    entityArmorStandClass.getMethod("setNoGravity", boolean.class).invoke(entityArmorStand, true);
                }
                entityArmorStandClass.getMethod("setBasePlate", boolean.class).invoke(entityArmorStand, false);
                entityArmorStandClass.getMethod("setSmall", boolean.class).invoke(entityArmorStand, true);
                if (versionNumber > 8) {
                    entityArmorStandClass.getMethod("setMarker", boolean.class).invoke(entityArmorStand, true);
                }
            } else {
                if (versionNumber < 19) {
                    craftArmorStandClass.getMethod("setVisible", boolean.class).invoke(armorStand, false);
                } else {
                    craftArmorStandClass.getMethod("setInvisible", boolean.class).invoke(armorStand, true);
                }
                craftArmorStandClass.getMethod("setCustomNameVisible", boolean.class).invoke(armorStand, true);
                craftArmorStandClass.getMethod("setCustomName", String.class).invoke(armorStand, line);
                craftArmorStandClass.getMethod("setGravity", boolean.class).invoke(armorStand, false);
                craftArmorStandClass.getMethod("setBasePlate", boolean.class).invoke(armorStand, false);
                craftArmorStandClass.getMethod("setSmall", boolean.class).invoke(armorStand, true);
                craftArmorStandClass.getMethod("setMarker", boolean.class).invoke(armorStand, true);
            }

            Constructor<?> spawnConstructor = spawnArmorStandClass.getConstructor(entityLivingClass);
            sendPacket(spawnConstructor.newInstance(entityArmorStand), player);

            sendMetaDataChange(entityArmorStand, armorStand);

            holograms.add(entityArmorStand);
            craftHolograms.add(armorStand);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException exception) {
            exception.printStackTrace();
            System.out.println("[CookieClicker] Error on creating the hologram");
        }
    }

    public void setCustomName(String customName) {
        try {
            for (int number = 0; number < holograms.size(); number++) {
                Object entityArmorStand = holograms.get(number);
                Object armorStand = craftHolograms.get(number);
                if (versionNumber < 18) {
                    if (versionNumber < 13) {
                        entityArmorStandClass.getMethod("setCustomName", String.class).invoke(entityArmorStand, customName);
                    } else {
                        entityArmorStandClass.getMethod("setCustomName", IChatBaseComponentClass).invoke(entityArmorStand, chatComponentClass.getConstructor(String.class).newInstance(customName));
                    }
                } else {
                    craftArmorStandClass.getMethod("setCustomName", String.class).invoke(craftHolograms.get(number), customName);
                }
                sendMetaDataChange(entityArmorStand, armorStand);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException exception) {
            exception.printStackTrace();
            System.out.println("[CookieClicker] Error on creating the hologram");
        }
    }

    private void sendMetaDataChange(Object entityArmorStand, Object armorStand) {
        try {
            Object id = null;
            Object ai = null;
            if (versionNumber < 18) {
                id = entityArmorStandClass.getMethod("getId").invoke(entityArmorStand);
                ai = entityArmorStandClass.getMethod("getDataWatcher").invoke(entityArmorStand);
            } else {
                id = craftArmorStandClass.getMethod("getEntityId").invoke(armorStand);
                ai = entityArmorStandClass.getMethod("ai").invoke(entityArmorStand);
            }
            Object changeConstructor = changeArmorStandClass.getConstructor(int.class, dataWatcherClass, boolean.class).newInstance(id, ai, false);
            sendPacket(changeConstructor, player);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException exception) {
            exception.printStackTrace();
            System.out.println("[CookieClicker] Error on removing the hologram");
        }
    }

    public void delete() {
        if (holograms.size() == 0) {
            return;
        }
        try {
            int[] ids = new int[holograms.size()];
            for (int number = 0; number < holograms.size(); number++) {
                if (versionNumber < 18) {
                    ids[number] = Integer.parseInt(entityArmorStandClass.getMethod("getId").invoke(holograms.get(number)).toString());
                } else {
                    ids[number] = Integer.parseInt(craftArmorStandClass.getMethod("getEntityId").invoke(craftHolograms.get(number)).toString());
                }
            }
            Object deleteConstructor = deleteArmorStandClass.getConstructor(int[].class).newInstance(ids);
            sendPacket(deleteConstructor, player);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException exception) {
            exception.printStackTrace();
            System.out.println("[CookieClicker] Error on removing the hologram");
        }
    }
}