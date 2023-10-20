package de.zillolp.cookieclicker.commands.subcommands;

import de.zillolp.cookieclicker.CookieClicker;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class HelpSubCommand extends SubCommand {
    public HelpSubCommand(CookieClicker plugin, String mainCommand, String... subCommands) {
        super(plugin, mainCommand, subCommands);
    }

    public boolean onCommand(CookieClicker plugin, CommandSender sender, Command command, String[] args) {
        sender.sendMessage("§6§lThe CookieClicker commands:");
        sender.sendMessage("§e/cookieclicker help.");
        sender.sendMessage("§7Shows you a list of all commands.");
        sender.sendMessage("§e/cookieclicker stats <player>.");
        sender.sendMessage("§7Shows you the stats of a player.");
        if (!(plugin.getPermissionsConfig().hasPermission(sender, "ADMIN_PERMISSION"))) {
            return true;
        }
        sender.sendMessage("§e/cookieclicker reload.");
        sender.sendMessage("§7Reloads the settings.");
        sender.sendMessage("§e/cookieclicker list.");
        sender.sendMessage("§7Shows you a list of all CookieClickers.");
        sender.sendMessage("§e/cookieclicker set clicker <number>.");
        sender.sendMessage("§7Creates a CookieClicker.");
        sender.sendMessage("§e/cookieclicker remove clicker <number>.");
        sender.sendMessage("§7Removes a CookieClicker.");
        sender.sendMessage("§e/cookieclicker set statswall <alltime/time> <number>.");
        sender.sendMessage("§7Creates the statswall.");
        sender.sendMessage("§e/cookieclicker set resettimer.");
        sender.sendMessage("§7Sets the resettimer.");
        sender.sendMessage("§e/cookieclicker reset <player>.");
        sender.sendMessage("§7Resets the stats of a player.");
        return true;
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return true;
    }
}
