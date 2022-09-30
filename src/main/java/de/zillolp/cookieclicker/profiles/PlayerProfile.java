package de.zillolp.cookieclicker.profiles;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.enums.Designs;
import de.zillolp.cookieclicker.enums.Setups;
import de.zillolp.cookieclicker.manager.PlayerManager;
import de.zillolp.cookieclicker.manager.SoundManager;
import de.zillolp.cookieclicker.runnables.ResetTimerUpdater;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PlayerProfile {
    private final CookieClicker cookieClicker = CookieClicker.cookieClicker;
    private final PlayerManager playerManager;
    private final SoundManager soundManager;
    private final InventoryProfile inventoryProfile;
    private final HashMap<String, Object> values;
    private final UUID uuid;
    private final String name;
    private boolean isOverCPS;
    private int cps;
    private long lastMove;
    private long lastClick;
    private Setups setups;
    private int setupNumber;

    public PlayerProfile(Player player) {
        playerManager = new PlayerManager(player);
        soundManager = new SoundManager(player);
        inventoryProfile = new InventoryProfile();
        values = playerManager.getValues();
        uuid = player.getUniqueId();
        name = player.getName();
        isOverCPS = false;
        cps = 0;
        lastMove = System.currentTimeMillis();
        lastClick = System.currentTimeMillis();
        setups = Setups.NONE;
        setupNumber = 0;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public SoundManager getSoundManager() {
        return soundManager;
    }

    public InventoryProfile getInventoryProfile() {
        return inventoryProfile;
    }

    public String getName() {
        return name;
    }

    public long getCookies() {
        return (long) values.get("COOKIES");
    }

    public long getPerClick() {
        return (long) values.get("PER_CLICK");
    }

    public long getClickerClicks() {
        return (long) values.get("CLICKER_CLICKS");
    }

    public Designs getDesign() {
        return Designs.valueOf((String) values.get("DESIGN"));
    }

    public long getPrice(int number) {
        if (number == 0) {
            return (long) values.get("PRICE");
        } else {
            return (long) values.get("PRICE" + number);
        }
    }

    public boolean isOverCPS() {
        return isOverCPS;
    }

    public int getCPS() {
        return cps;
    }

    public long getLastMove() {
        return lastMove;
    }

    public long getLastClick() {
        return lastClick;
    }

    public Setups getSetups() {
        return setups;
    }

    public int getSetupNumber() {
        return setupNumber;
    }

    public void setCookies(long cookies) {
        values.put("COOKIES", cookies);
    }

    public void setPerClick(long perClick) {
        values.put("PER_CLICK", perClick);
    }

    public void setClickerClicks(long clicks) {
        values.put("CLICKER_CLICKS", clicks);
    }

    public void setDesigns(Designs design) {
        values.put("DESIGN", design.name());
    }

    public void setPrice(int number, long price) {
        if (number == 0) {
            values.put("PRICE", price);
        } else {
            values.put("PRICE" + number, price);
        }
    }

    public void setOverCPS(boolean overCPS) {
        isOverCPS = overCPS;
    }

    public void setCPS(int cps) {
        this.cps = cps;
    }

    public void setLastMove(long lastMove) {
        this.lastMove = lastMove;
    }

    public void setLastClick(long lastClick) {
        this.lastClick = lastClick;
    }

    public void setSetups(Setups setups) {
        this.setups = setups;
    }

    public void setSetupNumber(int setupNumber) {
        this.setupNumber = setupNumber;
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

    public void addPrice(int number, long price) {
        setPrice(number, getPrice(number) + price);
    }

    public void addBoughtClicks(long clicks) {
        HashMap<String, Long> cachedData = cookieClicker.getTimeUpdater().getCachedData();
        if (cachedData.containsKey(name)) {
            cachedData.replace(name, cachedData.get(name) + clicks);
        } else {
            cachedData.put(name, clicks);
        }
    }

    public void addCPS(int cps) {
        setCPS(getCPS() + cps);
    }

    public void removeCookies(long cookies) {
        setCookies(getCookies() - cookies);
    }

    public void removeCPS(int cps) {
        int newCPS = getCPS() - cps;
        if (newCPS < 0) {
            setCPS(0);
            return;
        }
        setCPS(newCPS);
    }

    public void uploadProfile() {
        playerManager.uploadStats();
        playerManager.deleteHolograms();
        ResetTimerUpdater.deleteTimer(uuid);
        cookieClicker.getPlayerProfiles().remove(uuid);
    }
}
