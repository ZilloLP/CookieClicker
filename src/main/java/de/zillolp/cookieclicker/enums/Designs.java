package de.zillolp.cookieclicker.enums;

import de.zillolp.cookieclicker.config.LanguageTools;
import de.zillolp.cookieclicker.xclasses.XMaterial;

public enum Designs {
    BLACK_DESIGN(XMaterial.BLACK_STAINED_GLASS, XMaterial.BLACK_STAINED_GLASS_PANE, LanguageTools.getLanguage("BLACK_GLASS"), LanguageTools.getLanguage("BLACK_GLASS_SELECTED")),
    GRAY_DESIGN(XMaterial.GRAY_STAINED_GLASS, XMaterial.GRAY_STAINED_GLASS_PANE, LanguageTools.getLanguage("GRAY_GLASS"), LanguageTools.getLanguage("GRAY_GLASS_SELECTED")),
    LIGHT_GRAY_DESIGN(XMaterial.LIGHT_GRAY_STAINED_GLASS, XMaterial.LIGHT_GRAY_STAINED_GLASS_PANE, LanguageTools.getLanguage("LIGHT_GRAY_GLASS"), LanguageTools.getLanguage("LIGHT_GRAY_GLASS_SELECTED")),
    WHITE_DESIGN(XMaterial.WHITE_STAINED_GLASS, XMaterial.WHITE_STAINED_GLASS_PANE, LanguageTools.getLanguage("WHITE_GLASS"), LanguageTools.getLanguage("WHITE_GLASS_SELECTED")),
    BLUE_DESIGN(XMaterial.BLUE_STAINED_GLASS, XMaterial.BLUE_STAINED_GLASS_PANE, LanguageTools.getLanguage("BLUE_GLASS"), LanguageTools.getLanguage("BLUE_GLASS_SELECTED")),
    LIGHT_BLUE_DESIGN(XMaterial.LIGHT_BLUE_STAINED_GLASS, XMaterial.LIGHT_BLUE_STAINED_GLASS_PANE, LanguageTools.getLanguage("LIGHT_BLUE_GLASS"), LanguageTools.getLanguage("LIGHT_BLUE_GLASS_SELECTED")),
    CYAN_DESIGN(XMaterial.CYAN_STAINED_GLASS, XMaterial.CYAN_STAINED_GLASS_PANE, LanguageTools.getLanguage("CYAN_GLASS"), LanguageTools.getLanguage("CYAN_GLASS_SELECTED")),
    GREEN_DESIGN(XMaterial.GREEN_STAINED_GLASS, XMaterial.GREEN_STAINED_GLASS_PANE, LanguageTools.getLanguage("GREEN_GLASS"), LanguageTools.getLanguage("GREEN_GLASS_SELECTED")),
    LIME_DESIGN(XMaterial.LIME_STAINED_GLASS, XMaterial.LIME_STAINED_GLASS_PANE, LanguageTools.getLanguage("LIME_GLASS"), LanguageTools.getLanguage("LIME_GLASS_SELECTED")),
    BROWN_DESIGN(XMaterial.BROWN_STAINED_GLASS, XMaterial.BROWN_STAINED_GLASS_PANE, LanguageTools.getLanguage("BROWN_GLASS"), LanguageTools.getLanguage("BROWN_GLASS_SELECTED")),
    PURPLE_DESIGN(XMaterial.PURPLE_STAINED_GLASS, XMaterial.PURPLE_STAINED_GLASS_PANE, LanguageTools.getLanguage("PURPLE_GLASS"), LanguageTools.getLanguage("PURPLE_GLASS_SELECTED")),
    MAGENTA_DESIGN(XMaterial.MAGENTA_STAINED_GLASS, XMaterial.MAGENTA_STAINED_GLASS_PANE, LanguageTools.getLanguage("MAGENTA_GLASS"), LanguageTools.getLanguage("MAGENTA_GLASS_SELECTED")),
    PINK_DESIGN(XMaterial.PINK_STAINED_GLASS, XMaterial.PINK_STAINED_GLASS_PANE, LanguageTools.getLanguage("PINK_GLASS"), LanguageTools.getLanguage("PINK_GLASS_SELECTED")),
    YELLOW_DESIGN(XMaterial.YELLOW_STAINED_GLASS, XMaterial.YELLOW_STAINED_GLASS_PANE, LanguageTools.getLanguage("YELLOW_GLASS"), LanguageTools.getLanguage("YELLOW_GLASS_SELECTED")),
    ORANGE_DESIGN(XMaterial.ORANGE_STAINED_GLASS, XMaterial.ORANGE_STAINED_GLASS_PANE, LanguageTools.getLanguage("ORANGE_GLASS"), LanguageTools.getLanguage("ORANGE_GLASS_SELECTED")),
    RED_DESIGN(XMaterial.RED_STAINED_GLASS, XMaterial.RED_STAINED_GLASS_PANE, LanguageTools.getLanguage("RED_GLASS"), LanguageTools.getLanguage("RED_GLASS_SELECTED"));

    private final XMaterial type;
    private final XMaterial designType;
    private final String name;
    private final String selectedName;

    Designs(XMaterial type, XMaterial designType, String name, String selectedName) {
        this.type = type;
        this.designType = designType;
        this.name = name;
        this.selectedName = selectedName;
    }

    public XMaterial getType() {
        return type;
    }

    public XMaterial getDesignType() {
        return designType;
    }

    public String getName() {
        return name;
    }

    public String getSelectedName() {
        return selectedName;
    }
}
