package net.azisaba.lgw.presents.present;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * プレゼントを構成するクラス
 *
 * @author siloneco
 *
 */
@AllArgsConstructor
@Getter
public class Present {

    private final String name;
    private Date date;

    private List<String> commands;
    private DistributeMode mode;

    private final List<UUID> alreadyGavePlayers = new ArrayList<>();

    protected void setAlreadyGavePlayers(List<UUID> uuidList) {
        alreadyGavePlayers.clear();
        alreadyGavePlayers.addAll(uuidList);
    }

    public void execute(Player player) {
        if ( alreadyGavePlayers.contains(player.getUniqueId()) ) {
            return;
        }

        for ( String command : commands ) {
            command = command.replace("<player>", player.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }

        alreadyGavePlayers.add(player.getUniqueId());
    }

    public void execute(List<Player> players) {
        for ( Player p : players ) {
            execute(p);
        }
    }
}
