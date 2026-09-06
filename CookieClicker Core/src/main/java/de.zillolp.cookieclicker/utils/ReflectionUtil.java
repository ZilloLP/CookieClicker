package de.zillolp.cookieclicker.utils;

import com.google.common.collect.Multimap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.manager.VersionManager;
import net.minecraft.network.protocol.Packet;
import org.apache.commons.lang.reflect.FieldUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReflectionUtil {
    private final int MAX_API_CALLS_PER_MINUTE = 600;
    private final CookieClicker plugin;
    private final BukkitScheduler bukkitScheduler;
    private final Logger logger;
    private final int versionNumber;
    private final int subVersion;
    private final Semaphore apiRateLimiter = new Semaphore(MAX_API_CALLS_PER_MINUTE);
    private final Map<String, String> playerTextures = new LinkedHashMap<String, String>(16, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
            return size() > 500;
        }
    };
    private long lastRateLimitReset = System.currentTimeMillis();

    public ReflectionUtil(CookieClicker plugin) {
        this.plugin = plugin;
        bukkitScheduler = plugin.getServer().getScheduler();
        logger = plugin.getLogger();
        VersionManager versionManager = plugin.getVersionManager();
        versionNumber = versionManager.getVersionNumber();
        subVersion = versionManager.getSubVersion();
    }

    public Object invokeMethod(Object instance, String methodName) {
        Class<?> instanceClass = instance.getClass();
        try {
            Method method = instanceClass.getMethod(methodName);
            method.setAccessible(true);

            return method.invoke(instance);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            logger.log(Level.SEVERE, "Error invoking method " + methodName + " on " + instanceClass.getName(), exception);
            return null;
        }
    }

    public void sendPacket(Packet<?> packet, Player player) {
        String sendFieldName = "b";
        if (versionNumber >= 26) {
           sendFieldName = "send";
        }
        if (versionNumber <= 20 && subVersion <= 1) {
            sendFieldName = "a";
        }
        Object playerConnection = getPlayerConnection(player);
        try {
            Method sendPacketMethod = playerConnection.getClass().getMethod(sendFieldName, Packet.class);
            sendPacketMethod.invoke(playerConnection, packet);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            logger.log(Level.SEVERE, "Error sending Packet", exception);
        }
    }

    public String getIdMethodName() {
        return (versionNumber < 21 || (versionNumber == 21 && subVersion < 9)) ? "getId" : "id";
    }

    public String getNameMethodName() {
        return (versionNumber < 21 || (versionNumber == 21 && subVersion < 9)) ? "getName" : "name";
    }

    public String getPropertiesMethodName() {
        return (versionNumber < 21 || (versionNumber == 21 && subVersion < 9)) ? "getProperties" : "properties";
    }

    public String getPropertyValueName() {
        return (versionNumber == 20 && subVersion <= 1) ? "getValue" : "value";
    }

    public GameProfile convertPlayerProfileToGameProfile(PlayerProfile playerProfile) {
        URL skinURL = playerProfile.getTextures().getSkin();
        if (skinURL != null) {
            GameProfile gameProfile = new GameProfile(playerProfile.getUniqueId(), playerProfile.getName());
            addTexturePropertyToGameProfile(gameProfile, skinURL);
            return gameProfile;
        }

        try {
            Method buildGameProfileMethod = playerProfile.getClass().getMethod("buildGameProfile");
            buildGameProfileMethod.setAccessible(true);
            return (GameProfile) buildGameProfileMethod.invoke(playerProfile);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            logger.log(Level.WARNING, "Could not extract GameProfile from PlayerProfile", exception);
        }

        return new GameProfile(playerProfile.getUniqueId(), playerProfile.getName());
    }

    private void addTexturePropertyToGameProfile(GameProfile gameProfile, URL skinURL) {
        try {
            Property textureProperty = new com.mojang.authlib.properties.Property("textures",
                    Base64.getEncoder().encodeToString(
                            String.format("{\"textures\":{\"SKIN\":{\"url\":\"%s\"}}}", skinURL)
                                    .getBytes(StandardCharsets.UTF_8)
                    )
            );

            if (versionNumber < 21 || (versionNumber == 21 && subVersion < 10)) {
                ((PropertyMap) gameProfile.getClass().getMethod("getProperties").invoke(gameProfile)).put("textures", textureProperty);
            } else {
                Field propertiesField = PropertyMap.class.getDeclaredField("properties");
                propertiesField.setAccessible(true);
                Multimap<String, Property> multimap = com.google.common.collect.HashMultimap.create();
                multimap.put("textures", textureProperty);
                propertiesField.set((PropertyMap) gameProfile.getClass().getMethod("properties").invoke(gameProfile), multimap);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException |
                 NoSuchFieldException exception) {
            logger.log(Level.SEVERE, "Error setting Properties for custom texture", exception);
        }
    }

    public boolean isSpigot() {
        String serverName = Bukkit.getServer().getName();
        return serverName.equalsIgnoreCase("spigot") || serverName.equalsIgnoreCase("CraftBukkit");
    }

    public boolean isSameLocation(Location location, Location location1) {
        if (location == null || location1 == null) {
            return false;
        }
        World world = location.getWorld();
        World world1 = location1.getWorld();
        if (world == null || world1 == null) {
            return false;
        }
        return world.getName().equals(world1.getName()) && location.getX() == location1.getX() && location.getY() == location1.getY() && location.getZ() == location1.getZ();
    }

    public Object getValue(Object packet, String fieldName) {
        try {
            return FieldUtils.readField(packet, fieldName, true);
        } catch (Exception exception) {
            logger.log(Level.SEVERE, "Error getting Value", exception);
            return null;
        }
    }

    public Class<?> getCraftObjectClass(boolean isSpigot, String className) {
                String bukkitDomain = "org.bukkit.craftbukkit.";
                try {
                    String craftObjectClassName = bukkitDomain + className;
                    if ((isSpigot || (versionNumber < 21 && subVersion < 6)) && versionNumber < 26) {
                        String serverPackageName = plugin.getServer().getClass().getPackage().getName();
                        String version = serverPackageName.substring(serverPackageName.lastIndexOf(".") + 1);
                        craftObjectClassName = bukkitDomain + version + "." + className;
                    }
                    return Class.forName(craftObjectClassName);
                } catch (ClassNotFoundException exception) {
                    logger.log(Level.SEVERE, "Error getting CraftObject class", exception);
            return null;
        }
    }

    public Object getServerObject(Object object, Class<?> castingClass) {
        try {
            return castingClass.getMethod("getHandle").invoke(castingClass.cast(object));
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException exception) {
            logger.log(Level.SEVERE, "Error getting ServerObject", exception);
            return null;
        }
    }

    public PlayerProfile getRealPlayerProfile(Player player) {
        try {
            Object craftPlayer = getCraftObjectClass(isSpigot(), "entity.CraftPlayer").cast(player);
            GameProfile gameProfile = (GameProfile) craftPlayer.getClass().getMethod("getProfile").invoke(craftPlayer);

            PlayerProfile playerProfile = Bukkit.createPlayerProfile(
                    (UUID) gameProfile.getClass().getMethod(getIdMethodName()).invoke(gameProfile),
                    (String) gameProfile.getClass().getMethod(getNameMethodName()).invoke(gameProfile)
            );
            PropertyMap propertyMap = (PropertyMap) gameProfile.getClass().getMethod(getPropertiesMethodName()).invoke(gameProfile);
            if (!propertyMap.isEmpty()) {
                for (Property property : propertyMap.get("textures")) {
                    JsonObject textureObject = JsonParser.parseString(new String(Base64.getDecoder().decode(invokeMethod(property, getPropertyValueName()).toString()))).getAsJsonObject();
                    if (textureObject.has("textures") && textureObject.getAsJsonObject("textures").has("SKIN")) {
                        String skinUrl = textureObject.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();
                        PlayerTextures playerTextures = playerProfile.getTextures();
                        playerTextures.setSkin(new URL(skinUrl));
                        playerProfile.setTextures(playerTextures);
                    }
                }
            }
            return playerProfile;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException |
                 MalformedURLException exception) {
            logger.log(Level.SEVERE, "Error getting real player profile", exception);
            return player.getPlayerProfile();
        }
    }

    public CompletableFuture<PlayerProfile> getPlayerProfile(String url, String name) {
        CompletableFuture<PlayerProfile> completableFuture = new CompletableFuture<>();
        bukkitScheduler.runTaskAsynchronously(plugin, () -> {
            PlayerProfile playerProfile = Bukkit.createPlayerProfile(UUID.randomUUID(), name);
            PlayerTextures playerTextures = playerProfile.getTextures();
            URL urlObject = null;
            try {
                urlObject = new URL(url);
            } catch (MalformedURLException exception) {
                completableFuture.complete(playerProfile);
            }
            if (urlObject != null) {
                playerTextures.setSkin(urlObject);
            }
            playerProfile.setTextures(playerTextures);
            completableFuture.complete(playerProfile);
        });
        return completableFuture;
    }

    public CompletableFuture<String> getTextureURL(String playerName) {
        String inputName = playerName.toLowerCase();
        CompletableFuture<String> completableFuture = new CompletableFuture<>();

        synchronized (playerTextures) {
            if (playerTextures.containsKey(inputName)) {
                completableFuture.complete(playerTextures.get(inputName));
                return completableFuture;
            }
        }

        bukkitScheduler.runTaskAsynchronously(plugin, () -> {
            try {
                resetRateLimiterIfNeeded();

                if (!apiRateLimiter.tryAcquire()) {
                    logger.log(Level.WARNING, "API rate limit reached, skipping texture fetch for: " + playerName);
                    completableFuture.complete(null);
                    return;
                }

                try {
                    URL uuidURL = new URL("https://api.minecraftservices.com/minecraft/profile/lookup/name/" + playerName);
                    try (InputStreamReader uuidReader = new InputStreamReader(uuidURL.openStream())) {
                        JsonObject uuidJson = JsonParser.parseReader(uuidReader).getAsJsonObject();
                        if (uuidJson == null || !uuidJson.has("id")) {
                            completableFuture.complete(null);
                            return;
                        }
                        String uuid = uuidJson.get("id").getAsString();

                        URL profileURL = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid);
                        try (InputStreamReader profileReader = new InputStreamReader(profileURL.openStream())) {
                            JsonObject profileResponse = JsonParser.parseReader(profileReader).getAsJsonObject();
                            if (profileResponse == null || !profileResponse.has("properties")) {
                                completableFuture.complete(null);
                                return;
                            }

                            String base64Encoded = profileResponse.getAsJsonArray("properties").get(0).getAsJsonObject().get("value").getAsString();
                            String decodedJson = new String(Base64.getDecoder().decode(base64Encoded));

                            JsonObject textureObject = JsonParser.parseString(decodedJson).getAsJsonObject();
                            String textureURL = textureObject.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();

                            synchronized (playerTextures) {
                                playerTextures.put(inputName, textureURL);
                            }
                            completableFuture.complete(textureURL);
                        }
                    }
                } finally {
                    apiRateLimiter.release();
                }
            } catch (IOException exception) {
                if (exception.getMessage() != null && exception.getMessage().contains("404")) {
                    completableFuture.complete(null);
                } else {
                    logger.log(Level.SEVERE, "Error getting TextureURL for player: " + playerName, exception);
                    completableFuture.complete(null);
                }
            } catch (Exception exception) {
                logger.log(Level.SEVERE, "Error getting TextureURL for player: " + playerName, exception);
                completableFuture.complete(null);
            }
        });
        return completableFuture;
    }

    private synchronized void resetRateLimiterIfNeeded() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastRateLimitReset >= 60000) {
            int availablePermits = apiRateLimiter.availablePermits();
            int usedPermits = MAX_API_CALLS_PER_MINUTE - availablePermits;
            apiRateLimiter.release(usedPermits);
            lastRateLimitReset = currentTime;
        }
    }

    public Object getPlayerConnection(Player player) {
        String connectionFieldName = "c";
        if(versionNumber >= 26){
            connectionFieldName = "connection";
        } else if (versionNumber >= 21 && subVersion > 1) {
            if (subVersion <= 5) {
                connectionFieldName = "f";
            } else {
                connectionFieldName = "g";
            }
        }
        System.out.println(isSpigot() + ", " + connectionFieldName);
        return getValue(getServerObject(player, getCraftObjectClass(isSpigot(), "entity.CraftPlayer")), connectionFieldName);
    }

    public Object getSound(String soundName) {
        try {
            Class<?> soundClass = Class.forName("org.bukkit.Sound");
            Method valueOfMethod = soundClass.getMethod("valueOf", String.class);
            return valueOfMethod.invoke(null, soundName);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
                 InvocationTargetException exception) {
            logger.log(Level.SEVERE, "Error getting Sound", exception);
        }
        return null;
    }

    public Inventory getOpenInventory(Player player) {
        return (Inventory) invokeMethod(player.getOpenInventory(), "getTopInventory");
    }

    public String getInventoryTitle(InventoryEvent event) {
        return (String) invokeMethod(event.getView(), "getTitle");
    }

    public void setValue(Object packet, String fieldName, Object value) {
        try {
            Field field = packet.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(packet, value);
        } catch (Exception exception) {
            logger.log(Level.SEVERE, "Error setting Value", exception);
        }
    }
}
