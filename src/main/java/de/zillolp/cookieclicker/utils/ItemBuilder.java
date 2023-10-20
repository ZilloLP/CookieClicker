package de.zillolp.cookieclicker.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Material;
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
    private final Material material;
    private String displayName;
    private boolean enchanted;
    private List<String> lore;
    private ItemFlag itemFlag;
    private ItemFlag[] itemFlags;
    private String textureURL;

    public ItemBuilder(Material material) {
        this.material = material;
    }

    public ItemBuilder(Material material, String displayName) {
        this.material = material;
        this.displayName = displayName;
    }

    public ItemBuilder(Material material, String displayName, boolean enchanted) {
        this.material = material;
        this.displayName = displayName;
        this.enchanted = enchanted;
    }

    public ItemBuilder(Material material, String displayName, String[] lore) {
        this.material = material;
        this.displayName = displayName;
        this.lore = Arrays.asList(lore);
    }

    public ItemBuilder(Material material, String displayName, ItemFlag itemFlag) {
        this.material = material;
        this.displayName = displayName;
        this.itemFlag = itemFlag;
    }

    public ItemBuilder(Material material, String displayName, ItemFlag[] itemFlags) {
        this.material = material;
        this.displayName = displayName;
        this.itemFlags = itemFlags;
    }

    public ItemBuilder(Material material, String displayName, String textureURL) {
        this.material = material;
        this.displayName = displayName;
        this.textureURL = textureURL;
    }

    public ItemBuilder(Material material, String displayName, String textureURL, String[] lore) {
        this.material = material;
        this.displayName = displayName;
        this.textureURL = textureURL;
        this.lore = Arrays.asList(lore);
    }

    public ItemBuilder(Material material, String displayName, String[] lore, ItemFlag itemFlag) {
        this.material = material;
        this.displayName = displayName;
        this.lore = Arrays.asList(lore);
        this.itemFlag = itemFlag;
    }

    public ItemBuilder(Material material, String displayName, String[] lore, ItemFlag[] itemFlags) {
        this.material = material;
        this.displayName = displayName;
        this.lore = Arrays.asList(lore);
        this.itemFlags = itemFlags;
    }

    public ItemStack build() {
        ItemStack itemstack = new ItemStack(material, 1);
        ItemMeta itemMeta = itemstack.getItemMeta();
        if (itemMeta == null) {
            return itemstack;
        }
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
                GameProfile gameProfile = new GameProfile(UUID.randomUUID(), textureURL);
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
