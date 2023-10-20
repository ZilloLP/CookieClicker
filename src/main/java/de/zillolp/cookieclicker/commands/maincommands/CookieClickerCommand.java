package de.zillolp.cookieclicker.commands.maincommands;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.commands.subcommands.SubCommand;
import de.zillolp.cookieclicker.config.customconfigs.LanguageConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

public class CookieClickerCommand extends MainCommand {
    private final CookieClicker plugin;

    public CookieClickerCommand(CookieClicker plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        LanguageConfig languageConfig = plugin.getLanguageConfig();
        if (!(plugin.getDatabaseConnector().checkConnection())) {
            sender.sendMessage(languageConfig.getTranslatedLanguage("PREFIX") + "§cThe plugin isn't connected to the database!");
            return true;
        }
        if (args.length == 0) {
            PluginDescriptionFile pluginDescription = plugin.getDescription();
            sender.sendMessage("§6§lPlugin info:");
            sender.sendMessage("§7Plugin Name: §e" + pluginDescription.getName());
            sender.sendMessage("§7Plugin Version: §e" + pluginDescription.getVersion());
            sender.sendMessage("§7Author: §eZilloLP");
            sender.sendMessage("§7Discord: §ehttps://discord.gg/NBs27JK");
            return true;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(languageConfig.getLanguageWithPrefix("ONLY_PLAYER"));
            return true;
        }
        if (!(subCommands.containsKey(args[0].toLowerCase()))) {
            return false;
        }
        SubCommand subCommand = subCommands.get(args[0]).get(0);
        if (!(subCommand.hasPermission(sender))) {
            sender.sendMessage(languageConfig.getLanguageWithPrefix("NO_PERMISSION"));
            return true;
        }
        return subCommand.onCommand(plugin, sender, command, args);
    }
}
