package com.aloa.common.card.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Predicate;

@Getter
@AllArgsConstructor
public enum SideNode {
    KNIGHT(PredicateKnight::isKnight),
    NONE(Predicate.not(PredicateKnight::isKnight)),
    TOTAL(predicateKnight -> true),
    ;

    private final Predicate<PredicateKnight> predicate;

    public static SideNode get(int value) {
        for (SideNode node : SideNode.values()) {
            if (node.ordinal() == value) {
                return node;
            }
        }
        return NONE;
    }
}
