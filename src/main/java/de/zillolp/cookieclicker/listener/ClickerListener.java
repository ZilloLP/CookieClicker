package de.zillolp.cookieclicker.listener;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.ConfigTools;
import de.zillolp.cookieclicker.config.LanguageTools;
import de.zillolp.cookieclicker.manager.SoundManager;
import de.zillolp.cookieclicker.profiles.InventoryProfile;
import de.zillolp.cookieclicker.profiles.PlayerProfile;
import de.zillolp.cookieclicker.utils.ActionBarUtil;
import de.zillolp.cookieclicker.utils.InventorySetter;
import de.zillolp.cookieclicker.xclasses.XSound;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

public class ClickerListener implements Listener {
    private final CookieClicker cookieClicker = CookieClicker.cookieClicker;

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }
        Location location = clickedBlock.getLocation();
        if (!(clickedBlock.getType() != Material.AIR && cookieClicker.getClickerLocations().containsValue(location))) {
            return;
        }
        PlayerProfile playerProfile = cookieClicker.getPlayerProfiles().get(player.getUniqueId());
        if (playerProfile == null) {
            return;
        }
        event.setCancelled(true);
        Action action = event.getAction();
        SoundManager soundManager = playerProfile.getSoundManager();
        if (action == Action.RIGHT_CLICK_BLOCK) {
            if (playerProfile.getLastClick() + 250L > System.currentTimeMillis()) {
                return;
            }
            InventoryProfile inventoryProfile = playerProfile.getInventoryProfile();
            Inventory homeInventory = inventoryProfile.getHomeInventory();
            if (homeInventory == null) {
                homeInventory = inventoryProfile.setHomeInventory(Bukkit.createInventory(null, 5 * 9, LanguageTools.getLanguage("HOME_TITLE")));
            }
            InventorySetter.setHomeInventory(player, homeInventory);
            player.openInventory(homeInventory);
            playerProfile.setLastClick(System.currentTimeMillis());
            soundManager.playSound(XSound.BLOCK_CHEST_OPEN);
            return;
        }
        if (playerProfile.getLastMove() + ConfigTools.getAfkCoolDown() < System.currentTimeMillis()) {
            new ActionBarUtil(player, LanguageTools.getLanguage("AFK_MESSAGE")).sendActionbar();
            soundManager.playSound(XSound.BLOCK_ANVIL_BREAK);
            return;
        }
        playerProfile.addCPS(1);
        if (playerProfile.isOverCPS()) {
            new ActionBarUtil(player, LanguageTools.getLanguage("MAX_CPS")).sendActionbar();
            soundManager.playSound(XSound.BLOCK_ANVIL_BREAK);
            return;
        }
        playerProfile.addClickerClicks(1);
        playerProfile.addCookies(playerProfile.getPerClick());
        new ActionBarUtil(player, LanguageTools.getLanguageReplaced("CLICK_MESSAGE", "%cookies%", playerProfile.getCookies())).sendActionbar();
        soundManager.playSound(XSound.ENTITY_ITEM_PICKUP);
    }

    @EventHandler
    public void onPlayerAnimationEvent(PlayerAnimationEvent event) {
        Player player = event.getPlayer();
        Block block = player.getTargetBlock(null, 5);
        if (player.getGameMode() != GameMode.ADVENTURE || event.getAnimationType() != PlayerAnimationType.ARM_SWING || block.getType() == Material.AIR) {
            return;
        }
        if (!(cookieClicker.getClickerLocations().containsValue(block.getLocation()))) {
            return;
        }
        PlayerProfile playerProfile = cookieClicker.getPlayerProfiles().get(player.getUniqueId());
        if (playerProfile == null) {
            return;
        }
        SoundManager soundManager = playerProfile.getSoundManager();
        if (playerProfile.getLastMove() + ConfigTools.getAfkCoolDown() < System.currentTimeMillis()) {
            new ActionBarUtil(player, LanguageTools.getLanguage("AFK_MESSAGE")).sendActionbar();
            soundManager.playSound(XSound.BLOCK_ANVIL_BREAK);
            return;
        }
        playerProfile.addCPS(1);
        if (playerProfile.isOverCPS()) {
            new ActionBarUtil(player, LanguageTools.getLanguage("MAX_CPS")).sendActionbar();
            soundManager.playSound(XSound.BLOCK_ANVIL_BREAK);
            return;
        }
        playerProfile.addClickerClicks(1);
        playerProfile.addCookies(playerProfile.getPerClick());
        new ActionBarUtil(player, LanguageTools.getLanguageReplaced("CLICK_MESSAGE", "%cookies%", playerProfile.getCookies())).sendActionbar();
        soundManager.playSound(XSound.ENTITY_ITEM_PICKUP);
    }
}
