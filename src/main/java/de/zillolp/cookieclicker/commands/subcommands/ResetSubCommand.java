package de.zillolp.cookieclicker.commands.subcommands;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.enums.StatsWallType;
import de.zillolp.cookieclicker.profiles.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.UUID;

public class ResetSubCommand extends SubCommand {

    public ResetSubCommand(CookieClicker plugin, String mainCommand, String... subCommands) {
        super(plugin, mainCommand, subCommands);
    }

    public boolean onCommand(CookieClicker plugin, CommandSender sender, Command command, String[] args) {
        if (args.length != 2 || (!(args[0].equalsIgnoreCase("reset")))) {
            return false;
        }
        UUID uuid = Bukkit.getOfflinePlayer(args[1]).getUniqueId();
        String PREFIX = plugin.getLanguageConfig().getTranslatedLanguage("PREFIX");
        HashMap<UUID, PlayerProfile> playerProfiles = plugin.getCookieClickerManager().getPlayerProfiles();
        if (!(playerProfiles.containsKey(uuid))) {
            sender.sendMessage(PREFIX + "§cThis player does not exist!");
            return true;
        }
        PlayerProfile playerProfile = playerProfiles.get(uuid);
        playerProfile.setToDefault();
        plugin.getDatabaseManager().uploadProfile(playerProfile, true);
        StatsWallType.TIME.getStatsWall().getCachedData().remove(playerProfile.getName());
        sender.sendMessage(PREFIX + "§7The player §6" + playerProfile.getName() + " §7was reset.");
        return true;
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return plugin.getPermissionsConfig().hasPermission(sender, "ADMIN_PERMISSION");
    }
}
