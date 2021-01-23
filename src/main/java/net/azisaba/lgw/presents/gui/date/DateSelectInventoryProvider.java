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
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

@RequiredArgsConstructor
public class DateSelectInventoryProvider implements InventoryProvider {

    private final int year;
    private final int month;

    private final Calendar reflectCalendar;

    @Override
    public void init(Player player, InventoryContents contents) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, 1);

        setPaginationButton(contents, 8, 1);
        setPaginationButton(contents, 0, -1);

        contents.set(contents.inventory().getRows() - 1, contents.inventory().getColumns() - 1,
                ClickableItem.of(ItemBuilder.create(Material.BOOK_AND_QUILL, Chat.f("&c文字入力で設定")),
                        event -> TextInputOpener.open(player, "年/月/日", (player1, text) -> {
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
                            try {
                                reflectCalendar.setTimeInMillis(formatter.parse(text).getTime());
                                openTimeSelectInventory(player, contents);
                                return TextInputResponse.accept();
                            } catch (ParseException e) {
                                return TextInputResponse.error(Chat.f("&c無効な形式: &e年/月/日 &cで入力してください"));
                            }
                        })));

        cal.add(Calendar.DATE, cal.get(Calendar.DAY_OF_WEEK) * -1 + 1);
        for (int i = 0; i <= 42; i++) {
            final int year = cal.get(Calendar.YEAR);
            final int month = cal.get(Calendar.MONTH) + 1;
            final int date = cal.get(Calendar.DATE);
            final int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

            int color = 5;
            if (month - 1 != this.month) {
                color = 7;
            }

            ItemStack item = ItemBuilder.createItemWithData(Material.STAINED_GLASS_PANE, color, Chat.f("&a{0}/{1}/{2} &6({3})", year + "", month, date, getFormattedDayOfWeek(dayOfWeek)));
            if (item == null) {
                continue;
            }
            item.setAmount(date);
            contents.add(ClickableItem.of(
                    item,
                    event -> {
                        reflectCalendar.set(year, month - 1, date);
                        openTimeSelectInventory((Player) event.getWhoClicked(), contents);
                    }
            ));
            cal.add(Calendar.DATE, 1);
        }
    }

    @Override
    public void update(Player player, InventoryContents contents) {
        init(player, contents);
    }

    private void openTimeSelectInventory(Player player, InventoryContents contents) {
        SmartInventory.builder()
                .manager(contents.inventory().getManager())
                .parent(contents.inventory())
                .provider(new TimeInputInventoryProvider(reflectCalendar))
                .size(3, 9)
                .build().open(player);
    }

    private void setPaginationButton(InventoryContents contents, int column, int num) {
        contents.fillColumn(column, ClickableItem.of(
                ItemBuilder.createItemWithData(Material.STAINED_GLASS_PANE, 14, Chat.f("&c次ページ")),
                event -> {
                    Player p = (Player) event.getWhoClicked();
                    int nextYear = year;
                    int nextMonth = month + num;
                    if (nextMonth < 0) {
                        nextYear -= 1;
                        nextMonth = Calendar.DECEMBER;
                    } else if (nextMonth >= 12) {
                        nextYear += 1;
                        nextMonth = Calendar.JANUARY;
                    }

                    SmartInventory.builder()
                            .manager(contents.inventory().getManager())
                            .parent(contents.inventory().getParent().orElse(null))
                            .title(Chat.f("&2{0}年{1}月", nextYear + "", nextMonth + 1))
                            .provider(new DateSelectInventoryProvider(nextYear, nextMonth, reflectCalendar))
                            .build().open(p);
                }
        ));
    }

    private final List<String> dayOfWeekList = Arrays.asList("日", "月", "火", "水", "木", "金", "土");

    private String getFormattedDayOfWeek(int dayOfWeek) {
        return dayOfWeekList.get(dayOfWeek - 1);
    }
}
