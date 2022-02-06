package de.zillolp.cookieclicker.profiles;

import java.util.HashMap;

import org.bukkit.entity.Player;

import de.zillolp.cookieclicker.main.Main;
import de.zillolp.cookieclicker.utils.DatenManager;

public class PlayerProfil {
	private Main plugin = Main.plugin;
	private DatenManager datenmanager = plugin.datenmanager;
	private HashMap<Player, InventoryProfil> invprofiles = plugin.invprofiles;
	private HashMap<Player, ShopProfil> shops = plugin.shopprofiles;
	private HashMap<Player, BoosterProfil> booster = plugin.boosterprofiles;
	private final Player player;
	private final String uuid;
	private int clickerNummer;
	private int alltimeNummer;
	private int timeNummer;
	private Long cookies;
	private Long perClick;
	private Long clickerClicks;
	private Long design;
	private Long lastMove;
	private int CPS;
	private boolean OverCPS;
	private Long lastStats;

	public PlayerProfil(Player player) {
		this.player = player;
		this.uuid = player.getUniqueId().toString();
		this.clickerNummer = 0;
		this.alltimeNummer = 0;
		this.timeNummer = 0;
		this.cookies = datenmanager.getCookies(uuid);
		this.perClick = datenmanager.getProclick(uuid);
		this.clickerClicks = datenmanager.getClickerclicks(uuid);
		this.design = datenmanager.getDesign(uuid);
		this.lastMove = System.currentTimeMillis();
		this.CPS = 0;
		this.OverCPS = false;
		this.lastStats = System.currentTimeMillis();
		if (!(invprofiles.containsKey(player))) {
			invprofiles.put(player, new InventoryProfil());
		}
		if (!(shops.containsKey(player))) {
			shops.put(player, new ShopProfil(uuid));
		}
		if (!(booster.containsKey(player))) {
			booster.put(player, new BoosterProfil(uuid));
		}
	}

	public int getClickerNummer() {
		return clickerNummer;
	}

	public int getAlltimeNummer() {
		return alltimeNummer;
	}

	public int getTimeNummer() {
		return timeNummer;
	}

	public Long getCookies() {
		return cookies;
	}

	public Long getProclick() {
		return perClick;
	}

	public Long getClickerclicks() {
		return clickerClicks;
	}

	public Long getDesign() {
		return design;
	}

	public Long getLastMove() {
		return lastMove;
	}

	public int getCPS() {
		return CPS;
	}

	public boolean getOver_CPS() {
		return OverCPS;
	}

	public Long getLastStats() {
		return lastStats;
	}

	public void setClickerNummer(int clickerNummer) {
		clickerNummer = clickerNummer;
	}

	public void setAlltimeNummer(int alltimeNummer) {
		alltimeNummer = alltimeNummer;
	}

	public void setTimeNummer(int timeNummer) {
		timeNummer = timeNummer;
	}

	public void setCookies(Long cookies) {
		cookies = cookies;
	}

	public void setProclick(Long perClick) {
		perClick = perClick;
	}

	public void setClickerclicks(Long clickerclicks) {
		clickerClicks = clickerclicks;
	}

	public void setDesign(Long design) {
		design = design;
	}

	public void setLastMove(Long lastMove) {
		lastMove = lastMove;
	}

	public void setCPS(int clicks) {
		CPS = clicks;
	}

	public void setOver_CPS(boolean overCPS) {
		OverCPS = overCPS;
	}

	public void setLastStats(Long lastStats) {
		lastStats = lastStats;
	}

	public void addCookies(Long cookies) {
		cookies += cookies;
	}

	public void addProclick(Long proclick) {
		perClick += proclick;
	}

	public void addClickerclicks(Long clicks) {
		clickerClicks += clicks;
	}

	public void addCPS(int clicks) {
		CPS += clicks;
	}

	public void removeCookies(Long cookies) {
		cookies -= cookies;
	}

	public void removePerclick(Long proclick) {
		perClick -= proclick;
	}

	public void removeCPS(int clicks) {
		if (CPS - clicks >= 0) {
			CPS = CPS - clicks;
		} else {
			CPS = 0;
		}
	}

	public void reloadProfil() {
		if (invprofiles.containsKey(player)) {
			invprofiles.get(player).reloadInventorys();
		}
	}

	public void UploadStats() {
		if (booster.containsKey(player)) {
			booster.get(player).UploadMySQL();
		}
		datenmanager.setCookies(uuid, getCookies());
		datenmanager.setProclick(uuid, getProclick());
		datenmanager.setClickerclicks(uuid, getClickerclicks());
		datenmanager.setDesign(uuid, getDesign());
		if (invprofiles.containsKey(player)) {
			invprofiles.remove(player);
		}
		if (shops.containsKey(player)) {
			shops.get(player).UploadMySQL();
			shops.remove(player);
		}
		if (booster.containsKey(player)) {
			booster.remove(player);
		}
	}
}
