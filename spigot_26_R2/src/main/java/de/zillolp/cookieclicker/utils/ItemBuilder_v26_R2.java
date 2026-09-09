package de.zillolp.cookieclicker.utils;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.interfaces.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ItemBuilder_v26_R2 implements ItemBuilder {
    private final Logger logger;
    private final ReflectionUtil reflectionUtil;

    public ItemBuilder_v26_R2(CookieClicker plugin) {
        logger = plugin.getLogger();
        reflectionUtil = plugin.getReflectionUtil();
    }

    @Override
    public ItemStack build(Material material, String displayName, int amount) {
        return build(material, displayName, amount, null, false, false, null, null);
    }

    @Override
    public ItemStack build(Material material, String displayName, int amount, boolean hideItemInfos) {
        return build(material, displayName, amount, null, false, hideItemInfos, null, null);
    }

    @Override
    public ItemStack build(Material material, String displayName, int amount, String[] lore, boolean hideItemInfos) {
        return build(material, displayName, amount, lore, false, hideItemInfos, null, null);
    }

    @Override
    public ItemStack build(Material material, String displayName, int amount, boolean hideItemInfos, String textureURL) {
        return build(material, displayName, amount, null, false, hideItemInfos, textureURL, null);
    }

    @Override
    public ItemStack build(Material material, String displayName, int amount, String textureURL) {
        return build(material, displayName, amount, null, false, false, textureURL, null);
    }

    public ItemStack build(Material material, String displayName, int amount, String[] lore, boolean hasEnchantment, boolean hideItemInfos, String textureURL, PlayerProfile playerProfile) {
        if (material == null) {
            material = Material.BARRIER;
        }
        ItemStack itemStack = new ItemStack(material, amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return itemStack;
        }
        if (displayName != null) {
            itemMeta.setDisplayName(displayName);
        }
        if (lore != null) {
            itemMeta.setLore(Arrays.asList(lore));
        }
        if (hasEnchantment) {
            itemMeta.addEnchant(Enchantment.VANISHING_CURSE, 1, false);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        if (hideItemInfos) {
            if (!(reflectionUtil.isSpigot())) {
                NamespacedKey namespacedKey = new NamespacedKey("cookieclicker", "hide_attribute");
                AttributeModifier attributeModifier = new AttributeModifier(namespacedKey, 0.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND);
                itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE, attributeModifier);
                itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            } else {
                itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_JUKEBOX_PLAYABLE, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            }
        }
        if (material == Material.PLAYER_HEAD && itemMeta instanceof SkullMeta) {
            if (textureURL != null) {
                String url;
                if (textureURL.contains("http://textures.minecraft.net/texture/")) {
                    url = textureURL;
                } else {
                    url = "http://textures.minecraft.net/texture/" + textureURL;
                }
                SkullMeta skullMeta = (SkullMeta) itemMeta;
                reflectionUtil.getPlayerProfile(url, "").thenAccept(textureProfile -> {
                    skullMeta.setOwnerProfile(textureProfile);
                    itemStack.setItemMeta(skullMeta);
                }).exceptionally(throwable -> {
                    logger.log(Level.SEVERE, "Failed to load PlayerProfile for URL: " + url, throwable);
                    return null;
                });
            } else if (playerProfile != null) {
                SkullMeta skullMeta = (SkullMeta) itemMeta;
                skullMeta.setOwnerProfile(playerProfile);
                itemStack.setItemMeta(skullMeta);
            }
            return itemStack;
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}

