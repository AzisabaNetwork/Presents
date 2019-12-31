package net.azisaba.lgw.presents.command;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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
    private final HashMap<UUID, PresentBuilder> builders = new HashMap<>();

    private final List<String> correctArgs = Arrays.asList("date", "command", "commands", "mode");
    private final JSONMessage helpMessage = JSONMessage.create(Chat.f("&b&m{0}", Strings.repeat("━", 50))).newline()
            .then(Chat.f("&e/present list &7- &aプレゼントのリストを表示します")).suggestCommand("/present list").newline()
            .then(Chat.f("&e/present info <名前> &7- &aプレゼントの詳細を表示します")).suggestCommand("/present info ").newline()
            .then(Chat.f("&e/present create <名前> &7- &aビルダーを作成します")).suggestCommand("/present create ").newline()
            .then(Chat.f("&e/present delete <名前> &7- &aプレゼントを削除します")).suggestCommand("/present delete ").newline()
            .then(Chat.f("&e/present build &7- &aビルダーからプレゼントを作成します")).suggestCommand("/present build").newline()
            .then(Chat.f("&e/present date <yyyy/MM/dd hh:mm:ss> &7- &a日付を指定します")).suggestCommand("/present date ").newline()
            .then(Chat.f("&e/present mode <&cAll&e/&cOnline&e/&cOffline&e> &7- &aモードを指定します")).suggestCommand("/present mode ").newline()
            .then(Chat.f("&e/present command <&cadd&e/&cremove&e> <&ccmd&e/&cindex&e> &7- &aコマンドを変更します")).suggestCommand("/present command ").newline()
            .then(Chat.f("&b&m{0}", Strings.repeat("━", 50)));

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ( !(sender instanceof Player) ) {
            sender.sendMessage(Chat.f("&cコンソールからは未対応です"));
            return true;
        }
        Player p = (Player) sender;

        if ( args.length <= 0 ) {
            sendHelpMessage(p);
            return true;
        }

        if ( args[0].equalsIgnoreCase("list") ) {
            StringBuilder builder = new StringBuilder(Chat.f("&b&m{0}\n", Strings.repeat("━", 50)));
            List<Present> presents = container.getAllPresents();
            presents.forEach(present -> builder.append(Chat.f("&r  &7- &e{0}\n", present.getName())));

            if ( presents.size() <= 0 ) {
                builder.append(Chat.f("&r  &7- &cなし\n"));
            }
            builder.append(Chat.f("&b&m{0}", Strings.repeat("━", 50)));
            p.sendMessage(builder.toString());
            return true;
        }
        if ( args[0].equalsIgnoreCase("info") ) {
            if ( args.length < 2 ) {
                p.sendMessage(Chat.f("&c使い方: /{0} info <名前>", label));
                return true;
            }
            Present present = container.getPresent(args[1]);

            if ( present == null ) {
                p.sendMessage(Chat.f("&e{0}&cという名前のプレゼントが見つかりませんでした。", args[1]));
                return true;
            }

            StringBuilder builder = new StringBuilder(Chat.f("&b&m{0}\n", Strings.repeat("━", 50)));
            builder.append(Chat.f("&e名前&a: &d{0}\n", present.getName()));
            builder.append(Chat.f("&eモード&a: &d{0}\n", present.getMode().toString().toLowerCase()));
            builder.append(Chat.f("&e時刻&a: &d{0}\n", sdf.format(present.getDate())));
            builder.append(Chat.f("&eコマンド&a:\n"));
            for ( String cmd : present.getCommands() ) {
                builder.append(Chat.f("&r  &7- &d{0}\n", cmd));
            }
            builder.append(Chat.f("&e取得待機中プレイヤー&a:\n"));
            if ( present.getRetryPlayers().size() > 0 ) {
                for ( UUID uuid : present.getRetryPlayers() ) {
                    builder.append(Chat.f("&r  &7- &d{0}\n", uuid.toString()));
                }
            } else {
                builder.append(Chat.f("&r  &7- &cなし\n"));
            }
            builder.append(Chat.f("&b&m{0}", Strings.repeat("━", 50)));

            p.sendMessage(builder.toString());
            return true;
        }
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
            p.sendMessage(Chat.f("&aプレゼント&e{0}&aを正常に削除しました。", present.getName()));
            return true;
        }

        PresentBuilder builder = builders.getOrDefault(p.getUniqueId(), null);
        if ( builder == null && correctArgs.contains(args[0].toLowerCase()) ) {
            p.sendMessage(Chat.f("&c先に &e/{0} create &cを実行してビルダーを作成してください", label));
            return true;
        } else if ( builder == null ) {
            sendHelpMessage(p);
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
        } else if ( args[0].equalsIgnoreCase("command") || args[0].equalsIgnoreCase("commands") ) {
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
        } else if ( args[0].equalsIgnoreCase("date") ) {
            if ( args.length < 3 ) {
                p.sendMessage(Chat.f("&c使い方: /{0} date <yyyy/MM/dd HH:mm:ss>", label));
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
        } else if ( args[0].equalsIgnoreCase("mode") ) {
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
        }
        return true;
    }

    private JSONMessage getCommandViewer(String label, List<String> commands) {
        JSONMessage msg = JSONMessage.create(Chat.f("&b&m{0}", Strings.repeat("━", 50))).newline();
        for ( int i = 0; i < commands.size(); i++ ) {
            String cmd = commands.get(i);
            msg.then(Chat.f("&e{0}&a: &d{1} ", i, cmd));
            msg.then(Chat.f("&c[-]")).runCommand(Chat.f("/{0} command remove {1}", label, i));
            msg.newline();
        }

        if ( commands.size() <= 0 ) {
            msg.then(Chat.f("&cなし")).newline();
        }
        msg.then(Chat.f("&b&m{0}", Strings.repeat("━", 50)));

        return msg;
    }

    private void sendHelpMessage(Player p) {
        helpMessage.send(p);
    }
}
