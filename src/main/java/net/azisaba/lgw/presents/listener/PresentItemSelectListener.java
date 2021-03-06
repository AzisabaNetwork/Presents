package net.azisaba.lgw.presents.listener;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.InventoryManager;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import lombok.RequiredArgsConstructor;
import net.azisaba.lgw.presents.gui.item.PresentItemEditInventoryProvider;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class PresentItemSelectListener implements Listener {

    private final InventoryManager manager;

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) {
            return;
        }
        Player p = (Player) e.getWhoClicked();
        Inventory clickedInventory = e.getClickedInventory();
        ItemStack item = e.getCurrentItem();

        if (clickedInventory == null || p.getInventory() != clickedInventory) {
            return;
        }
        InventoryContents contents = manager.getContents(p).orElse(null);
        if (contents == null) {
            return;
        }
        SmartInventory smartInv = contents.inventory();

        if (smartInv.getProvider() == null || smartInv.getProvider().getClass() != PresentItemEditInventoryProvider.class) {
            return;
        }
        contents.set(1, 4, ClickableItem.empty(item));
        e.setCancelled(true);
    }
}
