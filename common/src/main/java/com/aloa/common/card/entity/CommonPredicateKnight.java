package com.aloa.common.card.entity;

public abstract class CommonPredicateKnight implements PredicateKnight {
    protected SideNode sideNode;

    protected CommonPredicateKnight(SideNode sideNode) {
        this.sideNode = sideNode;
    }

    @Override
    public boolean isKnight() {
        return SideNode.KNIGHT.equals(sideNode);
    }
}
