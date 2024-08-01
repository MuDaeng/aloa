package com.aloa.common.card.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Card {
    삼두사("삼두사", Engrave.COMMON),
    광기("광기", Engrave.COMMON),
    뒤운("뒤틀린 운명", Engrave.COMMON),
    부식("부식", Engrave.COMMON),
    유령("유령", Engrave.COMMON),
    도태("도태", Engrave.COMMON),
    균형("균형", Engrave.EMPRESS),
    심판("심판", Engrave.EMPRESS),
    달("달", Engrave.COMMON),
    별("별", Engrave.COMMON),
    로열("로열", Engrave.COMMON),
    운수("운명의 수레바퀴", Engrave.COMMON),
    광대("광대", Engrave.COMMON),
    환희("환희", Engrave.COMMON),
    황제("황제", Engrave.EMPEROR),
    재상("재상", Engrave.EMPEROR),
    제후("제후", Engrave.EMPEROR),
    EMPTY("", Engrave.COMMON),
    BLACK("", Engrave.COMMON),
    UNDEFINED("", Engrave.COMMON),
    ;

    private final String name;
    private final Engrave engrave;

}
