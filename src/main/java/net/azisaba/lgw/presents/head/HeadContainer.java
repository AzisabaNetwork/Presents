package net.azisaba.lgw.presents.head;

import lombok.RequiredArgsConstructor;
import net.azisaba.lgw.presents.Presents;
import net.azisaba.lgw.presents.utils.ItemBuilder;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

@RequiredArgsConstructor
public class HeadContainer {

    private final Presents plugin;
    private final Random rand = new Random();

    private final HashMap<String, String> base64Map = new HashMap<>();

    public HeadContainer load() {
        File file = new File(plugin.getDataFolder(), "heads.yml");
        if (!file.exists()) {
            return this;
        }

        YamlConfiguration conf = YamlConfiguration.loadConfiguration(file);

        for (String key : conf.getConfigurationSection("").getKeys(false)) {
            base64Map.put(key, conf.getString(key));
        }
        plugin.getLogger().info("Loaded " + base64Map.size() + " heads.");

        return this;
    }

    public String getBase64(String key) {
        return base64Map.getOrDefault(key, null);
    }

    public String getRandomBase64() {
        if (base64Map.isEmpty()) {
            return null;
        }

        return new ArrayList<>(base64Map.values()).get(rand.nextInt(base64Map.size()));
    }

    public ItemStack create(String key) {
        if (!base64Map.containsKey(key)) {
            return null;
        }
        return ItemBuilder.createSkull(base64Map.get(key));
    }
}
