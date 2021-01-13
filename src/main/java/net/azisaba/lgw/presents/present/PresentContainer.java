package net.azisaba.lgw.presents.present;

import lombok.RequiredArgsConstructor;
import net.azisaba.lgw.presents.Presents;
import net.azisaba.lgw.presents.task.ExecuteCommandTask;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * ロードしたプレゼントを格納するクラス
 *
 * @author siloneco
 */
@RequiredArgsConstructor
public class PresentContainer {

    private final Presents plugin;
    private final File folder;
    private final HashMap<String, Present> presents = new HashMap<>();

    public void register(Present present) throws IllegalArgumentException {
        // 既に登録されている名前が使用されている場合は例外
        if (presents.containsKey(present.getName())) {
            throw new IllegalArgumentException("The name \"" + present.getName() + "\" is already using.");
        }

        presents.put(present.getName(), present);

        if (present.getMode().isNeedTask() && present.getDate().after(new Date())) {
            new ExecuteCommandTask(plugin, present).runTaskLater(plugin, 0L);
        }
    }

    public Present getPresent(String name) {
        return presents.getOrDefault(name, null);
    }

    public List<Present> getAllPresents() {
        return new ArrayList<>(presents.values());
    }

    public void removePresent(Present present) {
        presents.remove(present.getName());

        File file = new File(folder, present.getName() + ".yml");
        if (file.exists()) {
            file.delete();
        }
    }

    public List<Present> getMatchPresents(Player player) {
        List<Present> p = new ArrayList<>();

        for (Present present : presents.values()) {
            if (present.getDate().after(new Date())) {
                continue;
            }
            if (present.isAlreadyGave(player)) {
                continue;
            }
            if (present.isRetryPlayer(player)) {
                p.add(present);
                continue;
            }
            if (present.getMode() == DistributeMode.ONLINE) {
                continue;
            }
//            if ( present.getMode() == DistributeMode.USER ) { // TODO 作成中
//                continue;
//            }

            p.add(present);
        }

        return p;
    }
}
