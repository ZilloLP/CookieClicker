package de.zillolp.cookieclicker.commands.subcommands;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.customconfigs.LanguageConfig;
import de.zillolp.cookieclicker.enums.StatsWallType;
import de.zillolp.cookieclicker.profiles.PlayerProfile;
import de.zillolp.cookieclicker.statswall.statswalls.StatsWall;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StatsSubCommand extends SubCommand {
    public StatsSubCommand(CookieClicker plugin, String mainCommand, String... subCommands) {
        super(plugin, mainCommand, subCommands);
    }

    public boolean onCommand(CookieClicker plugin, CommandSender sender, Command command, String[] args) {
        if (args.length != 2 || (!(args[0].equalsIgnoreCase("stats")))) {
            return false;
        }
        LanguageConfig languageConfig = plugin.getLanguageConfig();
        String PREFIX = languageConfig.getTranslatedLanguage("PREFIX");
        HashMap<UUID, PlayerProfile> playerProfiles = plugin.getCookieClickerManager().getPlayerProfiles();
        if (!(plugin.getDatabaseManager().playerExists(args[1]))) {
            sender.sendMessage(PREFIX + "§cThis player does not exist!");
            return true;
        }
        PlayerProfile playerProfile = playerProfiles.get(Bukkit.getOfflinePlayer(args[1]).getUniqueId());
        String place = "?";
        String name = playerProfile.getName();
        StatsWall statsWall = StatsWallType.ALLTIME.getStatsWall();
        if (statsWall.isRunning()) {
            int index = 0;
            for (Map.Entry<String, Long> rankEntry : statsWall.getSortedData().entrySet()) {
                index++;
                if (rankEntry.getKey().equalsIgnoreCase(name)) {
                    break;
                }
            }
            place = String.valueOf(index);
        }
        for (String line : languageConfig.getStatsInfoLanguage("PLAYER_STATS_INFO", place, playerProfile)) {
            sender.sendMessage(line);
        }
        return true;
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return true;
    }
}
