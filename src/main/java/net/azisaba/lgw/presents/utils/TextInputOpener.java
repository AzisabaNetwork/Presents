package net.azisaba.lgw.presents.utils;

import lombok.experimental.UtilityClass;
import me.rayzr522.jsonmessage.JSONMessage;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.BiFunction;

@UtilityClass
public class TextInputOpener {

    private static JavaPlugin plugin;

    public static void init(JavaPlugin plugin) {
        TextInputOpener.plugin = plugin;
    }

    public static void open(Player p, BiFunction<Player, String, TextInputResponse> callback) {
        open(p, "右の紙をクリックで確定", callback);
    }

    public static void open(Player p, String defaultText, BiFunction<Player, String, TextInputResponse> callback) {
        if (plugin == null) {
            throw new IllegalStateException("This class is not initialized. call #init(plugin)");
        }

        new AnvilGUI.Builder()
                .plugin(plugin)
                .onComplete((player, text) -> {
                    TextInputResponse res = callback.apply(player, text);
                    if (res.isAccept()) {
                        return AnvilGUI.Response.close();
                    }

                    JSONMessage.create(res.getErrorText()).subtitle(player);
                    JSONMessage.create("").title(0, 20, 10, player);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> open(player, defaultText, callback), 20L);
                    return AnvilGUI.Response.close();
                })
                .text(defaultText)
                .open(p);
    }
}
