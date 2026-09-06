package de.zillolp.cookieclicker.manager;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.enums.GameVersion;
import de.zillolp.cookieclicker.interfaces.Hologram;
import de.zillolp.cookieclicker.interfaces.ItemBuilder;
import de.zillolp.cookieclicker.interfaces.NmsBridge;
import de.zillolp.cookieclicker.interfaces.PacketReader;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VersionManager {
    private final CookieClicker plugin;
    private final Logger logger;
    private final int versionNumber;
    private final int subVersion;
    private final String[] wrongVersionMessage;
    private final GameVersion lowestGameVersion = GameVersion.v1_20_R1;
    private final GameVersion highestGameVersion = GameVersion.v26_R2;
    private final int highestVersionNumber = highestGameVersion.getVersionNumber();
    private final int highestSubVersionNumber = highestGameVersion.getSubVersionNumber();
    private final int lowestVersionNumber = lowestGameVersion.getVersionNumber();
    private final int lowestSubVersionNumber = lowestGameVersion.getSubVersionNumber();

    public VersionManager(CookieClicker plugin) {
        this.plugin = plugin;
        logger = plugin.getLogger();
        String version = plugin.getServer().getBukkitVersion();

        String[] versionParts = version.substring(0, version.indexOf('-')).split("\\.");
        int tempVersionNumber = 0;
        int tempSubVersion = 0;
        try {
            if (versionParts[0].equals("1")) {
                // Legacy format: 1.21.1-R0.1-SNAPSHOT → parts[1]=21, parts[2]=1
                if (versionParts.length >= 2) {
                    tempVersionNumber = Integer.parseInt(versionParts[1]);
                }
                if (versionParts.length >= 3) {
                    tempSubVersion = Integer.parseInt(versionParts[2]);
                }
            } else {
                // New format: 26.2.build.112-stable → parts[0]=26, parts[1]=2
                tempVersionNumber = Integer.parseInt(versionParts[0]);
                if (versionParts.length >= 2) {
                    tempSubVersion = Integer.parseInt(versionParts[1]);
                }
            }
        } catch (NumberFormatException e) {
            logger.log(Level.SEVERE, "Failed to parse server version: " + version + " - Plugin cannot verify compatibility!", e);
            tempVersionNumber = -1;
            tempSubVersion = -1;
        }

        versionNumber = tempVersionNumber;
        subVersion = tempSubVersion;
        wrongVersionMessage = new String[]{"You are using a CookieClicker version that is not programmed for your server version.", "Your server version must be 1." + lowestVersionNumber + "." + lowestSubVersionNumber + " - 1." + highestVersionNumber + "." + highestSubVersionNumber + ", otherwise the plugin will not work!"};
    }

    public boolean checkVersion() {
        if (versionNumber > lowestVersionNumber && versionNumber < highestVersionNumber) {
            return true;
        }
        if (versionNumber == lowestVersionNumber && subVersion >= lowestSubVersionNumber) {
            return true;
        }
        return versionNumber == highestVersionNumber && subVersion <= highestSubVersionNumber;
    }

    public PacketReader getPacketReader() {
        String packagePath = "listener.PacketReader";
        PacketReader packetReader = null;
        if (versionNumber <= 20 && subVersion < GameVersion.v1_20_R4.getSubVersionNumber()) {
            packetReader = (PacketReader) getPackageObject(packagePath, GameVersion.v1_20_R1, plugin);
        } else if (versionNumber <= 21){
            packetReader = (PacketReader) getPackageObject(packagePath, GameVersion.v1_20_R4, plugin);
        }else if (versionNumber == 26){
            packetReader = (PacketReader) getPackageObject(packagePath, GameVersion.v26_R2, plugin);
        }
        if (packetReader == null) {
            logger.log(Level.SEVERE, "VersionManager could not find a valid PacketReader implementation for this server version.");
        }
        return packetReader;
    }

    public ItemBuilder getItemBuilder() {
        String packagePath = "utils.ItemBuilder";
        ItemBuilder itemBuilder = null;
        if (versionNumber == 20) {
            if (subVersion < GameVersion.v1_20_R4.getSubVersionNumber()) {
                itemBuilder = (ItemBuilder) getPackageObject(packagePath, GameVersion.v1_20_R1, plugin);
            } else {
                itemBuilder = (ItemBuilder) getPackageObject(packagePath, GameVersion.v1_20_R4, plugin);
            }
        } else if (versionNumber == 21) {
            if (subVersion <= GameVersion.v1_21_R3.getSubVersionNumber()) {
                itemBuilder = (ItemBuilder) getPackageObject(packagePath, GameVersion.v1_21_R1, plugin);
            } else {
                itemBuilder = (ItemBuilder) getPackageObject(packagePath, GameVersion.v1_21_R5, plugin);
            }
        } else if (versionNumber == 26) {
            if(subVersion <= GameVersion.v26_R2.getSubVersionNumber()) {
                itemBuilder = (ItemBuilder) getPackageObject(packagePath, GameVersion.v26_R2, plugin);
            }
        }
        if (itemBuilder == null) {
            logger.log(Level.SEVERE, "VersionManager could not find a valid ItemBuilder implementation for this server version.");
        }
        return itemBuilder;
    }

    public Hologram getHologram(String line) {
        String packagePath = "holograms.Hologram";
        Hologram hologram = null;
        switch (versionNumber) {
            case 20:
                if (subVersion <= GameVersion.v1_20_R1.getSubVersionNumber()) {
                    hologram = (Hologram) getPackageObject(packagePath, GameVersion.v1_20_R1, plugin, line);
                    break;
                } else if (subVersion <= GameVersion.v1_20_R2.getSubVersionNumber()) {
                    hologram = (Hologram) getPackageObject(packagePath, GameVersion.v1_20_R2, plugin, line);
                    break;
                } else if (subVersion <= GameVersion.v1_20_R3.getSubVersionNumber()) {
                    hologram = (Hologram) getPackageObject(packagePath, GameVersion.v1_20_R3, plugin, line);
                    break;
                }else if (subVersion <= GameVersion.v1_20_R4.getSubVersionNumber()) {
                    hologram = (Hologram) getPackageObject(packagePath, GameVersion.v1_20_R4, plugin, line);
                    break;
                }
                hologram = (Hologram) getPackageObject(packagePath, GameVersion.v1_21_R5, plugin, line);
                break;
            case 21:
                if (subVersion <= GameVersion.v1_21_R1.getSubVersionNumber()) {
                    hologram = (Hologram) getPackageObject(packagePath, GameVersion.v1_21_R1, plugin, line);
                    break;
                } else if (subVersion <= GameVersion.v1_21_R2.getSubVersionNumber()) {
                    hologram = (Hologram) getPackageObject(packagePath, GameVersion.v1_21_R2, plugin, line);
                    break;
                } else if (subVersion <= GameVersion.v1_21_R3.getSubVersionNumber()) {
                    hologram = (Hologram) getPackageObject(packagePath, GameVersion.v1_21_R3, plugin, line);
                    break;
                } else if (subVersion <= GameVersion.v1_21_R4.getSubVersionNumber()) {
                    hologram = (Hologram) getPackageObject(packagePath, GameVersion.v1_21_R4, plugin, line);
                    break;
                } else if (subVersion <= GameVersion.v1_21_R5.getSubVersionNumber()) {
                    hologram = (Hologram) getPackageObject(packagePath, GameVersion.v1_21_R5, plugin, line);
                    break;
                } else if (subVersion <= GameVersion.v1_21_R7.getSubVersionNumber()) {
                    hologram = (Hologram) getPackageObject(packagePath, GameVersion.v1_21_R7, plugin, line);
                    break;
                }
                hologram = (Hologram) getPackageObject(packagePath, GameVersion.v1_21_R8, plugin, line);
                break;
            case 26:
                if (subVersion <= GameVersion.v26_R2.getSubVersionNumber()) {
                    hologram = (Hologram) getPackageObject(packagePath, GameVersion.v26_R2, plugin, line);
                }
        }
        if (hologram == null) {
            logger.log(Level.SEVERE, "VersionManager could not find a valid Hologram implementation for this server version.");
        }
        return hologram;
    }

    public NmsBridge getNmsBridge() {
        String packagePath = "utils.NmsBridge";
        NmsBridge nmsBridge = null;
        if (versionNumber == 26) {
            nmsBridge = (NmsBridge) getPackageObject(packagePath, GameVersion.v26_R2, plugin);
        }else{
            nmsBridge = (NmsBridge) getPackageObject(packagePath, GameVersion.v1_21_R8, plugin);
        }
        if (nmsBridge == null) {
            logger.log(Level.SEVERE, "VersionManager could not find a valid NmsBridge implementation for this server version.");
        }
        return nmsBridge;
    }

    private Object getPackageObject(String ClassName, GameVersion gameVersion, Object... Objects) {
        try {
            Class<?> versionClass = Class.forName(plugin.getClass().getPackage().getName() + "." + ClassName + "_" + gameVersion.name());
            if (Objects.length == 0) {
                return versionClass.getConstructor().newInstance();
            }
            Class<?>[] classes = Arrays.stream(Objects).map(Object::getClass).toArray(Class<?>[]::new);
            return versionClass.getConstructor(classes).newInstance(Objects);
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException exception) {
            logger.log(Level.SEVERE, "VersionManager could not find a valid implementation for this server version: " + exception);
        }
        return null;
    }

    public String[] getWrongVersionMessage() {
        return wrongVersionMessage;
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    public int getSubVersion() {
        return subVersion;
    }
}
