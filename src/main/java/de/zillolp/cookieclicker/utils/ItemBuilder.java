package de.zillolp.cookieclicker.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.zillolp.cookieclicker.xclasses.XMaterial;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ItemBuilder {
    private final XMaterial xMaterial;
    private String displayName;
    private boolean enchanted;
    private List<String> lore;
    private ItemFlag itemFlag;
    private ItemFlag[] itemFlags;
    private String textureURL;

    public ItemBuilder(XMaterial xMaterial) {
        this.xMaterial = xMaterial;
    }

    public ItemBuilder(XMaterial xMaterial, String displayName) {
        this.xMaterial = xMaterial;
        this.displayName = displayName;
    }

    public ItemBuilder(XMaterial xMaterial, String displayName, boolean enchanted) {
        this.xMaterial = xMaterial;
        this.displayName = displayName;
        this.enchanted = enchanted;
    }

    public ItemBuilder(XMaterial xMaterial, String displayName, String[] lore) {
        this.xMaterial = xMaterial;
        this.displayName = displayName;
        this.lore = Arrays.asList(lore);
    }

    public ItemBuilder(XMaterial xMaterial, String displayName, ItemFlag itemFlag) {
        this.xMaterial = xMaterial;
        this.displayName = displayName;
        this.itemFlag = itemFlag;
    }

    public ItemBuilder(XMaterial xMaterial, String displayName, ItemFlag[] itemFlags) {
        this.xMaterial = xMaterial;
        this.displayName = displayName;
        this.itemFlags = itemFlags;
    }

    public ItemBuilder(XMaterial xMaterial, String displayName, String textureURL) {
        this.xMaterial = xMaterial;
        this.displayName = displayName;
        this.textureURL = textureURL;
    }

    public ItemBuilder(XMaterial xMaterial, String displayName, String[] lore, ItemFlag itemFlag) {
        this.xMaterial = xMaterial;
        this.displayName = displayName;
        this.lore = Arrays.asList(lore);
        this.itemFlag = itemFlag;
    }

    public ItemBuilder(XMaterial xMaterial, String displayName, String[] lore, ItemFlag[] itemFlags) {
        this.xMaterial = xMaterial;
        this.displayName = displayName;
        this.lore = Arrays.asList(lore);
        this.itemFlags = itemFlags;
    }

    public ItemStack build() {
        ItemStack itemstack = new ItemStack(xMaterial.parseMaterial(), 1, xMaterial.getData());
        ItemMeta itemMeta = itemstack.getItemMeta();
        if (displayName != null) {
            itemMeta.setDisplayName(displayName);
        }
        if (enchanted) {
            itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        if (lore != null) {
            itemMeta.setLore(lore);
        }
        if (itemFlag != null) {
            itemMeta.addItemFlags(itemFlag);
        }
        if (itemFlags != null) {
            itemMeta.addItemFlags(itemFlags);
        }
        if (textureURL != null) {
            try {
                GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);
                if (textureURL.contains("http://textures.minecraft.net/texture/")) {
                    gameProfile.getProperties().put("textures", new Property("textures", Base64Coder.encodeString("{textures:{SKIN:{url:\"" + textureURL + "\"}}}")));
                } else {
                    gameProfile.getProperties().put("textures", new Property("textures", Base64Coder.encodeString("{textures:{SKIN:{url:\"" + "http://textures.minecraft.net/texture/" + textureURL + "\"}}}")));
                }
                Field profileField = itemMeta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(itemMeta, gameProfile);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        itemstack.setItemMeta(itemMeta);
        return itemstack;
    }
}
