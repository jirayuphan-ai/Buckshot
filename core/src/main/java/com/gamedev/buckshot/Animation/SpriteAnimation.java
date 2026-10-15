package com.gamedev.buckshot.Animation;

import com.badlogic.gdx.graphics.Texture;

/**
 * แอนิเมชันชุดเดียว = รูปหลายเฟรมเล่นเรียงกัน เฟรมละ frameTime วินาที (เล่นครั้งเดียวแล้วจบ)
 * (LibGDX มีคลาส Animation<TextureRegion> สำเร็จรูปด้วย — แต่ใช้ตาม Class Diagram: Texture[])
 */
public class SpriteAnimation {
    private final Texture[] frames;
    private final float frameTime;
    private float elapsed = 0f;

    public SpriteAnimation(Texture[] frames, float frameTime) {
        this.frames = frames;
        this.frameTime = frameTime;
    }

    public void update(float delta) {
        if (!isFinished()) elapsed += delta;
    }

    public Texture getCurrentFrame() {
        if (frames.length == 0) return null;
        int i = (int) (elapsed / frameTime);
        return frames[Math.min(i, frames.length - 1)];
    }

    public boolean isFinished() {
        return elapsed >= frames.length * frameTime;
    }

    public void reset() {
        elapsed = 0f;
    }
}
