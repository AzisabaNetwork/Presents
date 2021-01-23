package net.azisaba.lgw.presents.gui.date;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.RequiredArgsConstructor;
import net.azisaba.lgw.presents.utils.Chat;
import net.azisaba.lgw.presents.utils.ItemBuilder;
import net.azisaba.lgw.presents.utils.TextInputOpener;
import net.azisaba.lgw.presents.utils.TextInputResponse;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

@RequiredArgsConstructor
public class TimeInputInventoryProvider implements InventoryProvider {

    private final Calendar reflectCalendar;
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd");
    private final SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
    private final SimpleDateFormat allFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    private Calendar cal = null;

    @Override
    public void init(Player player, InventoryContents contents) {
        if (cal == null) {
            cal = (Calendar) reflectCalendar.clone();
        }
        update(player, contents);

        contents.set(1, 1, ClickableItem.of(ItemBuilder.createItemWithData(Material.STAINED_GLASS_PANE, 11, Chat.f("&c1時間減")), event -> add(Calendar.HOUR, -1)));
        contents.set(1, 2, ClickableItem.of(ItemBuilder.createItemWithData(Material.STAINED_GLASS_PANE, 9, Chat.f("&c1分減")), event -> add(Calendar.MINUTE, -1)));
        contents.set(1, 3, ClickableItem.of(ItemBuilder.createItemWithData(Material.STAINED_GLASS_PANE, 3, Chat.f("&c1秒減")), event -> add(Calendar.SECOND, -1)));

        contents.set(1, 5, ClickableItem.of(ItemBuilder.createItemWithData(Material.STAINED_GLASS_PANE, 14, Chat.f("&a1時間増")), event -> add(Calendar.HOUR, 1)));
        contents.set(1, 6, ClickableItem.of(ItemBuilder.createItemWithData(Material.STAINED_GLASS_PANE, 1, Chat.f("&a1分増")), event -> add(Calendar.MINUTE, 1)));
        contents.set(1, 7, ClickableItem.of(ItemBuilder.createItemWithData(Material.STAINED_GLASS_PANE, 4, Chat.f("&a1秒増")), event -> add(Calendar.SECOND, 1)));

        contents.set(2, 4, ClickableItem.of(ItemBuilder.createItemWithData(Material.STAINED_CLAY, 5, Chat.f("&a完了")),
                event -> {
                    Player p = (Player) event.getWhoClicked();
                    for (int field : Arrays.asList(Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND)) {
                        reflectCalendar.set(field, cal.get(field));
                    }
                    Bukkit.broadcastMessage(allFormatter.format(reflectCalendar.getTime()));
                    contents.inventory().close(p);
                    contents.inventory().getParent().flatMap(SmartInventory::getParent).ifPresent(inv2 -> inv2.open(p));
                }));

        contents.set(2, 0, ClickableItem.of(ItemBuilder.create(Material.ARROW, Chat.f("&6戻る")),
                event -> {
                    Player p = (Player) event.getWhoClicked();
                    contents.inventory().close(p);
                    contents.inventory().getParent().ifPresent(inv -> inv.open(p));
                }));

        contents.set(contents.inventory().getRows() - 1, contents.inventory().getColumns() - 1,
                ClickableItem.of(ItemBuilder.create(Material.BOOK_AND_QUILL, Chat.f("&c文字入力で設定")),
                        event -> TextInputOpener.open((Player) event.getWhoClicked(), "時:分:秒", (player1, text) -> {
                            try {
                                Date date = timeFormatter.parse(text);
                                cal.setTimeInMillis(date.getTime());
                                contents.inventory().open(player1);
                                return TextInputResponse.accept();
                            } catch (ParseException e) {
                                return TextInputResponse.error(Chat.f("&c無効な形式: &e時:分:秒 &cで入力してください"));
                            }
                        })));
    }

    @Override
    public void update(Player player, InventoryContents contents) {
        contents.set(0, 2, ClickableItem.empty(getCalendarFieldItem(Calendar.HOUR_OF_DAY)));
        contents.set(0, 4, ClickableItem.empty(getCalendarFieldItem(Calendar.MINUTE)));
        contents.set(0, 6, ClickableItem.empty(getCalendarFieldItem(Calendar.SECOND)));
        contents.set(1, 4, ClickableItem.empty(ItemBuilder.create(Material.SIGN, Chat.f("&r{0} {1}", getFormattedDate(reflectCalendar), getFormattedTime(cal)))));
    }

    private void add(int field, int amount) {
        cal.add(field, amount);
    }

    public String getFormattedDate(Calendar cal) {
        return dateFormatter.format(cal.getTime());
    }

    public String getFormattedTime(Calendar cal) {
        return timeFormatter.format(cal.getTime());
    }

    public ItemStack getCalendarFieldItem(int field) {
        int num = cal.get(field);
        int colorNum;
        String title;
        switch (field) {
            case Calendar.HOUR_OF_DAY:
                title = Chat.f("&6{0}時", num);
                colorNum = 5;
                break;
            case Calendar.MINUTE:
                title = Chat.f("&6{0}分", num);
                colorNum = 13;
                break;
            case Calendar.SECOND:
                title = Chat.f("&6{0}秒", num);
                colorNum = 9;
                break;
            default:
                return null;
        }

        ItemStack item;
        if (num > 0) {
            item = ItemBuilder.createItemWithData(Material.INK_SACK, colorNum, title);
        } else {
            item = ItemBuilder.createItemWithData(Material.INK_SACK, 8, title);
            num = 1;
        }

        if (item == null) {
            return null;
        }
        item.setAmount(num);
        return item;
    }
}
