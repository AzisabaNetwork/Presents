package net.azisaba.lgw.presents.task;

import lombok.RequiredArgsConstructor;
import net.azisaba.lgw.presents.Presents;
import net.azisaba.lgw.presents.present.DistributeMode;
import net.azisaba.lgw.presents.present.Present;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * 新年のカウントダウンを行うタスクです
 *
 * @author siloneco
 */
@RequiredArgsConstructor
public class ExecuteCommandTask extends BukkitRunnable {

    private final Presents plugin;
    private final Present present;

    private boolean executed = false;

    @Override
    public void run() {
        // 現在時刻を取得
        Calendar cal = Calendar.getInstance();

        // Long型に変換
        long now = cal.getTimeInMillis();
        long target = present.getDate().getTime();

        // 残りミリ秒数を取得
        long remaining = target - now;

        // 残りミリ秒が100を切っている場合
        if (remaining <= 100) {
            // 既に実行されている場合はreturn
            if (executed) {
                Bukkit.getLogger().info("already executed commands (" + getTaskId() + ")");
                return;
            }

            if (present.getMode() == DistributeMode.OFFLINE) {
                Bukkit.getOnlinePlayers().forEach(present::setAlreadyGave);
            } else {
                present.execute(new ArrayList<>(Bukkit.getOnlinePlayers()));
            }

            executed = true;
            return;
        }

        // tickに変換
        long remainingTicks = new BigDecimal(remaining)
                .divide(BigDecimal.valueOf(50), RoundingMode.DOWN)
                .divide(BigDecimal.valueOf(3), RoundingMode.DOWN) // どうしてもtick数は20以下になるので、3で割ることで遅れるのを防ぐ
                .setScale(0, RoundingMode.DOWN)
                .longValue();

        // tick数が0以下の場合は1にする
        if (remainingTicks <= 0) {
            remainingTicks = 1;
        }

        // 次のタスクを実行
        new ExecuteCommandTask(plugin, present).runTaskLater(plugin, remainingTicks);

        // このオブジェクトを削除
        cancel();
    }
}
