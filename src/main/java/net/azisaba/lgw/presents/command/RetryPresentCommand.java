package net.azisaba.lgw.presents.command;

import lombok.RequiredArgsConstructor;
import net.azisaba.lgw.presents.present.Present;
import net.azisaba.lgw.presents.present.PresentContainer;
import net.azisaba.lgw.presents.utils.Chat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class RetryPresentCommand implements CommandExecutor {

    private final PresentContainer container;
    private final HashMap<UUID, Long> lastExecuted = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Chat.f("&cこのコマンドはプレイヤーのみ実行できます！"));
            return true;
        }
        Player p = (Player) sender;

        if (lastExecuted.getOrDefault(p.getUniqueId(), 0L) + (1000L * 10L) > System.currentTimeMillis()) {
            p.sendMessage(Chat.f("&cコマンドのクールダウン中です！"));
            return true;
        }
        lastExecuted.put(p.getUniqueId(), System.currentTimeMillis());

        List<Present> presents = container.getMatchPresents(p);
        if (presents.size() <= 0) {
            p.sendMessage(Chat.f("&a受け取り忘れているプレゼントはありませんでした！"));
            return true;
        }

        presents.forEach(present -> present.execute(p));
        return true;
    }
}
