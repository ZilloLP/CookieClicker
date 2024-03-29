package de.zillolp.cookieclicker.utils;

import net.minecraft.network.protocol.Packet;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class ReflectionUtil {
    public static void sendPacket(Packet<?> packet, Player player) {
        ((CraftPlayer) player).getHandle().connection.send(packet);
    }

    public static Object getValue(Object packet, String fieldName) {
        try {
            Field field = packet.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(packet);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public static Object getSuperValue(Object packet, String fieldName) {
        try {
            Field field = packet.getClass().getSuperclass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(packet);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public static void setValue(Object packet, String fieldName, Object value) {
        try {
            Field field = packet.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(packet, value);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
