package de.zillolp.cookieclicker.profiles;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.enums.Design;
import de.zillolp.cookieclicker.enums.Price;
import de.zillolp.cookieclicker.manager.DatabaseManager;
import de.zillolp.cookieclicker.prices.CustomPrice;
import de.zillolp.cookieclicker.prices.NormalPrice;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PlayerProfile {
    private final CookieClicker plugin;
    private final UUID uuid;
    private String name;
    private long cookies;
    private long perClick;
    private long clickerClicks;
    private Design design;
    private HashMap<Price, Long> prices;

    public PlayerProfile(CookieClicker plugin, UUID uuid) {
        this.plugin = plugin;
        this.uuid = uuid;
        plugin.getDatabaseManager().loadProfile(this, false);
    }

    public PlayerProfile(CookieClicker plugin, Player player) {
        this.plugin = plugin;
        this.uuid = player.getUniqueId();
        this.prices = new HashMap<>();
        DatabaseManager databaseManager = plugin.getDatabaseManager();
        this.name = player.getName();
        if (!(databaseManager.playerExists(uuid, name))) {
            databaseManager.createPlayer(uuid, name);
            design = Design.BLACK_GLASS;
            setToDefault();
            return;
        }
        databaseManager.loadProfile(this, true);
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCookies() {
        return cookies;
    }

    public void setCookies(long cookies) {
        this.cookies = cookies;
    }

    public long getPerClick() {
        return perClick;
    }

    public void setPerClick(long perClick) {
        this.perClick = perClick;
    }

    public long getClickerClicks() {
        return clickerClicks;
    }

    public void setClickerClicks(long clickerClicks) {
        this.clickerClicks = clickerClicks;
    }

    public void setPrice(Price price, long value) {
        prices.put(price, value);
    }

    public Design getDesign() {
        return design;
    }

    public void setDesign(Design design) {
        this.design = design;
    }

    public HashMap<Price, Long> getPrices() {
        return prices;
    }

    public long getPrice(Price price) {
        return prices.get(price);
    }

    public void addCookies(long cookies) {
        setCookies(getCookies() + cookies);
    }

    public void addPerClick(long perClick) {
        setPerClick(getPerClick() + perClick);
    }

    public void addClickerClicks(long clicks) {
        setClickerClicks(getClickerClicks() + clicks);
    }

    public void addPrice(Price price) {
        CustomPrice customPrice = price.getCustomPrice();
        if (customPrice instanceof NormalPrice) {
            setPrice(price, getPrice(price) + (115L * ((NormalPrice) customPrice).getAddClick()));
        }
    }

    public void removeCookies(long cookies) {
        setCookies(getCookies() - cookies);
    }

    public void setToDefault() {
        cookies = 0;
        perClick = 1;
        clickerClicks = 0;
        if (prices == null) {
            return;
        }
        for (Price price : Price.values()) {
            setPrice(price, price.getCustomPrice().getDefaultPrice());
        }
    }

    public void uploadData() {
        plugin.getDatabaseManager().uploadProfile(this, false);
        design = null;
        prices = null;
    }
}
