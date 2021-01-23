package net.azisaba.lgw.presents.present;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.azisaba.lgw.presents.utils.Chat;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * プレゼントを構成するクラス
 *
 * @author siloneco
 */
@Getter
@RequiredArgsConstructor
public class Present {

    private final String name;
    private final Date date;

    private final List<String> commands;
    private final DistributeMode mode;
    private final int requireEmptySlots;

    @Getter(value = AccessLevel.PROTECTED)
    @Setter(value = AccessLevel.PROTECTED)
    private List<UUID> alreadyGavePlayers = new ArrayList<>();
    @Setter(value = AccessLevel.PROTECTED)
    private List<UUID> retryPlayers = new ArrayList<>();

    public void execute(Player player) {
        if (alreadyGavePlayers.contains(player.getUniqueId())) {
            return;
        }

        if (getEmptySlots(player) < requireEmptySlots) {
            String msg = Chat.f("&e[注意] &c&n十分な空きがないためプレゼントを受け取れませんでした。\n"
                    + "&e[注意] &e{0}個以上&cの空きを確認した後 &e/RetryPresent &cを実行してください", requireEmptySlots);
            player.sendMessage(msg);
            if (!retryPlayers.contains(player.getUniqueId())) {
                retryPlayers.add(player.getUniqueId());
            }
            return;
        }

        retryPlayers.remove(player.getUniqueId());

        for (String command : commands) {
            command = command.replace("<player>", player.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }

        alreadyGavePlayers.add(player.getUniqueId());
    }

    public void execute(List<Player> players) {
        for (Player p : players) {
            execute(p);
        }
    }

    public void setAlreadyGave(Player p) {
        if (!alreadyGavePlayers.contains(p.getUniqueId())) {
            alreadyGavePlayers.add(p.getUniqueId());
        }
    }

    public boolean isAlreadyGave(Player p) {
        return alreadyGavePlayers.contains(p.getUniqueId());
    }

    public boolean isRetryPlayer(Player p) {
        if (isAlreadyGave(p)) {
            return false;
        }
        return retryPlayers.contains(p.getUniqueId());
    }

    private int getEmptySlots(Player p) {
        int count = 0;
        for (int i = 0; i < 36; i++) {
            ItemStack item = p.getInventory().getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                count++;
            }
        }
        return count;
    }
}
