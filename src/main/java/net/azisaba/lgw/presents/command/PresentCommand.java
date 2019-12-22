package net.azisaba.lgw.presents.command;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.base.Strings;

import net.azisaba.lgw.presents.present.DistributeMode;
import net.azisaba.lgw.presents.present.Present;
import net.azisaba.lgw.presents.present.PresentBuilder;
import net.azisaba.lgw.presents.present.PresentContainer;
import net.azisaba.lgw.presents.present.PresentFactory;
import net.azisaba.lgw.presents.utils.Chat;

import lombok.RequiredArgsConstructor;

import me.rayzr522.jsonmessage.JSONMessage;

/**
 * /presentコマンドを実行するクラス
 *
 * @author siloneco
 *
 */
@RequiredArgsConstructor
public class PresentCommand implements CommandExecutor {

    private final PresentContainer container;
    private final PresentFactory factory;
    private HashMap<UUID, PresentBuilder> builders = new HashMap<>();

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ( args.length <= 0 ) {
            sender.sendMessage(Chat.f("&cひきすうたりないよ <3"));
            return true;
        }
        if ( !(sender instanceof Player) ) {
            sender.sendMessage(Chat.f("&cコンソールからは未対応です"));
            return true;
        }

        Player p = (Player) sender;

        if ( args[0].equalsIgnoreCase("create") ) {
            if ( args.length < 2 ) {
                p.sendMessage(Chat.f("&c使い方: /{0} create <名前>", label));
                return true;
            }

            PresentBuilder builder = factory.getPresentBuilder();
            builder.setName(args[1]);
            builders.put(p.getUniqueId(), builder);

            p.sendMessage(Chat.f("&a新しいビルダーを作成しました"));
            return true;
        }

        if ( args[0].equalsIgnoreCase("delete") ) {
            if ( args.length < 2 ) {
                p.sendMessage(Chat.f("&c使い方: /{0} delete <名前>", label));
                return true;
            }
            String name = args[1];
            Present present = container.getPresent(name);
            if ( present == null ) {
                p.sendMessage(Chat.f("&cその名前のプレゼントは見つかりませんでした"));
                return true;
            }

            container.removePresent(present);
            p.sendMessage(Chat.f("&a正常に削除しました。"));
            return true;
        }

        PresentBuilder builder = builders.getOrDefault(p.getUniqueId(), null);
        if ( builder == null ) {
            p.sendMessage(Chat.f("&c先に &e/{0} create &cを実行してビルダーを作成してください"));
            return true;
        }

        if ( args[0].equalsIgnoreCase("build") ) {
            List<String> errors = builder.getErrors();
            errors.stream()
                    .map(str -> Chat.f("&e{1}", str))
                    .collect(Collectors.toList());

            if ( errors.size() > 0 ) {
                p.sendMessage(Chat.f("&cプレゼントの作成に失敗しました。以下エラーです"));
                errors.forEach(p::sendMessage);
                return true;
            }

            Present present = builder.create();
            if ( present != null ) {
                p.sendMessage(Chat.f("&aプレゼントの作成に成功しました。"));
                builders.remove(p.getUniqueId());
            } else {
                p.sendMessage(Chat.f("&cプレゼントの作成に失敗しました。"));
            }
            return true;
        }

        if ( args[0].equalsIgnoreCase("command") ) {
            if ( args.length < 2 ) {
                p.sendMessage(Chat.f("&c使い方: /{0} command <&eadd&c/&eremove&c> <&ecommand&c/&enumber&c>", label));
                return true;
            }

            if ( args[1].equalsIgnoreCase("add") ) {
                String cmd = String.join(" ", args).substring(args[0].length() + args[1].length() + 2);
                builder.getCommands().add(cmd);
                getCommandViewer(label, builder.getCommands()).send(p);
            } else if ( args[1].equalsIgnoreCase("remove") ) {
                int number = -1;

                try {
                    number = Integer.parseInt(args[2]);
                } catch ( Exception e ) {
                    p.sendMessage(Chat.f("&c数字を入力してください。"));
                    return true;
                }

                builder.getCommands().remove(number);
                getCommandViewer(label, builder.getCommands()).send(p);
            } else {
                p.sendMessage(Chat.f("&c2つ目の引数は&eadd&cか&eremove&cを指定してください"));
            }
            return true;
        }

        if ( args[0].equalsIgnoreCase("date") ) {
            if ( args.length < 3 ) {
                p.sendMessage(Chat.f("&c使い方: /{0} date <yyyy/MM/dd hh:mm:ss>", label));
                return true;
            }

            String dateStr = args[1] + " " + args[2];
            Date date;
            try {
                date = sdf.parse(dateStr);
            } catch ( ParseException e ) {
                p.sendMessage(Chat.f("&c読み取りに失敗しました。フォーマットは &eyyyy/MM/dd hh:mm:ss &cです"));
                return true;
            }

            builder.setDate(date);
            p.sendMessage(Chat.f("&a時刻を設定しました"));
            return true;
        }

        if ( args[0].equalsIgnoreCase("mode") ) {
            if ( args.length < 2 ) {
                p.sendMessage(Chat.f("&c使い方: /{0} mode <&eall&c/&eonline&c/&eoffline&c>", label));
                return true;
            }
            DistributeMode mode;

            try {
                mode = DistributeMode.valueOf(args[1].toUpperCase());
            } catch ( Exception e ) {
                p.sendMessage(Chat.f("&e{0}&cという名前のモードはありません。", args[1]));
                return true;
            }

            builder.setMode(mode);
            p.sendMessage(Chat.f("&e{0}&aモードに設定しました", mode.name().toLowerCase()));
            return true;
        }

        return true;
    }

    private JSONMessage getCommandViewer(String label, List<String> commands) {
        JSONMessage msg = JSONMessage.create(Chat.f("&b{0}", Strings.repeat("━", 20)));
        for ( int i = 0; i < commands.size(); i++ ) {
            String cmd = commands.get(i);
            msg.newline();
            msg.then(Chat.f("&e{0}&a: &d{1} ", i, cmd));
            msg.then(Chat.f("&c[-]")).runCommand(Chat.f("/{0} command remove {1}", label, i)).tooltip(Chat.f("&c削除する"));
            i++;
        }

        if (commands.size() <= 0) {
            msg.then(Chat.f("&cなし"));
        }

        msg.newline();
        msg.then(Chat.f("&b{0}", Strings.repeat("━", 20)));

        return msg;
    }
}