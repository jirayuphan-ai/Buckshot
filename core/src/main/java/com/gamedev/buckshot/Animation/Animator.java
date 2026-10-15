package com.gamedev.buckshot.Animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * ตัวสั่งเล่นแอนิเมชัน: play(type) -> เลือก SpriteAnimation ของชนิดนั้นมาเล่น
 * GameScreen เรียก update(delta) ทุกเฟรม แล้วเรียก getCurrentFrame() ไปวาด
 */
public class Animator implements Disposable {
    private static final float FRAME_TIME = 0.07f;

    private final Map<AnimationType, SpriteAnimation> animations = new EnumMap<>(AnimationType.class);
    private final List<Texture> allTextures = new ArrayList<>();
    private SpriteAnimation current;

    /** โหลดเฟรมทุกชนิดจาก resources/anim (เรียงตามเลข จนกว่าจะไม่เจอไฟล์) */
    public Animator() {
        for (AnimationType type : AnimationType.values()) {
            List<Texture> frames = new ArrayList<>();
            for (int i = 0; Gdx.files.internal(type.getFramePath(i)).exists(); i++) {
                Texture t = new Texture(Gdx.files.internal(type.getFramePath(i)));
                t.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                frames.add(t);
                allTextures.add(t);
            }
            animations.put(type, new SpriteAnimation(frames.toArray(new Texture[0]), FRAME_TIME));
        }
    }

    public void play(AnimationType type) {
        current = animations.get(type);
        current.reset();
    }

    public void update(float delta) {
        if (current != null) current.update(delta);
    }

    public boolean isPlaying() {
        return current != null && !current.isFinished();
    }

    /** เฟรมที่ต้องวาดตอนนี้ (ไม่ได้เล่นอยู่ = null) */
    public Texture getCurrentFrame() {
        return isPlaying() ? current.getCurrentFrame() : null;
    }

    @Override
    public void dispose() {
        for (Texture t : allTextures) t.dispose();
        allTextures.clear();
    }
}
