package de.zillolp.cookieclicker.commands.subcommands;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.customconfigs.LocationConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RemoveSubCommand extends SubCommand {

    public RemoveSubCommand(CookieClicker plugin, String mainCommand, String... subCommands) {
        super(plugin, mainCommand, subCommands);
    }

    public boolean onCommand(CookieClicker plugin, CommandSender sender, Command command, String[] args) {
        if (args.length != 3 || (!(args[1].equalsIgnoreCase("clicker")))) {
            return false;
        }
        String PREFIX = plugin.getLanguageConfig().getTranslatedLanguage("PREFIX");
        if (!(isNumeric(args[2]))) {
            sender.sendMessage(PREFIX + "§cYour input §4" + args[2] + " §cis not a number!");
            return true;
        }
        int number = Integer.parseInt(args[2]);
        LocationConfig locationConfig = plugin.getLocationConfig();
        String locationName = "CookieClicker.Clicker-" + number;
        if (!(locationConfig.isLocation(locationName))) {
            sender.sendMessage(PREFIX + "§cThis CookieClicker does not exist!");
            return true;
        }
        Location location = locationConfig.getLocation(locationName);
        plugin.getCookieClickerManager().getClickerLocations().remove(location);
        locationConfig.removeLocation(locationName);
        for (Player player1 : Bukkit.getOnlinePlayers()) {
            plugin.getHologramManager().deleteHologram(player1, location);
        }
        sender.sendMessage(PREFIX + "§7You deleted the the §bCookieClicker-" + number + "§7.");
        return true;
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return plugin.getPermissionsConfig().hasPermission(sender, "ADMIN_PERMISSION");
    }
}
