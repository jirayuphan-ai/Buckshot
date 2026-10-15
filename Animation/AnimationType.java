package com.gamedev.buckshot.Animation;

/** ชนิดแอนิเมชัน — ชื่อตรงกับเฟรมใน resources/anim/<ชื่อตัวเล็ก>_0.png, _1.png, ... */
public enum AnimationType {
    PICK_UP_GUN,
    FIRE_LIVE,
    FIRE_BLANK,
    USE_MAGNIFIER,
    USE_CANDY,
    USE_SODA,
    USE_KNIFE;

    /** path ของเฟรมลำดับที่ i */
    public String getFramePath(int i) {
        return "Image/" + name().toLowerCase() + "_" + i + ".png";
    }
}
