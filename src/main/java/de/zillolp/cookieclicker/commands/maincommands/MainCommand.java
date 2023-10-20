package de.zillolp.cookieclicker.commands.maincommands;

import de.zillolp.cookieclicker.commands.subcommands.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class MainCommand implements TabExecutor {
    protected final HashMap<String, ArrayList<SubCommand>> subCommands;

    public MainCommand() {
        this.subCommands = new HashMap<>();
    }

    @Override
    public abstract boolean onCommand(CommandSender sender, Command command, String label, String[] args);

    public void registerSubCommand(SubCommand subCommand) {
        ArrayList<SubCommand> mainSubCommands;
        String mainCommand = subCommand.getMainCommand();
        if (subCommands.containsKey(mainCommand)) {
            mainSubCommands = subCommands.get(mainCommand);
        } else {
            mainSubCommands = new ArrayList<>();
            subCommands.put(mainCommand, mainSubCommands);
        }
        mainSubCommands.add(subCommand);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        int length = args.length;
        ArrayList<String> tabCommands = new ArrayList<>();
        for (Map.Entry<String, ArrayList<SubCommand>> mainSubCommands : subCommands.entrySet()) {
            for (SubCommand subCommand : mainSubCommands.getValue()) {
                if (!(subCommand.hasPermission(sender))) {
                    continue;
                }
                tabCommands.remove(subCommand.getMainCommand());
                if (length > 1) {
                    tabCommands.addAll(subCommand.getTabCommands(args[0], args[length - 2], length - 1));
                    continue;
                }
                tabCommands.add(subCommand.getMainCommand());
            }
        }
        return tabCommands;
    }
}
