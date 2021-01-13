package net.azisaba.lgw.presents.present;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * プレゼントを構成する要素を決めていくクラス。最後にcreateをして {@link Present} を作成する
 *
 * @author siloneco
 */
@Getter
@Setter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class PresentBuilder {

    private final PresentContainer container;

    // プレゼントの名前
    private String name;
    // 配布する日付
    private Date date;
    // 実行するコマンドリスト
    private List<String> commands = new ArrayList<>();
    // 配布モード
    private DistributeMode mode;
    // 必要なスロットの数
    private int requireEmptySlots = 0;

    /**
     * 指定されたデータに基づいてプレゼントを作成します。
     *
     * @return 作成されたプレゼント
     * @throws IllegalStateException 必要パラメータが指定されていない場合
     */
    public Present create() throws IllegalStateException {
        // getInvalidParamsメソッドを呼び出し、サイズが0より大きかった場合は例外
        List<String> invalids = getErrors();
        if (invalids.size() > 0) {
            List<String> params = invalids.stream().map(str -> str.split(":")[0]).collect(Collectors.toList());
            throw new IllegalStateException("There is/are " + invalids.size() + " invalid params (" + String.join(", ", params) + ")");
        }

        // プレゼントを作成する
        Present present = new Present(name, date, commands, mode, requireEmptySlots);
        // 登録する
        container.register(present);
        // 返す
        return present;
    }

    /**
     * 現在指定されているパラメータを分析し、無効なものを返します
     *
     * @return 無効なパラメーターとその理由
     */
    public List<String> getErrors() {
        List<String> invalids = new ArrayList<>();

        // nameはnull、もしくはすでに使用されている物であれば追加
        if (name == null) {
            invalids.add("Name: 必須パラメーターです。値を指定してください");
        } else if (container.getPresent(name) != null) {
            invalids.add("Name: 既に使用されている名前です");
        }

        // 日付がnull、もしくはonlineモードの際に指定した日付が今よりも前だった場合は配布できないので無効
        if (date == null) {
            invalids.add("Date: 必須パラメーターです。値を指定してください");
        } else if (mode == DistributeMode.ONLINE && date.before(new Date())) {
            invalids.add("Date: ONLINEモードかつ現在よりも前の時刻が指定されているため誰にも配布できません");
        }

        // コマンドリストがnullもしくは空の場合追加
        if (commands == null || commands.size() <= 0) {
            invalids.add("Commands: 必須パラメーターです。1つ以上の値を指定してください。");
        }

        // モードがnullなら追加
        if (mode == null) {
            invalids.add("Mode: 必須パラメーターです。値を指定してください");
        }

        return invalids;
    }
}
