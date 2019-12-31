package net.azisaba.lgw.presents.present;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

/**
 * Presentインスタンスを作成するクラス
 *
 * @author siloneco
 *
 */
@RequiredArgsConstructor
public class PresentFactory {

    private final PresentContainer container;

    /**
     * {@link PresentBuilder} を作成、取得します
     *
     * @return 作成された {@link PresentBuilder}
     */
    public PresentBuilder getPresentBuilder() {
        return new PresentBuilder(container);
    }

    protected Present loadPresent(String name, Date date, DistributeMode mode, List<String> commands, int emptySlots, List<UUID> alreadyGave, List<UUID> retryPlayers) {
        Present present = new Present(name, date, commands, mode, emptySlots);
        present.setAlreadyGavePlayers(alreadyGave);
        present.setRetryPlayers(retryPlayers);
        return present;
    }
}
