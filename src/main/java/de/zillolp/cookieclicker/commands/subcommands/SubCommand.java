package de.zillolp.cookieclicker.commands.subcommands;

import de.zillolp.cookieclicker.CookieClicker;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class SubCommand {
    protected final CookieClicker plugin;
    private final String mainCommand;
    private final String[] subCommands;

    public SubCommand(CookieClicker plugin, String mainCommand, String... subCommands) {
        this.plugin = plugin;
        this.mainCommand = mainCommand;
        this.subCommands = subCommands;
    }

    public abstract boolean onCommand(CookieClicker cookieClicker, CommandSender commandSender, Command command, String[] args);

    public boolean isNumeric(String value) {
        try {
            int number = Integer.parseInt(value);
            return number > 0;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    public abstract boolean hasPermission(CommandSender sender);

    public String getMainCommand() {
        return mainCommand;
    }

    public List<String> getTabCommands(String subCommand, String command, int position) {
        if (subCommands.length < position) {
            return new ArrayList<>();
        }
        position--;
        List<String> tabCommands = Arrays.asList(subCommands[position].split(";"));
        if (position > 0) {
            position--;
        } else if (mainCommand.equalsIgnoreCase(command)) {
            return tabCommands;
        }
        for (String replacedCommand : subCommands[position].split(";")) {
            if (replacedCommand.equalsIgnoreCase(command) && subCommand.equalsIgnoreCase(mainCommand)) {
                return tabCommands;
            }
        }
        return new ArrayList<>();
    }
}
