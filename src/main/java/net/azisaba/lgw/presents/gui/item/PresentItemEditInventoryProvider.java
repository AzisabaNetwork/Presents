package net.azisaba.lgw.presents.gui.item;

import com.cryptomorin.xseries.XMaterial;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.azisaba.lgw.presents.Presents;
import net.azisaba.lgw.presents.utils.Chat;
import net.azisaba.lgw.presents.utils.ItemBuilder;
import net.azisaba.lgw.presents.utils.TextInputOpener;
import net.azisaba.lgw.presents.utils.TextInputResponse;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.function.BiConsumer;

@AllArgsConstructor
public class PresentItemEditInventoryProvider implements InventoryProvider {

    private final Presents plugin;
    private ItemStack item;
    private final BiConsumer<Player, ItemStack> callback;

    @Override
    public void init(Player player, InventoryContents contents) {
        String randomBase64 = plugin.getHeadContainer().getRandomBase64();
        if (randomBase64 == null) {
            randomBase64 = "";
        }
        ItemStack skullSelectItem = ItemBuilder.createHead(randomBase64, Chat.f("&cHeadを選択"));

        if (item != null) {
            contents.set(1, 4, ClickableItem.empty(item));
        } else {
            contents.set(1, 4, ClickableItem.empty(ItemBuilder.create(Material.BARRIER, Chat.f("&cアイテムがありません"), Chat.f("&a左下からアイテムを設定する"))));
        }
        contents.set(3, 2, ClickableItem.of(skullSelectItem, event -> openHeadSelectInventory((Player) event.getWhoClicked(), contents.inventory())));
        contents.set(3, 3, ClickableItem.empty(ItemBuilder.create(Material.CHEST, Chat.f("&aアイテムを選択"), Chat.f("&c下の自分のインベントリ内のアイテムを"), Chat.f("&c  クリックして選択できます"))));
        contents.set(3, 5, ClickableItem.of(ItemBuilder.create(Material.PAPER, Chat.f("&6アイテム名を設定")), event -> {
            String displayName = "Display Name を入力";
            if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                displayName = item.getItemMeta().getDisplayName().replace(ChatColor.COLOR_CHAR + "", "&");
            }
            TextInputOpener.open((Player) event.getWhoClicked(), displayName, (p, text) -> {
                ItemMeta metaForSetTitle = item.getItemMeta();
                metaForSetTitle.setDisplayName(ChatColor.translateAlternateColorCodes('&', text));
                item.setItemMeta(metaForSetTitle);

                contents.inventory().open(p);
                return TextInputResponse.accept();
            });
        }));
        contents.set(3, 6, ClickableItem.of(ItemBuilder.create(Material.BOOK_AND_QUILL, Chat.f("&3アイテム説明文を設定")), event -> {
            if (item == null) {
                item = ItemBuilder.create(Material.PAPER);
            }
            SmartInventory.builder()
                    .manager(plugin.getInventoryManager())
                    .parent(contents.inventory())
                    .provider(new LoreEditInventoryProvider(item))
                    .size(6, 9)
                    .title(Chat.f("&6Lore編集"))
                    .build().open((Player) event.getWhoClicked());
        }));

        contents.fillRow(5, ClickableItem.of(ItemBuilder.create(XMaterial.LIME_STAINED_GLASS_PANE, Chat.f("&a完了")), event -> callback.accept((Player) event.getWhoClicked(), item)));
    }

    @Override
    public void update(Player player, InventoryContents inventoryContents) {
        // pass
    }

    private void openHeadSelectInventory(Player p, SmartInventory parent) {
        SmartInventory.builder()
                .manager(plugin.getInventoryManager())
                .parent(parent)
                .provider(new HeadSelectInventoryProvider(plugin.getHeadContainer(), (player, base64) -> {
                    ItemStack newItem = ItemBuilder.createSkull(base64);
                    ItemBuilder.copyDisplayNameAndLore(item, newItem);
                    item = newItem;
                    parent.open(player);
                }))
                .size(6, 9)
                .title(Chat.f("&6Head選択"))
                .build().open(p);
    }
}
