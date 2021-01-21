package net.azisaba.lgw.presents.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.UUID;

@UtilityClass
public class ItemBuilder {

    private static final int versionNumber;

    static {
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
        versionNumber = Integer.parseInt(version.substring(3).substring(0, version.substring(3).indexOf("_")));
    }

    public static ItemStack create(Material material) {
        return new ItemStack(material);
    }

    public static ItemStack create(Material material, String displayName, String... lore) {
        return create(material, false, displayName, lore);
    }

    public static ItemStack create(Material material, boolean enchanted, String displayName, String... lore) {
        ItemStack item = new ItemStack(material);
        return apply(item, enchanted, displayName, lore);
    }

    public static ItemStack createHead(String base64, String displayName, String... lore) {
        ItemStack item = createSkull(base64);
        return apply(item, false, displayName, lore);
    }

    public static ItemStack createItemWithData(Material material, int data, String displayName, String... lore) {
        ItemStack item = getItemStackWithoutWarning(material, data);
        if (item == null) {
            return null;
        }
        return apply(item, false, displayName, lore);
    }

    public static ItemStack createSkull() {
        if (versionNumber >= 13) {
            return new ItemStack(Material.valueOf("PLAYER_HEAD"));
        }
        return createItemWithData(Material.valueOf("SKULL_ITEM"), 3, "");
    }

    public static ItemStack createSkull(String base64) {
        return setBase64(createSkull(), base64);
    }

    @SuppressWarnings("deprecation")
    private static ItemStack setBase64(ItemStack item, String base64) {
        return Bukkit.getUnsafe().modifyItemStack(item,
                "{SkullOwner:{Id:\"" + new UUID(base64.hashCode(), base64.hashCode()).toString()
                        + "\",Properties:{textures:[{Value:\"" + base64 + "\"}]}}}");
    }

    private static ItemStack getItemStackWithoutWarning(Material material, int data) {
        try {
            return ItemStack.class.getConstructor(Material.class, int.class, short.class).newInstance(material, 1,
                    (short) data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static ItemStack apply(ItemStack item, boolean enchanted, String displayName, String... lore) {
        ItemMeta meta = item.getItemMeta();

        if (displayName != null) {
            meta.setDisplayName(displayName);
        }
        if (lore != null && lore.length > 0) {
            meta.setLore(Arrays.asList(lore));
        }

        if (enchanted) {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
        }

        item.setItemMeta(meta);
        return item;
    }
}
