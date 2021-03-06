package net.azisaba.lgw.presents;

import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import fr.minuskube.inv.InventoryManager;
import lombok.Getter;
import net.azisaba.lgw.presents.command.PresentCommand;
import net.azisaba.lgw.presents.command.RetryPresentCommand;
import net.azisaba.lgw.presents.gui.GUIOpenSupporter;
import net.azisaba.lgw.presents.head.HeadContainer;
import net.azisaba.lgw.presents.listener.PlayerJoinListener;
import net.azisaba.lgw.presents.listener.PresentItemSelectListener;
import net.azisaba.lgw.presents.listener.TestListener;
import net.azisaba.lgw.presents.present.PresentContainer;
import net.azisaba.lgw.presents.present.PresentFactory;
import net.azisaba.lgw.presents.present.PresentLoader;
import net.azisaba.lgw.presents.utils.Chat;
import net.azisaba.lgw.presents.utils.TextInputOpener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

/**
 * メインクラス
 *
 * @author siloneco
 */
@Getter
public class Presents extends JavaPlugin {

    private static TaskChainFactory taskChainFactory;
    private PresentContainer container;
    private PresentFactory factory;
    private PresentLoader loader;
    private HeadContainer headContainer;
    private GUIOpenSupporter guiOpenSupporter;

    private InventoryManager inventoryManager;

    public static <T> TaskChain<T> newChain() {
        return taskChainFactory.newChain();
    }

    public static <T> TaskChain<T> newSharedChain(String name) {
        return taskChainFactory.newSharedChain(name);
    }

    @Override
    public void onEnable() {
        taskChainFactory = BukkitTaskChainFactory.create(this);
        inventoryManager = new InventoryManager(this);
        guiOpenSupporter = new GUIOpenSupporter(this);
        inventoryManager.init();
        TextInputOpener.init(this);

        headContainer = new HeadContainer(this).load();

        File folder = new File(getDataFolder(), "Presents");

        container = new PresentContainer(this, folder);
        factory = new PresentFactory(container);
        loader = new PresentLoader(folder, container, factory);
        loader.loadAllPresents();

        Bukkit.getPluginCommand("present").setExecutor(new PresentCommand(container, factory));
        Bukkit.getPluginCommand("retrypresent").setExecutor(new RetryPresentCommand(container));

        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(container), this);
        Bukkit.getPluginManager().registerEvents(new TestListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PresentItemSelectListener(inventoryManager), this);

        Bukkit.getLogger().info(getName() + " enabled.");

        Bukkit.getOnlinePlayers().forEach(p -> newSharedChain("SearchPresents")
                .asyncFirst(() -> container.getMatchPresents(p))
                .abortIf(List::isEmpty)
                .sync(() -> p.sendMessage(Chat.f("&e受け取り忘れているプレゼントがあります！&a/retrypresent&eで受け取りを完了してください！")))
                .execute());
    }

    @Override
    public void onDisable() {
        container.getAllPresents().forEach(loader::savePresent);
        Bukkit.getLogger().info(getName() + " disabled.");
    }
}
