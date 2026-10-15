package com.gamedev.buckshot.Animation;

// ชนิดแอนิเมชัน
public enum AnimationType {
    PICK_UP_GUN,
    FIRE_LIVE,
    FIRE_BLANK,
    USE_MAGNIFIER,
    USE_CANDY,
    USE_SODA,
    USE_KNIFE;

    public String getFramePath(int i) {
        return "Image/" + name().toLowerCase() + "_" + i + ".png";
    }
}
