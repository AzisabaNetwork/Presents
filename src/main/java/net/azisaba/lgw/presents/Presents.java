package net.azisaba.lgw.presents;

import java.io.File;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import net.azisaba.lgw.presents.command.PresentCommand;
import net.azisaba.lgw.presents.command.RetryPresentCommand;
import net.azisaba.lgw.presents.listener.PlayerJoinListener;
import net.azisaba.lgw.presents.present.Present;
import net.azisaba.lgw.presents.present.PresentContainer;
import net.azisaba.lgw.presents.present.PresentFactory;
import net.azisaba.lgw.presents.present.PresentLoader;
import net.azisaba.lgw.presents.utils.Chat;

import lombok.Getter;

/**
 * メインクラス
 *
 * @author siloneco
 *
 */
@Getter
public class Presents extends JavaPlugin {

    // ロードされたプレゼントを格納するインスタンス
    private PresentContainer container;
    // プレゼントを作成するインスタンス
    private PresentFactory factory;
    // プレゼントをロードするインスタンス
    private PresentLoader loader;

    @Override
    public void onEnable() {

        File folder = new File(getDataFolder(), "Presents");

        container = new PresentContainer(this, folder);
        factory = new PresentFactory(container);
        loader = new PresentLoader(folder, container, factory);
        loader.loadAllPresents();

        Bukkit.getPluginCommand("present").setExecutor(new PresentCommand(container, factory));
        Bukkit.getPluginCommand("retrypresent").setExecutor(new RetryPresentCommand(container));

        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this, container), this);

        Bukkit.getLogger().info(getName() + " enabled.");

        Bukkit.getOnlinePlayers().forEach(p -> {
            List<Present> presents = container.getMatchPresents(p);

            if ( presents.size() > 0 ) {
                p.sendMessage(Chat.f("&e受け取り忘れているプレゼントがあります！&a/retrypresent&eで受け取りを完了してください！"));
            }
        });
    }

    @Override
    public void onDisable() {
        container.getAllPresents().forEach(loader::savePresent);
        Bukkit.getLogger().info(getName() + " disabled.");
    }
}
