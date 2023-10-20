package de.zillolp.cookieclicker.manager;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.database.DatabaseConnector;
import de.zillolp.cookieclicker.enums.Design;
import de.zillolp.cookieclicker.enums.Price;
import de.zillolp.cookieclicker.profiles.PlayerProfile;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class DatabaseManager {
    private final CookieClicker plugin;
    private final DatabaseConnector databaseConnector;

    public DatabaseManager(CookieClicker plugin) {
        this.plugin = plugin;
        this.databaseConnector = plugin.getDatabaseConnector();
        this.databaseConnector.updateStatement(databaseConnector.prepareStatement("CREATE TABLE IF NOT EXISTS cookieclicker_players(UUID VARCHAR(64), NAME VARCHAR(64), COOKIES BIGINT, PER_CLICK BIGINT, DESIGN VARCHAR(16), CLICKER_CLICKS BIGINT,"
                + "PRICE BIGINT, PRICE1 BIGINT, PRICE2 BIGINT, PRICE3 BIGINT, PRICE4 BIGINT, PRICE5 BIGINT, PRICE6 BIGINT, PREMIUM_PRICE BIGINT, PREMIUM_PRICE1 BIGINT, PREMIUM_PRICE2 BIGINT, PREMIUM_PRICE3 BIGINT, PREMIUM_PRICE4 BIGINT, PREMIUM_PRICE5 BIGINT, PREMIUM_PRICE6 BIGINT);"));
    }

    public void createPlayer(UUID uuid, String name) {
        try {
            PreparedStatement statement = databaseConnector.prepareStatement("INSERT INTO cookieclicker_players(UUID, NAME, COOKIES, PER_CLICK, DESIGN, CLICKER_CLICKS, " +
                    "PRICE, PRICE1, PRICE2, PRICE3, PRICE4, PRICE5, PRICE6, PREMIUM_PRICE, PREMIUM_PRICE1, PREMIUM_PRICE2, PREMIUM_PRICE3, PREMIUM_PRICE4, PREMIUM_PRICE5, PREMIUM_PRICE6)" +
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
            statement.setString(1, uuid.toString());
            statement.setString(2, name);
            statement.setLong(3, 1);
            statement.setLong(4, 1);
            statement.setString(5, "BLACK_GLASS");
            statement.setLong(6, 0);

            int parameterNumber = 7;
            for (Price price : Price.values()) {
                statement.setLong(parameterNumber, price.getCustomPrice().getDefaultPrice());
                parameterNumber++;
            }
            databaseConnector.updateStatement(statement);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public boolean playerExists(String name) {
        boolean isExisting = false;
        try {
            PreparedStatement statement = databaseConnector.prepareStatement("SELECT UUID, NAME FROM cookieclicker_players WHERE NAME= ?");
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                isExisting = resultSet.getString("UUID") != null;
            }
            resultSet.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return isExisting;
    }

    public boolean playerExists(UUID uuid, String name) {
        boolean isExisting = false;
        try {
            PreparedStatement statement = databaseConnector.prepareStatement("SELECT UUID, NAME FROM cookieclicker_players WHERE UUID= ?");
            statement.setString(1, uuid.toString());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                if (!(name.equalsIgnoreCase(resultSet.getString("NAME")))) {
                    setValue(uuid, "NAME", name);
                }
                isExisting = resultSet.getString("UUID") != null;
            }
            resultSet.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return isExisting;
    }

    public void loadProfiles() {
        try {
            PreparedStatement statement = databaseConnector.prepareStatement("SELECT UUID FROM cookieclicker_players");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                UUID uuid = UUID.fromString(resultSet.getString("UUID"));
                HashMap<UUID, PlayerProfile> playerProfiles = plugin.getCookieClickerManager().getPlayerProfiles();
                if (playerProfiles.containsKey(uuid)) {
                    continue;
                }
                playerProfiles.put(uuid, new PlayerProfile(plugin, uuid));
            }
            resultSet.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void loadProfile(PlayerProfile playerProfile, boolean isOnline) {
        try {
            PreparedStatement statement = databaseConnector.prepareStatement("SELECT * FROM cookieclicker_players WHERE UUID= ?");
            statement.setString(1, playerProfile.getUuid().toString());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                playerProfile.setName(resultSet.getString("NAME"));
                playerProfile.setCookies(resultSet.getLong("COOKIES"));
                playerProfile.setPerClick(resultSet.getLong("PER_CLICK"));
                playerProfile.setClickerClicks(resultSet.getLong("CLICKER_CLICKS"));
                if (isOnline) {
                    playerProfile.setDesign(Design.valueOf(resultSet.getString("DESIGN")));
                    for (Price price : Price.values()) {
                        playerProfile.getPrices().put(price, resultSet.getLong(price.name()));
                    }
                }
            }
            resultSet.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void uploadProfile(PlayerProfile playerProfile, boolean isReset) {
        try {
            PreparedStatement statement = databaseConnector.prepareStatement("UPDATE cookieclicker_players SET COOKIES= ?, PER_CLICK= ?, DESIGN= ?, CLICKER_CLICKS= ?," +
                    "PRICE= ?, PRICE1= ?, PRICE2= ?, PRICE3= ?, PRICE4= ?, PRICE5= ?, PRICE6= ?, PREMIUM_PRICE= ?, PREMIUM_PRICE1= ?, PREMIUM_PRICE2= ?, PREMIUM_PRICE3= ?, PREMIUM_PRICE4= ?, PREMIUM_PRICE5= ?, PREMIUM_PRICE6= ? " +
                    "WHERE UUID= ?");

            int parameterNumber = 5;
            if (isReset) {
                statement.setLong(1, 0);
                statement.setLong(2, 1);
                statement.setString(3, Design.BLACK_GLASS.name());
                statement.setLong(4, 0);
                for (Price price : Price.values()) {
                    statement.setLong(parameterNumber, price.getCustomPrice().getDefaultPrice());
                    parameterNumber++;
                }
            } else {
                statement.setLong(1, playerProfile.getCookies());
                statement.setLong(2, playerProfile.getPerClick());
                statement.setString(3, playerProfile.getDesign().name());
                statement.setLong(4, playerProfile.getClickerClicks());
                for (Price price : Price.values()) {
                    statement.setLong(parameterNumber, playerProfile.getPrice(price));
                    parameterNumber++;
                }
            }
            statement.setString(parameterNumber, playerProfile.getUuid().toString());
            databaseConnector.updateStatement(statement);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public int getRegisteredPlayerAmount() {
        int amount = 0;
        try {
            PreparedStatement statement = databaseConnector.prepareStatement("SELECT COUNT(UUID) FROM cookieclicker_players");
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                amount = resultSet.getInt("COUNT(UUID)");
            }
            resultSet.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return amount;
    }

    public void setValue(UUID uuid, String field, Object value) {
        try {
            PreparedStatement statement = databaseConnector.prepareStatement("UPDATE cookieclicker_players SET " + field + "= ? WHERE UUID= ?");
            statement.setObject(1, value);
            statement.setString(2, uuid.toString());
            databaseConnector.updateStatement(statement);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}
