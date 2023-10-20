package de.zillolp.cookieclicker.commands.subcommands;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.statswall.StatsWallManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ReloadSubCommand extends SubCommand {

    public ReloadSubCommand(CookieClicker plugin, String mainCommand, String... subCommands) {
        super(plugin, mainCommand, subCommands);
    }

    public boolean onCommand(CookieClicker plugin, CommandSender sender, Command command, String[] args) {
        plugin.getStatsWallManager().stop();
        plugin.unloadPlayers();
        plugin.getConfigManager().reloadConfigs();
        plugin.setStatsWallManager(new StatsWallManager(plugin));
        plugin.getSoundManager().load();
        plugin.getInventoryManager().loadInventoryLanguage();
        plugin.loadPlayers();
        sender.sendMessage(plugin.getLanguageConfig().getTranslatedLanguage("PREFIX") + "§7The §bsettings §7have been reloaded.");
        return true;
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return plugin.getPermissionsConfig().hasPermission(sender, "ADMIN_PERMISSION");
    }
}
