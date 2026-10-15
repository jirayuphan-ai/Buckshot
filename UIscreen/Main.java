package com.gamedev.buckshot.UIscreen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class Main extends Game { // Extend LibGDX create start setScreen
    private Screen currentScreen;

    @Override
    public void create() {
        start();
    }

    public void start() {
        setScreen(new Mainmenuscreen(this));
    }

    public void setScreen(Screen s) {
        currentScreen = s;
        super.setScreen(s);   // เรียก hide() หน้าเก่า -> show() หน้าใหม่ ให้เอง
    }

    public Screen getCurrentScreen() {
        return currentScreen;
    }

    /* สร้างฟอนต์ จากไฟล์ .otf ด้วย FreeType */
    public BitmapFont createFont(int size, Color color) {
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Inter-Regular.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter p = new FreeTypeFontGenerator.FreeTypeFontParameter();
        p.size = size;
        p.color = color;
        BitmapFont font = gen.generateFont(p);
        gen.dispose();
        return font;
    }

    /* รูปสีขาว 1x1 พิกเซล */
    public Texture createPixel() {
        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(Color.WHITE);
        pm.fill();
        Texture t = new Texture(pm);
        pm.dispose();
        return t;
    }
}
