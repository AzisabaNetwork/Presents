package net.azisaba.lgw.presents;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import net.azisaba.lgw.presents.command.PresentCommand;
import net.azisaba.lgw.presents.listener.PlayerJoinListener;
import net.azisaba.lgw.presents.present.PresentContainer;
import net.azisaba.lgw.presents.present.PresentFactory;
import net.azisaba.lgw.presents.present.PresentLoader;

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

        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this, container), this);

        Bukkit.getLogger().info(getName() + " enabled.");
    }

    @Override
    public void onDisable() {
        container.getAllPresents().forEach(loader::savePresent);
        Bukkit.getLogger().info(getName() + " disabled.");
    }
}
