package com.gamedev.buckshot.UIscreen;

import com.badlogic.gdx.Gdx;   
import com.badlogic.gdx.graphics.*;    
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.ScreenUtils;     

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Loginscreen extends Mainmenuscreen {

    private static final float W = 1920f, H = 1080f;

    private final Rectangle input = new Rectangle(610, H - 560 - 118, 700, 118);
    private final Rectangle exit = new Rectangle(738, H - 920 - 118, 444, 118);

    private String playerName = "";
    private String errorMsg = "";
    private float blinkTimer = 1f;

     public Loginscreen(Main game) {
        super(game);               
     }   

    @Override
    public void show() {
        super.show();

    Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.BACKSPACE && !playerName.isEmpty()) {
                    playerName = playerName.substring(0, playerName.length() - 1);
                    errorMsg = "";
                    return true;
                }
                if (keycode == Input.Keys.ENTER) {
                    Submitlogin();
                    return true;
                }
                return false;
            }

            @Override
            public boolean keyTyped(char character) {
                if (String.valueOf(character).matches("^[a-zA-Z0-9 _.-]*$") && playerName.length() < 13) {
                    playerName += character;
                    errorMsg = "";
                    return true;
                } else if (character >= 32 && character != 127) {
                    errorMsg = "Not Other Language Man English Only man";
                }
                return false;
            }
        });
    }


    @Override
    public void render(float delta) {
        blinkTimer += delta;
        boolean showCursor = ((int) (blinkTimer * 2) % 2 == 0);

        touch.set(Gdx.input.getX(), Gdx.input.getY());
        viewport.unproject(touch);

        if (Gdx.input.justTouched() && exit.contains(touch.x, touch.y)) {
            onExitClicked();
            return;
        }

        ScreenUtils.clear(1f, 1f, 1f, 1f);
        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // ใช้เมธอด drawCentered และ drawButton ที่สืบทอดมาจาก Mainmenuscreen ได้เลย
        drawCentered(titleFont, "Buckshot", W / 2f, 800f);
        drawCentered(buttonFont, "Enter Your name Bro", W / 2f, 600f);

        // วาดกล่องพิมพ์ชื่อ
        batch.setColor(0.92f, 0.92f, 0.92f, 1f);
        batch.draw(pixel, input.x, input.y, input.width, input.height);
        batch.setColor(Color.WHITE);

        String display = playerName + (showCursor ? "_" : "");
        drawCentered(buttonFont, display, input.x + input.width / 2f, input.y + input.height / 2f);

        // วาดปุ่ม EXIT
        drawButton(exit, "EXIT");
        //drawButton(Return, "Return MENU");

        // แสดงข้อความแจ้งเตือนกรณีเว้นวรรคเกิน 2 ครั้ง
        if (!errorMsg.isEmpty()) {
            drawCentered(buttonFont, errorMsg, W / 2f, 250f);
        }

        batch.end();
    }

    private void Submitlogin() {
        if (playerName.trim().isEmpty()) {
            errorMsg = "Bro Enter your name man WTF";
            return;
        }

        int spaceCount = 0;
        for (int i = 0; i < playerName.length(); i++) { // space == 2 -> Error
            if (playerName.charAt(i) == ' ') {
                spaceCount++;
            }
        }
        if (spaceCount == 2) {
            errorMsg = "Dont 2 Space Bro";
            return;
        }

        game.setPlayerName(playerName);
        Savefile(playerName);
        game.setScreen(new Gamescreen(game));
    }

    private void Savefile(String name) {
        File file = new File("User.csv");
        boolean isNew = !file.exists();
        try (FileWriter fw = new FileWriter(file, true);
             PrintWriter pw = new PrintWriter(fw)) {
            if (isNew) {
                pw.println("PlayerName, LoginTime");
            }
            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            pw.println("\"" + name + "\", " + time);
        } catch (IOException e) {
            System.err.println("CSV Error: " + e.getMessage());
        }
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
        super.hide(); // เรียก dispose() ของคลาสแม่
    }
    
}
