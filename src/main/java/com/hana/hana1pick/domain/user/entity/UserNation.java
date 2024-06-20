package com.hana.hana1pick.domain.user.entity;

public enum UserNation {
    KOR("Korea"),
    CN("China"),
    JP("Japan");

    private final String value;

    UserNation(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
