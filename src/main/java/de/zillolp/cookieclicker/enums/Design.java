package de.zillolp.cookieclicker.enums;

import de.zillolp.cookieclicker.config.customconfigs.LanguageConfig;
import org.bukkit.Material;

public enum Design {
    BLACK_GLASS(Material.BLACK_STAINED_GLASS, Material.BLACK_STAINED_GLASS_PANE, 10),
    GRAY_GLASS(Material.GRAY_STAINED_GLASS, Material.GRAY_STAINED_GLASS_PANE, 11),
    LIGHT_GRAY_GLASS(Material.LIGHT_GRAY_STAINED_GLASS, Material.LIGHT_GRAY_STAINED_GLASS_PANE, 12),
    WHITE_GLASS(Material.WHITE_STAINED_GLASS, Material.WHITE_STAINED_GLASS_PANE, 13),
    BLUE_GLASS(Material.BLUE_STAINED_GLASS, Material.BLUE_STAINED_GLASS_PANE, 14),
    LIGHT_BLUE_GLASS(Material.LIGHT_BLUE_STAINED_GLASS, Material.LIGHT_BLUE_STAINED_GLASS_PANE, 15),
    CYAN_GLASS(Material.CYAN_STAINED_GLASS, Material.CYAN_STAINED_GLASS_PANE, 16),
    GREEN_GLASS(Material.GREEN_STAINED_GLASS, Material.GREEN_STAINED_GLASS_PANE, 18),
    LIME_GLASS(Material.LIME_STAINED_GLASS, Material.LIME_STAINED_GLASS_PANE, 19),
    BROWN_GLASS(Material.BROWN_STAINED_GLASS, Material.BROWN_STAINED_GLASS_PANE, 20),
    PURPLE_GLASS(Material.PURPLE_STAINED_GLASS, Material.PURPLE_STAINED_GLASS_PANE, 21),
    MAGENTA_GLASS(Material.MAGENTA_STAINED_GLASS, Material.MAGENTA_STAINED_GLASS_PANE, 22),
    PINK_GLASS(Material.PINK_STAINED_GLASS, Material.PINK_STAINED_GLASS_PANE, 23),
    YELLOW_GLASS(Material.YELLOW_STAINED_GLASS, Material.YELLOW_STAINED_GLASS_PANE, 24),
    ORANGE_GLASS(Material.ORANGE_STAINED_GLASS, Material.ORANGE_STAINED_GLASS_PANE, 25),
    RED_GLASS(Material.RED_STAINED_GLASS, Material.RED_STAINED_GLASS_PANE, 26);

    private final Material type;
    private final Material designType;
    private final int slot;
    private String name;
    private String selectedName;

    Design(Material type, Material designType, int slot) {
        this.type = type;
        this.designType = designType;
        this.slot = slot;
    }

    public void load(LanguageConfig languageConfig) {
        name = languageConfig.getTranslatedLanguage(name());
        selectedName = languageConfig.getTranslatedLanguage(name() + "_SELECTED");
    }

    public Material getType() {
        return type;
    }

    public Material getDesignType() {
        return designType;
    }

    public int getSlot() {
        return slot;
    }

    public String getName() {
        return name;
    }

    public String getSelectedName() {
        return selectedName;
    }
}
