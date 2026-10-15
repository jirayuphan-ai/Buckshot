package com.gamedev.buckshot.Sound;

/** รายการเสียงทั้งหมดในเกม — ชื่อ enum ตรงกับชื่อไฟล์ใน resources/sounds/ (ตัวเล็ก + .wav) */
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
