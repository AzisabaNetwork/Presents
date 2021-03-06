package net.azisaba.lgw.presents.gui;

import fr.minuskube.inv.SmartInventory;
import lombok.RequiredArgsConstructor;
import net.azisaba.lgw.presents.Presents;
import net.azisaba.lgw.presents.gui.date.DateSelectInventoryProvider;
import net.azisaba.lgw.presents.gui.item.HeadSelectInventoryProvider;
import net.azisaba.lgw.presents.gui.item.PresentItemEditInventoryProvider;
import net.azisaba.lgw.presents.utils.Chat;
import net.azisaba.lgw.presents.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Calendar;
import java.util.function.BiConsumer;

@RequiredArgsConstructor
public class GUIOpenSupporter {

    private final Presents plugin;

    public void openDateInputGUI(Player p, SmartInventory parent, Calendar calendar) {
        if (calendar == null) {
            calendar = Calendar.getInstance();
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        SmartInventory.builder()
                .manager(plugin.getInventoryManager())
                .parent(parent)
                .provider(new DateSelectInventoryProvider(year, month, calendar))
                .size(6, 9)
                .title(Chat.f("&2{0}年{1}月", year + "", month + 1))
                .build().open(p);
    }

    public void openItemEditGUI(Player p, SmartInventory parent, ItemStack item, BiConsumer<Player, ItemStack> callback) {
        if (item == null) {
            String randomBase64 = plugin.getHeadContainer().getRandomBase64();
            if (randomBase64 != null) {
                item = plugin.getHeadContainer().create(randomBase64);
            } else {
                item = ItemBuilder.create(Material.CHEST);
            }
        }

        SmartInventory.builder()
                .manager(plugin.getInventoryManager())
                .parent(parent)
                .provider(new PresentItemEditInventoryProvider(plugin, item, callback))
                .size(6, 9)
                .title(Chat.f("&6アイテム編集"))
                .build().open(p);
    }
}
