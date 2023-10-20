package de.zillolp.cookieclicker.hologram.holograms;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class TextHologram extends Hologram {
    private final String[] lines;
    private final int linesSize;
    private final ArrayList<LineHologram> lineHolograms;

    public TextHologram(String[] lines) {
        this.lines = lines;
        this.linesSize = lines.length;
        this.lineHolograms = new ArrayList<>();
    }

    @Override
    public void spawn(Player player, Location location) {
        double holoHeight = 0.3 * linesSize + 0.75;
        for (int number = 0; number < linesSize; number++) {
            String line = this.lines[number];
            if (line.equalsIgnoreCase("%empty%") || line.equalsIgnoreCase("")) {
                continue;
            }
            LineHologram lineHologram = new LineHologram(line);
            lineHologram.spawn(player, location.clone().add(0, holoHeight - 0.3 * number, 0));
            lineHolograms.add(lineHologram);
        }
    }

    @Override
    public void destroy(Player player) {
        for (LineHologram lineHologram : lineHolograms) {
            lineHologram.destroy(player);
        }
    }
}
