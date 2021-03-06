package net.azisaba.lgw.presents.gui.item;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.SlotIterator;
import lombok.RequiredArgsConstructor;
import net.azisaba.lgw.presents.head.HeadContainer;
import net.azisaba.lgw.presents.utils.Chat;
import net.azisaba.lgw.presents.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

@RequiredArgsConstructor
public class HeadSelectInventoryProvider implements InventoryProvider {

    private final HeadContainer container;
    private final BiConsumer<Player, String> callback;

    @Override
    public void init(Player player, InventoryContents contents) {
        List<String> keyList = new ArrayList<>(container.getAllKeys());
        ClickableItem[] items = new ClickableItem[keyList.size()];
        for (int i = 0, size = keyList.size(); i < size; i++) {
            String key = keyList.get(i);
            String base64 = container.getBase64(key);
            ItemStack head = getHead(base64, Chat.f("&6{0}", key), Chat.f("&cこのHeadを選択"));
            items[i] = ClickableItem.of(head, event -> callback.accept((Player) event.getWhoClicked(), base64));
        }

        contents.pagination().setItems(items);
        contents.pagination().setItemsPerPage(45);
        contents.pagination().addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 0, 0));

        if (!contents.pagination().isFirst()) {
            contents.set(5, 3, ClickableItem.of(ItemBuilder.create(Material.ARROW, Chat.f("&6前ページ")),
                    e -> contents.inventory().open(player, contents.pagination().previous().getPage())));
        }
        if (!contents.pagination().isLast()) {
            contents.set(5, 5, ClickableItem.of(ItemBuilder.create(Material.ARROW, Chat.f("&6次ページ")),
                    e -> contents.inventory().open(player, contents.pagination().next().getPage())));
        }
    }

    @Override
    public void update(Player player, InventoryContents contents) {
        // pass
    }

    private ItemStack getHead(String base64, String displayName, String... lore) {
        ItemStack item = ItemBuilder.createSkull(base64);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        if (lore.length > 0) {
            meta.setLore(Arrays.asList(lore));
        }
        item.setItemMeta(meta);
        return item;
    }
}
