package net.azisaba.lgw.presents.gui;

import fr.minuskube.inv.SmartInventory;
import lombok.RequiredArgsConstructor;
import net.azisaba.lgw.presents.Presents;
import net.azisaba.lgw.presents.gui.date.DateSelectInventoryProvider;
import net.azisaba.lgw.presents.utils.Chat;
import org.bukkit.entity.Player;

import java.util.Calendar;

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
}
