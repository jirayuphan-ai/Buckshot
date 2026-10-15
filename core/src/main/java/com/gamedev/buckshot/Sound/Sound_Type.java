package com.gamedev.buckshot.Sound;

public enum Sound_Type {
    
    PICK_UP_GUN,
    FIRE_LIVE,
    FIRE_BLANK,
    EMPTY_CLICK,
    USE_MAGNIFIER,
    USE_CANDY,
    USE_SODA,
    USE_KNIFE;

    public String getFile() {
        return "sound/" + name().toLowerCase() + ".wav";
    }
}
