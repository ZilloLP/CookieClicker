package de.zillolp.cookieclicker.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

public class ReflectionUtil {
    public static String version;
    public static int versionNumber;
    public static String secondVersionNumber;
    private static Class<?> packetClass;

    public static void initialize() {
        String serverPackageName = Bukkit.getServer().getClass().getPackage().getName();
        version = serverPackageName.substring(serverPackageName.lastIndexOf(".") + 1);
        String bukkitVersion = Bukkit.getBukkitVersion().replace(".", "-");
        versionNumber = Integer.parseInt(bukkitVersion.split("-")[1]);
        secondVersionNumber = bukkitVersion.split("-")[2];
        try {
            if (versionNumber < 17) {
                packetClass = Class.forName("net.minecraft.server." + version + ".Packet");
            } else {
                packetClass = Class.forName("net.minecraft.network.protocol.Packet");
            }
        } catch (ClassNotFoundException exception) {
            exception.printStackTrace();
            System.out.println("Fehler beim initialisieren der NMS Klassen");
        }
    }

    public void sendPacket(Object packet) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendPacket(packet, player);
        }
    }

    public void sendPacket(Object packet, Player player) {
        try {
            Method getPlayerHandleMethod = player.getClass().getMethod("getHandle");
            Object entityPlayerObject = getPlayerHandleMethod.invoke(player);
            Object playerConnection = null;
            Method sendPacketMethod = null;
            if (versionNumber < 17) {
                playerConnection = entityPlayerObject.getClass().getField("playerConnection").get(entityPlayerObject);
                sendPacketMethod = playerConnection.getClass().getMethod("sendPacket", packetClass);
            } else if (versionNumber == 17) {
                playerConnection = entityPlayerObject.getClass().getField("b").get(entityPlayerObject);
                sendPacketMethod = playerConnection.getClass().getMethod("sendPacket", packetClass);
            } else {
                playerConnection = entityPlayerObject.getClass().getField("b").get(entityPlayerObject);
                sendPacketMethod = playerConnection.getClass().getMethod("a", packetClass);
            }
            sendPacketMethod.invoke(playerConnection, packet);
        } catch (Exception exception) {
            exception.printStackTrace();
            System.out.println("[CookieClicker] Error on sending the package");
        }
    }

}