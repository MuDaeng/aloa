package com.aloa.common.card.entity;

public enum SideNode {
    황후의기사,
    NONE,
    ;

    public static SideNode get(int value) {
        for (SideNode node : SideNode.values()) {
            if (node.ordinal() == value) {
                return node;
            }
        }
        return NONE;
    }
}
