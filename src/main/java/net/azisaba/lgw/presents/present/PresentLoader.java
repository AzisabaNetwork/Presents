package net.azisaba.lgw.presents.present;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.configuration.file.YamlConfiguration;

import lombok.RequiredArgsConstructor;

/**
 * プレゼントをセーブ、ロードするクラス
 *
 * @author siloneco
 *
 */
@RequiredArgsConstructor
public class PresentLoader {

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private final File folder;
    private final PresentContainer container;
    private final PresentFactory factory;

    public void loadAllPresents() {
        // 存在していない場合はreturn
        if ( !folder.exists() ) {
            return;
        }

        for ( File file : folder.listFiles() ) {
            // ディレクトリならcontinue
            if ( file.isDirectory() ) {
                continue;
            }
            // yamlファイルではない場合continue;
            if ( !file.getName().toLowerCase().endsWith(".yml") && !file.getName().toLowerCase().endsWith(".yaml") ) {
                continue;
            }

            // 読み込み
            YamlConfiguration conf = YamlConfiguration.loadConfiguration(file);

            String name = conf.getString("Name", null);
            Date date;
            try {
                date = sdf.parse(conf.getString("Date"));
            } catch ( ParseException e ) {
                e.printStackTrace();
                return;
            }
            DistributeMode mode = DistributeMode.valueOf(conf.getString("Mode"));
            List<String> commands = conf.getStringList("Commands");
            List<UUID> alreadyGave = conf.getStringList("AlreadyGave").stream()
                    .map(UUID::fromString)
                    .collect(Collectors.toList());
            List<UUID> retryPlayers = new ArrayList<>();
            if ( conf.isSet("RetryPlayers") ) {
                retryPlayers = conf.getStringList("RetryPlayers").stream()
                        .map(UUID::fromString)
                        .collect(Collectors.toList());
            }
            int emptySlots = conf.getInt("RequireEmptySlots", 0);

            // Present作成
            Present present = factory.loadPresent(name, date, mode, commands, emptySlots, alreadyGave, retryPlayers);
            // 登録
            container.register(present);
        }
    }

    public void savePresent(Present present) {
        File file = new File(folder, present.getName() + ".yml");
        YamlConfiguration conf = YamlConfiguration.loadConfiguration(file);

        conf.set("Name", present.getName());
        conf.set("Date", sdf.format(present.getDate()));
        conf.set("Mode", present.getMode().name());
        conf.set("RequireEmptySlots", present.getRequireEmptySlots());
        conf.set("Commands", present.getCommands());
        conf.set("AlreadyGave", present.getAlreadyGavePlayers().stream()
                .map(UUID::toString)
                .collect(Collectors.toList()));
        conf.set("RetryPlayers", present.getRetryPlayers().stream()
                .map(UUID::toString)
                .collect(Collectors.toList()));

        try {
            conf.save(file);
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }
}
