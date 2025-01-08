package com.aloa.common.card.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Engrave {
    EMPRESS("EMPRESS", "황후"),
    EMPEROR("EMPEROR", "황제"),
    COMMON("COMMON", "공통");
    private final String code;

    private final String name;

    public static Engrave getEngrave(String code) {
        for (Engrave e : Engrave.values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }

        return COMMON;
    }
}
