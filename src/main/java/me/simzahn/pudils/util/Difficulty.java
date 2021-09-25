package me.simzahn.pudils.util;

import java.util.PrimitiveIterator;

public enum Difficulty {

    IMMORTABLE("§2§lImmortable"),
    NORMAl("§a§lNormal"),
    UHC("§e§lUHC"),
    UUHC("§6§lUUHC"),
    HALF_HEART("§4§lHalf Heart");

    private String name;

    Difficulty(String name) {
        this.name=name;
    }

    public String getName() {
        return name;
    }
}
