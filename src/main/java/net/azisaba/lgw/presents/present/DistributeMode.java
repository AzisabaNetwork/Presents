package net.azisaba.lgw.presents.present;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * プレゼントの配布形式のenum
 *
 * @author siloneco
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum DistributeMode {

    ALL(true),
    ONLINE(true),
    OFFLINE(false),
//    USER(true) // TODO 後々実装
    ;

    @Getter
    private final boolean needTask;

}
