package net.azisaba.lgw.presents.utils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class TextInputResponse {

    private final boolean accept;
    private final String errorText;

    public static TextInputResponse accept() {
        return new TextInputResponse(true, null);
    }

    public static TextInputResponse error(String errorText) {
        return new TextInputResponse(false, errorText);
    }
}
