package de.zillolp.cookieclicker.timer;

import de.zillolp.cookieclicker.CookieClicker;
import org.bukkit.Bukkit;

public abstract class CustomTimer implements Runnable {
    protected final CookieClicker plugin;
    private final int taskID;

    public CustomTimer(CookieClicker plugin, int seconds) {
        this.plugin = plugin;
        this.taskID = Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, this, 0, seconds * 20L);
    }

    @Override
    public void run() {
        tick();
    }

    protected abstract void tick();

    public void stop() {
        Bukkit.getScheduler().cancelTask(taskID);
    }
}
