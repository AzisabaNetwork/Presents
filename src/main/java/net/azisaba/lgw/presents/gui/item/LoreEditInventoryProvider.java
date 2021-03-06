package net.azisaba.lgw.presents.gui.item;

import com.cryptomorin.xseries.XMaterial;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.RequiredArgsConstructor;
import net.azisaba.lgw.presents.utils.Chat;
import net.azisaba.lgw.presents.utils.ItemBuilder;
import net.azisaba.lgw.presents.utils.TextInputOpener;
import net.azisaba.lgw.presents.utils.TextInputResponse;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class LoreEditInventoryProvider implements InventoryProvider {

    private final ItemStack item;

    private final ItemStack writeNew = ItemBuilder.create(Material.BOOK_AND_QUILL, Chat.f("&a行を追加する"));
    private final ItemStack updateLine = ItemBuilder.create(Material.BOOK_AND_QUILL, Chat.f("&a行を編集する"));
    private final ItemStack delete = ItemBuilder.create(Material.BARRIER, Chat.f("&c行を削除する"));

    @Override
    public void init(Player player, InventoryContents contents) {
        ItemStack blackGlass = ItemBuilder.create(XMaterial.BLACK_STAINED_GLASS_PANE, "");
        contents.fillColumn(0, ClickableItem.empty(blackGlass));
        contents.fillColumn(8, ClickableItem.empty(blackGlass));

        contents.set(1, 2, ClickableItem.empty(item));
        contents.set(4, 2, ClickableItem.of(ItemBuilder.create(XMaterial.LIME_TERRACOTTA, Chat.f("&a完了")),
                event -> contents.inventory().getParent().ifPresent(parent -> parent.open((Player) event.getWhoClicked()))));

        updateItems(contents);
    }

    @Override
    public void update(Player player, InventoryContents contents) {
        if (!contents.property("RequireUpdate", false)) {
            return;
        }

        contents.setProperty("RequireUpdate", false);
        contents.set(1, 2, ClickableItem.empty(item));

        updateItems(contents);
    }

    private void updateItems(InventoryContents contents) {
        List<String> lore = item.getItemMeta().getLore();
        for (int i = 0; i <= 5; i++) {
            String line = null;
            if (lore != null && lore.size() > i) {
                line = lore.get(i);
            }
            contents.set(i, 4, ClickableItem.empty(ItemBuilder.create(Material.SIGN, line)));

            // line があれば、入力画面にそれを表示、なければ&rを表示
            final String unformattedLine = line != null ? line.replace(ChatColor.COLOR_CHAR + "", "&") : "&r";
            final int index = i;
            Consumer<InventoryClickEvent> func = event -> TextInputOpener.open((Player) event.getWhoClicked(), unformattedLine, (p, text) -> {
                setLore(index, ChatColor.translateAlternateColorCodes('&', text));
                contents.setProperty("RequireUpdate", true);
                contents.inventory().open(p);
                return TextInputResponse.accept();
            });

            if (line != null) {
                contents.set(i, 5, ClickableItem.of(updateLine, func));

                final int deleteRowNum = i;
                contents.set(i, 6, ClickableItem.of(delete, event -> {
                    deleteLore(deleteRowNum);
                    contents.setProperty("RequireUpdate", true);
                }));
            } else {
                contents.set(i, 5, ClickableItem.of(writeNew, func));
                contents.set(i, 6, null);
            }
        }
    }


    private void setLore(int index, String text) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }

        if (index >= lore.size()) {
            for (int i = lore.size(); i < index; i++) {
                lore.add("");
            }
            lore.add(text);
        } else {
            lore.set(index, text);
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    private void deleteLore(int index) {
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasLore()) {
            return;
        }
        List<String> lore = meta.getLore();
        if (lore.size() <= index) {
            return;
        }
        lore.remove(index);
        meta.setLore(lore);
        item.setItemMeta(meta);
    }
}
