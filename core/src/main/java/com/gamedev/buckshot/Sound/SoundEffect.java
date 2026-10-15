package com.gamedev.buckshot.Sound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Disposable;

/**
 * เสียงเอฟเฟกต์ 1 ไฟล์ — ห่อ com.badlogic.gdx.audio.Sound ของ LibGDX
 * (เขียนชื่อเต็มเพราะชื่อซ้ำกับคลาส Sound ของเราเอง)
 * LibGDX Sound ไม่มี isPlaying() เราเลยจับเวลาเองจากความยาวไฟล์ WAV
 */
public class SoundEffect implements Disposable {
    private final String sfxFile;
    private com.badlogic.gdx.audio.Sound sound;   // null = โหลดไม่ได้ -> เงียบ แต่เกมไม่พัง
    private float length = 1f;                    // วินาที
    private long startedAt = -1;

    public SoundEffect(String sfxFile) {
        this.sfxFile = sfxFile;
        try {
            FileHandle fh = Gdx.files.internal(sfxFile);
            sound = Gdx.audio.newSound(fh);
            length = readWavSeconds(fh);
        } catch (Exception e) {
            Gdx.app.error("SoundEffect", "cannot load " + sfxFile + ": " + e.getMessage());
            sound = null;
        }
    }

    public void play(float volume) {
        if (sound == null) return;
        sound.play(volume);
        startedAt = System.nanoTime();
    }

    public void stop() {
        if (sound != null) sound.stop();
        startedAt = -1;
    }

    public boolean isPlaying() {
        return startedAt >= 0 && (System.nanoTime() - startedAt) / 1_000_000_000f < length;
    }

    public String getSfxFile() {
        return sfxFile;
    }

    @Override
    public void dispose() {
        if (sound != null) sound.dispose();
    }

    /** อ่านความยาวจาก header ของ WAV (PCM มาตรฐาน 44 ไบต์) ไม่ใช่ WAV -> ถือว่า 1 วินาที */
    private static float readWavSeconds(FileHandle fh) {
        try {
            byte[] b = fh.readBytes();
            if (b.length > 44 && b[0] == 'R' && b[1] == 'I' && b[2] == 'F' && b[3] == 'F') {
                int byteRate = (b[28] & 0xff) | ((b[29] & 0xff) << 8) | ((b[30] & 0xff) << 16) | ((b[31] & 0xff) << 24);
                if (byteRate > 0) return (b.length - 44f) / byteRate;
            }
        } catch (Exception ignored) {
            // ใช้ค่าเริ่มต้น
        }
        return 1f;
    }
}
