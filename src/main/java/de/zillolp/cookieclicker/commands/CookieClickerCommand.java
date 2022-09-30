package de.zillolp.cookieclicker.commands;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.ConfigTools;
import de.zillolp.cookieclicker.config.LanguageTools;
import de.zillolp.cookieclicker.config.LocationTools;
import de.zillolp.cookieclicker.config.PermissionTools;
import de.zillolp.cookieclicker.database.DatabaseManager;
import de.zillolp.cookieclicker.enums.Setups;
import de.zillolp.cookieclicker.profiles.PlayerProfile;
import de.zillolp.cookieclicker.runnables.AlltimeUpdater;
import de.zillolp.cookieclicker.runnables.ResetTimerUpdater;
import de.zillolp.cookieclicker.runnables.TimeUpdater;
import de.zillolp.cookieclicker.utils.StringUtil;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CookieClickerCommand implements CommandExecutor {
    private final CookieClicker cookieClicker = CookieClicker.cookieClicker;
    private final DatabaseManager databaseManager = cookieClicker.getDatabaseManager();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!(commandSender instanceof Player)) {
            Bukkit.getConsoleSender().sendMessage(LanguageTools.getONLY_PLAYER());
            return false;
        }
        Player player = (Player) commandSender;
        switch (args.length) {
            case 0:
                player.sendMessage("§6§lThe CookieClicker commands:");
                player.sendMessage("§e/cookieclicker info.");
                player.sendMessage("§7Shows you info about the plugin.");
                player.sendMessage("§e/cookieclicker stats <player>.");
                player.sendMessage("§7Shows you the stats of a player.");
                if (!(player.hasPermission(PermissionTools.getAdminPermission()))) {
                    break;
                }
                player.sendMessage("§e/cookieclicker list.");
                player.sendMessage("§7Shows you a list of all CookieClickers.");
                player.sendMessage("§e/cookieclicker set clicker <number>.");
                player.sendMessage("§7Creates a CookieClicker.");
                player.sendMessage("§e/cookieclicker delete clicker <number>.");
                player.sendMessage("§7Removes a CookieClicker.");
                player.sendMessage("§e/cookieclicker set statswall <type> <number>.");
                player.sendMessage("§7Creates the statswall.");
                player.sendMessage("§e/cookieclicker set resettimer.");
                player.sendMessage("§7Sets the resettimer.");
                player.sendMessage("§e/cookieclicker reload.");
                player.sendMessage("§7Reloads the settings.");
                player.sendMessage("§e/cookieclicker reset <player>.");
                player.sendMessage("§7Resets the stats of a player.");
                break;
            case 1:
                if (args[0].equalsIgnoreCase("info")) {
                    player.sendMessage("§6§lPlugin info:");
                    player.sendMessage("§7Plugin Name: §eCookieClicker");
                    player.sendMessage("§7Plugin Version: §e" + cookieClicker.getDescription().getVersion());
                    player.sendMessage("§7Author: §eZilloLP");
                    player.sendMessage("§7Discord: §ehttps://discord.gg/NBs27JK");
                    break;
                }
                if (!(player.hasPermission(PermissionTools.getAdminPermission()))) {
                    player.sendMessage(LanguageTools.getNO_PERMISSION());
                    break;
                }
                if (args[0].equalsIgnoreCase("list")) {
                    HashMap<String, Location> clickerLocations = cookieClicker.getClickerLocations();
                    player.sendMessage("§6All CookieClicker§7:");
                    for (Map.Entry<String, Location> clicker : clickerLocations.entrySet()) {
                        Location clickerLocation = clicker.getValue();
                        double x = clickerLocation.getX() + 0.5;
                        double y = clickerLocation.getY() + 1;
                        double z = clickerLocation.getZ() + 0.5;
                        TextComponent textComponent = new TextComponent("§7- §e" + clicker.getKey());
                        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + x + " " + y + " " + z));
                        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§7Click to teleport")));
                        player.spigot().sendMessage(textComponent);
                    }
                    player.sendMessage("§7» There are §6" + clickerLocations.size() + " §7Clicker.");
                } else if (args[0].equalsIgnoreCase("reload")) {
                    ConfigTools.load();
                    LanguageTools.load();
                    PermissionTools.load();
                    HashMap<UUID, PlayerProfile> playerProfiles = cookieClicker.getPlayerProfiles();
                    for (Player player1 : Bukkit.getOnlinePlayers()) {
                        ResetTimerUpdater.deleteTimer(player1.getUniqueId());
                        playerProfiles.get(player1.getUniqueId()).uploadProfile();
                        playerProfiles.put(player1.getUniqueId(), new PlayerProfile(player1));
                    }
                    cookieClicker.loadCookieClicker();
                    new AlltimeUpdater().reload();
                    new ResetTimerUpdater().reload();
                    new TimeUpdater().reload();
                    player.sendMessage(LanguageTools.getLanguage("PREFIX") + "§7The §bsettings §7have been reloaded.");
                } else {
                    player.sendMessage(LanguageTools.getUNKNOWN_COMMAND());
                }
                break;
            case 2:
                if (!(player.hasPermission(PermissionTools.getAdminPermission()) || args[0].equalsIgnoreCase("stats"))) {
                    player.sendMessage(LanguageTools.getNO_PERMISSION());
                    break;
                }
                if (args[0].equalsIgnoreCase("reset") || args[0].equalsIgnoreCase("stats")) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
                    String name = offlinePlayer.getName();
                    UUID uuid = offlinePlayer.getUniqueId();
                    if (!(databaseManager.playerExists(uuid, name))) {
                        player.sendMessage(LanguageTools.getLanguage("PREFIX") + "§cThis player does not exist!");
                        return false;
                    }
                    if (args[0].equalsIgnoreCase("reset")) {
                        databaseManager.resetProfile(uuid, name);
                        player.sendMessage(LanguageTools.getLanguage("PREFIX") + "§7The player §6" + name + " §7was reset.");
                    } else {
                        String cookies;
                        String perClick;
                        String clickerClicks;
                        String place = String.valueOf(databaseManager.getRank(uuid, "PER_CLICK"));
                        PlayerProfile playerProfile = cookieClicker.getPlayerProfiles().get(uuid);
                        if (playerProfile == null) {
                            cookies = String.valueOf(databaseManager.getValue(uuid, "COOKIES"));
                            perClick = String.valueOf(databaseManager.getValue(uuid, "PER_CLICK"));
                            clickerClicks = String.valueOf(databaseManager.getValue(uuid, "CLICKER_CLICKS"));
                        } else {
                            cookies = String.valueOf(playerProfile.getCookies());
                            perClick = String.valueOf(playerProfile.getPerClick());
                            clickerClicks = String.valueOf(playerProfile.getClickerClicks());
                        }
                        for (String line : LanguageTools.getSTATS_LINES("PLAYER_STATS_INFO", name, place, cookies, perClick, clickerClicks)) {
                            player.sendMessage(line);
                        }
                    }
                } else if (args[0].equalsIgnoreCase("set") && args[1].equalsIgnoreCase("resettimer")) {
                    LocationTools locationTools = new LocationTools("ResetTimer", player.getLocation().getBlock().getLocation());
                    locationTools.saveLocation();
                    player.sendMessage(LanguageTools.getLanguage("PREFIX") + "§7You have set the §bresettimer§7.");
                } else {
                    player.sendMessage(LanguageTools.getUNKNOWN_COMMAND());
                }
                break;
            case 3:
                if (!(player.hasPermission(PermissionTools.getAdminPermission()))) {
                    player.sendMessage(LanguageTools.getNO_PERMISSION());
                    break;
                }
                if (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("delete")) {
                    if (args[1].equalsIgnoreCase("clicker")) {
                        if (!(StringUtil.isNumber(args[2]))) {
                            player.sendMessage(LanguageTools.getLanguage("PREFIX") + "§cPlease enter a correct number!");
                            break;
                        }
                        int number = Integer.parseInt(args[2]);
                        HashMap<UUID, PlayerProfile> playerProfiles = cookieClicker.getPlayerProfiles();
                        if (args[0].equalsIgnoreCase("delete")) {
                            LocationTools locationTools = new LocationTools("CookieClicker.Clicker-" + number);
                            if (!(locationTools.isLocation())) {
                                player.sendMessage(LanguageTools.getLanguage("PREFIX") + "§cThis CookieClicker does not exist!");
                                break;
                            }
                            locationTools.resetLocation();
                            cookieClicker.getClickerLocations().remove("Clicker-" + number);
                            for (Player player1 : Bukkit.getOnlinePlayers()) {
                                PlayerProfile playerProfile = playerProfiles.get(player1.getUniqueId());
                                playerProfile.getPlayerManager().reloadHolograms();
                            }
                            player.sendMessage(LanguageTools.getLanguage("PREFIX") + "§7You deleted the the §bCookieClicker-" + number + "§7.");
                        } else {
                            PlayerProfile playerProfile = playerProfiles.get(player.getUniqueId());
                            playerProfile.setSetups(Setups.CLICKER);
                            playerProfile.setSetupNumber(number);
                            player.sendMessage(LanguageTools.getLanguage("PREFIX") + "§7Make right click on a §eblock§7.");
                        }
                    } else {
                        player.sendMessage(LanguageTools.getUNKNOWN_COMMAND());
                    }
                } else {
                    player.sendMessage(LanguageTools.getUNKNOWN_COMMAND());
                }
                break;
            case 4:
                if (!(player.hasPermission(PermissionTools.getAdminPermission()))) {
                    player.sendMessage(LanguageTools.getNO_PERMISSION());
                    break;
                }
                if (args[0].equalsIgnoreCase("set") && args[1].equalsIgnoreCase("statswall")) {
                    HashMap<UUID, PlayerProfile> playerProfiles = cookieClicker.getPlayerProfiles();
                    if (args[2].equalsIgnoreCase("time")) {
                        if (!(StringUtil.isNumber(args[3]))) {
                            player.sendMessage(LanguageTools.getLanguage("PREFIX") + "§cPlease enter a correct number!");
                            break;
                        }
                        int number = Integer.parseInt(args[3]);
                        PlayerProfile playerProfile = playerProfiles.get(player.getUniqueId());
                        playerProfile.setSetups(Setups.STATSWALL_TIME_HEAD);
                        playerProfile.setSetupNumber(number);
                        player.sendMessage(LanguageTools.getLanguage("PREFIX") + "§7Make right click on a §ehead§7.");
                    } else if (args[2].equalsIgnoreCase("alltime")) {
                        if (!(StringUtil.isNumber(args[3]))) {
                            player.sendMessage(LanguageTools.getLanguage("PREFIX") + "§cPlease enter a correct number!");
                            break;
                        }
                        int number = Integer.parseInt(args[3]);
                        PlayerProfile playerProfile = playerProfiles.get(player.getUniqueId());
                        playerProfile.setSetups(Setups.STATSWALL_ALLTIME_HEAD);
                        playerProfile.setSetupNumber(number);
                        player.sendMessage(LanguageTools.getLanguage("PREFIX") + "§7Make right click on a §ehead§7.");
                    } else {
                        player.sendMessage(LanguageTools.getLanguage("PREFIX") + "§7Please use either §ealltime §7or §etime §7as type.");
                    }
                } else {
                    player.sendMessage(LanguageTools.getUNKNOWN_COMMAND());
                }
                break;
            default:
                player.sendMessage(LanguageTools.getUNKNOWN_COMMAND());
                break;
        }
        return false;
    }
}