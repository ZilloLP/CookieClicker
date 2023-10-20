package de.zillolp.cookieclicker.commands.subcommands;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.customconfigs.LocationConfig;
import de.zillolp.cookieclicker.enums.Setup;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class SetSubCommand extends SubCommand {
    public SetSubCommand(CookieClicker plugin, String mainCommand, String... subCommands) {
        super(plugin, mainCommand, subCommands);
    }

    public boolean onCommand(CookieClicker plugin, CommandSender sender, Command command, String[] args) {
        Player player = (Player) sender;
        String PREFIX = plugin.getLanguageConfig().getTranslatedLanguage("PREFIX");
        switch (args.length) {
            case 2:
                LocationConfig locationConfig = plugin.getLocationConfig();
                if (args[1].equalsIgnoreCase("resettimer")) {
                    Location resetTimerLocation = locationConfig.getLocation("ResetTimer");
                    if (resetTimerLocation != null) {
                        for (Player player1 : Bukkit.getOnlinePlayers()) {
                            plugin.getHologramManager().deleteHologram(player1, resetTimerLocation);
                        }
                    }
                    locationConfig.saveLocation("ResetTimer", player.getLocation().getBlock().getLocation());
                    sender.sendMessage(PREFIX + "§7You have set the §bresettimer§7.");
                    return true;
                }
                break;
            case 3:
                if (args[1].equalsIgnoreCase("clicker")) {
                    if (!(isNumeric(args[2]))) {
                        sender.sendMessage(PREFIX + "§cYour input §4" + args[2] + " §cis not a number!");
                        return true;
                    }
                    Setup setup = Setup.CLICKER;
                    setup.setSetupNumber(Integer.parseInt(args[2]));
                    plugin.getSetupManager().getSetups().put(player.getUniqueId(), setup);
                    sender.sendMessage(PREFIX + "§7Make right click on a §eblock§7.");
                    return true;
                }
                break;
            case 4:
                if (args[1].equalsIgnoreCase("statswall") && (args[2].equalsIgnoreCase("time") || args[2].equalsIgnoreCase("alltime"))) {
                    if (!(isNumeric(args[3]))) {
                        sender.sendMessage(PREFIX + "§cYour input §4" + args[3] + " §cis not a number!");
                        return true;
                    }
                    player.sendMessage(PREFIX + "§7Make right click on a §ehead§7.");
                    UUID uuid = player.getUniqueId();
                    HashMap<UUID, Setup> setups = plugin.getSetupManager().getSetups();
                    if (args[2].equalsIgnoreCase("time")) {
                        Setup setup = Setup.TIME_WALL;
                        setup.setSign(true);
                        setup.setSetupNumber(Integer.parseInt(args[3]));
                        setups.put(uuid, setup);
                        return true;
                    }
                    Setup setup = Setup.ALLTIME_WALL;
                    setup.setSign(true);
                    setup.setSetupNumber(Integer.parseInt(args[3]));
                    setups.put(uuid, setup);
                    return true;
                }
                break;
        }
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return plugin.getPermissionsConfig().hasPermission(sender, "ADMIN_PERMISSION");
    }
}
