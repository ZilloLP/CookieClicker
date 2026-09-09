package de.zillolp.cookieclicker.commands.subcommands;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.customconfigs.PluginConfig;
import de.zillolp.cookieclicker.custominventories.CustomInventory;
import de.zillolp.cookieclicker.database.DatabaseManager;
import de.zillolp.cookieclicker.enums.PluginLanguage;
import de.zillolp.cookieclicker.enums.PluginPermission;
import de.zillolp.cookieclicker.manager.ClickerPlayerManager;
import de.zillolp.cookieclicker.manager.CookieClickerManager;
import de.zillolp.cookieclicker.manager.DesignManager;
import de.zillolp.cookieclicker.profiles.ClickerStatsProfile;
import de.zillolp.cookieclicker.utils.ReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ResetSubCommand extends SubCommand {
    private final ReflectionUtil reflectionUtil;
    private final PluginConfig pluginConfig;
    private final DatabaseManager databaseManager;
    private final ClickerPlayerManager clickerPlayerManager;
    private final CookieClickerManager cookieClickerManager;
    private final DesignManager designManager;

    public ResetSubCommand(CookieClicker plugin, String mainCommand, String... subCommands) {
        super(plugin, mainCommand, subCommands);
        reflectionUtil = plugin.getReflectionUtil();
        pluginConfig = plugin.getPluginConfig();
        databaseManager = plugin.getDatabaseManager();
        clickerPlayerManager = plugin.getClickerPlayerManager();
        cookieClickerManager = plugin.getCookieClickerManager();
        designManager = plugin.getDesignManager();
    }

    @Override
    public boolean onlyPlayer() {
        return false;
    }

    @Override
    public boolean onCommand(CookieClicker plugin, CommandSender commandSender, Command command, String[] args) {
        if (args.length != 2) {
            return false;
        }
        if (!(databaseManager.playerExists(args[1]))) {
            commandSender.sendMessage(languageConfig.getLanguageWithPrefix(PluginLanguage.PLAYER_NOT_FOUND));
            return true;
        }
        Optional<UUID> uuidOptional = databaseManager.getUUIDbyName(args[1]);
        if (!(uuidOptional.isPresent())) {
            commandSender.sendMessage(languageConfig.getLanguageWithPrefix(PluginLanguage.PLAYER_NOT_FOUND));
            return true;
        }
        UUID uuid = uuidOptional.get();
        Player player = Bukkit.getPlayer(uuid);
        boolean isPlayerOnline = player != null && Bukkit.getOnlinePlayers().contains(player);
        if (isPlayerOnline) {
            for (CustomInventory customInventory : clickerPlayerManager.getInventoryProfile(player).getCustomInventories().values()) {
                Inventory inventory = reflectionUtil.getOpenInventory(player);
                if (inventory != null && inventory == customInventory.getInventory()) {
                    player.closeInventory();
                    break;
                }
            }
        }

        ClickerStatsProfile clickerStatsProfile = clickerPlayerManager.getStatsProfile(uuid);
        clickerStatsProfile.setCookies(0);
        clickerStatsProfile.setPerClick(1);
        clickerStatsProfile.setClickerClicks(0);
        clickerStatsProfile.setBlockDesign(0);
        clickerStatsProfile.setParticleDesign(0);
        clickerStatsProfile.setMenuDesign(0);

        clickerStatsProfile.getShopPrices().clear();
        clickerStatsProfile.getShopItems().clear();
        databaseManager.loadDefaultShops(clickerStatsProfile);
        databaseManager.saveClickerStatsProfile(clickerStatsProfile);

        if (isPlayerOnline) {
            designManager.stopParticleEffects(uuid);
            for (CustomInventory customInventory : clickerPlayerManager.getInventoryProfile(player).getCustomInventories().values()) {
                customInventory.reload();
            }
            for (Location location : cookieClickerManager.getClickerLocations()) {
                Block block = location.getBlock();
                if (block.getType() != Material.PLAYER_HEAD) {
                    player.sendBlockChange(location, block.getBlockData());
                    continue;
                }
                plugin.getNmsBridge().sendSkullBlock(player, location, block.getBlockData(), ((Skull) block.getState()).getOwnerProfile());
            }
        }

        commandSender.sendMessage(languageConfig.getTranslatedLanguage(PluginLanguage.PREFIX) + "§7The player §6" + clickerStatsProfile.getName() + " §7was reset.");
        return true;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return permissionsConfig.hasPermission(commandSender, PluginPermission.ADMIN_PERMISSION);
    }

    @Override
    public List<String> getCustomTabCompletions(String[] args, int position) {
        if (position != 2) {
            return null;
        }
        ArrayList<String> playerNames = new ArrayList<>();
        for (ClickerStatsProfile clickerStatsProfile : plugin.getClickerPlayerManager().getClickerStatsProfiles().values()) {
            playerNames.add(clickerStatsProfile.getName());
        }
        return playerNames;
    }
}
