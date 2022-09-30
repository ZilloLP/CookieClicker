package de.zillolp.cookieclicker.utils;

import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public class ActionBarUtil extends ReflectionUtil {
    private static Class<?> chatComponentClass;
    private static Class<?> actionBarClass;
    private static Class<?> IChatBaseComponentClass;
    private static Class<?> chatMessageTypeClass;

    private final Player player;
    private final String message;

    public ActionBarUtil(Player player, String message) {
        this.player = player;
        this.message = message;
    }

    public static void initialize() {
        try {
            if (versionNumber < 17) {
                chatComponentClass = Class.forName("net.minecraft.server." + version + ".ChatComponentText");
                actionBarClass = Class.forName("net.minecraft.server." + version + ".PacketPlayOutChat");
                IChatBaseComponentClass = Class.forName("net.minecraft.server." + version + ".IChatBaseComponent");
            } else if (versionNumber < 19) {
                chatComponentClass = Class.forName("net.minecraft.network.chat.ChatComponentText");
                actionBarClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutChat");
                IChatBaseComponentClass = Class.forName("net.minecraft.network.chat.IChatBaseComponent");
            } else {
                actionBarClass = Class.forName("net.minecraft.network.protocol.game.ClientboundSystemChatPacket");
            }
            if (versionNumber >= 12 && versionNumber < 17) {
                chatMessageTypeClass = Class.forName("net.minecraft.server." + version + ".ChatMessageType");
            } else if (versionNumber >= 17) {
                chatMessageTypeClass = Class.forName("net.minecraft.network.chat.ChatMessageType");
            }
        } catch (ClassNotFoundException exception) {
            exception.printStackTrace();
            System.out.println("[CookieClicker] Error on initializing the NMS classes from the actionbar");
        }
    }

    public void sendActionbar() {
        try {
            Object actionBarConstructor = null;
            Constructor chatComponentConstructor;
            if (versionNumber < 19) {
                Object chatComponent = chatComponentClass.getConstructor(String.class).newInstance(message);
                if (versionNumber < 12) {
                    chatComponentConstructor = actionBarClass.getConstructor(IChatBaseComponentClass, byte.class);
                    actionBarConstructor = chatComponentConstructor.newInstance(chatComponent, (byte) 2);
                } else if (versionNumber < 16) {
                    chatComponentConstructor = actionBarClass.getConstructor(IChatBaseComponentClass, chatMessageTypeClass);
                    Object chatMessageType = chatMessageTypeClass.getMethod("a", byte.class).invoke(chatComponent, (byte) 2);
                    actionBarConstructor = chatComponentConstructor.newInstance(chatComponent, chatMessageType);
                } else {
                    chatComponentConstructor = actionBarClass.getConstructor(IChatBaseComponentClass, chatMessageTypeClass, UUID.class);
                    Object chatMessageType = chatMessageTypeClass.getMethod("a", byte.class).invoke(chatComponent, (byte) 2);
                    actionBarConstructor = chatComponentConstructor.newInstance(chatComponent, chatMessageType, player.getUniqueId());
                }
            } else {
                chatComponentConstructor = actionBarClass.getConstructor(String.class, boolean.class);
                actionBarConstructor = chatComponentConstructor.newInstance("{\"text\":\"" + message + "\"}", true);
            }
            sendPacket(actionBarConstructor, player);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException exception) {
            exception.printStackTrace();
            System.out.println("[CookieClicker] Error on sending the actionbar");
        }
    }

}
