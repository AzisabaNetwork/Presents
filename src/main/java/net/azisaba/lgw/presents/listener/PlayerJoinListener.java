package net.azisaba.lgw.presents.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import net.azisaba.lgw.presents.Presents;
import net.azisaba.lgw.presents.present.PresentContainer;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlayerJoinListener implements Listener {

    private final Presents plugin;
    private final PresentContainer container;

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            private final Player player = p;

            @Override
            public void run() {
                if ( player != null && player.isOnline() ) {
                    container.getMatchPresents(player).forEach(present -> present.execute(player));
                }
            }
        }, 20L);
    }
}
