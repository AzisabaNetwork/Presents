package net.azisaba.lgw.presents.utils;

import lombok.experimental.UtilityClass;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.BiConsumer;

@UtilityClass
public class TextInputOpener {

    private static JavaPlugin plugin;

    public static void init(JavaPlugin plugin) {
        TextInputOpener.plugin = plugin;
    }

    public static void open(Player p, BiConsumer<Player, String> callback) {
        if (plugin == null) {
            throw new IllegalStateException("This class is not initialized. call #init(plugin)");
        }

        new AnvilGUI.Builder()
                .plugin(plugin)
                .onComplete((player, text) -> {
                    callback.accept(player, text);
                    return AnvilGUI.Response.close();
                })
                .preventClose()
                .text("右の紙をクリックで確定")
                .open(p);
    }
}
