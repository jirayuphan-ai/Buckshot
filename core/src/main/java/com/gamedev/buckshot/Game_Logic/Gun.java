package com.gamedev.buckshot.Game_Logic;

import java.util.ArrayList;
import java.util.List;

    // ปืนลูกซอง: เก็บกระสุนที่เหลือเป็น List 
public class Gun {
    private final List<Shell_Type> shells = new ArrayList<>();
    // มีดทำให้นัดถัดไปแรง 2 เท่า 
    private boolean doubled = false;

    // บรรจุกระสุนชุดใหม่ ล้างของเก่าทิ้ง 
    public void load(List<Shell_Type> newShells) {
        shells.clear();
        shells.addAll(newShells);
        doubled = false;
    }

    // ยิงนัดเเรกสุด ยิงแล้วเอาออกจาก index
    public Shell_Type fire() {
        if (shells.isEmpty()) {
            throw new IllegalStateException("Gun is empty");
        }
        doubled = false;
        return shells.remove(0);
    }

    // แว่นขยาย — ว่างจะได้ null 
    public Shell_Type peek() {
        return shells.isEmpty() ? null : shells.get(0);
    }

    public boolean isEmpty() {
        return shells.isEmpty();
    }

    public int remaining() {
        return shells.size();
    }

    // น้ำอัดลม
    public Shell_Type eject() {
        if (shells.isEmpty()) {
            throw new IllegalStateException("Gun is empty");
        }
        return shells.remove(0);
    }

    public int liveCount() {
        int n = 0;
        for (Shell_Type s : shells) {
            if (s == Shell_Type.LIVE) n++;
        }
        return n;
    }

    public int blankCount() {
        return shells.size() - liveCount();
    }

    public void doubleDamage() {
        doubled = true;
    }

    public boolean isDoubled() {
        return doubled;
    }

    // ดาเมจของนัดถัดไป 
    public int getDamage() {
        return doubled ? 2 : 1;
    }
}
