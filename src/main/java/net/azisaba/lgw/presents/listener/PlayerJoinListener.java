package net.azisaba.lgw.presents.listener;

import lombok.RequiredArgsConstructor;
import net.azisaba.lgw.presents.Presents;
import net.azisaba.lgw.presents.present.PresentContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@RequiredArgsConstructor
public class PlayerJoinListener implements Listener {

    private final PresentContainer container;

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        boolean firstJoin = !p.hasPlayedBefore();

        if (firstJoin) {
            Presents.newChain()
                    .async(() -> container.getMatchPresents(p).forEach(present -> present.setAlreadyGave(p)))
                    .execute();
        } else {
            Presents.newChain()
                    .delay(3)
                    .asyncFirst(() -> container.getMatchPresents(p))
                    .syncLast(presents -> presents.forEach(present -> present.execute(p)))
                    .execute();
        }
    }
}
