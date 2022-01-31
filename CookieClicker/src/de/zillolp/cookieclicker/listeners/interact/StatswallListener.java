package de.zillolp.cookieclicker.listeners.interact;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import de.zillolp.cookieclicker.config.tools.LanguageTools;
import de.zillolp.cookieclicker.config.tools.LocationTools;
import de.zillolp.cookieclicker.main.Main;
import de.zillolp.cookieclicker.profiles.BoosterProfil;
import de.zillolp.cookieclicker.profiles.PlayerProfil;
import de.zillolp.cookieclicker.utils.DatenManager;
import de.zillolp.cookieclicker.xclasses.XSound;

public class StatswallListener implements Listener {
	private Main plugin = Main.plugin;
	private DatenManager datenmanager = plugin.datenmanager;
	private HashMap<Player, PlayerProfil> profiles = plugin.playerprofiles;
	private HashMap<Player, BoosterProfil> boosterprofiles = plugin.boosterprofiles;

	@SuppressWarnings("deprecation")
	@EventHandler
	public void on(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		Block block = e.getClickedBlock();
		if (block != null && block.getType() != Material.AIR) {
			if (block.getState() instanceof Skull) {
				Skull s = (Skull) block.getState();
				int place = checkLocation(block);
				if (place > 0 && s.getOwner() != null) {
					if (!(p.hasPermission(plugin.permissiontools.getAdminpermission()))) {
						e.setCancelled(true);
					}
					if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
						OfflinePlayer k = Bukkit.getOfflinePlayer(s.getOwner());
						String uuid = k.getUniqueId().toString();
						if (datenmanager.inPlayers(uuid)) {
							PlayerProfil profil = profiles.get(p);
							if (profil.getLastStats() + 250L < System.currentTimeMillis()) {
								Long cookies;
								Long perClick;
								Long clickerclicks;
								Long milk;
								if (profiles.containsKey(k) && boosterprofiles.containsKey(k)) {
									PlayerProfil k_profil = profiles.get(k);
									BoosterProfil k_booster = boosterprofiles.get(k);
									cookies = k_profil.getCookies();
									perClick = k_profil.getProclick();
									clickerclicks = k_profil.getClickerclicks();
									milk = k_booster.getMilk();
								} else {
									cookies = datenmanager.getCookies(uuid);
									perClick = datenmanager.getProclick(uuid);
									clickerclicks = datenmanager.getClickerclicks(uuid);
									milk = datenmanager.getMilk(uuid);
								}
								LanguageTools languagetools = plugin.languagetools;
								String name = k.getName();
								p.sendMessage(languagetools.getPLAYER_STATS_INFO(name, place, cookies, perClick,
										clickerclicks, milk));
								p.sendMessage(languagetools.getPLAYER_STATS_INFO_1(name, place, cookies, perClick,
										clickerclicks, milk));
								p.sendMessage(languagetools.getPLAYER_STATS_INFO_2(name, place, cookies, perClick,
										clickerclicks, milk));
								p.sendMessage(languagetools.getPLAYER_STATS_INFO_3(name, place, cookies, perClick,
										clickerclicks, milk));
								p.sendMessage(languagetools.getPLAYER_STATS_INFO_4(name, place, cookies, perClick,
										clickerclicks, milk));
								p.sendMessage(languagetools.getPLAYER_STATS_INFO_5(name, place, cookies, perClick,
										clickerclicks, milk));
								p.sendMessage(languagetools.getPLAYER_STATS_INFO_6(name, place, cookies, perClick,
										clickerclicks, milk));
								if (plugin.configtools.getSounds()) {
									p.playSound(p.getLocation(), XSound.BLOCK_LAVA_POP.parseSound(), 10, 10);
								}
								profil.setLastStats(System.currentTimeMillis());
							}
						}
					}
				}
			} else if (block.getState() instanceof Sign) {
				Block head = block.getLocation().add(0, 1, 0).getBlock();
				if (head != null && head.getState() instanceof Skull && checkLocation(head) > 0) {
					if (!(p.hasPermission(plugin.permissiontools.getAdminpermission()))) {
						e.setCancelled(true);
					}
				}
			}
		}
	}

	private int checkLocation(Block block) {
		for (int i = 1; i < 6; i++) {
			LocationTools lu = new LocationTools("Alltime." + i);
			Location stats_loc = lu.loadLocation();
			if (stats_loc != null) {
				Block stats_block = stats_loc.getBlock();
				if (stats_block.getState() instanceof Skull && stats_block.getX() == block.getX()
						&& stats_block.getY() == block.getY() && stats_block.getZ() == block.getZ()) {
					return i;
				} else {
					continue;
				}
			} else {
				continue;
			}
		}
		for (int i = 1; i < 4; i++) {
			LocationTools lu = new LocationTools("Time." + i);
			Location stats_loc = lu.loadLocation();
			if (stats_loc != null) {
				Block stats_block = stats_loc.getBlock();
				if (stats_block.getState() instanceof Skull && stats_block.getX() == block.getX()
						&& stats_block.getY() == block.getY() && stats_block.getZ() == block.getZ()) {
					return i;
				} else {
					continue;
				}
			} else {
				continue;
			}
		}
		return 0;
	}
}
