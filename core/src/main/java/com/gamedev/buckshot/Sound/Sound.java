package com.gamedev.buckshot.Sound;

import com.badlogic.gdx.utils.Disposable;

import java.util.EnumMap;
import java.util.Map;


  //ตัวจัดการเสียงทั้งเกม
  
public class Sound implements Disposable {
    private final Map<Sound_Type, SoundEffect> effects = new EnumMap<>(Sound_Type.class);
    private SoundEffect current;
    private float volume = 1f;

    public Sound() {
        for (Sound_Type t : Sound_Type.values()) {
            effects.put(t, new SoundEffect(t.getFile()));
        }
    }

    public void play(Sound_Type type) {
        current = effects.get(type);
        current.play(volume);
    }

    public void stop() {
        if (current != null) current.stop();
    }

    public void setVolume(float v) {
        volume = Math.max(0f, Math.min(1f, v));
    }

    public boolean isPlaying() {
        return current != null && current.isPlaying();
    }

    @Override
    public void dispose() {
        for (SoundEffect e : effects.values()) e.dispose();
        effects.clear();
    }
}
