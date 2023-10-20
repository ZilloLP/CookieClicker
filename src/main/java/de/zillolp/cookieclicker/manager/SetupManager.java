package de.zillolp.cookieclicker.manager;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.customconfigs.LocationConfig;
import de.zillolp.cookieclicker.enums.Setup;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.UUID;

public class SetupManager {
    private final LocationConfig locationConfig;
    private final HashMap<UUID, Setup> setups;

    public SetupManager(CookieClicker plugin) {
        this.locationConfig = plugin.getLocationConfig();
        this.setups = new HashMap<>();
    }

    public void saveLocation(UUID uuid, Location location) {
        if (!(setups.containsKey(uuid))) {
            return;
        }
        Setup setup = setups.get(uuid);
        int setupNumber = setup.getSetupNumber();
        switch (setup) {
            case CLICKER:
                locationConfig.saveLocation("CookieClicker.Clicker-" + setupNumber, location);
                break;
            case ALLTIME_WALL:
                if (setup.hasSign()) {
                    locationConfig.saveLocation("StatsWalls.Alltime." + setupNumber + ".head", location);
                    break;
                }
                locationConfig.saveLocation("StatsWalls.Alltime." + setupNumber + ".sign", location);

                break;
            case TIME_WALL:
                if (setup.hasSign()) {
                    locationConfig.saveLocation("StatsWalls.Time." + setupNumber + ".head", location);
                    break;
                }
                locationConfig.saveLocation("StatsWalls.Time." + setupNumber + ".sign", location);
                break;
        }
    }

    public HashMap<UUID, Setup> getSetups() {
        return setups;
    }
}
