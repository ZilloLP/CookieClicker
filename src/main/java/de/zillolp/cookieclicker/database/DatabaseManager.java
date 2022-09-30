package de.zillolp.cookieclicker.database;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.enums.Designs;
import de.zillolp.cookieclicker.profiles.PlayerProfile;
import org.bukkit.Bukkit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.UUID;

public class DatabaseManager {
    private final DatabaseConnector databaseConnector;
    private final CookieClicker cookieClicker = CookieClicker.cookieClicker;

    public DatabaseManager(DatabaseConnector databaseConnector) {
        this.databaseConnector = databaseConnector;
        databaseConnector.update("CREATE TABLE IF NOT EXISTS cookieclicker_players(UUID varchar(64), NAME varchar(64), COOKIES long, PER_CLICK long, DESIGN varchar(16), CLICKER_CLICKS long," +
                " PRICE long, PRICE1 long, PRICE2 long, PRICE3 long, PRICE4 long, PRICE5 long, PRICE6 long, PRICE7 long, PRICE8 long, PRICE9 long, PRICE10 long, PRICE11 long, PRICE12 long, PRICE13 long);");
    }

    public void createPlayer(UUID uuid, String name) {
        databaseConnector.update("INSERT INTO cookieclicker_players(UUID, NAME, COOKIES, PER_CLICK, DESIGN, CLICKER_CLICKS," +
                " PRICE, PRICE1, PRICE2, PRICE3, PRICE4, PRICE5, PRICE6, PRICE7, PRICE8, PRICE9, PRICE10, PRICE11, PRICE12, PRICE13) VALUES " +
                "('" + uuid.toString() + "', '" + name + "', '1', '1', 'BLACK_DESIGN', '0', '30', '360', '690', '920', '1250', '1580', '1910', '2240', '2570', '2900', '3230', '3560', '3890', '4220');");
    }

    public boolean playerExists(UUID uuid, String name) {
        try {
            ResultSet resultSet = databaseConnector.query("SELECT UUID, NAME FROM cookieclicker_players WHERE UUID= '" + uuid.toString() + "'");
            if (resultSet.next()) {
                if (!name.equals(resultSet.getString("NAME"))) {
                    updateUsername(uuid, name);
                }
                return resultSet.getString("UUID") != null;
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return false;
    }

    public void updateUsername(UUID uuid, String name) {
        databaseConnector.update("UPDATE cookieclicker_players SET NAME= '" + name + "' WHERE UUID= '" + uuid.toString() + "'");
    }

    public void loadProfile(UUID uuid) {
        try {
            ResultSet resultSet = databaseConnector.query("SELECT * FROM cookieclicker_players WHERE UUID= '" + uuid.toString() + "'");
            if (resultSet.next()) {
                PlayerProfile playerProfile = cookieClicker.getPlayerProfiles().get(uuid);
                playerProfile.setCookies(resultSet.getLong("COOKIES"));
                playerProfile.setPerClick(resultSet.getLong("PER_CLICK"));
                playerProfile.setClickerClicks(resultSet.getLong("CLICKER_CLICKS"));
                playerProfile.setDesigns(Designs.valueOf(resultSet.getString("DESIGN")));

                for (int number = 0; number < 14; number++) {
                    if (number == 0) {
                        playerProfile.setPrice(number, resultSet.getLong("PRICE"));
                    } else {
                        playerProfile.setPrice(number, resultSet.getLong("PRICE" + number));
                    }
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void resetProfile(UUID uuid, String name) {
        cookieClicker.getAlltimeUpdater().getCachedData().remove(name);
        cookieClicker.getTimeUpdater().getCachedData().remove(name);
        PlayerProfile playerProfile = cookieClicker.getPlayerProfiles().get(uuid);
        if (playerProfile == null) {
            setValue(uuid, "COOKIES", 0);
            setValue(uuid, "PER_CLICK", 1);
            setValue(uuid, "DESIGN", Designs.BLACK_DESIGN.name());
            for (int number = 0; number < 14; number++) {
                if (number == 0) {
                    setValue(uuid, "PRICE", 30);
                } else {
                    setValue(uuid, "PRICE" + number, 30 + 330 * number);
                }
            }
            return;
        }
        Bukkit.getPlayer(uuid).closeInventory();
        playerProfile.setCookies(0);
        playerProfile.setPerClick(1);
        playerProfile.setDesigns(Designs.BLACK_DESIGN);
        for (int number = 0; number < 14; number++) {
            playerProfile.setPrice(number, 30 + 330 * number);
        }
    }

    public long getValue(UUID uuid, String field) {
        try {
            ResultSet resultSet = databaseConnector.query("SELECT " + field + " FROM cookieclicker_players WHERE UUID= '" + uuid.toString() + "'");
            if (resultSet.next()) {
                return resultSet.getLong(field);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    public int getRank(UUID uuid, String field) {
        int number = 0;
        try {
            ResultSet resultSet = databaseConnector.query("SELECT UUID FROM cookieclicker_players ORDER BY " + field + " DESC");
            while (resultSet.next()) {
                number++;
                if (!(resultSet.getString("UUID").equalsIgnoreCase(uuid.toString()))) {
                    continue;
                }
                return number;
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return number;
    }

    public int getRegisteredPlayerAmount() {
        try {
            ResultSet resultSet = databaseConnector.query("SELECT COUNT(UUID) FROM cookieclicker_players");
            if (resultSet.next()) {
                return resultSet.getInt("COUNT(UUID)");
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    public void setValue(UUID uuid, String field, Object value) {
        databaseConnector.update("UPDATE cookieclicker_players SET " + field + "= '" + value + "' WHERE UUID= '" + uuid.toString() + "'");
    }

    public LinkedHashMap<String, Long> orderBy(String field, int range) {
        ResultSet resultSet = databaseConnector.query("SELECT UUID," + field + ",NAME FROM cookieclicker_players ORDER BY " + field + " DESC LIMIT " + range);
        LinkedHashMap<String, Long> results = new LinkedHashMap<>();

        try {
            while (resultSet.next()) {
                results.put(resultSet.getString("NAME"), resultSet.getLong(field));
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return results;
    }

}
